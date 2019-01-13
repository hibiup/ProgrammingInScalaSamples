package programming_scala_2nd_edition

package Sample_25_Monad {
    package FunctorSample {
        /**
          * 本例将一个 Int -> Double 的集合变成 Int -> (Double) -> String 的集合，演示函数态射组合的二元操作。 p(356)
          */
        object TestFunctor {
            /**
              * 1) 使用更泛化的 Functor。参数是一个高级类型的容器。注意： F[_] 未必是一个具体的容器，在本例中它将被实例
              * 化成 X => Y 函数。 (并非定义成 F[_] 形式的参数都必须是容器，它的定义来自 product.)
              * */
            trait Functor[F[_]] {
                /** map 的参数是F[A]，和另外一个 X => Y 类型的函数参数。F[A] 将会被override 为一个类型为 X => Y 的函数*/
                def map[A, B](fa: F[A])(f: A => B): F[B]
            }

            /** 2) 定义一个射态类来实现二元操作 */
            object Morphism {
                /** 2-1) 这个 Morphism 的 map 方法的输入参数是我们要实现转换的两个函数，返回的结果是: A => (A2) => B。最终
                  *      将 A => A2 转为 A => B 类型的函数。转换的过程就是先执行原函数 f 得到 A2，再执行 f2: A2 => B */
                def map[A, A2, B](f: A => A2)(f2: A2 => B): A => B = {
                    /** 3) 实例化 Functor，将它的 F[_] （用 type lambda）实例化为 X => Y 类型的函数参数。 */
                    val functor = new Functor[({type λ[β] = A => β})#λ] {
                        /** 4) 因此 override map 第一个参数为第一个转换函数，第二个参数为第二个转换函数。*/
                        override def map[A3, B](fa: A => A3)(f: A3 => B): A => B = (a: A) => f(fa(a))
                    }
                    functor.map(f)(f2)
                }
            }

            def apply() = {
                // 测试 Morphism
                val i2d: Int => Double = i => 1.0 * i
                val d2s: Double => String = d => d.toString
                val fa = Morphism.map(i2d)(d2s)
                println(fa(2))                             // "2.0"

                /**
                  * Morphism.map(f1)(f2) == f2 compose f1
                  *
                  * compose 是定义在 Function1 里的方法。函数变量都具有这个方法。*/
                val i2s = d2s compose i2d
                println(i2s(2))                            // "2.0"


                // Seq 其实也是一个 A => B 函数：
                object SeqFunctor extends Functor[Seq] {
                    def map[A, B](seq: Seq[A])(f: A => B): Seq[B] = seq map f
                }
                println(SeqFunctor.map(List(1,2,3,4))(i2d))  // List(1.0, 2.0, 3.0, 4.0)

            }
        }
    }

    package MonadSample {
        /**
          * p(361)
          *
          * 用 M[_] 表示拥有 “Monad 性质” 的类型。跟 Functor 一样，它只带有一个类型参数。
          **/
        trait Monad[M[_]] {
            def flatMap[A, B](fa: M[A])(f: A => M[B]): M[B] // 注意传给flatMap 的参数f 类型为A => M[B]，而不是A => B。
            def unit[A](a: => A): M[A]                      // 输入 a，返回它的 Monad. unit 与case 类的 apply 方法多么相似，
                                                            // 两者都传入了一个值，然后返回一个类型实例！

            /** 其他异名方法 */
            def bind[A, B](fa: M[A])(f: A => M[B]): M[B] = flatMap(fa)(f) // 等同于 flatMap
            def pure[A](a: => A): M[A] = unit(a)                          // 等同于 unit

            /** >>= 和 return 是 Haskell 的标准。但在 Scala 中，这两个名字是有问题的。由于 = 操作符的 优先级问题，>>= 运算的 = 会带
              * 来其他行为，因此必须将它们放在一起定义成函数。 */
            def >>=[A, B](fa: M[A])(f: A => M[B]): M[B] = flatMap(fa)(f)  // 也等同于 flatMaps
            def `return`[A](a: => A): M[A] = unit(a)                      // 也等同于 unit。将 return 用引号引起，否则这个名字会与关键字冲突。
        }

        // 为 Seq 添加 Monad 方法，Seq 是具有 Monad 性质的类
        object SeqM extends Monad[Seq] {
            def flatMap[A, B](seq: Seq[A])(f: A => Seq[B]): Seq[B] = seq flatMap f

            def unit[A](a: => A): Seq[A] = Seq(a)
        }

        // 为 Option 添加 Monad 方法
        object OptionM extends Monad[Option] {
            def flatMap[A, B](opt: Option[A])(f: A => Option[B]): Option[B] = opt flatMap f

            def unit[A](a: => A): Option[A] = Option(a)
        }

        object TestMonad {
            def apply() = {
                val seqf: Int => Seq[Int] = i => 1 to i
                val optf: Int => Option[Int] = i => Option(i + 1)

                println(SeqM.flatMap(List(1, 2, 3))(seqf))        // Seq[Int]: List(1,1,2,1,2,3)
                println(SeqM.flatMap(List.empty[Int])(seqf))      // Seq[Int]: List()

                println(OptionM.flatMap(Some(2))(optf))           // Option[Int]: Some(3)
                println(OptionM.flatMap(Option.empty[Int])(optf)) // Option[Int]: None
            }
        }
    }

    object RandomState {
        /**
          * 通过一个无副作用的随机数生成器来阐述 FP 中的状态 State 转移 （《Scala函数式编程》 P-62）
          * */

        /**
          * 随机数生成器总是从一个状态转移到另一个状态，这样才能源源不断地生成新的数字。新生成的数字是状态转移的结果，
          * 称为 state action，记作 “A”，而在 FP 中，我们并不修改之前的状态，而是生成新的状态值（随机数生成器），
          * 记作 “S”。因此有以下随机数函数接口定义。
          * */
        trait Rng {
            def next:(Int, Rng)
        }

        /**
          * 定义一个函数去调用上面的这个接口来进行状态运算（状态运算后我们才能得到A），根据 Rng的定义，这个函数应该输入
          * 一个 Rng 接口，返回 (A, Rng)。由此我们可以得出这个函数类型的接口形态是：
          * */
        type Rand[+A] = Rng => (A, Rng)  // （注意：Rand[+A] 是一个函数的 type）

        /**
          * 因为 Rand 返回的是 Tuple，而我们要获得的是 Int，因此我们再定义一个获得 Int 的偏应用函数：
          *
          *   1) randInt 是一个 Rand[Int] 类型的偏应用函数
          *   2) 既然 randInt 是 Rand，并根据以上定义直到它的待定参数类型是 Rng，要得到返回值就必须执行待定参数的 next 方法。
          * */
        val randInt:Rand[Int] = _.next

        /**
          * 模拟随机数算法
          * */
        final case class RngNumber(seed:Int) extends Rng {
            override def next: (Int, Rng) = {
                val newSeed = seed + 1   // 不是很严谨的随机数算法
                (newSeed >>> 6, RngNumber(newSeed))
            }
        }
        def rollDie(n:Int) =
            for(i <- 1 to n)   // seed 一样的话，结果也必然一样
                println(randInt(RngNumber(i*100)))

        /** map 函数版本 */
        def map[A, B](s: Rand[A])(f: A => B): Rand[B] = rng => {
            val (a, rng2) = s(rng)
            (f(a), rng2)
        }
        def rollDieMap(n:Int) =
            for(i <- 1 to n)
                println(map(randInt)(x => x)(RngNumber(i*200)))
    }
}

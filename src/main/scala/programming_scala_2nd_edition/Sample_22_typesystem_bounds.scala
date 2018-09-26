package programming_scala_2nd_edition

package Sample_22_typesystem_bounds {
    object LowerBound {
        /**
          * B >: A  表示 B 必须是 A 的子类或同类.
          * */
        class Parent(val value: Int) {
            override def toString = s"${this.getClass.getName}($value)"
        }

        class Child(value: Int) extends Parent(value)

        def apply() = {
            /**
                // Option 的参数时协变参数.
                sealed abstract class Option[+A] extends Product with Serializable {
                    ...
                    // Option 的 getOrElse 方法尝试获得 Option 内的值,否则返回 default.
                    // 返回的 default 的值 B 必须是(或s可以转换成)泛型参数 A 的父类或同类.
                    @inline final def getOrElse[B >: A](default: => B): B = {...}
                    ...s
                }
              *
              * */
            // 虽然构造给入 Child, 但是因为是协变,所以可以转换成父类 Option[Parent], A =:= Parent
            val op1: Option[Parent] = Option(new Child(1))
            // op1 的类型是 Option[Parent]. 所以获得的是转换成 Parent 的 Child(1), 就是说实际上得到了 Parent(B) 类型,
            // Parent(B) >: Parent(A) 成立，因此可以通过编译
            val p1: Parent = op1.getOrElse(new Child(10))
            println(p1)

            // 同理于上例(不要被 Option[Child] 迷惑，op2 是 Option[Parent], A =:= Parent):
            val op2: Option[Parent] = Option[Child](null)
            // 将结果 Child(10) 转成 Parent(B) 返回, Parent(B) >: Parent(A) 成立, 因此 p2 可以得到.
            val p2: Parent = op2.getOrElse(new Child(10))
            println(p2)

            // 但是如果 A 是 Parent，而返回的是 Child(B), 那么无法通过编译。
            val op3: Option[Parent] = Option[Child](null)
            //val p3: Child = op3.getOrElse(new Child(10))  // 因为 Child(B) >: Parent(A) 不成立。
            //println(p3)
        }
    }

    object ViewBound1 {
        /**
          * 视图界定（View Bounds）:
          * */
        case class Writable(value: Any) {
            def write: String = s"-- $value --"  // 得到 value 的序列化值
        }

        object Serialization {
            /**
              * 1-A) 符号 [T <% U] 是视图界定表达式。它表示参数 T 必须有一个对应的视图，将其映射到类型 U
              *      本例中表示参数 T 应该可以被映射到 Writable。因此我们需要为 T => Writable 定义隐式转换。
              **/
            def serialized[T <% Writable](t: T): String = t.write

            /**
              *  1-B) 实际上 View bound 已经不被推荐 （https://issues.scala-lang.org/browse/SI-7629）
              *       因为单纯通过隐式转换就可以完成这项功能。
              * */
            def serialized2(t: Writable): String = t.write

            /**
              * 1-C) 或干脆写成隐式参数，都等价于 View Bound
              * */
            def serializedInt(t: Int)(implicit sInt: Int => Writable): String = sInt(t).write
            def serializedFloat(t: Float)(implicit sFloat: Float => Writable): String = sFloat(t).write
            def serializedString(t: String)(implicit sString: String => Writable): String = sString(t).write
        }

        /**
          *  2) 为满足 T <% Writable 定义的隐式数据类型转换函数
          */
        implicit def fromInt(i: Int) = Writable(i)
        implicit def fromFloat(f: Float) = Writable(f)
        implicit def fromString(s: String) = Writable(s)

        def apply() = {
            // 1-A) 使用 View Bound 函数。
            println(Serialization.serialized(100))
            println(Serialization.serialized(3.1f))
            println(Serialization.serialized("Hello"))

            // 1-B) 不使用 View Bound 函数，直接应用隐式。
            println(Serialization.serialized2(100))
            println(Serialization.serialized2(3.1f))
            println(Serialization.serialized2("Hello"))

            // 1-C) 或使用隐式参数。
            println(Serialization.serializedInt(100))
            println(Serialization.serializedFloat(3.1f))
            println(Serialization.serializedString("Hello"))
        }
    }

    object ViewBound2 {
        /**
          *  取代 View Bound 更通用的解决办法是：p(321)
          **/

        // 定义一个通用的泛型函数
        case class WritableOps[A](value: A) {
            def write: String = s"-- $value --"  // 得到 value 的序列化值
        }
        type Writable[A] = A => WritableOps[A]  // 定义一个类型。（不是必须的，只是为了更好用）

        // 定义隐式转换函数(变量)，完全等价于 ViewBound1 中的隐式函数。
        implicit val fromInt: Writable[Int] = (i: Int) => WritableOps(i)
        implicit val fromFloat: Writable[Float] = (f: Float) => WritableOps(f)
        implicit val fromString: Writable[String] = (s: String) => WritableOps(s)

        object Serialization {
            /**
              * [T : Writable] 取代了 [T <% Writable]。好处是 Writable 的类型参数 A 可以上下文隐式得到。
              */
            def serialized[T: Writable](t: T): String = t.write
        }

        def apply() = {
            // 1-A) 使用 View Bound 函数。
            println(Serialization.serialized(100))
            println(Serialization.serialized(3.1f))
            println(Serialization.serialized("Hello"))
        }
    }
}

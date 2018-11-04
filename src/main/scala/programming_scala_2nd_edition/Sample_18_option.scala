package programming_scala_2nd_edition

package Sample_18_option {

    import scala.util.Either.{LeftProjection, RightProjection}
    import scala.util.Try

    object ForOptionObject {
        def apply(): Iterable[Int] = {
            val seq: Seq[Option[Int]] = Vector(Some(10), None, Some(20))
            for {
                s <- seq // 得到 Some(a), 会过滤掉 None
                a <- s // 得到 a
            } yield (2 * a) // 执行结果: Seq[Int] = Vector(20, 40)

        }
    }

    object NoneWithForLoop {
        def apply():Either[String, Int] = {
            /**
              *  当 for 生成式得到　None 时会终止执行，并且“悄悄地”终止程序执行而不是崩溃或抛出异常。
              *  */
            def OptionPositive(i: Int): Option[Int] = if (i > 0) Some(i) else None  // i 必须 > 0
            val mustNone = for {
                i1 <- OptionPositive(5)
                i2 <- OptionPositive(-1 * i1) // 失败! 返回了 None
                i3 <- OptionPositive(25 * i2) // 不会被执行。
            } yield (i1 + i2 + i3)      // 返回 None

            assert(mustNone == None)

            /**
              * Either 可以用于防止遇到 None 时毫无声息。
              *
              * Either 概念的产生时间早于Scala。很长时间以来它被认为是抛出异常的一种替代方案。为了尊重历史习惯，当Either
              * 用于表示错误标志或某一对象值时，Left 值用于表示错误标志。
              *
              * 当以下 for 生成式试图访问 Either.right 而遇到 None 时，它会返回 left，并停止运行。因此以下最终结果将
              * 会得到 Left(nonpositive number -5)
              *
              * p(203)
              *
              * 　*/
            def EitherPositive(i: Int): Either[String,Int] =
                if (i > 0) Right(i)  // Right 和 Left 都是 Either 的子类s
                else Left(s"nonpositive number $i")   // Left 表示错误
            for {
                i1 <- EitherPositive(5).right         // 5
                i2 <- EitherPositive(-1 * i1).right   // Either(s"nonpositive number -5", None).right 遇到 None，实际会返回 left。
                i3 <- EitherPositive(25 * i2).right   // 不会被执行
            } yield (i1 + i2 + i3)
        }
    }

    object EitherLeftProjection {
        def apply() = {
            /** *
              * 1）虽然 Either 有两个泛型参数，但是 Projection 只持有一方的值，因为它的构造函数只接受一个值：
              * ===
              * val p: String Either Int = Right(2)
              * 或
              * val p: String Either Int = Left("boohoo")
              * ===
              * 由此可见从中得到的 Projection 或者是Right，或者是 Left
              *
              * p(203)
              *
              */
            leftProjection()
            rightProjection()
        }

        def leftProjection() = {
            /** *
              * 1) 可以通过 left或 right 方法得到 Either(Left或Right)的LeftProjection或RightProjection对象。
              *
              * 2）如果对 LeftProjection 使用 map，而 LeftProjection 恰好来自 Left，则将left 值传给 map。
              *
              * 3）如果 LeftProcection 来自 Right，那么 map 不会得到 left（left没有值），它会得到 right 的值:
              */
            val l:Either[String, Int] = Left("boo")
            val r:Either[String, Int] = Right(12)

            assert(l.left == LeftProjection(Left("boo")))   // l 是Left，因此 left 可以得到: Left("boo")
            // l.left.map(_.size)　会将 "boo".size　映射成 Left(3)，因此在次获得left就是 LeftProjection(Left(3)
            assert(l.left.map(_.size).left ==  LeftProjection(Left(3)))

            assert(r.left == LeftProjection(Right(12)))     // r 是Right，left 不存在，会返回: Right(12)
            //　对 left 的无论什么后续操作都不会生效，都只会得到 Right(12)
            assert(r.left.map(x=>x + x) ==  Right(12))
        }

        def rightProjection() = {
            val l:Either[String, Int] = Left("boo")
            val r:Either[String, Int] = Right(12)

            assert(l.right == RightProjection(Left("boo")))  // l 的 right 返回的是 Left("boo")，因为不存在 Right

            assert(r.right == RightProjection(Right(12)))    // r 的 right 返回的是正确的　RightProjection(Right(12))
            // 于 l.left 类似，因为 r.right可以得到有效值，因此可以对它做操作
            assert(r.right.map(_.toDouble) == Right(12.0))
        }
    }

    object TryEither {
        def apply(): Unit = {

            val l:Either[Exception, String] = Left(new RuntimeException("Failed"))
            l match {
                case Left(s:Exception) => println(s.getMessage)
            }

            val r:Either[Exception, String] = Right("custom_id")
            r match {
                case Right(s) => println(s)
            }

            val x1 = Try(r)
            println(x1)

            val x2 = Try(l)
            println(x2)
        }
    }
}

package programming_scala_2nd_edition

package Sample_19_Try {
    object ForWithTry {
        import scala.util.{ Try, Success, Failure }

        def apply():Try[Int] = {
            /** 定义一个　Try 实例：
              * １）用 assert 来断言
              * ２）如果断言成功，则返回值　*/
            def positive(i: Int): Try[Int] =
                if (i > 0) Success(i)
                else Failure(new AssertionError("assertion failed"))

            for {
                i1 <- positive(5)        // 会返回
                i2 <- positive(-1 * i1)  // 失败!
                i3 <- positive(25 * i2)
            } yield (i1 + i2 + i3)       // 返回值: scala.util.Try[Int] = Failure( java.lang.AssertionError: assertion failed: nonpositive number -5)
        }
    }
}

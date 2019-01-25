package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_19_Try._

class Sample_19_Tyr_test extends FunSuite{
    test("For with Try") {
        val res = ForWithTry()
        assert(res.isFailure)
    }

    test("Try of possible Failure") {
        /** Try 返回 Success[_] or Failure[_]*/
        var badUrl = "htttp://aaa"  // Bad url format
        var rightUrl = "http://aaa"

        import scala.util.Try
        import java.net.URL
        val res = Try(new URL(badUrl))
        // 检查是否失败
        assert(res.isFailure)  // Failure

        /** 对一个 Failure 执行 get，会得到 Throwable */
        try { val url = res.get }
        catch { case t:Throwable => println(t.getMessage) }

        /** 可以用 getOrElse 避免 */
        val url = res.getOrElse(new URL(rightUrl))  // http://aaa
        assert(new URL(rightUrl) == url)
        res.map{println}  // rightUrl: http://aaa

        val res1 = Try(new URL(rightUrl))
        assert(res1.isSuccess)

        /** 对一个 Success get 可以得到期待的结果 */
        assert(new URL(rightUrl) == res1.get)
    }

    test("Try with Future") {
        import scala.concurrent.Future
        import scala.concurrent.Await
        implicit val ec = scala.concurrent.ExecutionContext.global
        implicit val timeout = scala.concurrent.duration.Duration.Inf

        var badUrl = "htttp://aaa"  // Bad url format
        var rightUrl = "http://aaa"

        import scala.util.Try
        import java.net.URL

        val f = Future[URL](new URL(badUrl))
        val res = Try(f)
        /** Future 的创建是成功的，但是并不等于执行过程会成功 */
        assert(res.isSuccess)  // Success(Future(Failure))

        /** 对一个 内容失败的的 Successful Future 执行 get，会得到 Future(Failure) */
        val future_result = res.get   // Future(Failure)

        /** Await 或 map 出失败 */
        try { Await.result(future_result, timeout) }
        catch { case t:Throwable => println(t.getMessage) }
    }
}

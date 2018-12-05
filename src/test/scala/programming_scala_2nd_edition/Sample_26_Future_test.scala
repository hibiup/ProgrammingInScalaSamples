package programming_scala_2nd_edition.Sample_26_Future

import org.scalatest.FunSuite
import programming_scala_2nd_edition.DynamicFutureReturnType.Compute

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.forkjoin.ForkJoinPool
import scala.util.Success

class Sample_26_Future_test extends FunSuite{
    test("Future callback") {
        FutureCallbackFunctions()
    }

    test("Future be await") {
        FutureAwait()
    }

    test("Try a Future") {
        TryFuture()
    }

    test("Future on complete") {
        FutureComplete()
    }

    test("Async") {
        FutureAsync()
    }

    test("Test Future Await") {
        Compute()
    }

    test("Await multiple futures") {
        AwaitMultipleFutures()
    }

    test("Test Future Future exception") {
        FutureException()
    }

    test("Future should be able to avoid Stackoverflow") {
        import scala.concurrent.duration._
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(1))

        val p = Promise[ BigInt]()
        val f = p.future
        f.onComplete{ case Success(x:BigInt) => println(x)}

        def fib(n: Int, a:BigInt=0, b:BigInt=1):Unit = Future( if (n > 0) fib(n - 1, b, a+b) else p.success(a) )

        fib(10000)

        Await.result(f, Duration.Inf)
    }
}

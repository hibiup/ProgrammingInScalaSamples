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
        case class Fib(first: Long, second:Long)

        val p = Promise[Fib]()
        val f = p.future
        f.onComplete{case Success(Fib(first:Long, _)) => println(first)}

        def playGame(n: Int)(implicit fib: Fib=Fib(0,1)):Future[Fib] = Future {
            if (n > 0) playGame(n - 1)(Fib(fib.second, fib.first + fib.second)) else p.success(fib)
        }.asInstanceOf[Future[Fib]]

        playGame(1000000)

        Await.result(f, Duration.Inf)
    }
}

package programming_scala_2nd_edition.Sample_26_Future

import org.scalatest.FunSuite
import programming_scala_2nd_edition.DynamicFutureReturnType.Compute

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.forkjoin.ForkJoinPool

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
        implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(new ForkJoinPool(1))
        case class Round(count: Int)

        def playGame[T]: Round => Future[T] = (hit) => Future { playGame {
            (new RuntimeException(hit.count.toString)).printStackTrace()
            Round( hit.count + 1)
        }}(ec).asInstanceOf[Future[T]]

        playGame(Round(1))

        Thread.sleep(100)
    }
}

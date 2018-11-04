package programming_scala_2nd_edition.Sample_26_Future

import org.scalatest.FunSuite
import programming_scala_2nd_edition.DynamicFutureReturnType.Compute

class Sample_26_Future_test extends FunSuite{
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
}

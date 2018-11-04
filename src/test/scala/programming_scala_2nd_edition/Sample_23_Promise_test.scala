package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.PromisedFuture.FailurePromise

class Sample_23_Promise_test extends FunSuite{
    test("Test FailurePromise") {
        FailurePromise()
    }
}

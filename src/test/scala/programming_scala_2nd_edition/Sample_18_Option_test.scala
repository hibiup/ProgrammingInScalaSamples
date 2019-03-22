package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_18_option._

class Sample_18_Option_test extends FunSuite{
    test("For loop option objects") {
        ForOptionObject() foreach println
    }

    test("Stop process with meet None") {
        println(NoneWithForLoop())
    }

    test("Either Projection") {
        EitherLeftProjection()
    }

    test("Try either") {
        TryEither()
    }
}

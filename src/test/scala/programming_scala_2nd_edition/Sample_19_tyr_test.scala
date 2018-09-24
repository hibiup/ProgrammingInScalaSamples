package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_19_Try._

class Sample_19_tyr_test extends FunSuite{
    test("For with Try") {
        val res = ForWithTry()
        assert(res.isFailure)
    }
}

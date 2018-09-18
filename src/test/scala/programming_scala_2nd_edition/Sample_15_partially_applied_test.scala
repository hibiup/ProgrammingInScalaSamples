package programming_scala_2nd_edition.sample_15

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_15_partially_applied._

class Sample_15_partially_applied_test extends FunSuite {
    test("partially applied samples") {
        apply_tuple_for_function()
        uplift_partially_applied_function()
    }
}

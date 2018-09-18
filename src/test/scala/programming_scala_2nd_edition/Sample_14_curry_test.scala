package programming_scala_2nd_edition.sample_14

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_14_curry._

class Sample_14_curry_test extends FunSuite{
    test("curry samples") {
        general_curry()
        curry_without_underscore()
        curry_without_underscore_with_multiple_param()
        curried_normal_function()
    }
}

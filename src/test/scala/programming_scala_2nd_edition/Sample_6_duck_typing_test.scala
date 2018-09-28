package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_6._

class Sample_6_duck_typing_test extends FunSuite{
    test("Structural Type for Observation pattern") {
        StructialType()
    }

    test("Another duck typing with structural type") {
        duck_typing()
    }
}

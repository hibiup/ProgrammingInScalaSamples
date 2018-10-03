package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_28_tag._

class Sample_28_tag_test extends FunSuite{
    test("TestClassTag") {
        TestClassTag()
    }

    test("TestTypeTag") {
        TestTypeTag()
    }

    test("TypeTagInVariant") {
        TypeTagInVariant()
    }

    test("GetTypeRef") {
        GetTypeRef()
    }
}

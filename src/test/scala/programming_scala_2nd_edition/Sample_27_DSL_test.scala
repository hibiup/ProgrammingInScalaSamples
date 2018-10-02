package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_27_Dynamic._

class Sample_27_DSL_test extends FunSuite{
    test("Dynamic") {
        DynamicMethod()
    }

    test("AnnualSalaryDSL") {
        AnnualSalaryDSL()
    }
}

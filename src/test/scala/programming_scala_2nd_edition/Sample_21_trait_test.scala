package programming_scala_2nd_edition

import org.scalatest.FunSuite

class Sample_21_trait_test extends FunSuite{
    test("Override method") {
        import programming_scala_2nd_edition.Sample_21_trait.OverrideMethod._
        ClickButton()
    }

    test("Test execution order") {
        import programming_scala_2nd_edition.Sample_21_trait.ExecutionOrder._
        GenerateClass()
    }

    test("Lazy for trait") {
        import programming_scala_2nd_edition.Sample_21_trait.LazyForParent._
        CreateAnonymous()
    }
}

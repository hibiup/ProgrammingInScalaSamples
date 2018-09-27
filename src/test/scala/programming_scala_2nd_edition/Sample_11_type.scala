package programming_scala_2nd_edition.sample_11

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_11.types.SelfTypeAnnotation.TestSubjectObserver
import programming_scala_2nd_edition.sample_11.types._

import scala.reflect.runtime.universe._

class Sample_11_type extends FunSuite {
    test("Types") {
        TypeOfIntroduce()
    }

    test ("Inner type") {
        ShadowType()
    }

    test("Test single type") {
        InstanceType()
    }

    test("Structural type") {
        StructuralType()
    }

    test("Self-Type　Annotation") {
        TestSubjectObserver()
    }

    test("Abstract Type") {
        AbstractType.TypeHeritage()
    }

}

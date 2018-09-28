package programming_scala_2nd_edition.sample_11

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_11.types.SelfTypeAnnotation.TestSubjectObserver
import programming_scala_2nd_edition.sample_11.types.SelfTypeAnnotation2.SelfTypeAnnotationSample2
import programming_scala_2nd_edition.sample_11.types.TypePath.TestTypePath
import programming_scala_2nd_edition.sample_11.types.{SelfTypeAnnotation2, _}

import scala.reflect.runtime.universe._

class Sample_11_type extends FunSuite {
    test("Types") {
        TypeOfIntroduce()
    }

    test ("Inner type") {
        ShadowType()
        ShadowType2()
    }

    test ("Type path") {
        TestTypePath()
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

    test ("Self-Type　Annotation sample 2") {
        SelfTypeAnnotationSample2()
    }

    test("Test This Alias") {
        ThisAlias()
    }

}

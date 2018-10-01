package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_24_higherkinded.F_Bounded.FBounded
import programming_scala_2nd_edition.Sample_24_higherkinded._

class Sample_24_higherkinded_test extends FunSuite{
    test("Type Lambda") {
        typeLambda.TypeLambda() foreach println
    }

    test("F-Bounded") {
        FBounded()
    }
}

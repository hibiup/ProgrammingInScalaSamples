package programming_scala_2nd_edition.Sample_30_macro

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_30_micro._

class Sample_30_macro_test  extends FunSuite{
    test("Macro test") {
        val n = ShowCode {
            val a = 1
            val b = a + 2
            a + b
        }

        println(n)
    }

    test("Compile time print") {
        val printA = new PrintA[Int]
        val x = 2
        printA.myPrint(x + 2)
    }
}

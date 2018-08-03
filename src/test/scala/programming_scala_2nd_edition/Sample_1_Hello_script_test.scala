package programming_scala_2nd_edition.sample_1

import org.scalatest.FunSuite

class Sample_1_Hello_script_test extends FunSuite {
    test("Hello") {
        Hello.main("Hello", "World")
    }
}

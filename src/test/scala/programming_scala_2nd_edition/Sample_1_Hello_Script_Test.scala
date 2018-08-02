package programming_scala_2nd_edition

import org.scalatest.FunSuite

class Sample_1_Hello_Script_Test extends FunSuite {
    test("Hello") {
        Hello.main("Hello", "World")
    }
}

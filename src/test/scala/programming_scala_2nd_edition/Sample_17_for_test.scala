package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_17_for._

class Sample_17_for_test extends FunSuite{
    test("filters write space") {
        val input = RemoveBlanks("src/main/scala/programming_scala_2nd_edition/Sample_17_for.scala")

        // loop
        input.foreach(println)

        // 等同于以上
        for (line <- input) println(line)

        // 删除 2 行，然后打印 5 行
        input drop 2 take 5 foreach println
    }

    test("multiple condition") {
        MultipleCondition() map println
    }

    test("for loop generator with definition") {
        ForWithDefinition()
    }

    test("Read properties") {
        for {
            (k, v) <- ReadProperties()
        }println(k,v)
    }

    test("Regular expression") {
        RegExpression() foreach println
    }
}

package programming_scala_2nd_edition.sample_5

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_2.Point

class Sample_5_curry_test extends FunSuite {
    val s = new Circle(Point(1.0, 1.0), 1.2)

    test("Test currys") {
        // curry 风格的调用方式
        s.draw(Point(1.0, 2.0))(str => println(s"ShapesDrawingActor: $str"))

        // 可以用 {} 代替 ()
        s.draw(Point(1.0, 2.0)) {
            str => println(s"ShapesDrawingActor: $str")
        }
    }

    test("Test implicit parameter") {
        implicit def say(s:String) = println(s)

        val imp = new HelloImplicitParameter
        val g = imp.greeting("Hello")
    }
}

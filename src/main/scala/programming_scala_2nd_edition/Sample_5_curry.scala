package programming_scala_2nd_edition.sample_5

import programming_scala_2nd_edition.sample_2.Point

abstract class Shape() {
    /**
      * draw 带两个参数列表(用 "()" 划分成两组)，第一个参数（列表）是表示绘制偏移量，第二个参数(列表)是输出一段字符串。
      */
    def draw(offset: Point = Point(0.0, 0.0))(f: String => Unit): Unit =
        f(s"draw(offset = $offset), ${this.toString}")
}

case class Circle(center: Point, radius: Double) extends Shape
case class Rectangle(lowerLeft: Point, height: Double, width: Double) extends Shape


/**
    Curry 和 implicit parameter 结合
 */
class HelloImplicitParameter {
    def greeting(greeting:String)(implicit f: String => Unit):Unit = {
        f(s"greeting: Hello, ${this.toString}")
    }
}

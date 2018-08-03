package programming_scala_2nd_edition.sample_2

import org.scalatest.FunSuite

class Sample_2_case_class_test extends FunSuite {
    test("Compare case class") {
        val p1 = new Point(2.0, 0.0)
        val p2 = new Point(2.0)
        val p3 = new Point(y=2.0)
        val p4 = Point.apply(y=2.0)   // apply 方法实际上是构建 Point 对象的工厂方法，自动生成
        val p4_1 = Point(y=2.0)       // 等同于 Point.apply(y=2.0)

        assert(p1 == p2)
        assert(p2 != p3)
        assert(p3 == p4)
        assert(p4 == p4_1)
    }
}

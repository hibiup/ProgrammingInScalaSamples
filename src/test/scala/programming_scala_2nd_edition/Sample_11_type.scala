package programming_scala_2nd_edition.sample_11

import org.scalatest.FunSuite
import scala.reflect.runtime.universe._

class Sample_11_type extends FunSuite {
    class A{
        class X
        def foo(x:A#X) =        // 父类型　“A#X”　是子类型的投影类型，可以做为参数类型被接受。
            x match {
                case _: A#X => println(typeOf[x.type]) // 可以匹配，但是不会改变类型推断的结果。
            }
    }
    class B extends A
    class C extends A

    test("Types") {
        //class A
        //val a = new A
        //println(typeOf[a.type])  // a.type

        /**
          * 类型（type）是介于类和类实例中间的一个抽象的概念: class > type > instance
          *
          *
          * 当我们实例化一个　class 的时候，实际上是实例化了一个类型。直观地理解就是冒号后面的部分实际上是 type，而不是 class:
          *   val name:type
          *
          * class 和 type 的区别是：
          *
          * 1) List 是类，List[Int] 是类型，它不等于 List[Double]。一个实例　intList: List[Int]　来自List[Int]，
          * 　　它的类型不同于　doubleList: List[Double]: typeOf[intList] != type[doubleList]
          *    但是它们的　class 是相同的。所以对于泛型类型取决于它们的泛型参数。
          */
        val intList: List[Int] = List[Int]()
        val doubleList: List[Double] = List[Double]()

        // 类相同
        assert(intList.getClass == doubleList.getClass)
        // 值（空集）也相同
        assert(intList == doubleList)

        // 类型不相同
        assert(typeOf[List[Int]] != typeOf[List[Double]])
        assert(typeOf[intList.type] != typeOf[doubleList.type])
    }

    test ("Inner type") {
        /**
          * type 同时具有路径属性，相同的 class 在不同的路径下被实例化产生的 type 也不同。
          */
        val b = new B
        val c = new C
        assert(typeOf[b.X] != typeOf[c.X])

        /** 但是它们的　"投影类型"(A#X)　是相同的　*/
        val x = new c.X
        b.foo(x)
    }

    test("Test single type") {
        import single_type._

        val b = new single_type.B
        val self = b.method_this_type
        self.method_B

        /** method_type_A 因为明确要求返回 A type，所以无法得到子类方法。*/
        //val parent = b.method_type_A
        //parent.method_B
    }

    test("Structural type") {
        /**
          * type 不仅仅可以继承，还可以动态定义，比如定义“鸭类”时常用的“结构类型”也可以定义成 type
          */
        type Closable = { def close():Unit }
        def free(res:Closable) = res.close  // name:type
    }
}

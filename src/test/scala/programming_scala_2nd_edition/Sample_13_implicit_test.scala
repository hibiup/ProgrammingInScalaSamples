package programming_scala_2nd_edition.sample_13

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_13.implicitly_sample.MyList

class Sample_13_implicit_test extends FunSuite{
    test("Test implicit type conversion") {
        Implicate_Conversion()
    }

    test("Test implicit class") {
        Implicit_Class()
    }

    test("Test implicit evidence") {
        Implicit_Evidence()
    }

    test("test implicit value") {
        Implicit_value()
    }

    test("Test implicit for trait") {
        Implicitly_add_trait_for_class()
    }

    test("implicit sort list") {
        val list = MyList(List(1, 3, 5, 2, 4))
        /** 　
          * SortBy　的第二个参数是隐式传递的，无需明确写出。
          * ｉ=> -i 会导致最大的 5 变成 -5 这样在排序的时候就排到了最后。
          * */
        println(list SortBy1 (i => -i))
        println(list SortBy2 (i => -i))
    }

}

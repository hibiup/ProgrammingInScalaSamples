package programming_scala_2nd_edition.sample_12

import org.scalatest.FunSuite

class Sample_12_tailrec_test extends FunSuite{
    test("tail recursive") {
        assert(10 == sum_of_list_with_match(List(1,2,3,4), 0))
    }
}

package programming_scala_2nd_edition

import org.scalatest.FlatSpec

class Sample_31_Stream_test extends FlatSpec{
    "Stream fib" should "" in {
        import Sample_31_Stream._
        fib_stream()
    }

    "View fib" should "" in {
        import Sample_31_Stream._
        fib_view()
    }
}

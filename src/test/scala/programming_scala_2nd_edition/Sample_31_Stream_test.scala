package programming_scala_2nd_edition

import org.scalatest.FlatSpec

import Sample_31_Stream._
class Sample_31_Stream_test extends FlatSpec{
    "Stream fib take" should "" in {
        fib_stream()
        stream_collect()
    }

    "View fib" should "" in {
        fib_view()
    }

    "parallelized stream" should "" in {
        stream_parallelized()
    }
}

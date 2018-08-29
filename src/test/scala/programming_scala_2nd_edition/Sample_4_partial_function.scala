package programming_scala_2nd_edition.sample_4

import org.scalatest.FunSuite
import programming_scala_2nd_edition.sample_4.Sample_4_partial_function._

class Sample_4_partial_function_test extends FunSuite {
    test("Test partial function") {
        printf("%-5s | %-14s | %-14s | %-14s\n",
            " ", "pf1 - String","pf2 - Double","pf - All")
        printf("%-5s | %-5s | %-6s | %-5s | %-6s | %-5s | %-6s\n",
            "x", "def?","pf1(x)","def?","pf2(x)","def?","pf(x)")
        println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
        List("str", 3.14, 10) foreach { x =>
            printf("%-5s | %-5s | %-6s | %-5s | %-6s | %-5s | %-6s\n", x.toString,
                check_defined(x,pf1),
                try_pf(x,pf1),
                check_defined(x,pf2),
                try_pf(x,pf2),
                check_defined(x,pf),
                try_pf(x,pf))
        }
    }
}

package programming_scala_2nd_edition

import org.scalatest.FunSuite
import programming_scala_2nd_edition.Sample_17_require_validator._

class Sample_20_require_validator extends FunSuite{
    test("require method to validate cons parameters") {
        ZipCode(12345)                     // 结果: ZipCode = 12345
        ZipCode(12345, Some(6789))         // 结果: ZipCode = 12345-6789
        ZipCode(12345, 6789)               // 结果: ZipCode = 12345-6789

        try {
            ZipCode(0, 6789)               // Invalid Zip+4 specified: 0-6789
        } catch {
            case e: java.lang.IllegalArgumentException => e
        }

        try {
            ZipCode(12345, 0)              // Invalid Zip+4 specified: 12345-0
        } catch {
            case e: java.lang.IllegalArgumentException => e
        }
    }

}

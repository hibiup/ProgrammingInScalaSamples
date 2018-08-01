import org.scalatest.FunSuite

class TestCases extends FunSuite {
    test("") {
      assert(1 -> "a" == (1, "a"))
      val numberSet = Set(1, 3, 2)
      val max = numberSet.reduceLeft((a,b) => a.max(b))
      assert(3 == max)
    }
  }

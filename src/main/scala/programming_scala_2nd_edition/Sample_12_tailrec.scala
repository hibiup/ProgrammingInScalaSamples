package programming_scala_2nd_edition.sample_12

import scala.annotation.tailrec

object sum_of_list_with_match {
    /**
      * tail recursive 的窍门是：
      * 1) 保证不到最后条件到达时，一直会调用到函数自己。所以函数体内一定有一条唯一结尾条件的判断句。
      * 2) 函数一旦返回，其值就是最终结果。所以每次计算的累计值要作为参数传给函数自己，决不返回中间结果。
      */
    @tailrec
    def apply(list: List[Int], sum:Int):Int = {
        list match {
            case Nil => sum       // 1) 唯一结尾条件
            case head +: tail => sum_of_list_with_match(
                tail,
                sum + head        // 2)累加计算结果，并传给自己
            )
        }
    }
}

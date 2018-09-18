package programming_scala_2nd_edition

package Sample_15_partially_applied {
    object apply_tuple_for_function {
        /** 定义一个普通函数 */
        def mult(d1: Int, d2: Int, d3: Int) = d1 + d2 + d3

        /** 定义一个 tuple 类型的参数 */
        val params = (1, 2, 3)

        def apply(): Unit = {
            /** 一般的调用不得不逐个取得 tuple 的内容传入 */
            mult(params._1, params._2, params._3)

            /** 用 Function.tupled 可以让函数接受 tuple */
            val multTupled = Function.tupled(mult _)
            println(multTupled(params))
        }
    }

    object uplift_partially_applied_function {
        /** 通过 uplift 一个部分应用函数，让它返回 Option 以覆盖未覆盖的情况 */

        /** 生成一个 partially applied 函数 */
        val finicky: PartialFunction[String,String] = {
            case "finicky" => "FINICKY"   // 只覆盖 "finicky" 这一总值
        }

        def apply(): Unit = {
            val finickyOption = finicky.lift   // "提升"　覆盖率
            println(finickyOption("finicky"))  // 返回 Option[String] = Some(FINICKY)
            println(finickyOption("other"))    // None
        }
    }
}

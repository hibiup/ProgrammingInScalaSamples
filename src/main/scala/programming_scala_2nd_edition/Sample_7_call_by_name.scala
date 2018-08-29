package programming_scala_2nd_edition.sample_7

object Sample_7_call_by_name extends App{
    /**
      * call by result: 在调用 call_by_result 之前，先计算出参数的结果，将结果传递给函数体。也就是说参数值在执行函数体之前就确定了。
      */
    def call_by_result(x: () => Int): Int = x() + x()

    /**
      * call by name: 在调用 call_by_result 时不对参数求值，将参数转换成一个 lazy 的"表达式" 传入函数体，当函数体使用该
      * 参数的时候才进行求值。每次使用都会执行一次求值运算，相当于一个 inner call
      */
    def call_by_name(x: => Int): Int = x + x

    /**
      * Call by value(reference): 和 call by result 一样，先求取参数值。
      */
    def call_by_value(x: Int): Int = x + x

    //********* 测试 ***********
    println(
        call_by_name(
            { println("call_by_name(): "); 2 }   // lazy 求值，整个参数被转换成 "表达式" 传入函数体，"表达式"被使用时才求值
        )
    )
    println(
        call_by_result(
            { println("call_by_result(): "); () => 2 }   // eager 求值，得到 "() => 2" 传入函数体
        )
    )
    println(
        call_by_value(
            { println("call_by_value(): "); 2 }          // eager 求值，得到 2 传入函数体
        )
    )
}

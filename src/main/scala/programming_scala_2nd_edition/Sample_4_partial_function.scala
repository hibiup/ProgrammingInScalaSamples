package programming_scala_2nd_edition.sample_4

object Sample_4_partial_function {
    // 偏函数 pf1 只覆盖了 String 输入，但是定义是 Any
    val pf1: PartialFunction[Any, String] = { case s:String => "YES" }
    val pf2: PartialFunction[Any,String] = { case d:Double => "YES" }

    // PartialFunction.orElse[A1 <: A, B1 >: B]　可以合并两个偏函数:
    val pf = pf1 orElse pf2   // 如果pf1 不匹配，就会尝试pf2，接着是pf3...，以此类推。

    // 1. 尝试将 x 应用于偏函数，并捕获可能的异常
    def try_pf(x: Any, f: PartialFunction[Any,String]): String =
        try { f(x).toString } catch { case _: MatchError => "ERROR!" }

    // 2. 更好的办法是使用 isDefinedAt　方法来探测 x 是否适用于偏函数(输出"true" or "false")
    def check_defined(x: Any, f: PartialFunction[Any, String]) =
        f.isDefinedAt(x)
}

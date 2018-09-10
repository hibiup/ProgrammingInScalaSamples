package programming_scala_2nd_edition.sample_8


object TryCatch {
    import scala.io.Source
    import scala.util.control.NonFatal  // NonFatal 非致命的错误的基类

    /**
      * Run with parameters "foo README.md"
      */

    def main(args: Array[String]) = {
        args foreach countLines
    }

    def countLines(fileName: String) = {
        println()
        var source: Option[Source] = None
        try {
            source = Some(Source.fromFile(fileName))  // Some() 保证不会取得 None 值
            val size = source.get.getLines.size       // get 方法可以从 Some 类型中获得原始值
            println(s"file $fileName has $size lines")
        } catch {
            case NonFatal(ex) => println(s"Non fatal exception! $ex")  // 只捕获业务逻辑兼容的异常( 包括 FileNotFoundException )
        } finally {
            for (s <- source) {  // “<-” 也可以从 Some 类型中获得原始值并且可以和 for 推导式结合(for 推导式要求第一个参数必须是生成式)，因此这里不用 get
                println(s"Closing $fileName...")
                s.close
            }
        }
    }
}

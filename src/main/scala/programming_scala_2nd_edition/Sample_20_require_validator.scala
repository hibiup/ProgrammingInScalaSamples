package programming_scala_2nd_edition

package Sample_17_require_validator {
    /**
      * 如果我们想验证输入的参数，以确保产生的实例处于有效状态，该怎么做呢？ Predef
      * （http://www.scala-lang.org/api/current/index.html#scala.Predef$）定义了一系列名为 require 的重载方法，
      * 可以实现这一目的。
      *
      * require 属于断言函数，其他断言函数还包括 assert, assume. 它们都定义在 Predef 包中。
      */
    // object 构造方法没有参数，因此我们要定义这个 case class。
    case class ZipCode(zip: Int, extension: Option[Int] = None) {
        require(valid(zip, extension), s"Invalid Zip+4 specified: $toString")

        protected def valid(z: Int, e: Option[Int]): Boolean = {
            if (0 < z && z <= 99999) e match {
                case None => validUSPS(z, 0)
                case Some(e) => 0 < e && e <= 9999 && validUSPS(z, e)
            }
            else false
        }

        // 这是个有效的美国邮政编码吗？
        protected def validUSPS(i: Int, e: Int): Boolean = true

        override def toString = if (extension != None) s"$zip-${extension.get}" else zip.toString
    }

    object ZipCode {
        def apply (zip: Int, extension: Int): ZipCode =
            new ZipCode(zip, Some(extension))
    }
}

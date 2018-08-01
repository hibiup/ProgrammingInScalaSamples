/**
  * 测试 scala 脚本:
  *
  * 命令行执行：
  *     scala src/main/scala/programming_scala_2nd_edition/Sample_1_Hello_Script.scala
  *
  *  命令行编译：
  *     scalac -Xscript Upper1 src/main/scala/programming_scala_2nd_edition/Sample_1_Hello_Script.scala
  *     scala Upper1
  *
  *  反编译：
  *     javap -cp . Upper1
  *     scalap -classpath Upper1
  */

object Upper {
    /**
      * object 关键词定义的是 "单例模式"　不是 "静态类"．虽然它包含的方法为"静态方法"，但是object本身不是 "静态类（static）"，
      * scala　只是在运行时创建了一个与类（Upper）同名的实例．你也无法通过 new Upper　来创建新的实例　- (p13 ~ P14)
      */
    def upper(strings: String*): Seq[String] = strings.map((s:String) => s.toUpperCase())
}

//println(Upper.upper("Hello", "World!"))
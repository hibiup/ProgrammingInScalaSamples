package programming_scala_2nd_edition

/**
  * Macro 是实验特性，缺省关闭，需要显示打开：
  *
  * Idea:
  *     Setting -> Build, Execution, Deployment -> Compilers -> Scala Compoler -> Macros
  *
  * 命令行：build.sbt 加:
  *     scalacOptions ++= Seq("-feature")
  *
  * 或 scalac 编译的时候加上：
  *     -language:experimental.macros
  * */

package Sample_30_micro {
    /** 1) 引入 experimental.macros 和 blackbox*/
    import scala.language.experimental.macros
    import scala.reflect.macros.blackbox

    object ShowCode {
        /* 下面这个例子会打印出 apply() 的参数， 因为测试案例传入的 apply() 的参数是一段代码，所以它会打印出每一行代码。 */

        /** 2) 定义一个函数，名称随意。接受两个参数：
          *     第一个是 blackbox.Context。指的是 macro 被应用的宿主，也就是 apply() 函数的上下文。（注：有 blackbox 就有 whitebox ）
          *     第二个参数是第一个参数的 Tree，Tree 定义在 scala.reflect.macros 中，它保存着 c 的参数，在这里就是 apply 的参数。*/
        def impl(c: blackbox.Context)(x: c.Tree) = {
            import c.universe._

            /** 3) q 叫做 “quasiquotes”。它的作用是将字符串解释为命令。它利用的原理是 unapply，也就是将 x 解构后赋予字符串中的 stats 变量。
              *    Scala 中的 unapply 语法在很多地方都用到，比如 RegExp 的 match。而在这里相当于反编译了 x */
            val q"..$stats" = x

            /** 4) 逐行得到反编译的内容赋予 msg，然后生成 println 命令字面量，保存到 loggedStats　中。。*/
            val loggedStats = stats.flatMap {stat =>
                val msg = "executing " + showCode(stat)
                List(q"println(${msg})",stat)
            }

            /** 5) 执行 loggedStats 中的命令字面量（逐行打印出指令。） */
            q"..$loggedStats"

            /* 引申：如果需要可以修改或添加新的指令，让执行结果不同于传入的参数。一个应用场景是实现 Spring 的依赖注入功能。 */
        }

        /** 6) 将函数作为 macro 应用到别处。 */
        def apply[T](x: => T):T = macro impl
    }

    /**
     * 下面这个例子的 _println　会在编译时执行而不是在运行时。
     *
     * 编译器它先运行了这个宏内的代码，再把 q 中的结果填到原来使用的该方法的地方去。
     * */
    object TestImpl {
        def _println[T:c.WeakTypeTag](c:blackbox.Context)(cond:c.Tree) = {
            import c.universe._
            //这样写编译时就会打印，而不是运行时。
            q"""${println(cond)}"""    // 如果返回这一行，因为 println(cond) 返回 Unit，因此 myPrint 啥也得不到。

            q"""println($cond)"""      // 返回 println(cond) 指令。
        }
    }

    class PrintA[T] {
        def myPrint(cond:T):Unit = macro TestImpl._println[T]
    }

    /** 另一种执行原码的方法是用 toolbox 将源代码实时编译并执行
      * 需要　"org.scala-lang" % "scala-compiler" % scalaVersion.value 支持 */
    object Compile_Source_Code {
        import scala.reflect.runtime.currentMirror
        import scala.tools.reflect.ToolBox
        val toolbox = currentMirror.mkToolBox()

        val source = """for (1 <- List(1, 1, 2)) println("ha")"""
        toolbox.eval(toolbox.parse(source))
    }
}

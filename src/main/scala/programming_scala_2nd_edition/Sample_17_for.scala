package programming_scala_2nd_edition

/**
  * for-comprehension 的完整语法（ＥＢＮＦ）的定义如下：
  *
  *   Expression     ::= for (<Enumerators>) | {<Enumerators>}  yield Expr1 | Predef
  *     Enumerators    ::=  <Generator> {; <Generator>}
  *       Generator      ::=  Pattern <- Expr {<Guard> | ; <Definition>}
  *         Guard          ::=  if PostfixExpr
  *         Definition     ::=  Pattern = Expr2
  *
  * 从上面的语法结构可以看到，<Enumerator> 是条件的最上层结构，它包括多个 <Generator>,
  *   每个 Generator 又分为必选的 <Pattern <- Expr> 结构和可选的 <Pattern <- Expr> 和 <Definition>,
  *     其中 Guard 是一个 if 子句。
  *     Definition 是一个赋值表达式
  */

package Sample_17_for {
    import scala.util.Either.{LeftProjection, RightProjection}
    import scala.util.Properties

    /**
      * 每个 for-comprehension 至少包含一个 Enumerator，而每个 Enumerator 至少包含一个 Generator. Generator 分为以下几种模式：
      *
      * 例1）"value <- Expr": 称为确定模式（irrefutable pattern）其中 value 是一个确定值。它的含义是如果 Expr 中存在确定
      *    (irrefutable)的值，那么就执行后面的表达式（yield Expr1 | Predef），否则不执行。编译器将这个 Pattern 展开为 withFilter..foreach
      *    形式：
      *
      *      Expr.withFilter( x => x match {
      *          case value => true
      *          case _ => false
      *      }).foreach { n => n match {
      *          case value => Predef
      *      }}
      **/
    object Generator_IrrefutablePattern {
        import scala.reflect.runtime.universe._
        def apply(): Unit = {
            // 反编译：
            val expr = reify(for (1 <- List(1, 1, 2)) println("ha"))
            println(expr)

            // 得到：
            List(1, 1, 2)
                    .withFilter{
                        case 1 => true     // Irrefutable Pattern
                        case _ => false
                    }.foreach{
                        case 1 => Predef.println("ha")
                    }
        }
    }
    /**
      * 例2) “case <- Expr”： 它的含义是将 Expr 中的值 match 到 case 然后同样执行 Irrefutable Pattern
      */
    object Generator_MatchPattern {
        import scala.reflect.runtime.universe._
        def apply(): Unit =
        {
            // 反编译：
            val expr = reify( for ((a,b) <- List((1,2), (3,4))) println(a,b) )
            println(expr)

            // 得到
            List((1, 2), (3, 4))
                    .withFilter {                // match
                        case (a@_, b@_) => true  // Irrefutable Pattern
                        case _ => false
                    }.foreach {
                        case (a@_, b@_) => Predef.println(a,b)
                    }

            // 一个更复杂的 match 表达式
            val expr1 = reify(
                for { li @ (head :: _) <- List(1, 2, 3) :: List.empty :: List(5, 3) :: Nil }
                    println(li.size)
            )
            println(expr1)
        }
    }

    /**
      * 例3）通过以上我们会看到 for-comprehension 会将 -> 操作符转化成 withFilter。同样适用于 if 选项：
      * */
    object Generator_If {
        import scala.reflect.runtime.universe._
        def apply(): Unit = {
            // 反编译：
            val expr = reify( for ((a,b) <- List((1,2), (3,4)) if a==1) println(a,b) )
            println(expr)

            // 得到：
            List((1, 2), (3, 4))
                    .withFilter {
                        case (a @ _, b @ _) => true
                        case _ => false
                    }.withFilter {             // 相比于 例2）if 条件子句也被转换成了 withFilter
                        case (a @ _, b @ _) => a == 1
                    }.foreach {
                        case (a @ _, b @ _) => Predef.println((a, b))  // 结果执行有副作用的操作。
                    }

            // 对于 yield ，最大的区别在于结果集被执行了 map 操作。
            val expr1 = reify( for ((a,b) <- List((1,2), (3,4)) if a==1) yield(a,b) )
            println(expr1)

            // 反编译后得到：
            val list = List((1, 2), (3, 4))
                    .withFilter {
                        case (a @ _, b @ _) => true
                        case _ => false
                    }.withFilter {
                        case (a @ _, b @ _) => a==1
                    }.map {                         // 函子操作
                        case (a @ _, b @ _) => (a, b)
                    }(List.canBuildFrom)
            println(list)
        }
    }

    /**
      * 例4）对于包含 Definition 子句的 Generator 会生成双重循环，可能导致效率问题。 (p197)
      **/
    object ForWithDefinition {
        import scala.reflect.runtime.universe._

        def apply(): Iterable[Int] = {
            val list = List((1,2), (3,4))

            // 带 Definition 的 for 循环
            val m1 = for {
                (a, b) <- list; v = b + 10    // Definition
            } yield (v)                       // List(12, 14)  抛弃了 (key, value)

            // 反编译：
            val expr = reify(for { (a, b)<-list; v = b+10 } yield (v))
            println(expr)
            // 得到：
            val m2 = list
                    .withFilter {
                        case (a @ _, b @ _) => true   // Irrefutable Pattern
                        case _ => false
                    }.map{
                        /* Definition 会作用于 generator，生成第一次 map 循环。得到 (element, value) 合集
                         * 所不同的是 definition 被转换成了 map, 而不是 withFilter. 区别是 withFilter 是 lazy 的。
                         * 因此每当执行 Definition 的时候，会重新执行一边 withFilter。 */
                        case x @ (a @ _, b @ _) => {
                            val v = b + 10
                            (x, v)
                        }
                    }.map{
                        /* 然后在上一次 map 的结果集上二次 map 循环，得到结果集（抛弃了 key, value）。
                         * 反过来考虑，map 实际上等于另一个 for-comprehension, 因此 Definition 等效于两个 for 循环嵌套。 */
                        case ((a @ _, b @ _), x @ _) => x
                    }(Iterable.canBuildFrom)
            assert(m1 == m2)
            m2
        }
    }

    /**
      * 例5）通过前面的例子，我们看到 yield 会对应到 map 操作。对于多个 Generator 的 for-comprehension 表达式，如果后面的表达式是
      * yield，就会转化成 flatMap => flatMap .. => map 的形式(最后的一个映射无需重新装箱，因此保持 map)
      * */
    object Generator_Multiple_Pattern {
        import scala.reflect.runtime.universe._

        def apply(): Unit = {
            val list1 = for (l1 <- List(1,2); l2 <- List(3,4); l3 <- List(5,6)) yield (l1, l2, l3)
            println(list1)

            // 等价于：
            val list2 = List(1,2).flatMap( l1 =>
                List(3,4).flatMap( l2 =>    // 生成嵌套映射
                    List(5,6).map( l3 =>
                        (l1,l2,l3) ) ) )
            println(list2)

            // 动态反编译：
            println( q"""for (l1 <- List(1,2); l2 <- List(3,4); l3 <- List(5,6)) yield (l1, l2, l3)""" )
        }
    }

    /** 例6）有前后关联的多 Generator 和无关联的一样生成嵌套映射 */
    object MultipleCondition {
        import scala.reflect.runtime.universe._

        def apply() = {
            val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

            // 反编译：
            val expr = reify (
                for {
                    s <- states if s.startsWith("A")
                    c <- s                  // 逐个取出字符
                } yield s"$c-${c.toUpper} " // 结果值: List("A-A", "l-L", "a-A", "b-B", ...)
            )
            println(expr)
            // 得到：
            val r = states
                    .withFilter(s => s.startsWith("A"))       // if 生成的 withFilter
                    .flatMap{s =>
                        Predef.augmentString(s)               // 逐个取出字符
                            .map(c => s"$c-${c.toUpper} ")    // 同样嵌套生成映射
                    }
            println(r)
        }
    }

    /** for-comprehension 在应用上的一些例子。*/

    /**  正则表达式匹配检取 */
    object RegExpression {
        import scala.reflect.runtime.universe._

        def apply() = {
            val reg = """([0-9]+)""".r  // 必须(用括号)定义 group。不算 group0(group0 就是输入本身)。
            val r1 = for {
                x <- Seq("1","123")
                reg(n) = x              // 表达式出现在左边！
            } yield (n)

            // 等价于:
            val _ = for {
                x <- Seq("1","123")
                n = x match {           // 展开后等于 pattern matching
                    case reg(a) => a    // 变长参数从 group1 开始，所以匹配表达式必须定义 group。
                }
            } yield (n)

            // 反编译
            println(reify(for {
                x <- Seq("1","123")
                reg(n) = x
            } yield (n)))
            // 得到：
            val r2 = Seq("1", "123")
                    .map(x => {
                        val a = x match {
                            case b @ reg(n @ _) => (b, n)
                        }
                        val b = a._1
                        val n = a._2
                        (x, b)
                    }).map{
                case (x @ _, reg(n @ _)) => n
            }
            assert(r1 == r2)

            // reg(n) = x 是匹配语法，通过 unapply 生效，单独使用的语法如下：
            val reg(n) = "123"
            println(n)

            r2
        }
    }

    /** 从文件中移除空行 */
    object RemoveBlanks {
        def apply(path: String): Seq[String] = // 返回调用 scala.collection.Iterator 的toSeq 方法时， 会调用子类型 scala.collection.TraversableOnce 中的默认实现并返回 Stream 类型对象。
            for{line <- scala.io.Source.fromFile(path).getLines.toSeq      // generator
                if line.matches("""^\s*$""") == false               // filter: 如果存在空白字符(\s*)，^\s*$　则表示空行，if 的意思是不选取空行
                replaced = line.replaceAll("\\s+", " ")  // 对非空行做过滤，删除多余的空白字符(多个空白字符只保留一个)
            } yield replaced
    }

    /** 读取属性文件 */
    object ReadProperties {
        def apply() = {
            val ignoreRegex = """^\s*(#.*|\s*)$""".r                    // "#" 注释
            val kvRegex = """^\s*([^=]+)\s*=\s*([^#]+)\s*.*$""".r       // 属性
            val properties = """
                               |# Book properties
                               |
                               |book.name = Programming Scala, Second Edition # A comment
                               |book.authors = Dean Wampler and Alex Payne
                               |book.publisher = O'Reilly
                               |book.publication-year = 2014
                               |""".stripMargin                         //　删除空格，空行和制表符“|”

            val kvPairs = for {
                prop <- properties.split(Properties.lineSeparator)
                if ignoreRegex.findFirstIn(prop) == None     //  过滤掉注释行
                kvRegex(key, value) = prop   // 本行左侧代码运用了模式表达式；通过正则表达式从有效的属性行中抽取出键及值对应的字符串。
            } yield (key.trim, value.trim)   // 返回 kv

            kvPairs
        }
    }
}

package programming_scala_2nd_edition

package Sample_17_for {

    import scala.util.Either.{LeftProjection, RightProjection}
    import scala.util.Properties

    object RemoveBlanks {
        /** 从文件中移除空行 */
        def apply(path: String): Seq[String] = // 返回调用 scala.collection.Iterator 的toSeq 方法时， 会调用子类型 scala.collection.TraversableOnce 中的默认实现并返回 Stream 类型对象。
            for{line <- scala.io.Source.fromFile(path).getLines.toSeq      // generator
                if line.matches("""^\s*$""") == false               // filter: 如果存在空白字符(\s*)，^\s*$　则表示空行，if 的意思是不选取空行
                replaced = line.replaceAll("\\s+", " ")  // 对非空行做过滤，删除多余的空白字符(多个空白字符只保留一个)
            } yield replaced
    }

    object MultipleCondition {
        def apply(): Seq[String] = {
            val states = List("Alabama", "Alaska", "Virginia", "Wyoming")
            for {
                s <- states if s.startsWith("A")
                c <- s
            } yield s"$c-${c.toUpper} " // 结果值: List("A-A", "l-L", "a-A", "b-B", ...)
        }
    }

    object ForWithDefinition {
        def apply(): Iterable[Int] = {
            /**
              * Definition 会生成双重循环，可能导致效率问题。 (p197)
              **/
            val map = Map("one" -> 1, "two" -> 2)


            /** 带 Definition 的 for 循环 */
            val list1 = for {
                (key, value) <- map; i10 = value + 10    // Definition
            } yield (i10)                                // List(11, 12)  抛弃 (key, value)


            /** 等价于以下： */
            val list2 = for {
                (_, i10) <- for {             // 1) Definition 会作用于 generator，生成嵌套循环。
                    x1 @ (key, value) <- map  // 2) generator 会作用于内循环（除了获得 map 的返回值个部分外，还会生成一个 alias）
                } yield {
                    val x2 = value + 10       // 3) Definition 会作为循环的 yield
                    (x1, x2)                  // 4) 然后返回所有值（alias_value + definition_value）。
                }
            } yield (i10)                     // 5) 外循环决定最终返回哪些值。(抛弃 (key, value))

            assert(list1 == list2)
            list2
        }
    }

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

    object RegExpression {
        def apply() = {
            val reg = """([0-9]+)""".r  // 必须(用括号)定义 group。不算 group0(group0 就是输入本身)。
            val numberOnly = for {
                x <- Seq("1","123")
                reg(n) = x              // 表达式出现在左边！
            } yield (n)

            // reg(n) = x 是匹配语法，通过 unapply 生效，单独使用的语法如下：
            val reg(n) = "123"
            println(n)

            // 等价于:
            val _ = for {
                x <- Seq("1","123")
                n = x match {           // 展开后等于 pattern matching
                    case reg(a) => a    // 变长参数从 group1 开始，所以匹配表达式必须定义 group。
                }
            } yield (n)

            numberOnly
        }
    }
}

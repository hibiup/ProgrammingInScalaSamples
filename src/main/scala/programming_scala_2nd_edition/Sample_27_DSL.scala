package programming_scala_2nd_edition

package Sample_27_Dynamic {
    object DynamicMethod {
        /** p400
          *
          * 动态为类加上方法实现 DSL 风格。
          *
          * 以下这个例子将用一个 map 对象模拟一个包含 states 信息的数据库。包含以下三个字段：
          *   name
          *   capital
          *   statehood
          *
          * CLINQ 类实例 states 将可以动态将字段映射为方法名称。
          *
          * */
        import scala.language.dynamics

        /** 1）Scala 编译器在遇到 Dynamic trait 的时候，会通过 selectDynamic 和 applyDynamic 方法为 class 动态添加方法。*/
        case class CLINQ[T](records: Seq[Map[String,T]]) extends Dynamic {
            /**
              *  2）s实现 selectDynamics 方法，参数名将会被作为方法名来使用。在这里，我们期待传入的参数会是字段名 */
            def selectDynamic(name: String): CLINQ[T] =
                if (name == "all" || records.length == 0) this // 假如传入了all 这一“关键字”，或者内存中没有任何记录，将返回所有的字段信息。
                else {
                    val fields = name.split("_and_")    // 由于CLINQ 使用_and_ 连接词将两个或多个字段连接起来，因此我们需要将方法名分解成一组字段名称。
                    val seed = Seq.empty[Map[String,T]]
                    val newRecords = (records foldLeft seed) {
                        (results, record) =>
                            val projection = record filter {   // 对 map 对象进行过滤，只返回名字中包含的字段。
                                case (key, value) => fields contains key
                            }
                            // 如果没有找到记录
                            if (projection.size > 0) results :+ projection
                            else results
                    }
                    CLINQ(newRecords)                          // ➏构造并返回新的CLINQ 对象。
                }

            /**
              * 3) applyDynamic 可以为 由 selectDynamic 动态加上的方法添加后续方法。
              *
              *    在此，执行 applyDynamic 方法后将得到一个新的 Where 类实例，Where 类也继自Dynamic 特征。*/
            def applyDynamic(name: String)(field: String): Where = name match {
                case "where" => new Where(field)
                case _ => throw CLINQ.BadOperation(field, """Expected "where".""")
            }

            /** 3) Where 类会根据field 字段值，对记录集进行过滤。 */
            protected class Where(field: String) extends Dynamic {
                def filter(value: T)(op: (T,T) => Boolean): CLINQ[T] = {
                    val newRecords = records filter {
                        _ exists {
                            case (k, v) => field == k && op(value, v)
                        }
                    }
                    CLINQ(newRecords)
                }

                def applyDynamic(op: String)(value: T): CLINQ[T] = op match {
                    case "EQ" => filter(value)(_ == _) // 假如向applyDynamic 方法传入EQ 操作符，该方法将调用filter 方法对记录集进行过
                                                       // 滤，只有那些指定字段的数值与用户指定值相等的记录才会被返回。
                    case "NE" => filter(value)(_ != _) // applyDynamic 方法对不等于操作提供了支持。
                    case _ => throw CLINQ.BadOperation(field, """Expected "EQ" or "NE".""")
                }
            }
            override def toString: String = records mkString "\n" //  根据记录信息，创建易读的字符串。
        }

        object CLINQ {
            case class BadOperation(name: String, msg: String) extends RuntimeException(
                s"Unrecognized operation $name. $msg")  // 在伴生对象中定义了BadOperation 异常。
        }

        def apply() = {

            // 准备数据库
            def makeMap(name: String, capital: String, statehood: Int) =
                Map("name" -> name, "capital" -> capital, "statehood" -> statehood)

            val states = CLINQ(
                List(
                        makeMap("Alaska", "Juneau", 1959),
                        makeMap("California", "Sacramento", 1850),
                        makeMap("Illinois", "Springfield", 1818),
                        makeMap("Virginia", "Richmond", 1788),
                        makeMap("Washington", "Olympia", 1889)))

            println(states.name)
            println(states.capital)
            println(states.statehood)

            println(states.name_and_capital)
            println(states.name_and_statehood)
            println(states.capital_and_statehood)

            println(states.all)

            println(states.all.where("name").NE("Alaska"))
            println(states.all.where("statehood").EQ(1889))
            println(states.name_and_statehood.where("statehood").NE(1850))
        }
    }

    object AnnualSalaryDSL {
        import scala.util.parsing.combinator._

        /** 内部 DSL：p410
          *
          * 内部 DSL 指的是 DSL 指令直接由 Scala 生成，比如以下 object 中定义了 federal_tax, state_tax 等方法都可以被当作 DSL 指令来使用。
          *
          * 例如以下一个扣除工资税收的例子：
          **/
        // 封装应该扣除的工资数额
        sealed trait Amount { def amount: Double }

        // 按百分比扣除
        case class Percentage(amount: Double) extends Amount {
            override def toString = s"$amount%"
        }

        // 扣除固定额度
        case class Dollars(amount: Double) extends Amount {
            override def toString = s"$$$amount"
        }

        // implicit 类，用于完成 Double 到对应正确 Amount 子类的转换。
        implicit class Units(amount: Double) {
            def percent = Percentage(amount)
            def dollars = Dollars(amount)
        }

        // 扣除的项目，具有名称name 和数额amount 字段。
        case class Deduction(name: String, amount: Amount) {
            override def toString = s"$name: $amount"
        }

        // 所有的扣除项目
        case class Deductions( name: String,
                               divisorFromAnnualPay: Double = 1.0,
                               deductions: Vector[Deduction] = Vector.empty) {
            def gross(annualSalary: Double): Double =  annualSalary / divisorFromAnnualPay  // 总工资和净工资
            def net(annualSalary: Double): Double = {
                val g = gross(annualSalary)
                (deductions foldLeft g) {
                    case (total, Deduction(deduction, amount)) => amount match {
                        case Percentage(value) => total - (g * value / 100.0)
                        case Dollars(value) => total - value
                    }
                }
            }
            //override def toString = s"$name Deductions:" + deductions.mkString("\n ", "\n ", "")
        }

        /** 封装内部 DSL 指令 */
        object internal_dsl {
            def biweekly(f: DeductionsBuilder => Deductions):Deductions = f(new DeductionsBuilder("Biweekly", 26.0))

            // Deductions builder 它扩展自 Deductions，所以各项税收的修改都是就地（inner）修改，因为 deducts 是父类中的 var deductions，因此可以就地修改。
            class DeductionsBuilder( name: String,
                                     divisor: Double = 1.0,
                                     var deducts: Vector[Deduction] = Vector.empty) extends Deductions( name, divisor, deducts) {
                /** DSL 指令 */
                // 计算各项税率
                def federal_tax(amount: Amount): Deductions = {          // 联邦税
                    deducts = deducts :+ Deduction("federal taxes", amount)
                    this
                }
                def state_tax(amount: Amount): Deductions = {            // 省税
                    deducts = deducts :+ Deduction("state taxes", amount)
                    this
                }
                def insurance_premiums(amount: Amount): Deductions = {   // 保险
                    deducts = deducts :+ Deduction("insurance premiums", amount)
                    this
                }
                def retirement_savings(amount: Amount): Deductions = {   // 退休金
                    deducts = deducts :+ Deduction("retirement savings", amount)
                    this                                                 // 返回自身 DeductionsBuilder 其实也就是返回 Deductions
                }

                override def toString = s"$name Deductions:" + deducts.mkString("\n ", "\n ", "")
            }
        }

        def internal_dsl_sample() = {
            import scala.language.postfixOps  // 这个类允许将方法名后置
            import internal_dsl._

            val biweeklyDeductions = biweekly {  // biweeklyDeductions: Deductions
                deductions =>   // deductions = new DeductionsBuilder("Biweekly", 26.0)
                    /** 使用 DSL 指令 */
                    deductions federal_tax (25.0 percent)    // 逐项扣除
                    deductions state_tax (5.0 percent)
                    deductions insurance_premiums (500.0 dollars)
                    deductions retirement_savings (10.0 percent)
                    // 返回 this(DeductionsBuilder) 其实也就是返回 Deductions，满足 f: DeductionsBuilder => Deductions
            }
            println(biweeklyDeductions)  // 调用 DeductionsBuilder.toString()

            val annualGross = 100000.0
            val gross = biweeklyDeductions gross annualGross
            val net = biweeklyDeductions net annualGross
            print(f"Biweekly pay (annual: $$${annualGross}%.2f): ")
            println(f"Gross: $$${gross}%.2f, Net: $$${net}%.2f")
        }

        /** 外部 DSL:
          *
          * 外部 DSL 的意思是可以读入一个脚本，解释这个外部脚本中的 DSL 指令。
          *
          * 比如下面这个 Parser 同样实现了上面的功能，但是 DSL 来自外部脚本。
          * */
        // 外部脚本
        val input ="""
            biweekly {
                federal tax 20.0 percent,
                state tax 3.0 percent,
                insurance premiums 250.0 dollars,
                retirement savings 15.0 percent
            }"""

        /** DSL 解释器 */
        object external_dsl {
            class PayrollParser extends JavaTokenParsers {
                // @return Parser[(Deductions)]
                def biweekly =
                    /**
                      * 找到 3 个结束标记（terminal token），biweekly、{、}，以及对{…} 中内容计算扣除的结果。类似箭头的操作符
                      * （其实是方法名）~> 和<~ 表示将~ 一侧的标记丢掉。于是语法标记都去掉了，将剩下的内容传给 deductions 方法。
                      *
                      * ^^ 将左边（标记，也就是 deductions 函数返回值）和右边（语法规则）分开。语法规则带一个参数，是保留下来的标记
                      * （deductions 函数返回值）。如果存在多个标记，则需要使用一个形如{ case t1 ~ t2 ~ t2 => ... } 的偏函数字
                      * 面量。在这个例子中只有一个返回值 ds 是个 Vector，用于构造 Deductions 实例。
                      *
                      * 调用顺序是： biweekly -> deductions -> deduction -> federal_tax | state_tax | insurance | retirement
                      * */
                    "biweekly" ~> "{" ~> deductions <~ "}" ^^ {
                        ds => Deductions("Biweekly", 26.0, ds)
                }

                /** 调用 deduction 方法解析外部 DSL */
                // @return Parser[Vector[Deduction]]
                def deductions = repsep(deduction, ",") ^^ { ds =>
                    ds.foldLeft(Vector.empty[Deduction]) (_ :+ _)
                }

                /** 外部 DSL 解释 */
                // @return Parser[Deduction]
                def deduction = federal_tax | state_tax | insurance | retirement
                // @return Parser[Deduction]
                def federal_tax = parseDeduction("federal", "tax")
                def state_tax = parseDeduction("state", "tax")
                def insurance = parseDeduction("insurance", "premiums")
                def retirement = parseDeduction("retirement", "savings")

                /** 用于处理这4 个扣除项目的辅助方法。*/
                private def parseDeduction(word1: String, word2: String) =
                    /** 语法同 biweekly 使用的 parser 语法 */
                    word1 ~> word2 ~> amount ^^ {
                        amount => Deduction(s"${word1} ${word2}", amount)
                    }
                // @return Parser[Amount]
                def amount = dollars | percentage
                // @return Parser[Dollars]
                def dollars = doubleNumber <~ "dollars" ^^ { d => Dollars(d) }
                // @return Parser[Percentage]
                def percentage = doubleNumber <~ "percent" ^^ { d => Percentage(d) }
                def doubleNumber = floatingPointNumber ^^ (_.toDouble)
            }
        }

        def external_dsl_sample() = {
            // 引入解析器
            import external_dsl._
            val parser = new PayrollParser

            // 解释外部脚本
            val biweeklyDeductions = parser.parseAll(parser.biweekly, input).get

            println(biweeklyDeductions)
            val annualGross = 100000.0
            val gross = biweeklyDeductions.gross(annualGross)
            val net = biweeklyDeductions.net(annualGross)
            print(f"Biweekly pay (annual: $$${annualGross}%.2f): ")
        }

        def apply() ={
            println("Testing internal DSL: ")
            internal_dsl_sample()

            println("\nTesting external DSL: ")
            external_dsl_sample()
        }
    }
}

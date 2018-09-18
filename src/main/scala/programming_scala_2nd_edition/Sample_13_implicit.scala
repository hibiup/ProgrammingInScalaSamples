package programming_scala_2nd_edition

package sample_13 {
    object Implicate_Conversion{
        /**
          * 隐式转换可以将某个类型转换成另一个类型：比如以下的 to[A] 方法．
          * */
        trait AdditionOps[A] {
            def combine(a1: A, a2: A): A
        }

        object implicits {
            // def apply[A](implicit instance: AdditionOps[A]) = instance

            /** 1）生成隐式 Semigroup 变量 */
            implicit val intPlusInstance = new AdditionOps[Int] {
                def combine(a1: Int, a2: Int): Int = a1 + a2
            }

            /**
              * 2) 隐式将 A类型(在这里也就是 Int) 装换成 Syntax 类型，以便能够使用 "|+|" 方法。
              *    隐式参数 "I"　将第 1 步生成的 intPlusInstance 作为隐式参数传递给了 "new Syntax" 的构造函数
              */
            implicit def to[A](target: A)(implicit I: AdditionOps[A]): Syntax[A] =
                new Syntax[A] {  // 根据 Syntax 的定义，需要一个 AdditionOps 实例。由隐式参数提供了(显然也就是 intPlusInstance)，会被隐式绑定到实例中去。
                    val a1 = target
                }

            /** 2-1) 在生成 Synctax 的时候隐式绑定一个 AdditionOps 实例(在实例化的过程中由上下文提供) */
            abstract class Syntax[A](implicit I: AdditionOps[A]) {
                def a1: A
                def |+|(a2: A): A = I.combine(a1, a2)   // 调用 intPlusInstance 单例的 combine 执行运算。
            }
        }

        def apply() {
            import implicits._
            println(f"1 |+| 2 = ${1 |+| 2}")
        }
    }


    object Implicit_Class {
        /**
          * 隐式类用于为主构造函数的参数添加新的方法，比如下面这个例子中，jsonForStringContext　的主构造函数的参数是
          * StringContext，jsonForStringContext　为它添加上 json 方法．
          **/
        import scala.util.parsing.json._

        def apply() {
            /** 1. 将　StringContext　类型转换成 jsonForStringContext 类型 */
            implicit class jsonForStringContext(val sc: StringContext) {
                /**
                  * 2. 定义　json 方法。该方法的输入是字符串中嵌套的参数，该方法返回构造好的
                  * scala.util.parsing.json.JSONObject 对象　*/
                def json(values: Any*): JSONObject = {
                    val keyRE = """^[\s{,]*(\S+):\s*""".r
                    val keys = sc.parts map {
                        case keyRE(key) => key
                        case str => str
                    }
                    val kvs = keys zip values
                    JSONObject(kvs.toMap)
                }
            }

            val name = "Dean Wampler"
            val book = "Programming Scala, Second Edition"

            /**
              * 3. 使用我们自己的字符串插入器，使用起来与内置插入器无异。
              *
              * 当编译器看到像 x"foo bar" 这样的表达式时，它会查找scala.StringContext中定义的x 方法。
              * 因此以下代码会被编译其转换成：StringContext("name ", " ", "").x(name, book)
              * 的形式．s
              *
              * */
            val jsonobj = json"{name: $name, book: $book}"
            println(jsonobj)
        }
    }


    object Implicit_Evidence {
        import scala.reflect.runtime.universe._
        /**
          * 隐式类型可以用于"类型证明"，类型证明只是利用类型的定义来做约束，而不用于计算．
          *
          * 因为 Java 的（执行过程中的）类型擦除问题可能导致容器类型内的数据类型丢失,　比如 Seq[Int] 类型等同于 Seq[String] == Seq[AnyRef]
          * 可以通过　"类型证明" 来规避这个问题，用于类型证明的类并不需要定义特定的运算，它只需要一个定义　*/
        implicit object IntMarker
        implicit object StringMarker

        /** 然后通过给定一个隐式类型证明参数来区分同名函数，如果没有这个隐式参数，Java 将不能识别这两个函数的区别.　*/
        def m(seq: Seq[Int])(implicit iType: IntMarker.type): Unit = println(s"Seq[Int]: ${iType}")
        def m(seq: Seq[String])(implicit sType:StringMarker.type): Unit = println(s"Seq[String]: ${sType}")

        /** 类型证明只能约束调用，如果希望在函数体内获得并使用类型值就需要使用　"scala.reflect.runtime.universe"　中的 TypeTag 隐式方法来获得　*/
        def m1[T](seq: Seq[T])(implicit ev:TypeTag[T]): Unit = println(s"Seq[_]: ${ev}")

        def apply() {
            /**　应为类型擦除发生在运行时，因此以下代码在编译时可以被正确编译并对应到正确的函数上． */
            m(List(1, 2, 3))
            m(List("one", "two", "three"))

            m1(List(1, 2, 3))
            m1(List("one", "two", "three"))
        }
    }


    object Implicit_value {
        /**
          * 隐式参数：可以根据需要动态灵活地为函数选择参数．
          **/
        class SRow(jrow: String) {
            /** 1) get()　定义了类型签名，参数将根据类型签名隐式匹配不同的（函数）变量　*/
            def get[T](implicit toT: (String) => T): T = {
                toT(jrow)
            }
        }

        object implicits {
            /** 2) 将方法定义成变量以适应隐式参数匹配　*/
            implicit val jrowToInt: (String) => Int =
                (in: String) => in.toInt

            implicit val jrowToBoolean: (String) => Boolean =
                (in: String) => in.toBoolean
        }

        def apply() = {
            import implicits._  // 将隐式变量引入上下文

            val booleanRow = new SRow("True")
            /** 3) 为　get 方法传入类型签名进行隐式转换　*/
            val booleanBoolean = booleanRow.get[Boolean]
            println(s"boolean value -> $booleanBoolean")

            val intRow = new SRow("1")
            val intValue = intRow.get[Int]
            println(s"int values -> $intValue")
        }
    }


    object implicitly_sample {
        import math.Ordering

        /** 该类隐式定义了 Ordering，引入它就在上下文中隐式包含了一个 Ordering 实例。 */

        case class MyList[A](list: List[A]) {
            /** 将隐式定义的 Ordering 声明为 "ord" 。 */
            def SortBy1[B](f: A => B)(implicit ord: Ordering[B]): List[A] = {
                /**
                  * 在函数体内就可以直接以参数名: ord 来使用它s
                  **/
                list.sortBy(f)(ord) //
            }

            /**
              * 但是如果没有在参数上声明 ord。这时候如果确定并非没有这个实例，它只不过是匿名了。
              * 那么就可以利用 context bounds(上下文界定) 语法糖：[B:Ordering]
              * 来声明存在这么一个匿名实例，这个语法糖暗指函数体内的 implicitly 的参数是 Ordering[B]
              **/
            def SortBy2[B: Ordering](f: A => B): List[A] = {
                /**
                  * implicitly　可以利用context bounds 语法糖寻找上下文中的匿名实例。(p114)
                  **/
                list.sortBy(f)(implicitly[Ordering[B]])
            }
        }
    }

    object Implicitly_add_trait_for_class {
        /** implicit class 也可以用于为 trait　添加方法　*/

        /** 1) 目标类　*/
        case class Address(street: String, city: String)

        /** 2) typeclass，包含将要加载到目标类上去的方法． */
        trait ToJSON {
            def toJSON(level: Int = 0): String
            val INDENTATION = " "
            def indentation(level: Int = 0): (String,String) =
                (INDENTATION * level, INDENTATION * (level+1))
        }

        /** 3) 为 Address 隐式加上 trait ToJSON （并实例化） */
        implicit class AddressToJSON(address: Address) extends ToJSON {
            def toJSON(level: Int = 0): String = {
                val (outdent, indent) = indentation(level)
                s"""{
                   |${indent}"street": "${address.street}",
                   |${indent}"city": "${address.city}"
                   |$outdent}""".stripMargin
            }
        }

        def apply() {
            val a = Address("1 Scala Lane", "Anytown")
            println(a.toJSON())
        }
    }
}
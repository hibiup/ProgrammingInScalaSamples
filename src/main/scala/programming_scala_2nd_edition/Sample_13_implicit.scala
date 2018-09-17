package programming_scala_2nd_edition

package sample_13 {

    import programming_scala_2nd_edition.sample_13.ImplicitEvidence.IntMarker

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

    object DB {
        class SRow(jrow: String) {
            /** 1) get()　定义了类型签名，参数将根据类型签名隐式匹配不同的（函数）变量　*/
            def get[T](implicit toT: (String) => T): T = {
                toT(jrow)
            }
        }

        /** 2) 将方法定义成变量已适应隐式参数匹配　*/
        implicit val jrowToInt: (String) => Int =
            (in: String) => in.toInt

        implicit val jrowToBoolean: (String) => Boolean =
            (in: String) => in.toBoolean

        def apply() = {
            val booleanRow = new SRow("True")
            /** 为　get 方法传入类型签名进行隐式转换　*/
            val booleanBoolean = booleanRow.get[Boolean]
            println(s"boolean value -> $booleanBoolean")

            //val intRow = new SRow("1")
            //val intValue = intRow.get[Int]
            //println(s"int values -> $intValue")
        }
    }

    object ImplicitEvidence {
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
}
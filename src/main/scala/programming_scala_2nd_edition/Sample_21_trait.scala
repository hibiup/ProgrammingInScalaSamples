package programming_scala_2nd_edition

package Sample_21_trait {
    package OverrideMethod {
        /** trait　允许多重继承，继承顺序从右到左。
          *
          * 1) 下面的例子从 Clickable 中分别生成 ObservableClicks 和 VetoableClicks 两个子类。它们都重载了 click 方法，
          * 　　并调用 super 的 click
          *
          * 2) 在实例化　Clickable　的时候依次混入 ObservableClicks 和 VetoableClicks，VetoableClicks.click() 会最先被执行。
          *    执行结束后它的 super 指向 ObservableClicks，然后 ObservableClicks.click() 被执行，ObservableClicks 的 super
          *    指向 Clickable.
          *
          *    VetoableClicks.click() -> ObservableClicks.click() -> Clickable.click()
          *
          * */

        class Clickable(val label: String) {
            def click(): Unit =  { println(s"${label} is clicking!") }
        }

        // 记录 Click 事件。
        trait ObservableClicks extends Clickable {
            abstract override def click(): Unit = {
                super.click()
                println("Clicked!")
            }
        }

        // 控制可以 Click　的次数。
        trait VetoableClicks extends Clickable {
            // 默认的允许点击数。
            val maxAllowed = 1
            private var count = 0
            abstract override def click() = {
                if (count < maxAllowed) {
                    count += 1
                    println(s"Click count: ${count}/${maxAllowed}")
                    super.click()
                }
                else println("Nothing gonna happens.")
            }
        }

        object ClickButton {
            def apply(): Unit = {
                val button =
                    new Clickable("Click Me!") with ObservableClicks with VetoableClicks {
                        override val maxAllowed = 2
                    }

                (1 to 5) foreach (_ => button.click())
            }
        }
    }

    package ExecutionOrder {
        /**
          *  与方法的执行顺序相反，Base 类和 Trait 的初始化顺序是从左到右执行的。以下初始化执行顺序为：
          *
          *    Base -> Trait2 -> Trait1 -> Clazz
          *
          **/
        trait Trait1 {
            println("1st trait is initiating")
        }

        trait Trait2 {
            println("2nd trait is initiating")
        }

        class Base {
            println("Base class is initiating")
        }

        class Clazz extends Base with Trait2 with Trait1 {
            println("Class is instancing")
        }

        object GenerateClass {
            def apply() = {
                val c = new Clazz()
            }
        }
    }

    package LazyForParent {
        trait Parent1 {
            /**
              * 新建 Parent 子类匿名实例时会先初始化 Parent（参考例子：ExecutionOrder）。
              * 此时编译器会将 value 假设为无限大（infinity）。
              * */
            println("In Parent1")
            def value: Int
            val inverse = 1.0/value  // value 会是 infinity
        }

        trait Parent2 {
            /** lazy 惰性值可以有效避免父类属性在初始化时被先于子类被求值的问题。　*/
            println("In Parent2")
            def value: Int
            lazy val inverse = 1.0/value  // 延迟获得 inverse
        }

        object CreateAnonymous {
            def apply() = {
                val anony1 = new Parent1 {
                    //println("In obj:")
                    val value = 10
                }
                assert(anony1.value == 10)
                assert(anony1.inverse.isInfinite)

                // 注意这个语法, 得到匿名类的另一种方法.
                val anony2 = new {
                    //println("In obj:")  // 以这种方式获得的匿名类只允许出现类型定义或具体字段定义,不能执行其它指令.
                    val value = 10
                } with Parent2  // 以这种方式生成类在本例中其实可以不需要lazy 关键字,因为根据初始化顺序,这意味着 value 的定义早于 Parent2
                assert(anony2.value == 10)
                assert(anony2.inverse == 0.1)  // anony2.inverse 在此时求值会使用 value = 10。
            }
        }
    }
}

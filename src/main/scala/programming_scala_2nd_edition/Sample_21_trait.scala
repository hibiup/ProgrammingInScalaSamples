package programming_scala_2nd_edition

package Sample_21_trait {
    package MultipleTrait {
        /** trait　允许多重继承，继承顺序从右到左。
          *
          * 1) 下面的例子从 Clickable 中分别生成 ObservableClicks 和 VetoableClicks 两个子类。它们都重载了 click 方法，
          * 　　并调用 super 的 click
          *
          * 2) 在实例化　Clickable　的时候依次混入 ObservableClicks 和 VetoableClicks，VetoableClicks.click() 会最先被执行。
          *    执行结束后它的 super 指向 ObservableClicks，然后 ObservableClicks.click() 被执行，ObservableClicks 的 super
          *    指向 Clickable.
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
}

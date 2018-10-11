package programming_scala_2nd_edition

package Sample_29_Symbo {
    object SymboTest {
        /** Symbol: 符号字面量类型。语法为 '<标识符>。两个相同标识符的符号指向同一个对象。
          *
          * 符号字面量典型的应用场景是在动态语言中作为一个标识符。比如要执行数据库操作，参数可能是一个字段名，比如 firstName，
          * 动态语言可能直接使用 firstName 作为参数：
          *
          *   updateRecord(firstName, "xxx")
          *
          * 这在静态类型语言中会出错，因为 firstName　未
          * 被定义过。作为提到的方法，可以将参数定义成 Symbol 类型。然后调用的时候使用：
          *
          *   pdateRecord('firstName, "xxx")
          *
          * */
        def updateRecord(col_name: Symbol, value: Any): Unit = {
            println(col_name, col_name.name)
        }

        def apply() {
            updateRecord('firstName, "Jeff")
        }
    }
}

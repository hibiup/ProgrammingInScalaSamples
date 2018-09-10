package programming_scala_2nd_edition.sample_11

import scala.reflect.runtime.universe._

package single_type {
    /**
      * 参考: http://hongjiang.info/scala-type-system-singleton-type/
      */
    class A {
        def method_this_type: this.type = this // 当 A 被继承后，在子类中调用 "this" 的话指向的是子类型的实例。 所以 this.type == B，得到的也是 B 实例自己.
        def method_type_A: A = this // 明确要求返回 A 的 type，相当于强制转换到父类 (B)this => (A)this
    }

    class B extends A {
        def method_B = {
            println(s"Hello ${typeOf[this.type]}")
        }
    }
}

package programming_scala_2nd_edition.sample_11

import scala.reflect.runtime.universe._

package types {
    object TypeOfIntroduce {
        /**
          * 类型（type）是介于类和类实例中间的一个抽象的概念: class > type > instance
          *
          * 当我们实例化一个　class 的时候，实际上是实例化了一个类型。直观地理解就是冒号后面的部分实际上是 type，而不是 class:
          *   val name:type
          *
          * class 和 type 的区别是：
          *
          * 1) List 是类，List[Int] 是类型，它不等于 List[Double]。一个实例　intList: List[Int]　来自List[Int]，
          * 　　它的类型不同于　doubleList: List[Double]: typeOf[intList] != type[doubleList]
          *    但是它们的　class 是相同的。所以对于泛型类型取决于它们的泛型参数。
          */
          def apply()= {
              //class A
              //val a = new A
              //println(typeOf[a.type])  // a.type

              val intList: List[Int] = List[Int]()
              val doubleList: List[Double] = List[Double]()

              // 类相同
              assert(intList.getClass == doubleList.getClass)
              // 值（空集）也相同
              assert(intList == doubleList)

              // 类型不相同
              assert(typeOf[List[Int]] != typeOf[List[Double]])
              assert(typeOf[intList.type] != typeOf[doubleList.type])
        }
    }

    object InstanceType {
        /**
          * 参考: http://hongjiang.info/scala-type-system-singleton-type/
          */
        class A {
            /** 动态获得运行时实例的类型 */
            def method_this_type: this.type = this // 当 A 被继承后，在子类中调用 "this" 的话指向的是子类型的实例。 所以 this.type == B，得到的也是 B 实例自己.
            def method_type_A: A = this // 明确要求返回 A 的 type，相当于强制转换到父类 (B)this => (A)this
        }

        class B extends A {
            def method_B = {
                println(s"Hello ${typeOf[this.type]}")
            }
        }

        def apply(): Unit = {
            val b = new B
            val self = b.method_this_type
            self.method_B

            /** method_type_A 因为明确要求返回 A type，所以无法得到子类方法。*/
            //val parent = b.method_type_A
            //parent.method_B
        }
    }

    object ShadowType {
        /** 投影类型及其路径 */
        class A{
            class X
            def foo(x:A#X) =        // 父类型　“A#X”　是子类型的投影类型，可以做为参数类型被接受。
                x match {
                    case _: A#X => println(typeOf[x.type]) // 可以匹配，但是不会改变类型推断的结果。
                }
        }
        class B extends A
        class C extends A

        def apply() = {
            /**
              * type 同时具有路径属性，相同的 class 在不同的路径下被实例化产生的 type 也不同。
              */
            val b = new B
            val c = new C
            assert(typeOf[b.X] != typeOf[c.X])

            /** 但是它们的　"投影类型"(A#X)　是相同的　 */
            val x = new c.X
            b.foo(x)
        }
    }

    object StructuralType {
        def apply(): Unit = {
            /**
              * type 不仅仅可以继承，还可以动态定义，比如定义“鸭类”时常用的“structural type”也可以定义成 type
              */
            type Closable = { def close():Unit }
            def free(res:Closable) = res.close  // name:type
        }
    }
    
    package AbstractType {
        /**
          * p (323)
          *
          * Scala 支持抽象类型，允许脱离实际类型约定它们之间的逻辑关系：
          **/

        /** 1) 比如有以下抽象类型，它们之间存在关系如下： */
        trait TypeTrait {
            type t1                // t1 是抽象类型，没有任何约束
            type t3 <: t1          // t3 也是抽象的，但是必须是 t1 的子类型
            type t2 >: t3 <: t1    // t2 必须是 t3 的父类型，t1 的子类型。注意它们的定义语法，从左到右递归，先当于：(t2 >: t3) <: t1
            type t4 <: Seq[t1]     // t4 必须是 t1 的序列的子类型
        }

        /** 2) 相应地定义三个测试类 */
        object Classes {
            trait T1 { val name1: String }                            // T1
            trait T2 extends T1 { val name2: String }                 // T2 <: T1
            case class T3(name1: String, name2: String) extends T2    // T2 >: T3 <: T1
        }

        object TypeHeritage extends TypeTrait{
            import Classes._
            /** 3) 继承 TypeTrait 后根据其对类型的约束具体化它们对应的类型。
              **/
            type t1 = T1   // 具体化类型 t1
            type t3 = T3   // 顺利成章具体化　T2　和 T3。 如果 t1 = T2；t3 = T1 则无法通过编译，因为将违背 TypeTrait 的定义。
            type t2 = T2
            type t4 = Vector[T1]

            def apply() = {
                /** 4) 实例化类型 */
                val v1: t1 = new T2 { val name2=""; val name1 = "T1"}       // t1 定义后才能初始化
                val v3: t3 = T3("1", "2")
                val v2 = new T2 { val name1 = "T1"; val name2 = "T2" }
                val v4 = Vector(T3("3", "4"))  // 可以容纳 T1　的子类型

                // 5) 使用实例 ...
            }
        }
    }

    package SelfTypeAnnotation {
        /** 用自类型标记　(self-type annotation)　将实例自身指向抽象类型 */

        trait SubjectObserver {
            /** 1) 在观察者模式中，我们定义两个类型，一个是消息发布者（S），一个是消息关注者（O）　*/
            type S <: Subject
            type O <: Observer

            /** 2) 消息关注者在接收到消息时会得到一个发布者的实例（subject: S）　*/
            trait Observer {
                def receiveUpdate(subject: S)
            }

            /** 3) 消息发布者有两个主要方法。a) 添加观察者。b) 在发布消息时将自身(this)传递给观察者 */
            trait Subject {
                self: S =>  /** 5) 关键: 所以为Subject 声明一个自类型标记self: S。这意味着我们现在可以“假设”Subject 为子类型 S 的实例。*/
                def label:String
                private var observers = List[O]()
                def addObserver(observer: O) = observers ::= observer

                /** 4) 然而，当我们编译时，如果传递给 receiveUpdate 的是 this ，会出错，因为 this 是 Subject，而不是要求的 S。 */
                def notifyObservers() = observers.foreach(_.receiveUpdate(self))
            }
        }

        object TestSubjectObserver extends SubjectObserver {
            /** 1) 具体化 S */
            type S = SubjectButton

            /**
              * 2) 如果没有(1) extends Subject 会出错，因为 Subject 的 self: S => 意味着我们视 Subject 为类型 S，
              * 但是如果没有(1) S 又不存在，它们互相依赖。所以缺少任何一句都无法通过编译。
              **/
            class SubjectButton(name:String) extends Subject {
                val label = name
                def click() = {
                    notifyObservers()
                }
            }

            /** Observer 没有这样的缠绕关系 */
            type O = ButtonObserver
            class ButtonObserver extends Observer {
                def receiveUpdate(button: SubjectButton)= {
                    println(button.label)
                }
            }

            def apply() = {
                val button = new SubjectButton("Button")
                button.addObserver(new ButtonObserver())
                button.notifyObservers()
            }
        }
    }
}

package programming_scala_2nd_edition.sample_6

object StructialType {
    /** 以下是利用 structural type 实现观察者模型的例子 */
    trait SubjectFunc {
        type State
        type Observer = State => Unit  // 1) 观察者被定义成一个函数变量
        private var observers: List[Observer] = Nil

        def addObserver(observer:Observer): Unit = observers ::= observer            // 添加观察者
        def notifyObservers(state: State): Unit = observers foreach (o => o(state))  // 调用观察者
    }

    def apply() = {
        val subject = new SubjectFunc {
            type State = Int
        }
        // 2) 实现观察者函数变量
        val observer: Int => Unit = (state: Int) => println("got one! " + state)

        // 3) 添加并调用
        subject.addObserver(observer)
        subject.notifyObservers(1)
    }
}

object duck_typing {
    // 鸭类 (typeclass)
    trait Bird {
        def fly:Unit = println("I can fly!")
        def twit:Unit
    }

    trait Swimming {
        def swim:Unit = println("Only a duck can swimming")
    }

    trait Quacking extends Bird{
        override def twit:Unit = println("Quack! quack!!")
    }

    trait Coloring {
        def color:String
        /**
          * 语法糖：方法名_= 的方法在 scala 中是一个代替 setter 的语法糖s，它具有以下特征：
          *
          * 1) 首先，必须存在一个与“方法名”同名的属性
          *
          * 2）其次，用下划线（_）连接“方法名” 和 "=" 让他们连成同一个变量(方法)名："color_="
          *
          * 3) 然后，在引用这个变量（方法）的时候，下划线可以省略，也就是说。引用形式可以写成："color =" 或 "color="，等同于"color_="
          *    需要注意的是，这种便利只是一个调用端的约定，并不意味着对象真的引出了 "color =" 或 "cclor=" 名称
          *
          * 参考： https://www.dustinmartin.net/getters-and-setters-in-scala:
          */
        def color_=(value:String) = println("No, you can't change it")   // "color_=" 是一个函数名，不是赋值操作
    }

    def apply() = {
        val duckFactory = (age:Int, c:String) => {    // 1）返回一个带有 trait Duck 接口的类实例
            if (age >= 18) new Bird with Swimming with Quacking with Coloring {
                override def color = c
            }
            else new Bird with Swimming with Quacking with Coloring {
                override def fly = println("I can't fly!")
                private var _color = c
                override def color:String = _color
                override def color_=(value:String) = _color = value
            }
        }

        /** structural type */
        def DuckFly(duck: {def fly: Unit}) {
            duck.fly
        } // 2）接受的数据类型可以为 AnyRef 但是只需要含有 def fly: Unit 方法
        def DuckTwit(duck: {def twit: Unit}) {
            duck.twit
        }

        def DuckSwimming(duck: {def swim: Unit}) {
            duck.swim
        }

        def DuckShowColor(duck: {def color: String}) {
            println(duck.color)
        }

        /**
          * 引入 "def color:String" 只是为了做类型匹配，实际上并没有被调用到。duck.color=c 实际上调用了 color_=() 方法。
          * 利用的是 scala 方法或函数名称中下的划线在调用端可以省略的语法糖。
          **/
        def ChangeColor(duck: {def color_=(value: String); def color: String})(c: String) {
            duck.color = c
        }

        val childDuck = duckFactory(1, "YELLO")
        val adultDuck = duckFactory(18, "BLACK")

        DuckSwimming(childDuck)
        DuckTwit(childDuck)
        DuckFly(childDuck)
        DuckShowColor(childDuck)
        ChangeColor(childDuck)("RED")
        DuckShowColor(childDuck)

        DuckSwimming(adultDuck)
        DuckTwit(adultDuck)
        DuckFly(adultDuck)
        DuckShowColor(adultDuck)
        ChangeColor(adultDuck)("GREEN")
    }
}

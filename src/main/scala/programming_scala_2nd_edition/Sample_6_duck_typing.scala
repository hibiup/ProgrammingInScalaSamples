package programming_scala_2nd_edition.sample_6

object duck_typing extends App{
    // 鸭类
    trait Bird {
        def fly:Unit = println("I can fly!")
        def twit:Unit
    }

    trait Swimming {
        def swim:Unit = println("Only a duck can swimmsing")
    }

    trait Quacking extends Bird{
        override def twit:Unit = println("Quack! quack!!")
    }

    trait Coloring {
        def color:String
        /**
          * 语法糖：这里是最容易引起“误解”的地方。
          *
          * 1) 首先，下划线（_）连接了 "color" 和 "=" 让他们连成同一个变量(函数)名："color_="
          *
          * 2) 其次，在引用这个变量（函数）的时候，下划线可以省略，也就是说。引用形式可以写成："color =" 或 "color="，等同于"color_="
          *    需要注意的是，这种便利只是一个调用端的约定，并不意味着对象真的引出了 "color =" 或 "cclor=" 名称
          *
          * 参考： https://www.dustinmartin.net/getters-and-setters-in-scala:
          */
        def color_=(value:String) = println("No, you can't change it")   // "color_=" 是一个函数名，不是赋值操作
    }

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

    def DuckFly(duck: {def fly: Unit}) {duck.fly}     // 2）接受的数据类型可以为 AnyRef 但是只需要含有 def fly: Unit 方法
    def DuckTwit(duck: {def twit: Unit}) {duck.twit}
    def DuckSwimming(duck: {def swim: Unit}) {duck.swim}
    def DuckShowColor(duck: {def color: String}) {println(duck.color)}
    /**
      * 引入 "def color:String" 只是为了做类型匹配，实际上并没有被调用到。duck.color=c 实际上调用了 color_=() 方法。
      * 利用的是 scala 方法或函数名称中下的划线在调用端可以省略的语法糖。
      * */
    def ChangeColor(duck: {def color_=(value:String); def color:String})(c:String) { duck.color=c }

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

object enhanced_with_implicate extends App{
    trait Semigroup[A] {
        def combine(a1: A, a2: A): A
    }

    object Semigroup {
        def apply[A](implicit instance: Semigroup[A]) = instance

        // 1）为 apply() 方法隐式获得 Semigroup 实例
        implicit val intPlusInstance = new Semigroup[Int] {
            def combine(a1: Int, a2: Int): Int = a1 + a2
        }

        // 2) 隐式将 A类型(在这里也就是 Int) 装换成 Syntax 类型，以便能够使用 "|+|" 方法。(隐式参数 "I" 为 "new Syntax" 传递了一个所需的隐式上下文参数)
        implicit def to[A](target: A)(implicit I: Semigroup[A]): Syntax[A] =
            new Syntax[A] {        // 实例化的时候，根据 Syntax 的定义，需要一个 Semigroup 实例。由隐式参数提供了(显然也就是 intPlusInstance)，会被隐式绑定到实例中去。
                val a1 = target
            }

        // 2-1) 在生成 Synctax 的时候隐式绑定一个 Semigroup 实例(在实例化的过程中由上下文提供)
        abstract class Syntax[A](implicit I: Semigroup[A]) {
            def a1: A
            def |+|(a2: A): A = I.combine(a1, a2)   // 调用 intPlusInstance 单例的 combine 执行运算。
        }
    }

    import Semigroup._
    println (f"1 |+| 2 = ${1 |+| 2}")
}
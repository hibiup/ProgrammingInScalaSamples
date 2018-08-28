package programming_scala_2nd_edition.sample_6

import programming_scala_2nd_edition.sample_6.duck_typing.DuckShowColor

object duck_typing extends App{
    // 鸭类
    trait Bird {
        def fly:Unit = {None}
        def twit:Unit = {None}
    }

    trait Swimming {
        def swim:Unit = println("Only a duck can swimmsing")
    }

    trait Quacking extends Bird{
        override def twit:Unit = println("Quack! quack!!")
    }

    trait Coloring {
        def color:String
    }

    val duckFactory = (age:Int, c:String) => {    // 1）返回一个带有 trait Duck 接口的类实例
        if (age >= 18) new Bird with Swimming with Quacking with Coloring {
            override def fly = println("I can fly!")
            override def color:String = c
        }
        else new Bird with Swimming with Quacking with Coloring {
            override def color:String = c
        }
    }

    def DuckFly(duck: {def fly: Unit}) {duck.fly}     // 2）接受的数据类型可以为 AnyRef 但是只需要含有 def fly: Unit 方法
    def DuckTwit(duck: {def twit: Unit}) {duck.twit}
    def DuckSwimming(duck: {def swim: Unit}) {duck.swim}
    def DuckShowColor(duck: {def color: String}) {println(duck.color)}

    val childDuck = duckFactory(1, "YELLO")
    val adultDuck = duckFactory(18, "BLACK")

    DuckSwimming(childDuck)
    DuckTwit(childDuck)
    DuckFly(childDuck)
    DuckShowColor(childDuck)

    DuckSwimming(adultDuck)
    DuckTwit(adultDuck)
    DuckFly(adultDuck)
    DuckShowColor(adultDuck)
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
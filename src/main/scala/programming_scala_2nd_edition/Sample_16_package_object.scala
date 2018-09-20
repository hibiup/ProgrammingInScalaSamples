package programming_scala_2nd_edition

package Sample_16_package_object {
    /** package object 最大的作用是可以在包一级定义变量　*/
    package object packageItems {
        type HashMap[A,B] = scala.collection.mutable.HashMap[A,B];  // 为类型声明别名是　package object 的一大作用。
        val HashMap =  scala.collection.mutable.HashMap
        def say = println("hello, ggd543")
    }

    object user1 {
        // 可以直接象对象一样使用　package object 中定义的对象。
        val map = packageItems.HashMap
        def sayHello = packageItems.say
    }
}

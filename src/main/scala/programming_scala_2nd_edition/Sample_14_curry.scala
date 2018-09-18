package programming_scala_2nd_edition

package Sample_14_curry {
    object general_curry {
        /** curry 函数的一般定义形式　*/

        def curry_add(s1: Int)(s2: Int) = s1 + s2

        def apply(): Unit = {
            val curried_add = curry_add(1) _   // has "_"
            println("curry_add(1) _ => curried_add(2)=",curried_add(2))
        }
    }

    object curry_without_underscore {
        /** 如果在使用　curry　函数时不希望出现下划线“_“　*/

        def curry_add(s1: Int) = (s2: Int) => s1 + s2
        def apply(): Unit = {
            val curried_add = curry_add(1)   // Without "_"
            println("curry_add(1) => curried_add(2)",curried_add(2))
        }
    }

    object curry_without_underscore_with_multiple_param {
        /** 可以将上一例子中的参数全部用函数值来定义　*/

        // curry_add 的返回类型是:  Int => Int => Int
        def curry_add = (s1: Int) => (s2: Int) => s1 + s2

        // curry_add2 的返回类型是: Int => (Int => Int) 和 Int => Int => Int 等价
        def curry_add2: Int =>(Int => Int) = (s1: Int) => (s2: Int) => s1 + s2

        def apply(): Unit = {
            println("Int => Int => Int ",curry_add(1)(2))
            println("Int => (Int => Int) ",curry_add2(1)(2))
        }
    }

    object curried_normal_function {
        /** 将普通函数转换成　curry 函数　*/

        def curry_add(s1: Int, s2: Int) = s1 + s2
        def cat3(s1: Int, s2: Int) = s1 + s2

        def apply(): Unit = {
            // "_" 表示所有的参数，等价于得到: def curried_add(s1: Int, s2: Int) = s1 + s2
            val curried_add = (curry_add _).curried
            println("(curry_add _).curried => ", curried_add(1)(2))

            /** 可以将 curried 值反向获得普通函数　*/
            val uncurried_add = Function.uncurried(curried_add)  // 只能作用于　curried 值，不能作用于 curry 函数．
            println("Function.uncurried(curried_add) => ", uncurried_add(1, 2))
        }
    }
}

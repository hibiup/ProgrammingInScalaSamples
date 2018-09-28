package programming_scala_2nd_edition

package Sample_24_higherkinded {
    /**
      * Type lambda 例子．（p348-349）
      * */
    import scala.language.higherKinds

    /** 1) 函子 Functor 包含一个 map2 函数，这个函数接受一个参数，这个参数将 A 类型转成 B 类型。最终返回 M[B]，这意味着函数 f 只能处理
      * 　　容器 M 中的一个参数。 */
    trait Functor[A, +M[_]] {
        def map2[B](f: A => B): M[B]
    }

    object TypeLambda {
        /** 2.1) 扩展这个函子得到一个新的函子，新的函子接受一个内容是个 Map 的容器类型，也就意味着它的内容它包含两个参数 K 和 V
          *      但是 map2 只接受对 V 的处理。我们需要屏蔽掉 K */
        implicit class MapFunctor[K, V](kvMap: Map[K, V])
                /** 2.2) 因此在扩展 Functor 的时候我们定义一个新的 λ 类型，这个类型只“关注” M 的 V 类型，用 α 来表示它。
                  * 写成：type λ[α] = Map[K,α]。整个 type lambda　表达式的格式是：
                  *
                    (                        // 表达式开始
                        {                    // 开始定义结构化类型
                        type λ[α] = Map[K,α] // lambda
                        }                    // 结束结构化类型定义
                    )#λ                      // 表达式结束.与此同时，运用类型映射机制将类型λ 从参数化类型中取出,它是带有嵌入类型
                                             // 参数的 Map 的别名. 类型影射可以得到一个只包含类（Lambda是实例）路径的“纯粹”的类型。
                  *
                  * 2.3) 将 λ 作为 Functor 的第二个参数以满足 Functor 的定义。
                  * */
                extends Functor[V,({type λ[α] = Map[K,α]})#λ] {  // ({type λ[α] = Map[K,α]})#λ 是 "type lambda"
            /** 3) 给出第二个参数和其转换函数*/
            def map2[V2](f: V => V2): Map[K,V2] = kvMap map {
                case (k,v) => (k,f(v))
            }
        }

        def apply() = {
            val m = Map("one" -> 1, "two" -> 2, "three" -> 3)
            m map2 (_ * 2) // Map(one -> 2, two -> 4, three -> 6)
        }
    }
}

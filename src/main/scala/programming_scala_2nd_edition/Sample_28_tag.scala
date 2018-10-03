package programming_scala_2nd_edition

object Sample_28_tag {
    object TestClassTag {
        /** ClassTag 用来保留类型参数的信息，避免 JVM 的类型擦除带来的问题。 */
        import scala.reflect.ClassTag

        def mkArray[T: ClassTag](elems: T*) = Array[T](elems: _*)
        def apply() = {
            val ia = mkArray(1, 2, 3)                  // T:Int
            val sa = mkArray("one", "two", "three")    // T:String
            val  nn = mkArray(1, "two", 3.14)          // T:Any
        }
    }

    object TestTypeTag {
        /** ，TypeTag 保留了完整的编译时类型信息，同时ClassTag 只保留了运行时类型信息。 */
        import scala.reflect.runtime.universe._

        // 使用了TypeTag[T]r类型的隐含参数，来返回该参数的类型。
        def toType2[T](t: T)(implicit tag: TypeTag[T]): Type = tag.tpe

        // 绑定上下文，这是一种更简便的方法。typeOf[T] 方法是 implicitly [TypeTag[T]].tpe 简写。
        def toType[T : TypeTag](t: T): Type = typeOf[T]

        def apply() = {
            println(toType(1))
            println(toType(true))
            println(toType((i: Int) => i.toString))

            /** 可以得到详细类型信息．*/
            val ts = toType(Seq(1, true, 3.14))
            println(ts.typeSymbol)
            println(ts.erasure)
            println(ts.typeArgs)
            println(ts.baseClasses)
            println(ts.members)

            /** =:= 等符号是类型逻辑运算符 */
            assert(toType(1) =:= toType(1))
            assert(typeOf[Seq[Int]] <:< typeOf[Seq[Any]])
        }
    }

    object TypeTagInVariant {
        import scala.reflect.runtime.universe._

        class CSuper { def msuper() = println("CSuper") }
        class C extends CSuper { def m() = println("C") }
        class CSub extends C { def msub() = println("CSub") }


        def apply() = {
            assert(typeOf[C => C] =:= typeOf[C => C])
            assert(typeOf[C => C ] <:< typeOf[C => C])       // 任何类型都是其自身的子类型
            assert(typeOf[CSuper => CSub ] <:< typeOf[C => C])
        }
    }

    object GetTypeRef {
        import scala.reflect.runtime.universe._

        def toType[T : TypeTag](t: T): Type = typeOf[T]

        def toTypeRefInfo[T : TypeTag](x: T): (Type, Symbol, Seq[Type]) = {
            /** 类型提取器，采用类型匹配语法．RegEx 匹配中也用到类似语法．它通过 unapply 生效． */
            val TypeRef(pre, typName, parems) = toType(x)
            (pre, typName, parems)
        }

        def apply() = {
            println(toTypeRefInfo( (i: Int) => i.toString ) )  // (scala.type,trait Function1,List(Int, String))
            println(toTypeRefInfo(Seq(1, true, 3.14)))         // (scala.collection.type,trait Seq,List(AnyVal))
            println(toTypeRefInfo(Seq(1, 2, 3.14)))            // (scala.collection.type,trait Seq,List(Double))
        }
    }
}

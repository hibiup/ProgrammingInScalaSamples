package programming_scala_2nd_edition.sample_10

import scala.annotation.tailrec

package Sample_10_seq_match {
    object regular_matching extends App {
        val nonEmptySeq = Seq(1, 2, 3, 4, 5)
        val emptySeq = Seq.empty[Int]
        val nonEmptyList = List(1, 2, 3, 4, 5)
        val emptyList = Nil
        val nonEmptyVector = Vector(1, 2, 3, 4, 5)
        val emptyVector = Vector.empty[Int]
        val nonEmptyMap = Map("one" -> 1, "two" -> 2, "three" -> 3)
        val emptyMap = Map.empty[String, Int]

        def seqToString[T](seq: Seq[T]): String = seq match {
            case head +: tail => // 中缀匹配。
                s"$head +: " + seqToString(tail)
            case Nil => "Nil" // 覆盖不含值的情况
        }

        // for 推导式
        for (seq <- Seq( // 构建一个新的 Seq 的 Seq（二维）
            nonEmptySeq, emptySeq, nonEmptyList, emptyList,
            nonEmptyVector, emptyVector, nonEmptyMap.toSeq, emptyMap.toSeq)) {
            println(seqToString(seq)) // 打印成 String
        }
    }

    object tailrec_in_matching extends App {
        @tailrec
        def reverse[T](list: List[T], res: List[T]): List[T] = list match {
            case prefix :+ end => {
                reverse(prefix, res :+ end) // 不能写成 tail +: res
            }
            case Nil => res
        }
        println(reverse(List[Int](1, 2, 3, 4), Nil))
    }

    object case_class_for_matching extends App {
        @tailrec
        def list_sliding(list: Seq[Any], res: Seq[(Any, Any)]): Seq[(Any, Any)] = list match {
            /** 中缀表达式在 match case 中可以递归　 */
            case head1 +: head2 +: tail => list_sliding(tail, res :+ (head1, head2))

            /** 等价以上语法，但是用　"变量名 @ _*" 的形式来匹配可变参数。这得益于　Seq(...) 接受可变参数 */
            case Seq(head1, head2, tail@_*) => list_sliding(tail, res :+ (head1, head2))
            case Seq(head) => list_sliding(list.tail, res :+ (head, None))
            case Nil => res
        }

        println(list_sliding(List[Int](1, 2, 3, 4, 5), Nil))
        println(List[Int](1, 2, 3, 4, 5).sliding(3, 2).toList)
    }

    object assignment_in_matching extends App {
        /** @　作用于case class 具有赋值得作用 (p104) */
        case class Address(street: String, city: String, country: String)
        case class Person(name: String, age: Int, address: Address)

        def get_value_from_case(persons: Seq[Person]) = {
            for (person <- persons) {
                person match {
                    /** 将整个 Person("Alice", 25, address) 赋值给变量 p */
                    case p@Person("Alice", 25, address) => println(s"Hi Alice! $p")

                    /** 将整个 Address(street, city, country) 赋值给变量 a */
                    case p@Person("Bob", 29, a@Address(street, city, country)) => println(s"Hi ${p.name}! age ${p.age}, in ${a.city}")

                    /** @ 支持用"_" 修饰的匹配符 */
                    case p@Person(name, age, _) => println(s"Who are you, $age year-old person named $name? $p")
                }
            }
        }

        // 定义三个变量
        val alice = Person("Alice", 25, Address("1 Scala Lane", "Chicago", "USA"))
        val bob = Person("Bob", 29, Address("2 Java Ave.", "Miami", "USA"))
        val charlie = Person("Charlie", 32, Address("3 Python Ct.", "Boston", "USA"))
        val persons = Seq(alice, bob, charlie)
        get_value_from_case(persons)
    }

    object case_with_difference_types extends App{
        /** 类型擦除（p105）会导致二级序列的类型信息丢失　*/
        val res = for ( x <- Seq(List(5.5, 5.6, 5.7), 1, List("a", "b"), "Hello" ))
            yield (x match {
                    case seq: Seq[_] => {
                        seq match {
                            /** 信息丢失的结果是 Seq[Double] 实际上可以匹配所有的数据　*/
                            case dbSeq : Seq[Double] => ("Double Seq: ", dbSeq)
                            case strSeq : Seq[String] => ("String Seq: ", strSeq)
                        }
                    }
                    case _ => ("Non-Seq: ", x)
                })
        println(res)
    }

    object seq_filter extends App {
        var a = 0
        /** 例一、List 和 filter 都是 eager 运算，因此运算的优先级是：
          *    List 完成数列生成 ->
          *      一次性过滤完成（过滤条件为a==0）得到的结果当然也就是全集了。 ->
          *        a = a+1 对filter已经不起作用了，因为 eager filter已经结束。所以打印出全集。*/
        List.range(1, 10)
            .filter(_ => a % 2 == 0)
                .foreach(x => {
                    a = a+1;
                    println(x)
                })

        a = 0
        /** 例二（不推荐，原因见下）、Stream 是 Lazy 运算，所以第一次 filter 时集合中只有一个元素，因此运算等价于：
          *   List(1).filger(_ =>a % 2 == 0).foreach(x => { a = a+1;println(x)}) 当计算完成后 a == 1，
          *
          * 第二轮运算时 Stream 对象重新生成，再次调用 filter时因为 a%2 ！= 0 ，因此输出为空，foreach 没有输入。
          * 第三轮以后全都因为 a == 1 输出为空。。。*/
        Stream.range(1, 10)
            .filter(_ => a % 2 == 0)
                .foreach(x => {
                    a = a+1;
                    println(x)
                })

        /** 例三、List 一尺性生成 1 到 10 ，但是因为 withFilter 是 lazy 的，因此它不会一起性过滤全部集合，而是根据需求
          * 每次被 foreach 回调，可是foreach在打印第一个元素时改变了a, 结果悲剧了，它再也得不到输入了。。。*/
        a = 0
        List.range(1, 10)
            .withFilter(_ => a % 2 == 0)
                .foreach(x => {
                    a = a+1;
                    println(x)
                })

        /** 例四（推荐）、情况类似第三个例子，唯一不同的是 Stream 也是 Lazy 的，因此它和 withFilter 在语意上保持了一致，但是结果
          * 和例三是一样的。*/
        a = 0
        Stream.range(1, 10)
            .withFilter(_ =>
                a % 2 == 0)
                .foreach(x => {
                    a = a+1;
                    println(x)
                })

        /** 从例二开始，以上运算在到达 foreach 之前都存在 lazy 运算，因此都会反应 foreach 对 a 的改变，因此都只能完成一次输出。
          * 其中例二和例四都直接回调到源头，而例三则只回调到 withFilter。但是例四的前后语意是一致的，因此例二不被推荐。 */
    }
}

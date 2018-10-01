package programming_scala_2nd_edition

package Sample_26_Future {
    object FutureSample{
        import scala.concurrent.{Await, Future}
        import scala.concurrent.duration.Duration
        import scala.concurrent.ExecutionContext.Implicits.global

        def apply() = {
            /** 1) 创建 10 个 Future 对象 */
            val futures = (0 to 9) map {
                // Future.apply 方法带有两个参数列表。第一个参数列表中只包含一个需要并发执行的内容（以 call by-name 方式定义）
                // 第二个参数列表包含了隐式的 ExecutionContext 对象。*/
                i => Future {
                    val s = i.toString
                    print(s)
                    s
                }
            }

            /** 2) 把一组 Future 类型压缩成一个单独的 Future[String] 对象 */
            val reduceF = Future.reduceLeft(futures)((s1, s2) => s1 + s2)  // 把每个 Future 对象返回的字符串合并为一个字符串。

            /** 3) 直到 Future reduceF 完成之前，我们通过 scala.concurrent.Await 对象阻塞代码。 */
            val n = Await.result(reduceF, Duration.Inf)   // Duration.Inf 参数表示，如果需要的话，代码便会一直等待下去。

            // 需要强调至关重要的一点，执行Future 体中的print 语句时，print 语句的输出是无序的。 不过，由于fold 方法会依照Future
            // 对象构造的顺序遍历这些对象，因此 fold 方法生成的字符串总是会严格按数值次序排列，即 0123456789。*/
            println(s"\n${n}")
        }
    }
}

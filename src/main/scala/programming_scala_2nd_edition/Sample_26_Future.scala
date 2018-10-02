package programming_scala_2nd_edition

package Sample_26_Future {
    object FutureAwait{
        def apply() = {
            import scala.concurrent.{Await, Future}
            import scala.concurrent.duration.Duration
            import scala.concurrent.ExecutionContext.Implicits.global

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

            /** 2) 把一组 Future 类型压缩成一个单独的 Future[String] 对象. Future.reduceLeft 以及其它方法，比如 fold 等也都是
              * 异步方法，返回的是新的 Future 对象。*/
            val reduceF = Future.reduceLeft(futures)((s1, s2) => s1 + s2)  // 把每个 Future 对象返回的字符串合并为一个字符串。

            /** 3) 直到 Future reduceF 完成之前，我们通过 scala.concurrent.Await 对象阻塞代码。 */
            val n = Await.result(reduceF, Duration.Inf)   // Duration.Inf 参数表示，如果需要的话，代码便会一直等待下去。

            // 需要强调至关重要的一点，执行Future 体中的print 语句时，print 语句的输出是无序的。 不过，由于fold 方法会依照Future
            // 对象构造的顺序遍历这些对象，因此 fold 方法生成的字符串总是会严格按数值次序排列，即 0123456789。*/
            println(s"\n${n}")
        }
    }

    object FutureComplete {
        /** p370
          *
          * 遍历一个数字集合，如果遇到奇数抛出失败。偶数正常结束 */
        import scala.concurrent.Future
        import scala.concurrent.ExecutionContext.Implicits.global

        case class ThatsOdd(i: Int) extends RuntimeException( // 发现奇数，我们将抛出异常
            s"odd $i received!")

        def apply() = {
            import scala.util.{Try, Success, Failure}

            /** 无论执行结果是否成功，我们为这两个结果定义相同的回调处理程序。为了对成功和失败事件进行封装，封装回调函数的输入参数
              * 是 Try[A] 类型，因此回调函数的类型是 PartialFunction[Try[String],Unit] 其中A 是String 类型。由于回调函数
              * 异步执行，不会返回任何事物，因此回调函数的返回类型是Unit 类型。如果我们需要构建web 服务器，回调函数应该向调用者发
              * 送回复信息。 */
            val doComplete: PartialFunction[Try[String], Unit] = {
                case s@Success(_) => println(s)
                case f@Failure(_) => println(f)
            }

            val futures = (0 to 9) map {
                case i if i % 2 == 0 => Future.successful(i.toString)
                case i => Future.failed(ThatsOdd(i))
            }

            // 遍历future 对象
            futures map (_ onComplete doComplete)
        }
    }

    object FutureAsync {
        /**
          * Async 与 Future 的例子
          *
          * 在下面的例子中，我们模拟了一组同步调用，在第一次同步调用中，我们首先判断指定 id 的“记录”是否存在。假如该记录存在，
          * 我们将返回记录；否则，便返回错误记录。
          *
          * Async 参考: https://tudorzgureanu.com/a-quick-look-at-scala-async
          * */
        import scala.concurrent.{Await,Future}
        import scala.concurrent.duration.Duration
        import scala.async.Async.{async, await}
        import scala.concurrent.ExecutionContext.Implicits.global

        /** 假如输入的 id 大于 0，那么返回 true。 */
        def recordExists(id: Long): Boolean = {
            println(s"recordExists($id)...")
            Thread.sleep(1)
            id > 0
        }

        /** 根据 id 返回记录 */
        def getRecord(id: Long): (Long, String) = {
            println(s"getRecord($id)...")
            Thread.sleep(1)
            (id, s"record: $id")
        }

        def apply() ={
            /** asyncGetRecord 方法将多个异步操作组合起来顺序执行。
              *
              * 该方法首先将以异步方式调用 recordExists 方法。之后，asyncGetRecord 方法会等待recordExists 方法的执行结果。*/
            def asyncGetRecord(id: Long): Future[(Long, String)] = async {
                val exists = async {             // async 会生成 Future 实例
                    val b = recordExists(id)
                    println(b)
                    b
                }

                if (await(exists))               // await 取代 Await.result 获取 async 产生的结果。
                    await(async {
                        val r = getRecord(id)
                        println(r)
                        r                        // 如果存在，返回 (id, s"record: $id")
                    })
                else (id, "Record not found!")   // 否则返回 (id, "Record not found!")
            }  // 无论失败与否，返回 Future[(Long, String)] 对象。(async 会生成 Future 对象。)

            (-1 to 1) foreach { id =>
                val fut = asyncGetRecord(id)
                println(Await.result(fut, Duration.Inf))
            }
        }
    }
}

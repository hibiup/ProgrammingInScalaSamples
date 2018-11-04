package programming_scala_2nd_edition

package PromisedFuture {
    import concurrent.ExecutionContext.Implicits.global
    import scala.util.{Failure, Success, Try}

    object FailurePromise {
        import scala.concurrent.{Future, Promise}

        def apply(): Unit = {
            /** 1) Promise 存在两个可能的状态：Success 或 Failure, 但是它本身并不决定将会是什么状态。最终调用哪个状态由
              *    调用者决定。Promise 的作用仅仅是当调用者完成自己的任务后，将成功或失败的后续任务交给一个新的 Future
              *    去处理，而调用者线程则可以放心结束。*/
            val f = getPromisedFuture(0)

            /** 2) 收获 Promise 返回的新的 Future 后需要定义 onComplete 方法来决定如何处理 Success 或 Failure */
            f.onComplete{
                case Success(o) =>
                    println(s"Success with ${o.get}")
                case Failure(ex) =>
                    println(s"Failed with ${ex.getMessage}")
            }

            /** 5) 用 Try 来捕获 Promised Future 的结果. 因为 Promise 的 failure 要求抛出异常。Try 可以捕获这个可能
              * 的异常（见下面的定义）。*/
            val res = Try(f)

            val r = res.get
            r.foreach(println)
            assert(r.isInstanceOf[Future[Failure[RuntimeException]]])
        }

        def getPromisedFuture(implicit condition: Int): Future[Option[String]] = {
            // TODO: 调用者根据 considtion 计算结果，然后决定 stat 是成功还是失败。
            val stat = if (condition == 0) false else true

            /** 3) 为结果准备 Promise */
            val promise = Promise[Option[String]]()
            if (stat)
                promise.success {
                    /** success 返回结果。 */
                    Option(s"We did!")
                }
            else
                promise.failure{
                    /** failure 要求返回一个异常 */
                    new RuntimeException(s"We failed...")
                }

            /** 4）将 Promise 放到一个 Future 去执行，调用者线程结束。*/
            promise.future
        }
    }
}
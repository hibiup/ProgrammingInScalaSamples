package programming_scala_2nd_edition

package PromisedFuture {
    import concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration.Duration
    import scala.concurrent.Await
    import scala.util.{Failure, Success}

    object FailurePromise {
        import scala.concurrent.{Future, Promise}

        def apply(): Unit = {
            println(s"[Thread-${Thread.currentThread().getId}]: main thread")

            /* 获取承诺的未来 */
            val f = getPromisedFuture(1)

            /** 6) 需要注意的是，Promise 给出的 Future 就是新线程-2。也就是说如果此时线程-2尚未结束，那么当前线程-1 才需要等待（当然也可以不等）。*/
            println(s"[Thread-${Thread.currentThread().getId}]: waiting for promise")
            val res = Await.result(f, Duration.Inf)
            val r = res.get
            println(s"[Thread-${Thread.currentThread().getId}]: Got promised result -> ${r}")
            //assert(r.isInstanceOf[Future[Failure[RuntimeException]]])
        }

        def getPromisedFuture(implicit condition: Int): Future[Option[String]] = {
            println(s"[Thread-${Thread.currentThread().getId}]: creating promise")

            /** 1) 在当前线程（线程-1）中为结果准备 Promise */
            val promise = Promise[Option[String]]()

            /** 2) 将 Promise 交给另外一个线程（线程-2）。Promise 是用来存放新线程可能给出的结果的，它在被设置结果的时候不
              *    产生新的线程。 */
            Future {
                println(s"[Thread-${Thread.currentThread().getId}]: doing in new thread")
                // TODO: 调用者根据 condition 计算结果，然后决定是成功还是失败。
                val stat = if (condition == 0) false else true
                Thread.sleep(2000)

                /** 3) 新线程（线程-2）可能返回两个不同的状态：Success 或 Failure, Promise 本身并不决定将会是什么状态。这
                  *    个由调用者线程自己来决定。Promise 只根据结果存放返回值。新线程（线程-2）也不需要与调用者线程来协调何时
                  *    读取结果，因为它只与 Promise 发生关联。而 Promise 将接收到结果后将接管新线程（线程-2）以在未来不确定
                  *    的时间里被调用者线程读取结果。 */
                if (stat)
                    promise.success {
                        println(s"[Thread-${Thread.currentThread().getId}]: promise.success()")
                        /** success 返回结果。 */
                        Option(s"We did!")
                    }
                else
                    promise.failure {
                        println(s"[Thread-${Thread.currentThread().getId}]: promise.failure()")
                        /** failure 要求返回一个异常 */
                        new RuntimeException("We failed...")
                    }
            }

            /** 4）调用者线程也不用关心上面那个线程将会执行多久，它只需要从 Promise 中获得一个可以期待的结果。
              *    这段代码甚至可以执行在产生新线程（线程-2）之前。因为两者是异步的。新线程（线程-2）只关心将结果存入 Promise
              *    而调用者（当前线程-1）只关心由 Promise 产生的（可期获得的）结果。*/
            val f = promise.future

            /** 5) 当前线程-1 可以附加定义 Promise.Future 线程在收到结果后如何处理 Success 或 Failure。*/
            f.onComplete{
                case Success(o) =>
                    println(s"[Thread-${Thread.currentThread().getId}]: p.f.onComplete() -> Success: ${o.get}")
                case Failure(ex) =>
                    println(s"[Thread-${Thread.currentThread().getId}]: p.f.onComplete() -> Failed: ${ex.getMessage}")
            }

            println(s"[Thread-${Thread.currentThread().getId}]: got promise.future")
            f
        }
    }
}
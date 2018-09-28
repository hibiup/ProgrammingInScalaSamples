package programming_scala_2nd_edition

package DynamicFutureReturnType {
    /** 下面这个例子演示了如何动态适应 Future 的返回类型 */
    import scala.concurrent.ExecutionContext.Implicits.global
    import scala.concurrent.duration._
    import scala.concurrent.{Await, Future}

    /** 1) 下面两个类没有任何继承关系 */
    case class LocalResponse(statusCode: Int)

    case class RemoteResponse(message: String)

    /** 2) 用类型变量来记录类型。不使用泛型的目的是为了定义 handle 的时候能够更方便得到类型值 */
    sealed trait Computation {
        type Response
        val work: Future[Response]
    }

    /** 3) 为 Computation 派生两个 case class，他们具体定义了 work 和 Response */
    case class LocalComputation(work: Future[LocalResponse]) // 为 work 赋值为 Future[LocalResponse] 类型
            extends Computation {
        type Response = LocalResponse
    }

    case class RemoteComputation(work: Future[RemoteResponse]) // 为 work 赋值为 Future[RemoteResponse] 类型
            extends Computation {
        type Response = RemoteResponse
    }

    object Service {
        /** 4) 定义 handle 方法，由于LocalResponse 与 RemoteResponse 毫不相关， 因此 handle 方法并未返回它们共有的某个父类
          * 而是另辟蹊径，它将依据输入参数 computation 决定返回类型 computation.Response。
          *
          * computation 可能是 ocalResponse 对象或 RemoteResponse 对象，因此 handle 具有返回动态类型的特质。
          *
          * */
        def handle(computation: Computation): computation.Response = {
            val duration = Duration(2, SECONDS)

            /**
              * 5) 通过 scala.concurrent.Await 对象等待 Future 对象执行完毕。Await.result 根据输入的
              * Computation 对象的具体类型，返回相应的 LocalResponse 对象或 RemoteResponse 对象。
              */
            Await.result(computation.work, duration)
        }
    }

    object Compute {
        def apply() = {
            // Result: LocalResponse = LocalResponse(0)
            assert(Service.handle(LocalComputation(Future(LocalResponse(0)))) == LocalResponse(0))

            // Result: RemoteResponse = RemoteResponse(remote call)
            assert(Service.handle(RemoteComputation(Future(RemoteResponse("remote call")))) == RemoteResponse("remote call"))
        }
    }
}
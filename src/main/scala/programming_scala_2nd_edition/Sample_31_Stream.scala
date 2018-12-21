package programming_scala_2nd_edition

object Sample_31_Stream {
    def fib_stream() = {
        /**
          * a) Stream 的 #:: 运算和 List 的 :: 运算类似，都是将右边的值一个一个取出，然后和左边的值结合在一起形成新的列表．
          * 但是 Stream 是 lazy 的，直到 take 发生的时候才会实际执行．并且是堆栈安全的．因此以下死循环是安全的。
          * */
        def fibStream(a: Int, b: Int): Stream[Int] = a #:: fibStream(b, a + b)

        /** 同样的运算在一个基于 List 的 fib 中必定会导致 StackOverflow，因为死循环会立刻执行．*/
        def fibList(a: Int, b: Int): List[Int] = a :: fibList(b, a + b)

        /** 获取 fib stream 不会立即执行．*/
        val fib = fibStream(0,1)
        /** 直到被触发．（注意，不要试图 collect 所有结果．）*/
        val res = fib.take(100000)
        assert(100000 == res.toList.size)

        /**
          * b) 要注意避免强制处理整个 stream．比如 force 函数，能够强制评估 stream 再返回结果。还有比如 size()、tolist() 等，
          * 也不在没有退出机制的情况下使用 foreach()．以下的 foreach() 是安全的．
          * */
        import scala.util.control.Breaks._
        breakable {
            fib.foreach {
                case x if x < 10 => println(x)
                case _ => break
            }
        }

        /**
          * c) 可以从 List 中获得 Stream
          * */
        assert(100 == (0 to 10000).toStream.take(100).toList.size)
    }

    /**
      * view 和 Stream 类似都是懒加载的，view 可以将一个集合转变成懒加载
      * */
    def fib_view() = {
        /** 以下并不会立刻生成 1~1000000 集合,而是将它作为表达式保存起来。*/
        val numsView = (1 to 1000000).view

        /** 直道被 take 或 force */
        assert(List(1,2,3,4,5) == numsView.take(5).toList)
        assert(1000000 == numsView.force.size)   // Free monad 的集合生成是安全的

        /** 作用于 view 的 map 方法也不会立刻触发执行，因此也是安全的。*/
        val numsDouble = numsView.map(x => {
            print(s"$x * 2 = ")   // 不会立刻被打印
            x * 2
        })

        /** 同样可以执行有条件遍历 */
        import scala.util.control.Breaks._
        breakable {
            numsDouble.foreach {
                case x if x < 10 => println(x)  // 会执行上面的打印： x * 2 = 2x
                case _ => break
            }
        }
    }
}

package programming_scala_2nd_edition.sample_3

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * 一个 actor 的例子
  */
abstract class Shape() {
    /**
      * draw 方法接受一个函数参数　f，f　必须能够接受一个字符格式的参数来执行绘制工作。没有返回值
      */
    def draw(f:String => Unit):Unit = f(s"draw: ${this.toString}")
}

case class Point(x: Double=0.0, y: Double=0.0)
// Circle、Rectangle 和 Triangle 类都是 Shape 类的具体子类。这些类并没有类主体，这是因为 case 关键字为它们定义好了所有必须的方法，
// 如Shape.draw 所需要的 toString 方法。
case class Circle(center: Point, radius: Double) extends Shape
case class Rectangle(lowerLeft: Point, height: Double, width: Double) extends Shape
case class Triangle(point1: Point, point2: Point, point3: Point) extends Shape

object Messages {
    object Exit          // object 是单例，不能 new，退出和结束都不传递值，因此定义成 object
    object Finished
    object Start
    case class Response(message: String)   // case class 缺省也会产生一个单例，也可以 new 实例
}

/**
  * Actor
  */
class ShapesDrawingActor extends Actor {
    import Messages._

    override def receive: Receive = {
        case s: Shape =>
            s.draw(str => println(s"${this.getClass.getSimpleName()}: $str"))
            sender ! Response(s"${this.getClass.getSimpleName()}: $s drawn")  // 发送新的实例
        case Exit =>
            println(s"${this.getClass.getSimpleName()}: exiting...")
            sender ! Finished                                                 // 发送单例对象
        case unexpected => // default. Equivalent to "unexpected: Any"
            val response = Response(s"ERROR: Unknown message: $unexpected")
            println(s"${this.getClass.getSimpleName()}: $response")
            sender ! response
    }
}

/**
  * Driver
  */
object ShapesDrawingDriver {
    import Messages._

    def main(args: Array[String]) {
        val system = ActorSystem(s"${this.getClass.getSimpleName().filterNot("$".contains(_))}".toString(), ConfigFactory.load())
        val drawer = system.actorOf(
            Props(new ShapesDrawingActor), "drawingActor")
        val driver = system.actorOf(
            Props(new ShapesDrawingDriver(drawer)), "drawingService")
        driver ! Start
    }
}

class ShapesDrawingDriver(drawerActor: ActorRef) extends Actor {
    import Messages._

    def receive = {
        case Start =>
            drawerActor ! Circle(Point(0.0,0.0), 1.0)
            drawerActor ! Rectangle(Point(0.0,0.0), 2, 5)
            drawerActor ! 3.14159
            drawerActor ! Triangle(Point(0.0,0.0), Point(2.0,0.0), Point(1.0,2.0))
            drawerActor ! Exit
        case Finished =>
            println(s"${this.getClass.getSimpleName()}: cleaning up...")
            context.system.terminate()
        case response: Response =>
            println(s"${this.getClass.getSimpleName()}: Response = " + response)
        case unexpected =>
            println(s"${this.getClass.getSimpleName()}: ERROR: Received an unexpected message = "
                    + unexpected)
    }
}
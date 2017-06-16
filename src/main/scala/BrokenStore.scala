/*

Overview:
               +------------+   +--------+
               |            |<=>|        |
+----------+   |            |   |        |
|   User   |<=>| Controller |   |   DB   |
+----------+   |            |   |        |
               |            |<=>|        |
               +------------+   +--------+

*/

import actoverse._
import akka.actor._

class User(controller: ActorRef) extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case "start" =>
      controller !+ AddAccount(300)
      controller !+ AddAccount(-200)
  }
}

@Comprehensive case class AddAccount(value: Int)

class Controller(db: ActorRef) extends Actor with DebuggingSupporter {
  @State var tmpVal = Map[String, Int]()
  val receive: Receive = {
    case AddAccount(value) =>
      tmpVal = tmpVal + (sender.path.name -> value)
      db !+ GetAccount(sender.path.name)
    case (name: String, value:Int) =>
      db !+ SetAccount(name, value + tmpVal(name))
  }
}

@Comprehensive case class GetAccount(name: String)
@Comprehensive case class SetAccount(name: String, value: Int)

class DataBase extends Actor with DebuggingSupporter {
  @State var data = Map[String, Int]("foo" -> 100)
  val receive: Receive = {
    case GetAccount(name) =>
      sender !+ (name, data(name))
    case SetAccount(name, value) =>
      data = data + (name -> value)
  }
}

object BrokenStore {
  def main(args: Array[String]){
    implicit val system = ActorSystem()
    val debuggingSystem = new DebuggingSystem
    debuggingSystem.introduce(system)

    val database = system.actorOf(Props[DataBase], name="db")
    val controller = system.actorOf(Props(classOf[Controller], database),
                                     name="controller")
    val user =  system.actorOf(Props(classOf[User], controller),
                                     name="foo")
    user ! "start"

    println("Ctrl-C to halt")
  }
}

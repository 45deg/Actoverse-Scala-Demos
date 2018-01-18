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


import akka.actor._

class User(controller: ActorRef) extends Actor {
  val receive: Receive = {
    case "start" =>
      controller ! AddAccount(300)
      controller ! AddAccount(-200)
  }
}

 case class AddAccount(value: Int)

class Controller(db: ActorRef) extends Actor {
   var tmpVal = Map[String, Int]()
  val receive: Receive = {
    case AddAccount(value) =>
      tmpVal = tmpVal + (sender.path.name -> value)
      db ! GetAccount(sender.path.name)
    case (name: String, value:Int) =>
      db ! SetAccount(name, value + tmpVal(name))
  }
}

 case class GetAccount(name: String)
 case class SetAccount(name: String, value: Int)

class DataBase extends Actor {
   var data = Map[String, Int]("foo" -> 100)
  val receive: Receive = {
    case GetAccount(name) =>
      sender ! (name, data(name))
    case SetAccount(name, value) =>
      data = data + (name -> value)
      println(s"Updated: $name: $value")
  }
}

object BrokenStore {
  def main(args: Array[String]){
    implicit val system = ActorSystem("demo")
    val database = system.actorOf(Props[DataBase], name="db")
    val controller = system.actorOf(Props(classOf[Controller], database),
                                     name="controller")
    val user =  system.actorOf(Props(classOf[User], controller),
                                     name="foo")
    user ! "start"

    println("Ctrl-C to halt")
  }
}

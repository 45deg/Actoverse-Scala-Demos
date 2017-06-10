import actoverse._
import akka.actor._

class Foo extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case s =>
      print(s)
  }
}

object Main {
  def main(args: Array[String]){
    implicit val system = ActorSystem()
    val debuggingSystem = new DebuggingSystem
    debuggingSystem.introduce(system)
    val foo = system.actorOf(Props[Foo], name="foo")
    foo ! "a"
    foo ! "b"
    foo ! "c"
  }
}

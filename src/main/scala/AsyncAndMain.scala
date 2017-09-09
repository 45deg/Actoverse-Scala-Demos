import actoverse._
import akka.actor._

class Output extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case msg =>
      println(msg)
  }
}

@Comprehensive case class SetCount(num: Int)

class AsyncAnd(out: ActorRef) extends Actor with DebuggingSupporter {
  @State var count: Int = 0
  val receive: Receive = {
    case SetCount(n) => 
      count = n
    case (v: Boolean) =>
      if (v) {
        count -= 1
        if(count == 0) {
          out !+ true
        }
      } else {
        out !+ false
      }
  }
}

class MockResponder (response: Boolean, target: ActorRef)
 extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case _ =>
      target !+ response
  }
}


class CoordinatorActor(asyncAnd: ActorRef, creditChecker: ActorRef, addressChecker: ActorRef) extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case "start" => 
      asyncAnd !+ SetCount(2)
      creditChecker !+ "CARDNUMBER"
      addressChecker !+ "ADDRESS"
  }
}

object AsyncAndMain {
  def main(args: Array[String]){
    implicit val system = ActorSystem()
    val debuggingSystem = new DebuggingSystem
    debuggingSystem.introduce(system)

    val printer = system.actorOf(Props[Output], name="result")
    val asyncAnd = system.actorOf(Props(classOf[AsyncAnd], printer)
                                  , name="async_and")
    val creditChecker = system.actorOf(Props(classOf[MockResponder], true, asyncAnd)
                                  , name="credit_checker")
    val addressChecker = system.actorOf(Props(classOf[MockResponder], true, asyncAnd)
                                  , name="address_checker")

    val coordinator = system.actorOf(Props(classOf[CoordinatorActor], asyncAnd, creditChecker, addressChecker)
                                  , name="coordinator")

    coordinator ! "start"

    println("Ctrl-C to halt")
  }
}

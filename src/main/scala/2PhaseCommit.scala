import actoverse._
import akka.actor._
import scala.collection.mutable.ListBuffer

class Coordinator(cohorts: Seq[ActorRef]) extends Actor with DebuggingSupporter {
  @State var responses = ListBuffer[Boolean]()
  val receive: Receive = {
    case "start_2pc" =>
      cohorts.foreach(_ !+ "query")
    case ("agreement", d: Boolean) =>
      responses += d
      if( responses.size == cohorts.size ) {
        if(responses.forall(a => a)) {
          cohorts.foreach(_ !+ "commit")
        } else {
          cohorts.foreach(_ !+ "rollback")
        }
        responses.clear()
      }
  }
}

class Cohort(decision: Boolean) extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case "query" =>
      sender !+ ("agreement", decision)
    case "commit" =>
      sender !+ "commit_ack"
    case "rollback" =>
      sender !+ "rollback_ack"
  }
}

object TwoPhaseCommit {
  def main(args: Array[String]){
    implicit val system = ActorSystem()
    val debuggingSystem = new DebuggingSystem
    debuggingSystem.introduce(system)
    val cohorts = (1 to 3) map { (n: Int) =>
      system.actorOf(Props(classOf[Cohort], true), name=s"cohort-$n")
    }
    val coordinator = system.actorOf(Props(classOf[Coordinator], cohorts),
                                     name="coordinator")
    coordinator ! "start_2pc"

    println("Ctrl-C to halt")
  }
}

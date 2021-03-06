
import akka.actor._

class Coordinator(cohorts: Seq[ActorRef]) extends Actor {
  var responses = List[Boolean]()
  val receive: Receive = {
    case "start_2pc" =>
      cohorts.foreach(_ ! "query")
    case ("agreement", d: Boolean) =>
      responses = d :: responses
      if( responses.size == cohorts.size ) {
        if(responses.forall(a => a)) {
          cohorts.foreach(_ ! "commit")
        } else {
          cohorts.foreach(_ ! "rollback")
        }
        responses = List[Boolean]()
      }
    case _ => ()
  }
}

class Cohort(decision: Boolean) extends Actor {
  val receive: Receive = {
    case "query" =>
      sender ! ("agreement", decision)
    case "commit" =>
      sender ! "commit_ack"
    case "rollback" =>
      sender ! "rollback_ack"
  }
}

object TwoPhaseCommit {
  def main(args: Array[String]){
    implicit val system = ActorSystem("demo")
    val cohorts = (1 to 3) map { (n: Int) =>
      system.actorOf(Props(classOf[Cohort], true), name=s"cohort-$n")
    }
    val coordinator = system.actorOf(Props(classOf[Coordinator], cohorts),
                                     name="coordinator")
    coordinator ! "start_2pc"

    println("Ctrl-C to halt")
  }
}

/***

This example comes from [1].

[1] A. Lienhard, J. Fierz, and O. Nierstrasz, “Flow-centric, back-in-time debugging,” in Lecture Notes in Business Information Processing, 2009, vol. 33 LNBIP, pp. 272–288.

***/


import actoverse._
import akka.actor._
import scala.collection.mutable.ListBuffer

class Bank(bankAccount: ActorRef, inputReader: ActorRef)
  extends Actor with DebuggingSupporter {
  val receive: Receive = {

    case "start" =>
      inputReader !+ "read"
    case InputValue(account) =>
      bankAccount !+ Deposit(account)

    case "ack-deposit" =>
      bankAccount !+ "addInterest"
  }
}

case class Deposit(deposit: Int)

class BankAccount(interestCalculator: ActorRef)
  extends Actor with DebuggingSupporter {
  @State var deposit: Int = -1
  val receive: Receive = {
    case Deposit(d) =>
      deposit = d
      sender !+ "ack-deposit"
    case "addInterest" =>
      interestCalculator !+ ("calculateNewDeposit", deposit, 0.05)
  }
}

case class InputValue(value: Int)
class InputReader extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case "read" =>
      sender !+ InputValue(-1)
  }
}

class InterestCalculator extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case ("calculateNewDeposit", deposit, interest) =>
      print("Boom!")
  }
}


object InterestCalculatorExample {
  def main(args: Array[String]){
    implicit val system = ActorSystem()
    val debuggingSystem = new DebuggingSystem
    debuggingSystem.introduce(system)

    val interestCalculator = system.actorOf(Props[InterestCalculator], name="interest")
    val inputReader = system.actorOf(Props[InputReader], name="inputreader")
    val bankAccount = system.actorOf(Props(classOf[BankAccount], interestCalculator),
                                     name="bankaccount")
    val bank = system.actorOf(Props(classOf[Bank], bankAccount, inputReader),
                                     name="bank")

    bank ! "start"

    println("Ctrl-C to halt")
  }
}

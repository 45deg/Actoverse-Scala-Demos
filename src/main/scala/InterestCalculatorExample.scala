/***

This example comes from [1].

[1] A. Lienhard, J. Fierz, and O. Nierstrasz, “Flow-centric, back-in-time debugging,” in Lecture Notes in Business Information Processing, 2009, vol. 33 LNBIP, pp. 272–288.

***/

import actoverse._
import akka.actor._
import scala.collection.mutable.ListBuffer

@Comprehensive
case class Ack(body: Any)

class Bank(bankAccount: ActorRef, inputReader: ActorRef)
  extends Actor with DebuggingSupporter {
  val receive: Receive = {

    case "start" =>
      inputReader !+ "read"
    case Ack(InputValue(account)) =>
      bankAccount !+ Deposit(account)

    case Ack("deposit") =>
      bankAccount !+ "add_interest"
  }
}

@Comprehensive
case class Deposit(d: Int)

class BankAccount(interestCalculator: ActorRef)
  extends Actor with DebuggingSupporter {
  @State var deposit: Int = -1
  val receive: Receive = {
    case Deposit(d) =>
      deposit = d
      sender !+ Ack("deposit")
    case "add_interest" =>
      interestCalculator !+ DoCaculate(deposit, 0.05)
  }
}

@Comprehensive
case class InputValue(value: Int)

class InputReader extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case "read" =>
      sender !+ Ack(InputValue(-1))
  }
}

@Comprehensive
case class DoCaculate(deposit: Int, interest: Double)

class InterestCalculator extends Actor with DebuggingSupporter {
  val receive: Receive = {
    case DoCaculate(deposit, interest) =>
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

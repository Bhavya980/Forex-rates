package forex.actors

import akka.actor.Actor
import forex.actors.domain.Rates.UpdateRates
import forex.util.OneFrame

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class PeriodicRatesActor extends Actor {

  override def preStart(): Unit = {
    context.system.scheduler.scheduleAtFixedRate(
      initialDelay = 0.seconds,
      interval = 4.minutes,
      receiver = self,
      message = UpdateRates
    )
    ()
  }

  override def receive: Receive = {
    case UpdateRates =>
      OneFrame.updateRatesData()
  }
}

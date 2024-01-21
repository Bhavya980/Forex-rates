package forex.util

import akka.actor.{ActorSystem, Cancellable, Props}
import forex.actors.PeriodicRatesActor
import forex.actors.domain.Rates.UpdateRates

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

object Scheduler {

  val cancellables = ListBuffer[Cancellable]()

  def schedule() = {

    println("running scheduler")

    val system: ActorSystem = ActorSystem("Periodic-actor")

    val periodicRatesActorRef = system.actorOf(Props(new PeriodicRatesActor))
    //Use system's dispatcher as ExecutionContext
    import system.dispatcher

    //This will schedule to send the UpdateRates-message
    //to the PeriodicRatesActor after 0ms repeating every 5 minutes
//    val cancellable =
    system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.minutes, periodicRatesActorRef, UpdateRates)

    //This cancels further UpdateRates to be sent
//    cancellable.cancel()
    ()
  }

}

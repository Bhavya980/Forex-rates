package forex.util

import akka.actor.{ActorSystem, Props}
import forex.actors.PeriodicRatesActor

object Scheduler {

  def schedule(system: ActorSystem) = {
    system.actorOf(Props(new PeriodicRatesActor))
  }

}

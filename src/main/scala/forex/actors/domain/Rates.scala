package forex.actors.domain

sealed trait Rates

object Rates {
  case object UpdateRates extends Rates
}
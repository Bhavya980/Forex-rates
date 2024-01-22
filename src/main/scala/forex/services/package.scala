package forex

import cats.effect.{ConcurrentEffect, ContextShift, IO}
import cats.effect.IO.ioConcurrentEffect

package object services {
  type RatesService[F[_]] = rates.Algebra[F]
  final val RatesServices = rates.Interpreters

  implicit val CS: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
  implicit val CE: ConcurrentEffect[IO] = ioConcurrentEffect
}

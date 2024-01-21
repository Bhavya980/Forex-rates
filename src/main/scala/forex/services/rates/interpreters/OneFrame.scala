package forex.services.rates.interpreters

import cats.Applicative
import cats.effect.IO.ioConcurrentEffect
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import cats.syntax.applicative._
import cats.syntax.either._
import forex.domain.{CurrencyExchange, Price, Rate, Timestamp}
import forex.http.oneframe.OneFrameHttpRoutes
import forex.services.rates.Algebra
import forex.services.rates.errors._


class OneFrame[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    val result = processApiResult().head
    Rate(pair, Price(result.price), Timestamp.now).asRight[Error].pure[F]
  }

  def processApiResult(): List[CurrencyExchange] = {
    implicit val CS: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
    implicit val CE: ConcurrentEffect[IO] = ioConcurrentEffect

    val result: IO[List[CurrencyExchange]] = OneFrameHttpRoutes.getRatesFromOneFrame(CE)

    // Use unsafeRunSync to execute the IO action and get the result
    val actualResult: List[CurrencyExchange] = result.unsafeRunSync()

    actualResult
  }

}

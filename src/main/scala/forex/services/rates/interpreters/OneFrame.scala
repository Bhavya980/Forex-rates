package forex.services.rates.interpreters

import cats.Applicative
import cats.effect.IO.ioConcurrentEffect
import cats.effect.{ConcurrentEffect, ContextShift, IO}
import cats.syntax.applicative._
import cats.syntax.either._
import forex.data.RatesData.rates
import forex.domain.{CurrencyExchange, Price, Rate, Timestamp}
import forex.http.oneframe.OneFrameHttpRoutes
import forex.services.rates.Algebra
import forex.services.rates.errors._


class OneFrame[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    Rate(pair, Price(BigDecimal(100)), Timestamp.now).asRight[Error].pure[F]
  }

}
 object OneFrame {

   def updateRatesData(): Unit = {
     val newRates = getRatesFromOneFrame

     newRates.foreach { newRate =>
       val existingIndex = rates.indexWhere(r => r.from == newRate.from && r.to == newRate.to)

       if(existingIndex != -1) {
         rates.update(existingIndex, newRate)
       } else {
         rates.append(newRate)
       }
     }
   }

   private def getRatesFromOneFrame: List[CurrencyExchange] = {
     implicit val CS: ContextShift[IO] = IO.contextShift(scala.concurrent.ExecutionContext.global)
     implicit val CE: ConcurrentEffect[IO] = ioConcurrentEffect

     val result: IO[List[CurrencyExchange]] = OneFrameHttpRoutes.makeOneFrameAPICall(CE)

     // Use unsafeRunSync to execute the IO action and get the result
     val actualResult: List[CurrencyExchange] = result.unsafeRunSync()

     actualResult
   }

 }
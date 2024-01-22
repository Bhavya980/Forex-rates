package forex.services
package rates.interpreters

import cats.Applicative
import cats.effect.IO
import forex.data.RatesData.rates
import forex.domain.Currency.show
import forex.domain.{CurrencyExchange, Price, Rate, Timestamp}
import forex.http.oneframe.OneFrameHttpRoutes
import forex.services.rates.Algebra
import forex.services.rates.errors._


class OneFrame[F[_]: Applicative] extends Algebra[F] {

  override def get(pair: Rate.Pair): F[Error Either Rate] = {
    rates.find { rate =>
      rate.from == show.show(pair.from) &&
        rate.to == show.show(pair.to)
    } match {
      case Some(rate) =>
        Applicative[F].pure(Right(Rate(pair, Price(rate.price), Timestamp.now)))
      case None =>
        Applicative[F].pure(Left(Error.OneFrameLookupFailed("Could not find rate")))
    }
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
     val result: IO[List[CurrencyExchange]] = OneFrameHttpRoutes.makeOneFrameAPICall(CE)

     // Use unsafeRunSync to execute the IO action and get the result
     val actualResult: List[CurrencyExchange] = result.unsafeRunSync()

     actualResult
   }

 }
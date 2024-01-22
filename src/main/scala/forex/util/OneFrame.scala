package forex.util

import cats.effect.IO
import forex.data.RatesData.rates
import forex.domain.CurrencyExchange
import forex.http.oneframe.OneFrameHttpRoutes
import forex.services.CE

object OneFrame {

  def createUri(host: String, port: String, pairs: List[String]): String = {
    val pairsQueryString = pairs.mkString("&pair=", "&pair=", "")
    s"http://$host:$port/rates?$pairsQueryString"
  }

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

    val actualResult: List[CurrencyExchange] = result.unsafeRunSync()

    actualResult
  }

}

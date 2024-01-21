package forex.data

import forex.domain.CurrencyExchange

import scala.collection.mutable.ListBuffer

object RatesData {

  val rates: ListBuffer[CurrencyExchange] = ListBuffer.empty
}

package forex.util

object Currency {

  def makeAllCurrencyPairs(currencies: List[String]): List[String] = {
    currencies.flatMap { currency =>
      currencies.filter(_ != currency).map(otherValue => (currency + otherValue))
    }
  }

}

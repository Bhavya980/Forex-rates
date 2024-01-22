package forex.services
package rates.interpreters

import cats.Applicative
import forex.data.RatesData.rates
import forex.domain.Currency.show
import forex.domain.{Price, Rate, Timestamp}
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

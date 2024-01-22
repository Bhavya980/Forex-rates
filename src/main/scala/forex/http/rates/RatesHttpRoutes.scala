package forex.http
package rates

import cats.effect.Sync
import cats.syntax.flatMap._
import forex.programs.RatesProgram
import forex.programs.rates.errors.Error
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F]) extends Http4sDsl[F] {

  import Converters._, QueryParams._, Protocol._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root :? MaybeFromQueryParam(from) +& MaybeToQueryParam(to) =>
      val fromCurrencyOpt = from.flatten
      val toCurrencyOpt = to.flatten

      (fromCurrencyOpt, toCurrencyOpt) match {
        case (Some(fromCurrency), Some(toCurrency)) =>
          rates.get(RatesProgramProtocol.GetRatesRequest(fromCurrency, toCurrency)).flatMap { result =>

            result.fold(
              {
                case Error.RateLookupFailed(msg) => BadRequest(msg)
              },
              rate => Ok(rate.asGetApiResponse)
            )
          }

        case _ =>
          BadRequest("Please provide valid 'from' and 'to' currencies as a parameter")
      }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}

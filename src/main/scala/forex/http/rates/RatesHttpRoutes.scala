package forex.http
package rates

import cats.effect.Sync
import cats.implicits._
import forex.config.AppConfig.TokenConfig
import forex.domain.Currency
import forex.programs.RatesProgram
import forex.programs.rates.errors.Error
import forex.programs.rates.{Protocol => RatesProgramProtocol}
import forex.util.TokenEncryption
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.Router
import org.http4s.{HttpRoutes, _}

class RatesHttpRoutes[F[_]: Sync](rates: RatesProgram[F], tokenEncryption: TokenEncryption, tokenConfig: TokenConfig) extends Http4sDsl[F] {

  import Converters._

  private[http] val prefixPath = "/rates"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] { request =>

    val maybeToken: Option[String] = request.headers.get(Authorization.name).map(_.head.value)

    def processRequest: Either[String, F[Response[F]]] = for {
      _ <- Either.cond(request.method == Method.GET, (), "Invalid HTTP method")
      token <- maybeToken.toRight("Authorization header missing")
      decryptedToken <- tokenEncryption.decrypt(token)
      _ <- Either.cond(decryptedToken == tokenConfig.token, (), "Invalid token")
      currencies <- parseFromTo(request)
    } yield fetchAndProcessRates(currencies._1, currencies._2)

    val response = processRequest.fold(
      errorMsg => BadRequest(errorMsg),
      _.map(response => response)
    )

    response
  }

  private def parseFromTo(request: Request[F]): Either[String, (Currency, Currency)] = {
    val fromCurrencyOpt = request.params.get("from").flatMap(Currency.fromString)
    val toCurrencyOpt = request.params.get("to").flatMap(Currency.fromString)

    for {
      from <- fromCurrencyOpt.toRight("Parameter 'from' is missing or invalid")
      to <- toCurrencyOpt.toRight("Parameter 'to' is missing or invalid")
    } yield (from, to)
  }

  private def fetchAndProcessRates(from: Currency, to: Currency): F[Response[F]] = {
    rates.get(RatesProgramProtocol.GetRatesRequest(from, to)).flatMap { result =>
      result.fold(
        {
          case Error.RateLookupFailed(msg) => BadRequest(msg)
        },
        rate => Ok(rate.asGetApiResponse)
      )
    }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}

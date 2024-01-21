package forex.http
package oneframe

import cats.effect.{ConcurrentEffect, IO}
import forex.domain.{Currency, CurrencyExchange}
import io.circe.Json
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.ci.CIString
import forex.domain.Currency.show
import forex.util.Currency.makeAllCurrencyPairs
import forex.util.OneFrame.createUri

import scala.concurrent.ExecutionContext.Implicits.global

object OneFrameHttpRoutes {

  def makeOneFrameAPICall(implicit CE: ConcurrentEffect[IO]): IO[List[CurrencyExchange]] = {

    BlazeClientBuilder[IO](global).resource.use { client =>
      val currencies = makeAllCurrencyPairs(Currency.all.map(show.show))

      for {
        uri <- IO.fromEither(Uri.fromString(createUri("0.0.0.0", "8080", currencies)))

        request = Request[IO](Method.GET, uri, headers = Headers(Header.Raw(CIString("token"), "10dc303535874aeccc86a8251e6992f5")))

        response <- client.expect[Json](request)

        currencyExchange <- IO.fromEither(response.as[List[CurrencyExchange]])

      } yield {
        currencyExchange
      }
    }

  }
}

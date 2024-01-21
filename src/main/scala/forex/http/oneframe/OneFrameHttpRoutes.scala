package forex.http
package oneframe

import cats.effect.{ConcurrentEffect, IO}
import forex.domain.CurrencyExchange
import io.circe.Json
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.typelevel.ci.CIString

import scala.concurrent.ExecutionContext.Implicits.global

object OneFrameHttpRoutes {

  def makeOneFrameAPICall(implicit CE: ConcurrentEffect[IO]): IO[List[CurrencyExchange]] = {
    BlazeClientBuilder[IO](global).resource.use { client =>
      for {
        // Define your API endpoint URI
        uri <- IO.fromEither(Uri.fromString(createUri("0.0.0.0", "8080", "USDJPY")))

        // Create an HTTP request
        request = Request[IO](Method.GET, uri, headers = Headers(Header.Raw(CIString("token"), "10dc303535874aeccc86a8251e6992f5")))

        // Send the request and handle the response
        response <- client.expect[Json](request)

        currencyExchange <- IO.fromEither(response.as[List[CurrencyExchange]])
      } yield {
        currencyExchange
      }
    }
  }

  def createUri(host: String, port: String, pair: String): String = {
    s"http://$host:$port/rates?pair=$pair"
  }
}

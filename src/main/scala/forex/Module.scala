package forex

import cats.effect.{Concurrent, Timer}
import forex.config.{HttpConfig, TokenConfig}
import forex.http.rates.RatesHttpRoutes
import forex.programs._
import forex.services._
import forex.util.TokenEncryption
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.middleware.{AutoSlash, Timeout}

class Module[F[_]: Concurrent: Timer](httpConfig: HttpConfig, tokenConfig: TokenConfig) {

  private val ratesService: RatesService[F] = RatesServices.oneFrame[F]

  private val ratesProgram: RatesProgram[F] = RatesProgram[F](ratesService)

  private val tokenEncryption =  new TokenEncryption(tokenConfig)

  tokenEncryption.encrypt(tokenConfig.token)

  private val ratesHttpRoutes: HttpRoutes[F] = new RatesHttpRoutes[F](ratesProgram, tokenEncryption, tokenConfig).routes

  type PartialMiddleware = HttpRoutes[F] => HttpRoutes[F]
  type TotalMiddleware   = HttpApp[F] => HttpApp[F]

  private val routesMiddleware: PartialMiddleware = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  private val appMiddleware: TotalMiddleware = { http: HttpApp[F] =>
    Timeout(httpConfig.http.timeout)(http)
  }

  private val http: HttpRoutes[F] = ratesHttpRoutes

  val httpApp: HttpApp[F] = appMiddleware(routesMiddleware(http).orNotFound)

}

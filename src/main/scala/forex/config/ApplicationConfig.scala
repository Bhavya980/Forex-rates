package forex.config

import scala.concurrent.duration.FiniteDuration

trait AppConfig

object AppConfig {
  case class HttpConfig(http: Http) extends AppConfig
  case class TokenConfig(token: String, secretKey: String, initializationVector: String) extends AppConfig
}

case class Http(
  host: String,
  port: Int,
  timeout: FiniteDuration
)

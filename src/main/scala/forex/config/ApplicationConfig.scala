package forex.config

import scala.concurrent.duration.FiniteDuration

case class HttpConfig(
  http: Http,
)

case class TokenConfig(
  token: String,
  secretKey: String,
  initializationVector: String
)

case class Http(
  host: String,
  port: Int,
  timeout: FiniteDuration
)

package forex.config

import cats.effect.Sync
import fs2.Stream

import pureconfig.ConfigSource
import pureconfig.generic.auto._

object Config {

  /**
   * @param path the property path inside the default configuration
   */
  def streamHttpConfig[F[_]: Sync](path: String): Stream[F, HttpConfig] = {
    Stream.eval(Sync[F].delay(
      ConfigSource.default.at(path).loadOrThrow[HttpConfig]))
  }

  def streamTokenConfig[F[_]: Sync](path: String): Stream[F, TokenConfig] = {
    Stream.eval(Sync[F].delay(
      ConfigSource.default.at(path).loadOrThrow[TokenConfig]))
  }

}

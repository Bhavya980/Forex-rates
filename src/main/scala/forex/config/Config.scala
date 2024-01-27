package forex.config

import cats.effect.Sync
import fs2.Stream

import pureconfig.ConfigSource
import pureconfig.generic.auto._
import scala.reflect.ClassTag


object Config {
  private def streamConfig[F[_]: Sync, A <: AppConfig](path: String)(
    implicit reader: pureconfig.ConfigReader[A],
    classTag: ClassTag[A]
  ): Stream[F, A] = {
    Stream.eval(Sync[F].delay(
      ConfigSource.default.at(path).loadOrThrow[A]
    ))
  }

  def streamHttpConfig[F[_]: Sync](path: String): Stream[F, AppConfig.HttpConfig] = {
    streamConfig[F, AppConfig.HttpConfig](path)
  }

  def streamTokenConfig[F[_]: Sync](path: String): Stream[F, AppConfig.TokenConfig] = {
    streamConfig[F, AppConfig.TokenConfig](path)
  }
}
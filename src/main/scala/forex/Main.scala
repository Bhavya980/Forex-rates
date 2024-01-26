package forex

import akka.actor.ActorSystem
import cats.effect._
import forex.config._
import forex.util.Scheduler
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder

import scala.concurrent.ExecutionContext

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      system <- IO(ActorSystem("Periodic-actor"))
      _ = Scheduler.schedule(system)
      exitCode <- new Application[IO].stream[IO](executionContext).compile.drain.as(ExitCode.Success)
    } yield exitCode
  }

}

class Application[F[_]] {

  def stream[G[_]: ConcurrentEffect: Timer](ec: ExecutionContext): Stream[G, Unit] =
    for {
      httpConfig <- Config.streamHttpConfig[G]("app")
      tokenConfig <- Config.streamTokenConfig[G]("token")
      module = new Module[G](httpConfig, tokenConfig)
      _ <- BlazeServerBuilder[G](ec)
            .bindHttp(httpConfig.http.port, httpConfig.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()

}

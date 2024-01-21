package forex

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext
import cats.effect._
import forex.config._
import forex.util.Scheduler
import fs2.Stream
import org.http4s.blaze.server.BlazeServerBuilder

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
      config <- Config.stream[G]("app")
      module = new Module[G](config)
      _ <- BlazeServerBuilder[G](ec)
            .bindHttp(config.http.port, config.http.host)
            .withHttpApp(module.httpApp)
            .serve
    } yield ()

}

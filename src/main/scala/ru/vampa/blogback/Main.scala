package ru.vampa.blogback

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run: IO[Unit] = BlogbackServer.run[IO]
}

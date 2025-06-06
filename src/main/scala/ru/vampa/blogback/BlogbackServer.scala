package ru.vampa.blogback

import cats.effect.Async
import com.comcast.ip4s.IpLiteralSyntax
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import ru.vampa.blogback.repository.ArticleRepository
import ru.vampa.blogback.service.ArticleService

object BlogbackServer {

  def run[F[_]: Async: Network]: F[Nothing] = {
    for {
      _ <- EmberClientBuilder.default[F].build
      articleRepo = ArticleRepository.impl
      articleService = ArticleService.impl(articleRepo)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = BlogbackRoutes.articleRoutes[F](articleService).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      _ <- 
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}

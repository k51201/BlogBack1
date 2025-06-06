package ru.vampa.blogback

import cats.effect.Async
import cats.implicits._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, HttpRoutes}
import ru.vampa.blogback.dto.Article
import ru.vampa.blogback.service.ArticleService

object BlogbackRoutes {

  private implicit def longEntityEncoder[F[_]]: EntityEncoder[F, Long] = jsonEncoderOf
  private implicit def stringEntityEncoder[F[_]]: EntityEncoder[F, String] = jsonEncoderOf
  private implicit def articleListEntityEncoder[F[_]]: EntityEncoder[F, Seq[Article]] = jsonEncoderOf


  def articleRoutes[F[_]: Async](articleService: ArticleService[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    HttpRoutes.of[F] {
      case req@PUT -> Root / "api" / "article" =>
        for {
          article <- req.as[Article]
          result <- articleService.addArticle(article)
          resp <- result match {
            case Left(err) => BadRequest(err)
            case Right(id) => Created(id)
          }
        } yield resp
        
      case GET -> Root / "api" / "article" / LongVar(id) =>
        for {
          articleE <- articleService.getArticle(id)
          resp <- articleE match {
            case Left(err) => NotFound(err)
            case Right(article) => Ok(article)
          }
        } yield resp
      case req@POST -> Root / "api" / "article" / LongVar(id) =>
        for {
          article <- req.as[Article]
          result <- articleService.updateArticle(id, article)
          resp <- result match {
            case Left(err) => NotFound(err)
            case Right(_) => Ok()
          }
        } yield resp
      
      case DELETE -> Root / "api" / "article" / LongVar(id) =>
        for {
          result <- articleService.deleteArticle(id)
          resp <- result match {
            case Left(err) => NotFound(err)
            case Right(_) => Ok()
          }
        } yield resp

      case GET -> Root / "api" / "article" =>
        for {
          result <- articleService.listArticles()
          resp <- result match {
            case Left(err) => BadRequest(err)
            case Right(articles) => Ok(articles)
          }
        } yield resp
    }
  }
}
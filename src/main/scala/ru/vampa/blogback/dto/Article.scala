package ru.vampa.blogback.dto

import cats.effect.Concurrent
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

import java.time.ZonedDateTime
import org.http4s.{EntityDecoder, EntityEncoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

final case class Article(
  id: Option[Long],
  authorName: String,
  text: String,
  published: Option[ZonedDateTime],
)

object Article {
  implicit val entityDecoder: Decoder[Article] = deriveDecoder[Article]
  implicit def articleEntityDecoder[F[_]: Concurrent]: EntityDecoder[F, Article] = jsonOf

  implicit val entityEncoder: Encoder[Article] = deriveEncoder[Article]
  implicit def articleEntityEncoder[F[_]]: EntityEncoder[F, Article] = jsonEncoderOf[F, Article]
}

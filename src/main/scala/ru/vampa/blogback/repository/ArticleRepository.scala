package ru.vampa.blogback.repository

import cats.effect.Sync
import ru.vampa.blogback.repository.model.ArticleRecord

trait ArticleRepository[F[_]] {
  type Id = Long
  type Err = String

  def add(article: ArticleRecord): F[Either[Err, Id]]
  def get(id: Id): F[Either[Err, ArticleRecord]]
  def update(id: Id, article: ArticleRecord): F[Either[Err, Unit]]
  def delete(id: Id): F[Either[Err, Unit]]
  def list(): F[Either[Err, Seq[ArticleRecord]]]
}

object ArticleRepository {
  def impl[F[_]: Sync]: ArticleRepository[F] = new ArticleInMemoryRepository[F]
}

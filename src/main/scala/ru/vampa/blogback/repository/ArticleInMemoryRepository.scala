package ru.vampa.blogback.repository

import cats.effect.Sync
import ru.vampa.blogback.repository.model.ArticleRecord
import scala.collection.mutable

class ArticleInMemoryRepository[F[_]: Sync] extends ArticleRepository[F] {
  private val storage = mutable.Map.empty[Id, ArticleRecord]
  private var counter: Id = 0L

  override def add(article: ArticleRecord): F[Either[Err, Id]] = Sync[F].delay {
    counter += 1
    val _ = storage.put(counter, article.copy(id = Some(counter)))
    Right(counter)
  }

  override def get(id: Id): F[Either[Err, ArticleRecord]] = Sync[F].delay {
    storage.get(id).toRight(s"Article with id=$id not found")
  }

  override def update(id: Id, article: ArticleRecord): F[Either[Err, Unit]] = Sync[F].delay {
    if (storage.contains(id)) {
      storage.update(id, article)
      Right(())
    } else Left(s"Article with id=$id not found")
  }

  override def delete(id: Id): F[Either[Err, Unit]] = Sync[F].delay {
    if (storage.contains(id)) {
      val _ = storage.remove(id)
      Right(())
    } else Left(s"Article with id=$id not found")
  }

  override def list(): F[Either[Err, Seq[ArticleRecord]]] = Sync[F].delay {
    Right(storage.values.toSeq)
  }
}

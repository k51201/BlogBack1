package ru.vampa.blogback.service

import cats.effect.Sync
import cats.implicits._
import ru.vampa.blogback.dto.Article
import ru.vampa.blogback.repository.ArticleRepository
import ru.vampa.blogback.repository.model.ArticleRecord

import java.time.{ZoneOffset, ZonedDateTime}

trait ArticleService[F[_]] {
  type Err = String

  def addArticle(article: Article): F[Either[Err, Long]]
  def getArticle(id: Long): F[Either[Err, Article]]
  def updateArticle(id: Long, article: Article): F[Either[Err, Unit]]
  def deleteArticle(id: Long): F[Either[Err, Unit]]
  def listArticles(): F[Either[Err, Seq[Article]]]
}

object ArticleService {
  def impl[F[_]: Sync](articleRepo: ArticleRepository[F]): ArticleService[F] = new ArticleServiceImpl[F](articleRepo)
}

class ArticleServiceImpl[F[_]: Sync](articleRepo: ArticleRepository[F]) extends ArticleService[F] {

  override def addArticle(article: Article): F[Either[Err, Long]] =
    articleRepo.add(ArticleRecord(
      id = None,
      authorName = article.authorName,
      text = article.text,
      published = ZonedDateTime.now(ZoneOffset.UTC)
    ))

  override def getArticle(id: Long): F[Either[Err, Article]] =
    articleRepo.get(id).map(_.map(r => Article(
      id = r.id,
      authorName = r.authorName,
      text = r.text,
      published = Option(r.published)
    )))

  override def updateArticle(id: Long, article: Article): F[Either[Err, Unit]] =
    articleRepo.update(id, ArticleRecord(
      id = Some(id),
      authorName = article.authorName,
      text = article.text,
      published = article.published.getOrElse(ZonedDateTime.now(ZoneOffset.UTC))
    ))

  override def deleteArticle(id: Long): F[Either[Err, Unit]] =
    articleRepo.delete(id)

  override def listArticles(): F[Either[Err, Seq[Article]]] =
    articleRepo.list().map(_.map(_.map(r => Article(
      id = r.id,
      authorName = r.authorName,
      text = r.text,
      published = Option(r.published),
    ))))
}
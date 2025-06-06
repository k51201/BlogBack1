package ru.vampa.blogback.repository.model

import java.time.ZonedDateTime

case class ArticleRecord(
  id: Option[Long],
  authorName: String,
  text: String,
  published: ZonedDateTime,
)

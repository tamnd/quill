package io.getquill.sources

import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration

// import io.getquill.naming.Literal
import io.getquill._

package object couchbase {
  val mirrorSource = source(new CouchbaseMirrorSourceConfig("test"))

  def await[T](f: Future[T]): T = Await.result(f, Duration.Inf)
}
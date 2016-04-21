package io.getquill.sources.couchbase.mirror

import io.getquill.naming.Literal
import io.getquill.sources.couchbase.CouchbaseSource
import io.getquill.sources.mirror.{ MirrorDecoders, MirrorEncoders, Row }
import io.getquill.CouchbaseMirrorSourceConfig
import scala.util.Failure
import scala.util.Success

class CouchbaseMirrorSource(config: CouchbaseMirrorSourceConfig)
  extends CouchbaseSource[Literal, Row, Row]
  with MirrorEncoders
  with MirrorDecoders {

  override def close = ()

  override def probe(n1ql: String) =
    if (n1ql.contains("fail"))
      Failure(new IllegalStateException())
    else
      Success(())

  override type QueryResult[T] = QueryMirror[T]
  override type ActionResult[T] = ActionMirror

  case class ActionMirror(n1ql: String, bind: Row)

  def execute(n1ql: String, bind: Row => Row, generated: Option[String] = None) =
    ActionMirror(n1ql, bind(Row()))

  case class QueryMirror[T](n1ql: String, binds: Row, extractor: Row => T)

  def query[T](n1ql: String, bind: Row => Row, extractor: Row => T) =
    QueryMirror(n1ql, bind(Row()), extractor)
}
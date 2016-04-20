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
}
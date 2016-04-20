package io.getquill.sources.couchbase

import io.getquill.ast._
import io.getquill.naming.NamingStrategy
import io.getquill.util.Show._

object N1qlIdiom {
  implicit def astShow(implicit strategy: NamingStrategy, queryShow: Show[Query]): Show[Ast] = Show[Ast] {
    case _ => "Invalid n1ql"
  }

  implicit def queryShow(implicit strategy: NamingStrategy): Show[Query] = Show[Query] {
    case q => "Query show"
  }
}
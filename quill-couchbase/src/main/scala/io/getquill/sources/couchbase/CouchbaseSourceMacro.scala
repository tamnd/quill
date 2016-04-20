package io.getquill.sources.couchbase

import scala.reflect.macros.whitebox.Context

import io.getquill.ast._
import io.getquill.naming.LoadNaming
// import io.getquill.naming.NamingStrategy
import io.getquill.quotation.IsDynamic
import io.getquill.sources.SourceMacro
import io.getquill.util.Messages.RichContext

class CouchbaseSourceMacro(val c: Context) extends SourceMacro {
  import c.universe.{ Ident => _, _ }

  override protected def prepare(ast: Ast, params: List[Ident]) =
    if (!IsDynamic(ast)) {
      implicit val n = LoadNaming.static(c)(namingType)
      val (n1ql, idents, _) = Prepare(ast, params)
      c.info(n1ql)
      probe(n1ql)
      q"($n1ql, $idents, None)"
    } else {
      c.info("Dynamic query")
      q"""
      {
        implicit val n = ${LoadNaming.dynamic(c)(namingType)}
        io.getquill.sources.cassandra.Prepare($ast, $params)
      }
      """
    }

  private def probe(n1ql: String): Unit = {}

  private def namingType =
    c.prefix.actualType
      //.baseType(c.weakTypeOf[CouchbaseSource[NamingStrategy, Row, BoundStatement]].typeSymbol)
      .typeArgs.head
}
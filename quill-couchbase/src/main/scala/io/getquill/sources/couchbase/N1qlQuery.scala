package io.getquill.sources.couchbase

import io.getquill.ast._
import io.getquill.ast.AstShow._
import io.getquill.util.Messages.fail
import io.getquill.util.Show._

case class N1qlQuery(
  entity:   Entity,
  filter:   Option[Ast],
  orderBy:  List[OrderByCriteria],
  limit:    Option[Ast],
  select:   List[Ast],
  distinct: Boolean
)

case class OrderByCriteria(
  property: Property,
  ordering: PropertyOrdering
)

object N1qlQuery {

  def apply(q: Query): N1qlQuery =
    q match {
      case Distinct(q: Query) =>
        apply(q, true)
      case other =>
        apply(q, false)
    }

  private def apply(q: Query, distinct: Boolean): N1qlQuery =
    q match {
      case Map(q: Query, x, p) =>
        apply(q, select(p), distinct)
      case Aggregation(AggregationOperator.`size`, q: Query) =>
        apply(q, List(Aggregation(AggregationOperator.`size`, Constant(1))), distinct)
      case other =>
        apply(q, List(), distinct)
    }

  private def apply(q: Query, select: List[Ast], distinct: Boolean): N1qlQuery =
    q match {
      case Take(q: Query, limit) =>
        apply(q, Some(limit), select, distinct)
      case other =>
        apply(q, None, select, distinct)
    }

  private def apply(q: Query, limit: Option[Ast], select: List[Ast], distinct: Boolean): N1qlQuery =
    q match {
      case SortBy(q: Query, x, p, o) =>
        apply(q, orderByCriterias(p, o), limit, select, distinct)
      case other =>
        apply(q, List(), limit, select, distinct)
    }

  private def apply(q: Query, orderBy: List[OrderByCriteria], limit: Option[Ast], select: List[Ast], distinct: Boolean): N1qlQuery =
    q match {
      case Filter(q: Query, x, p) =>
        apply(q, Some(p), orderBy, limit, select, distinct)
      case other =>
        apply(q, None, orderBy, limit, select, distinct)
    }

  private def apply(q: Query, filter: Option[Ast], orderBy: List[OrderByCriteria], limit: Option[Ast], select: List[Ast], distinct: Boolean): N1qlQuery =
    q match {
      case q: Entity =>
        new N1qlQuery(q, filter, orderBy, limit, select, distinct)
      case (_: FlatMap) =>
        fail(s"N1ql doesn't support flatMap.")
      case (_: Union) | (_: UnionAll) =>
        fail(s"N1ql doesn't support union/unionAll.")
      case Join(joinType, _, _, _, _, _) =>
        fail(s"N1ql doesn't support ${joinType.show}.")
      case _: GroupBy =>
        fail(s"N1ql doesn't support groupBy.")
      case q =>
        fail(s"Invalid N1ql query: $q")
    }

  private def select(ast: Ast): List[Ast] =
    ast match {
      case Tuple(values)  => values.flatMap(select)
      case p: Property    => List(p)
      case i @ Ident("?") => List(i)
      case i: Ident       => List()
      case other          => fail(s"N1ql supports only properties as select elements. Found: $other")
    }

  private def orderByCriterias(ast: Ast, ordering: Ast): List[OrderByCriteria] =
    (ast, ordering) match {
      case (Tuple(properties), ord: PropertyOrdering) => properties.flatMap(orderByCriterias(_, ord))
      case (Tuple(properties), TupleOrdering(ord))    => properties.zip(ord).flatMap { case (a, o) => orderByCriterias(a, o) }
      case (a: Property, o: PropertyOrdering)         => List(OrderByCriteria(a, o))
      case other                                      => fail(s"Invalid order by criteria $ast")
    }
}

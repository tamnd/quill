package io.getquill.sources.couchbase

import io.getquill._
import io.getquill.naming.Literal
// import io.getquill.sources.couchbase._

class N1qlIdiomSpec extends Spec {

  "query" - {
    "map" in {
      val q = quote {
        qr1.map(t => t.i)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT i FROM TestEntity"
    }
    "take" in {
      val q = quote {
        qr1.take(1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity LIMIT 1"
    }
    "sortBy" in {
      val q = quote {
        qr1.sortBy(t => t.i)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i ASC"
    }
    "all terms" in {
      val q = quote {
        qr1.filter(t => t.i == 1).sortBy(t => t.s).take(1).map(t => t.s)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s FROM TestEntity WHERE i = 1 ORDER BY s ASC LIMIT 1"
    }
    "generated" in {
      val q = quote {
        query[TestEntity](_.generated(_.i))
      }
      "mirrorSource.run(q).n1ql" mustNot compile
    }
  }

  "distinct" - {
    "simple" in {
      val q = quote {
        qr1.distinct
      }
      "mirrorSource.run(q).n1ql" mustNot compile
    }

    "distinct single" in {
      val q = quote {
        qr1.map(i => i.i).distinct
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT DISTINCT i FROM TestEntity"
    }

    "distinct tuple" in {
      val q = quote {
        qr1.map(i => (i.i, i.l)).distinct
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT DISTINCT i, l FROM TestEntity"
    }
  }

  "order by criteria" - {
    "asc" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.asc)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i ASC"
    }
    "desc" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.desc)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i DESC"
    }
    "ascNullsFirst" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.ascNullsFirst)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i ASC"
    }
    "descNullsFirst" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.descNullsFirst)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i DESC"
    }
    "ascNullsLast" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.ascNullsLast)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i ASC"
    }
    "descNullsLast" in {
      val q = quote {
        qr1.sortBy(t => t.i)(Ord.descNullsLast)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity ORDER BY i DESC"
    }
  }

  "operation" - {
    "binary" in {
      val q = quote {
        qr1.filter(t => t.i == 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i = 1"
    }
    "unary (not supported)" in {
      val q = quote {
        qr1.filter(t => !(t.i == 1))
      }
      "mirrorSource.run(q)" mustNot compile
    }
    "function apply (not supported)" in {
      val q = quote {
        qr1.filter(t => infix"f".as[Int => Boolean](t.i))
      }
      "mirrorSource.run(q)" mustNot compile
    }
  }

  "aggregation" - {
    "count" in {
      val q = quote {
        qr1.filter(t => t.i == 1).size
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT COUNT(1) FROM TestEntity WHERE i = 1"
    }
    "invalid" in {
      val q = quote {
        qr1.map(t => t.i).max
      }
      "mirrorSource.run(q)" mustNot compile
    }
  }

  "binary operation" - {
    "==" in {
      val q = quote {
        qr1.filter(t => t.i == 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i = 1"
    }
    "&&" in {
      val q = quote {
        qr1.filter(t => t.i == 1 && t.s == "s")
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i = 1 AND s = 's'"
    }
    ">" in {
      val q = quote {
        qr1.filter(t => t.i > 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i > 1"
    }
    ">=" in {
      val q = quote {
        qr1.filter(t => t.i >= 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i >= 1"
    }
    "<" in {
      val q = quote {
        qr1.filter(t => t.i < 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i < 1"
    }
    "<=" in {
      val q = quote {
        qr1.filter(t => t.i <= 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i <= 1"
    }
    "invalid" in {
      val q = quote {
        qr1.filter(t => t.i * 2 == 4)
      }
      "mirrorSource.run(q)" mustNot compile
    }
  }

  "value" - {
    "string" in {
      val q = quote {
        qr1.filter(t => t.s == "s")
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE s = 's'"
    }
    "unit" in {
      val q = quote {
        qr1.filter(t => t.i == (()))
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i = 1"
    }
    "int" in {
      val q = quote {
        qr1.filter(t => t.i == 1)
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i = 1"
    }
    "tuple" in {
      val q = quote {
        qr1.map(t => (t.i, t.s))
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT i, s FROM TestEntity"
    }
    "collection" in {
      val q = quote {
        qr1.filter(t => List(1, 2).contains(t.i))
      }
      mirrorSource.run(q).n1ql mustEqual
        "SELECT s, i, l, o FROM TestEntity WHERE i IN (1, 2)"
    }
    "null (not supported)" in {
      val q = quote {
        qr1.filter(t => t.s == null)
      }
      "mirrorSource.run(q)" mustNot compile
    }
  }
}

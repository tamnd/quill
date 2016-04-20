package io.getquill

import io.getquill.naming.NamingStrategy
import io.getquill.sources.SourceConfig

abstract class CouchbaseSourceConfig[N <: NamingStrategy, T](val name: String) extends SourceConfig[T] {

}

/*
class CouchbaseAsyncSourceConfig[N <: NamingStrategy](name: String)
  extends CouchbaseSourceConfig[N, CouchbaseAsyncSource[N]](name)

class CouchbaseSyncSourceConfig[N <: NamingStrategy](name: String)
  extends CouchbaseSyncConfig[N, CouchbaseSyncSource[N]](name)
*/ 
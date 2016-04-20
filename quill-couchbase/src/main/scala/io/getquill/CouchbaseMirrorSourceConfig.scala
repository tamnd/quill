package io.getquill

import io.getquill.sources.SourceConfig
import io.getquill.sources.couchbase.mirror.CouchbaseMirrorSource

class CouchbaseMirrorSourceConfig(val name: String) extends SourceConfig[CouchbaseMirrorSource]
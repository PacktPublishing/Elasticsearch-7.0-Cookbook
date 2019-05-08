package com.packtpub

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.mappings.IndexMappings

object MappingExample extends App with ElasticSearchClientTrait {
  val indexName = "myindex"
  if (client.execute { indexExists(indexName) }.await.result.isExists) {
    client.execute { deleteIndex(indexName) }.await
  }

  client.execute {
    createIndex(indexName) shards 1 replicas 0 mappings (
      mapping("_doc") as (
        textField("name").termVector("with_positions_offsets").stored(true)
      )
    )
  }.await
  Thread.sleep(2000)

  client.execute {
    putMapping(indexName / "_doc").as(
      keywordField("tag")
    )
  }.await

  val myMapping = client
    .execute {
      getMapping(indexName / "_doc")
    }
    .await
    .result

  val tagMapping = myMapping.seq.head
  println(tagMapping)


  client.execute(deleteIndex(indexName)).await

  //we need to close the client to free resources
  client.close()
}

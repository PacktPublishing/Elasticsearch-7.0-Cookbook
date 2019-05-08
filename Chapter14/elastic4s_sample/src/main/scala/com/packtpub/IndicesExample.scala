package com.packtpub
import com.sksamuel.elastic4s.http.ElasticDsl._

object IndicesExample extends App with ElasticSearchClientTrait {
  val indexName = "test"
  if (client.execute { indexExists(indexName) }.await.result.isExists) {
    client.execute { deleteIndex(indexName) }.await
  }

  client.execute {
    createIndex(indexName) shards 1 replicas 0 mappings (
      mapping("_doc") as (
        textField("name").termVector("with_positions_offsets").stored(true),
        keywordField("tag")
      )
    )
  }.await

  Thread.sleep(2000)
  client.execute(forceMerge(indexName)).await
  client.execute(closeIndex(indexName)).await
  client.execute(openIndex(indexName)).await
  client.execute(deleteIndex(indexName)).await

  client.close()
}

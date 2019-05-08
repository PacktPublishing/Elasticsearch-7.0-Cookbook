package com.packtpub

import com.sksamuel.elastic4s.HealthStatus
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}

trait ElasticSearchClientTrait {
  val client: ElasticClient = {
    ElasticClient(ElasticProperties("http://127.0.0.1:9200"))
  }

  def ensureIndexMapping(indexName: String,
                         mappingName: String = "_doc"): Unit = {
    if (client
          .execute {
            indexExists(indexName)
          }
          .await
          .result
          .isExists) {
      client.execute {
        deleteIndex(indexName)
      }.await
    }

    client.execute {
      createIndex(indexName) shards 1 replicas 0 mappings (
        mapping(mappingName).as(
          textField("name") termVector "with_positions_offsets" stored true,
          longField("size"),
          doubleField("price"),
          geopointField("location"),
          keywordField("tag") stored true
        )
      )
    }.await

    client.execute {
      clusterHealth waitForStatus HealthStatus.Yellow
    }

  }

  def populateSampleData(indexName: String,
                         mappingName: String,
                         size: Int = 1000): Unit = {
    import scala.util.Random
    val tags = List("cool", "nice", "bad", "awesome", "good")
    client.execute {
      bulk(0.to(size).map { i =>
        indexInto(indexName / mappingName)
          .id(i.toString)
          .fields(
            "name" -> s"name_${i}",
            "size" -> (i % 10) * 8,
            "price" -> (i % 10) * 1.2,
            "location" -> List(30.0 * Random.nextDouble(),
                               30.0 * Random.nextDouble()),
            "tag" -> Random.shuffle(tags).take(3)
          )
      }: _*)
    }.await

    Thread.sleep(2000)

  }
}

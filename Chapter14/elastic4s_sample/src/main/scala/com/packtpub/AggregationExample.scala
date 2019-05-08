package com.packtpub

import com.sksamuel.elastic4s.http.ElasticDsl._

object AggregationExample extends App with ElasticSearchClientTrait {
  val indexName = "myindex"
  val typeName = "_doc"
  ensureIndexMapping(indexName, typeName)
  populateSampleData(indexName, typeName, 1000)

  val resp = client
    .execute {
      search(indexName) size 0 aggregations (termsAggregation("tag") field "tag" size 100 subAggregations (
        extendedStatsAggregation("price") field "price", extendedStatsAggregation(
          "size") field "size", geoBoundsAggregation("centroid") field "location"
      ))
    }
    .await
    .result

  val tagsAgg = resp.aggregations.terms("tag")

  println(s"Result Hits: ${resp.size}")
  println(s"number of tags: ${tagsAgg.buckets.size}")
  println(
    s"max price of first tag ${tagsAgg.buckets.head.key}: ${tagsAgg.buckets.head.extendedStats("price").max}")
  println(
    s"min size of first tag ${tagsAgg.buckets.head.key}: ${tagsAgg.buckets.head.extendedStats("size").min}")
//println(s"center of first tag ${tagsAgg.getBuckets.head.getKey}: ${tagsAgg.getBuckets.head.getAggregations.get[InternalGeoCentroid]("centroid").centroid()}")

  client.execute(deleteIndex(indexName)).await

  client.close()
}

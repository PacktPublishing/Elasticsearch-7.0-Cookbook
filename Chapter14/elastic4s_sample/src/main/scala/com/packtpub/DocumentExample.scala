package com.packtpub

import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.circe._

object DocumentExample extends App with ElasticSearchClientTrait {
  val indexName = "myindex"

  ensureIndexMapping(indexName)

  client.execute {
    indexInto(indexName / "_doc") id "0" fields (
      "name" -> "brown",
      "tag" -> List("nice", "simple")
    )
  }.await

  val bwn = client.execute {
    get("0") from indexName
  }.await

  println(bwn.result.sourceAsString)

  client.execute {
    update("0").in(indexName).script("ctx._source.name = 'red'")
  }.await

  val red = client.execute {
    get("0") from indexName
  }.await

  println(red.result.sourceAsString)

  client.execute {
    delete("0") from indexName
  }.await

  case class Place(id: Int, name: String)
  case class Cafe(name: String, place: Place)

  import io.circe.generic.auto._
  import com.sksamuel.elastic4s.Indexable
  implicitly[Indexable[Cafe]]

  val cafe = Cafe("nespresso", Place(20, "Milan"))

  client.execute {
    indexInto(indexName).id(cafe.name).source(cafe)
  }.await

  client.execute(deleteIndex(indexName)).await

  client.close()
}

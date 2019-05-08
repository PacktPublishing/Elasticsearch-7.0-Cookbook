import elasticsearch

es = elasticsearch.Elasticsearch()

index_name = "my_index"

if es.indices.exists(index_name):
    es.indices.delete(index_name)

es.indices.create(index_name)

es.cluster.health(wait_for_status="yellow")

es.indices.close(index_name)

es.indices.open(index_name)

es.cluster.health(wait_for_status="yellow")

es.indices.forcemerge(index_name)

es.indices.delete(index_name)


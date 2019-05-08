import elasticsearch
from pprint import pprint

es = elasticsearch.Elasticsearch()
index_name = "my_index"
type_name = "_doc"

if es.indices.exists(index_name):
    es.indices.delete(index_name)

from code.utils import create_and_add_mapping, populate

create_and_add_mapping(es, index_name)
populate(es, index_name)

results = es.search(index_name, type_name,
                    { "size":0,
                        "aggs": {
                            "pterms": {"terms": {"field": "name", "size": 10}}
                        }
                    })
pprint(results)

results = es.search(index_name, type_name,
                    { "size":0,
                        "aggs": {
                            "date_histo": {"date_histogram": {"field": "date", "interval": "month"}}
                        }
                    })
pprint(results)

es.indices.delete(index_name)

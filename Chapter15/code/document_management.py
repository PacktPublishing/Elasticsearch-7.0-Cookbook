import elasticsearch
from datetime import datetime

es = elasticsearch.Elasticsearch()

index_name = "my_index"
type_name = "_doc"

if es.indices.exists(index_name):
    es.indices.delete(index_name)

from code.utils import create_and_add_mapping
create_and_add_mapping(es, index_name)

es.index(index=index_name, id=1,
                 body={"name": "Joe Tester", "parsedtext": "Joe Testere nice guy", "uuid": "11111",
                       "position": 1,
                       "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})
es.index(index=index_name, id="1.1",
                 body={"name": "data1", "value": "value1", "join_field": {"name": "metadata", "parent": "1"}},
                 routing=1)
es.index(index=index_name, id=2,
                 body={"name": "Bill Baloney", "parsedtext": "Bill Testere nice guy", "uuid": "22222",
                       "position": 2,
                       "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})
es.index(index=index_name, id="2.1",
                 body={"name": "data2", "value": "value2", "join_field": {"name": "metadata", "parent": "2"}},
                 routing=2)
es.index(index=index_name, id=3, body={"name": "Bill Clinton", "parsedtext": """Bill is not
nice guy""", "uuid": "33333", "position": 3, "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})

es.update(index=index_name, doc_type=type_name, id=2, body={"script": 'ctx._source.position += 1'})

document=es.get(index=index_name, doc_type=type_name, id=2)
print(document)

es.delete(index=index_name, doc_type=type_name, id=3)

from elasticsearch.helpers import bulk
bulk(es, [
    {"_index":index_name, "_type":type_name, "_id":"1", "source":{"name": "Joe Tester", "parsedtext": "Joe Testere nice guy", "uuid": "11111", "position": 1,
               "date": datetime(2018, 12, 8)}},

    {"_index": index_name, "_type": type_name, "_id": "1",
     "source": {"name": "Bill Baloney", "parsedtext": "Bill Testere nice guy", "uuid": "22222", "position": 2,
               "date": datetime(2018, 12, 8)}}
])

es.indices.delete(index_name)

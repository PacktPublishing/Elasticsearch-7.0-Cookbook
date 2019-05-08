__author__ = 'alberto'

import elasticsearch

# client using all the defaults: localhost:9200 and http urllib3 transport
es = elasticsearch.Elasticsearch()

# client using localhost:9200 and http requests transport
from elasticsearch.connection import RequestsHttpConnection

es = elasticsearch.Elasticsearch(sniff_on_start=True, connection_class=RequestsHttpConnection)

# client using two nodes
es = elasticsearch.Elasticsearch(["search1:9200", "search2:9200"])

# client using a node with sniffing
es = elasticsearch.Elasticsearch("localhost:9200", sniff_on_start=True)


curl -XDELETE "http://localhost:9200/mybooks"

curl -XPUT "http://localhost:9200/mybooks" -H 'Content-Type: application/json' -d'
{
  "mappings": {
      "properties": {
        "join_field": {
          "type": "join",
          "relations": {
            "order": "item"
          }
        },
        "position": {
          "type": "integer",
          "store": true
        },
        "uuid": {
          "store": true,
          "type": "keyword"
        },
        "date": {
          "type": "date"
        },
        "quantity": {
          "type": "integer"
        },
        "price": {
          "type": "double"
        },
        "description": {
          "term_vector": "with_positions_offsets",
          "store": true,
          "type": "text"
        },
        "title": {
          "term_vector": "with_positions_offsets",
          "store": true,
          "type": "text",
          "fielddata": true,
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        }
      }
    }
}'

curl -XPOST "http://localhost:9200/_bulk?refresh" -H 'Content-Type: application/json' -d'
{"index":{"_index":"mybooks", "_id":"1"}}
{"uuid":"11111","position":1,"title":"Joe Tester","description":"Joe Testere nice guy","date":"2015-10-22","price":4.3,"quantity":50}
{"index":{"_index":"mybooks", "_id":"2"}}
{"uuid":"22222","position":2,"title":"Bill Baloney","description":"Bill Testere nice guy","date":"2016-06-12","price":5,"quantity":34}
{"index":{"_index":"mybooks", "_id":"3"}}
{"uuid":"33333","position":3,"title":"Bill Klingon","description":"Bill is not\n                nice guy","date":"2017-09-21","price":6,"quantity":33}
'

curl -XDELETE "http://localhost:9200/mybooks-join"

curl -XPUT "http://localhost:9200/mybooks-join" -H 'Content-Type: application/json' -d'
{
  "mappings": {
      "properties": {
        "join": {
          "type": "join",
          "relations": {
            "book": "author"
          }
        },
        "position": {
          "type": "integer",
          "store": true
        },
        "uuid": {
          "store": true,
          "type": "keyword"
        },
        "date": {
          "type": "date"
        },
        "quantity": {
          "type": "integer"
        },
        "price": {
          "type": "double"
        },
        "rating": {
          "type": "double"
        },
        "description": {
          "term_vector": "with_positions_offsets",
          "store": true,
          "type": "text"
        },
        "title": {
          "term_vector": "with_positions_offsets",
          "store": true,
          "type": "text",
          "fielddata": true,
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "name": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "surname": {
          "type": "text",
          "fields": {
            "keyword": {
              "type": "keyword",
              "ignore_above": 256
            }
          }
        },
        "versions": {
          "type": "nested",
          "properties": {
            "color": {
              "type": "keyword"
            },
            "size": {
              "type": "integer"
            }
          }
        }
      }
    }
}'


curl -XPOST "http://localhost:9200/_bulk?refresh" -H 'Content-Type: application/json' -d'
{"index":{"_index":"mybooks-join", "_id":"1"}}
{"uuid":"11111","position":1,"title":"Joe Tester","description":"Joe Testere nice guy","date":"2015-10-22","price":4.3,"quantity":50,"join": {"name": "book"}, "versions":[{"color":"yellow", "size":5},{"color":"blue", "size":15}]}
{"index":{"_index":"mybooks-join", "_id":"a1", "routing":"1"}}
{"name":"Peter","surname":"Doyle","rating":4.5,"join": {"name": "author", "parent":"1"}}
{"index":{"_index":"mybooks-join", "_id":"a12", "routing":"1"}}
{"name":"Mark","surname":"Twain","rating":4.2,"join": {"name": "author", "parent":"1"}}
{"index":{"_index":"mybooks-join", "_id":"2"}}
{"uuid":"22222","position":2,"title":"Bill Baloney","description":"Bill Testere nice guy","date":"2016-06-12","price":5,"quantity":34,"join": {"name": "book"}, "versions":[{"color":"red", "size":2},{"color":"blue", "size":10}]}
{"index":{"_index":"mybooks-join", "_id":"a2", "routing":"2"}}
{"name":"Agatha","surname":"Princeton","rating":2.1,"join": {"name": "author", "parent":"2"}}
{"index":{"_index":"mybooks-join", "_id":"3"}}
{"uuid":"33333","position":3,"title":"Bill Klingon","description":"Bill is not\n                nice guy","date":"2017-09-21","price":6,"quantity":33,"join": {"name": "book"}, "versions":[{"color":"red", "size":2}]}
{"index":{"_index":"mybooks-join", "_id":"a3", "routing":"3"}}
{"name":"Martin","surname":"Twisted","rating":3.2,"join": {"name": "author", "parent":"3"}}
'

curl -XPOST "http://localhost:9200/mybooks-join/_refresh"

curl -XDELETE 'http://localhost:9200/mygeo-index'
curl -XPUT "http://localhost:9200/mygeo-index" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "pin": {
        "properties": {
          "location": {
            "type": "geo_point"
          }
        }
      }
    }
  }
}'

curl -XPUT 'http://localhost:9200/mygeo-index/_doc/1' -H 'Content-Type: application/json' -d '{"pin": {"location": {"lat": 40.12, "lon": -71.34}}}'
curl -XPUT 'http://localhost:9200/mygeo-index/_doc/2' -H 'Content-Type: application/json' -d '{"pin": {"location": {"lat": 40.12, "lon": 71.34}}}'
curl -XPOST 'http://localhost:9200/mygeo-index/_refresh'

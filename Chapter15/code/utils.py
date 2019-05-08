__author__ = 'alberto'
from datetime import datetime


def create_and_add_mapping(connection, index_name):
    try:
        connection.indices.create(index_name)
    except:
        # we skip exception if index already exists
        pass
    connection.cluster.health(wait_for_status="yellow")


    connection.indices.put_mapping(index=index_name, body={"properties": {
        "join_field": {
            "type": "join",
            "relations": {
                "book": "metadata"
            }
        },
        "uuid": {"type": "keyword", "store": True},
        "title": {"type": "text", "store": True, "term_vector": "with_positions_offsets"},
        "parsedtext": {"type": "text", "store": True, "term_vector": "with_positions_offsets", "fielddata": "true"},
        "nested": {"type": "nested", "properties": {"num": {"type": "integer", "store": True},
                                                    "name": {"type": "keyword", "store": True},
                                                    "value": {"type": "keyword", "store": True}}},
        "date": {"type": "date", "store": True},
        "position": {"type": "integer", "store": True},
        "value": {"type": "text"},
        "name": {"type": "text", "store": True, "term_vector": "with_positions_offsets", "fielddata": "true"}}})


def populate(connection, index_name):
    connection.index(index=index_name, id=1,
                     body={"name": "Joe Tester", "parsedtext": "Joe Testere nice guy", "uuid": "11111",
                           "position": 1,
                           "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})
    connection.index(index=index_name, id="1.1",
                     body={"name": "data1", "value": "value1", "join_field": {"name": "metadata", "parent": "1"}},
                     routing=1)
    connection.index(index=index_name, id=2,
                     body={"name": "Bill Baloney", "parsedtext": "Bill Testere nice guy", "uuid": "22222",
                           "position": 2,
                           "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})
    connection.index(index=index_name, id="2.1",
                     body={"name": "data2", "value": "value2", "join_field": {"name": "metadata", "parent": "2"}},
                     routing=2)
    connection.index(index=index_name, id=3, body={"name": "Bill Clinton", "parsedtext": """Bill is not
    nice guy""", "uuid": "33333", "position": 3, "date": datetime(2018, 12, 8), "join_field": {"name": "book"}})

    connection.indices.refresh(index_name)

package com.packtpub;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class QueryCreation {

    public static void main(String[] args) throws IOException {
        String index = "mytest";
        String type = "mytype";
        RestHighLevelClient client = RestHighLevelClientHelper.createHighLevelClient();
        IndicesOperations io = new IndicesOperations(client);
        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);
            try {
                client.indices().create(
                        new CreateIndexRequest()
                                .index(index)
                                .mapping(type, XContentFactory.jsonBuilder()
                                        .startObject()
                                        .startObject(type)
                                        .startObject("properties")
                                        .startObject("text").field("type", "integer").field("store", "true").endObject()
                                        .startObject("number1").field("type", "integer").field("store", "true").endObject()
                                        .startObject("number2").field("type", "integer").field("store", "true").endObject()
                                        .endObject()
                                        .endObject()
                                        .endObject()),
                        RequestOptions.DEFAULT
                );
            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

            BulkRequest bulker = new BulkRequest();
            for (int i = 1; i < 1000; i++) {
                bulker.add(new IndexRequest(index, type, Integer.toString(i)).source("text", Integer.toString(i), "number1", i + 1, "number2", i % 2));
            }

            client.bulk(bulker, RequestOptions.DEFAULT);
            client.indices().refresh(new RefreshRequest(index), RequestOptions.DEFAULT);

            TermQueryBuilder filter = termQuery("number2", 1);
            RangeQueryBuilder range = rangeQuery("number1").gt(500);
            BoolQueryBuilder query = boolQuery().must(range).filter(filter);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query);
            SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response.getHits().getTotalHits());

            SearchResponse response2 = client.search(new SearchRequest().indices(index), RequestOptions.DEFAULT);
            System.out.println("Matched records of elements: " + response2.getHits().getTotalHits());

            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //we need to close the client to free resources
            client.close();
        }


    }
}

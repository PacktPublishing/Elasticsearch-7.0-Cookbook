package com.packtpub;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class QueryExample {
    public static void main(String[] args) throws IOException {
        String index = "mytest";
        String type = "mytype";
        QueryHelper qh = new QueryHelper();
        qh.populateData(index, type);
        RestHighLevelClient client = qh.getClient();

        QueryBuilder query = boolQuery().must(rangeQuery("number1").gte(500)).filter(termQuery("number2", 1));

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query).highlighter(new HighlightBuilder().field("name"));
        SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        if (response.status().getStatus() == 200) {
            System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
            System.out.println("Maximum score: " + response.getHits().getMaxScore());

            for (SearchHit hit : response.getHits().getHits()) {
                System.out.println("hit: " + hit.getIndex() + ":" + hit.getType() + ":" + hit.getId());
            }
        }
        qh.dropIndex(index);


        //we need to close the client to free resources
        client.close();

    }
}

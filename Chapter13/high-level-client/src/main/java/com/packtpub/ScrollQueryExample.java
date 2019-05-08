package com.packtpub;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.*;

public class ScrollQueryExample {
    public static void main(String[] args) throws IOException {
        String index = "mytest";
        String type = "mytype";
        QueryHelper qh = new QueryHelper();
        qh.populateData(index, type);
        RestHighLevelClient client = qh.getClient();

        QueryBuilder query = boolQuery().must(rangeQuery("number1").gte(500)).filter(termQuery("number2", 1));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(query).size(30);
        SearchRequest searchRequest = new SearchRequest()
                .indices(index).source(searchSourceBuilder)
                .scroll(TimeValue.timeValueMinutes(2));
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

        do {
            for (SearchHit hit : response.getHits().getHits()) {
                System.out.println("hit: " + hit.getIndex() + ":" + hit.getType() + ":" + hit.getId());
            }
            response = client.scroll(new SearchScrollRequest(response.getScrollId()).scroll(TimeValue.timeValueMinutes(2)), RequestOptions.DEFAULT);
        } while (response.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.


        qh.dropIndex(index);

        //we need to close the client to free resources
        client.close();

    }
}

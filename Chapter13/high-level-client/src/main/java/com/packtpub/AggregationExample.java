package com.packtpub;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.ExtendedStats;
import org.elasticsearch.search.aggregations.metrics.ExtendedStatsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.extendedStats;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

public class AggregationExample {
    public static void main(String[] args) throws IOException {
        String index = "mytest";
        String type = "mytype";
        QueryHelper qh = new QueryHelper();
        qh.populateData(index, type);
        RestHighLevelClient client = qh.getClient();
        AggregationBuilder aggsBuilder = terms("tag").field("tag");
        ExtendedStatsAggregationBuilder aggsBuilder2 = extendedStats("number1").field("number1");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQuery()).aggregation(aggsBuilder).
                aggregation(aggsBuilder2).size(0);
        SearchRequest searchRequest = new SearchRequest().indices(index).source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);


        if (response.status().getStatus() == 200) {
            System.out.println("Matched number of documents: " + response.getHits().getTotalHits());
            Terms termsAggs = response.getAggregations().get("tag");
            System.out.println("Aggregation name: " + termsAggs.getName());
            System.out.println("Aggregation total: " + termsAggs.getBuckets().size());
            for (Terms.Bucket entry : termsAggs.getBuckets()) {
                System.out.println(" - " + entry.getKey() + " " + entry.getDocCount());
            }

            ExtendedStats extStats = response.getAggregations().get("number1");
            System.out.println("Aggregation name: " + extStats.getName());
            System.out.println("Count: " + extStats.getCount());
            System.out.println("Min: " + extStats.getMin());
            System.out.println("Max: " + extStats.getMax());
            System.out.println("Standard Deviation: " + extStats.getStdDeviation());
            System.out.println("Sum of Squares: " + extStats.getSumOfSquares());
            System.out.println("Variance: " + extStats.getVariance());

        }
        qh.dropIndex(index);

        //we need to close the client to free resources
        client.close();

    }
}

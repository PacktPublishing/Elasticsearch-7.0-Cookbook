package com.packtpub;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class InitClientExample {
    public static void main(String[] args) throws IOException {
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        RestClientBuilder restClient = RestClient.builder(httpHost);
        RestHighLevelClient client = new RestHighLevelClient(restClient);

        //we need to close the client to free resources
        client.close();

    }
}

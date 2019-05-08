package com.packtpub;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class PopulateIndex {
    private static final Logger logger = getLogger(PopulateIndex.class);
    public boolean checkIndexExists(RestHighLevelClient client, String name) throws IOException {
        return client.indices().exists(new GetIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void createIndex(RestHighLevelClient client, String name) throws IOException {
        client.indices().create(new CreateIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void deleteIndex(RestHighLevelClient client, String name) throws IOException {
        client.indices().delete(new DeleteIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void populateIndex() throws IOException {
        HttpHost httpHost = new HttpHost("localhost", 9200, "http");
        RestClientBuilder restClient = RestClient.builder(httpHost);
        RestHighLevelClient client = new RestHighLevelClient(restClient);

        String indexName="iris";
        if(!checkIndexExists(client, indexName))
            createIndex(client, indexName);

        InputStreamReader bReader = new InputStreamReader(
                getClass().getResourceAsStream("/" + "iris.txt"));

        BulkRequest bulker = new BulkRequest();

        try (CSVReader reader = new CSVReader(bReader, ',', '\"'))
        {

            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) {
                return;
            }
            int i=0;
            for(i=0; i<rows.size(); i++) {
                String[] line=rows.get(i);
                Map<String, Object> source=new HashMap<>(5);
                source.put("f1", Float.valueOf(line[0]) );
                source.put("f2", Float.valueOf(line[1]));
                source.put("f3", Float.valueOf(line[2]));
                source.put("f4", Float.valueOf(line[3]));
                source.put("label", Integer.valueOf(line[4]));

                bulker.add(new IndexRequest(indexName, "_doc", Integer.toString(i))
                        .source(source));

            }
            client.bulk(bulker, RequestOptions.DEFAULT);

        }
        catch (IOException e)
        {
            logger.warn("Error parsing CSV file", e);

            throw new IllegalStateException(e);
        }

        //we need to close the client to free resources
        client.close();
    }



    public static void main(String[] args) throws  IOException {
        PopulateIndex pop=new PopulateIndex();
        pop.populateIndex();
    }
}

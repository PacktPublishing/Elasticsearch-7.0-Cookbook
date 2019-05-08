package com.packtpub;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Priority;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.Random;

public class QueryHelper {
    private final RestHighLevelClient client;
    private final IndicesOperations io;

    public QueryHelper() {
        this.client = RestHighLevelClientHelper.createHighLevelClient();
        io = new IndicesOperations(client);
    }

    private String[] tags = new String[]{"nice", "cool", "bad", "amazing"};

    private String getTag() {
        return tags[new Random().nextInt(tags.length)];
    }

    public void populateData(String index, String type) throws IOException {
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
                                    .startObject("name")
                                    .field("type", "text")
                                    .field("term_vector", "with_positions_offsets")
                                    .field("store", "true")
                                    .endObject()
                                    .startObject("tag")
                                    .field("type", "keyword")
                                    .endObject()
                                    .endObject()
                                    .endObject()
                                    .endObject()),
                    RequestOptions.DEFAULT
            );
        } catch (IOException e) {
            System.out.println("Unable to create mapping");
        }
        client.cluster().health(new ClusterHealthRequest().waitForEvents(Priority.LANGUID).waitForGreenStatus(), RequestOptions.DEFAULT);

        BulkRequest bulker = new BulkRequest();
        for (int i = 1; i < 1000; i++) {
            bulker.add(new IndexRequest(index, type, Integer.toString(i)).source("text", Integer.toString(i), "number1", i + 1, "number2", i % 2, "tag", getTag()));
        }
        System.out.println("Number of actions for index: " + bulker.numberOfActions());

        client.bulk(bulker, RequestOptions.DEFAULT);

        client.indices().refresh(new RefreshRequest().indices(index), RequestOptions.DEFAULT);

    }

    public void dropIndex(String index) throws IOException {
        io.deleteIndex(index);
    }

    public RestHighLevelClient getClient() {
        return client;
    }
}

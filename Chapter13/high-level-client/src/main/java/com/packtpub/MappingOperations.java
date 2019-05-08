package com.packtpub;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class MappingOperations {

    public static void main(String[] args) {
        String index = "mytest";
        String type = "mytype";
        RestHighLevelClient client = RestHighLevelClientHelper.createHighLevelClient();
        IndicesOperations io = new IndicesOperations(client);
        try {
            if (io.checkIndexExists(index))
                io.deleteIndex(index);
            io.createIndex(index);
            XContentBuilder builder = null;
            try {
                builder = jsonBuilder().
                        startObject().
                        field("type1").
                        startObject().
                        field("properties").
                        startObject().
                        field("nested1").
                        startObject().
                        field("type").
                        value("nested").
                        endObject().
                        endObject().
                        endObject().
                        endObject();
                AcknowledgedResponse response = client.indices()
                        .putMapping(new PutMappingRequest(index).type(type).source(builder), RequestOptions.DEFAULT);
                if (!response.isAcknowledged()) {
                    System.out.println("Something strange happens");
                }

            } catch (IOException e) {
                System.out.println("Unable to create mapping");
            }

            io.deleteIndex(index);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            //we need to close the client to free resources
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

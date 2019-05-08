package com.packtpub;

import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class IndicesOperations {
    private final RestHighLevelClient client;

    public IndicesOperations(RestHighLevelClient client) {
        this.client = client;
    }

    public boolean checkIndexExists(String name) throws IOException {
        return client.indices().exists(new GetIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void createIndex(String name) throws IOException {
        client.indices().create(new CreateIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void deleteIndex(String name) throws IOException {
        client.indices().delete(new DeleteIndexRequest(name), RequestOptions.DEFAULT);
    }

    public void closeIndex(String name) throws IOException {
        client.indices().close(new CloseIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void openIndex(String name) throws IOException {
        client.indices().open(new OpenIndexRequest().indices(name), RequestOptions.DEFAULT);
    }

    public void putMapping(String index, String typeName, String source) throws IOException {
        client.indices().putMapping(new PutMappingRequest(index).type(typeName).source(source), RequestOptions.DEFAULT);
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        RestHighLevelClientHelper nativeClient = new RestHighLevelClientHelper();
        RestHighLevelClient client = nativeClient.getClient();
        IndicesOperations io = new IndicesOperations(client);
        String myIndex = "test";
        if (io.checkIndexExists(myIndex))
            io.deleteIndex(myIndex);
        io.createIndex(myIndex);
        Thread.sleep(1000);
        io.closeIndex(myIndex);
        io.openIndex(myIndex);
        io.deleteIndex(myIndex);

        //we need to close the client to free resources
        nativeClient.close();

    }
}

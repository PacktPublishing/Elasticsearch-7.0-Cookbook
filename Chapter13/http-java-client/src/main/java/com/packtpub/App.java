package com.packtpub;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class App {

    private static String wsUrl = "http://127.0.0.1:9200";


    public static void main(String[] args) {
        CloseableHttpClient client = HttpClients.custom()
                .setRetryHandler(new MyRequestRetryHandler()).build();
        HttpGet method = new HttpGet(wsUrl + "/mybooks/_doc/1");
        // Execute the method.


        try {
            CloseableHttpResponse response = client.execute(method);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.err.println("Method failed: " + response.getStatusLine());
            } else {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                System.out.println(responseBody);
            }

        } catch (IOException e) {
            System.err.println("Fatal transport error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
    }
}

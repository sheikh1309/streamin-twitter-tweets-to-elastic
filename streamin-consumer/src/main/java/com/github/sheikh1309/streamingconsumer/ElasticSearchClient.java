package com.github.sheikh1309.streamingconsumer;

import org.apache.http.HttpHost;
import org.apache.kafka.common.protocol.types.Field;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class ElasticSearchClient {

    private RestHighLevelClient restHighLevelClient;

    public ElasticSearchClient(String hostName, int port, String schema) throws IOException {
        this.createElasticClient(hostName, port, schema);
//        IndexRequest indexRequest = new IndexRequest("twitter","tweets").source(json, XContentType.JSON);
//        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    private void createElasticClient(String hostName, int port, String schema) {
        RestClientBuilder restClientBuilder = RestClient.builder(this.getHttpHost(hostName, port, schema));
        this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    private HttpHost getHttpHost(String hostName, int port, String schema) {
        return new HttpHost(hostName, port, schema);
    }
}

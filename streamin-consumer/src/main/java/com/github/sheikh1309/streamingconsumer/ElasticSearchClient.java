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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticSearchClient {

    private RestHighLevelClient restHighLevelClient;
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchClient.class.getName());

    public ElasticSearchClient(String hostName, int port, String schema) {
        this.createElasticClient(hostName, port, schema);
    }

    private void createElasticClient(String hostName, int port, String schema) {
        RestClientBuilder restClientBuilder = RestClient.builder(this.getHttpHost(hostName, port, schema));
        this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost getHttpHost(String hostName, int port, String schema) {
        return new HttpHost(hostName, port, schema);
    }

    public void pushData(String index, String type, String content) throws IOException {
        IndexRequest indexRequest = this.getIndexRequest(index, type, content);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        logger.info(indexResponse.getId() + " has been pushed to elastic");
    }

    private IndexRequest getIndexRequest(String index, String type, String content) {
        return new IndexRequest(index, type).source(content, XContentType.JSON);
    }
}

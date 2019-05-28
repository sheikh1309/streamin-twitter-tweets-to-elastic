package com.github.sheikh1309.streamingconsumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
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
    private JsonParser jsonParser;
    private BulkRequest bulkRequest = new BulkRequest();

    public ElasticSearchClient(String hostName, int port, String schema) {
        this.createElasticClient(hostName, port, schema);
        this.jsonParser = new JsonParser();

    }

    private void createElasticClient(String hostName, int port, String schema) {
        RestClientBuilder restClientBuilder = RestClient.builder(this.getHttpHost(hostName, port, schema));
        this.restHighLevelClient = new RestHighLevelClient(restClientBuilder);
    }

    private HttpHost getHttpHost(String hostName, int port, String schema) {
        return new HttpHost(hostName, port, schema);
    }

    public void pushAllDataFromBulk() {
        BulkResponse bulkResponse = null;
        try {
            bulkResponse = this.restHighLevelClient.bulk(this.bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(bulkResponse.getItems().length + " Pushed to Elastic");

    }

    public void addDataToBulkRequest(String index, String type, String content) {
        String elasticObjectID = this.getContentID(content);
        if(elasticObjectID != null) {
            IndexRequest indexRequest = this.getIndexRequest(index, type, content, elasticObjectID);
            this.bulkRequest.add(indexRequest);
        }
    }

    private IndexRequest getIndexRequest(String index, String type, String content, String elasticObjectID) {
        return new IndexRequest(index, type, elasticObjectID).source(content, XContentType.JSON);
    }

    private String getContentID(String content) {
        try {
            return this.jsonParser.parse(content).getAsJsonObject().get("id_str").getAsString();
        } catch (NullPointerException e) {
            return null;
        }
    }
}

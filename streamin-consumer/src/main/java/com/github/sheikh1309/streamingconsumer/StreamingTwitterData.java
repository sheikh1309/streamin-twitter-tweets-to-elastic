package com.github.sheikh1309.streamingconsumer;

import io.github.cdimascio.dotenv.Dotenv;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class StreamingTwitterData {

    private RestHighLevelClient restHighLevelClient;
    private static Logger logger = LoggerFactory.getLogger(StreamingTwitterData.class.getName());

    public StreamingTwitterData() throws IOException {

        Dotenv dotenv = Dotenv.load();
        String hostName = dotenv.get("elasticHostName");
        String schema = dotenv.get("elasticConnectionSchema");
        int port = Integer.parseInt(Objects.requireNonNull(dotenv.get("elasticPort")));
        ElasticSearchClient elasticSearchClient = new ElasticSearchClient(hostName, port, schema);
        this.restHighLevelClient = elasticSearchClient.getRestHighLevelClient();
    }
}

package com.github.sheikh1309.streamingconsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;

public class StreamingTwitterData {

    private RestHighLevelClient restHighLevelClient;
    ElasticSearchClient elasticSearchClient;
    private static Logger logger = LoggerFactory.getLogger(StreamingTwitterData.class.getName());

    public StreamingTwitterData(String elasticHostName,
                                int elasticPort,
                                String elasticConnectionSchema,
                                String streamingServer,
                                String streamingTopic ) throws IOException {

        this.elasticSearchClient = new ElasticSearchClient(elasticHostName, elasticPort, elasticConnectionSchema);
        StreamingConsumer streamingConsumer = new StreamingConsumer(streamingServer);
        KafkaConsumer<String, String> kafkaConsumer = streamingConsumer.getKafkaConsumer();
        kafkaConsumer.subscribe(Arrays.asList(streamingTopic));
        String index = "twitter", type = "tweets";
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
            for( ConsumerRecord<String, String> record : consumerRecords) {
                this.elasticSearchClient.pushData(index, type, record.value());
            }
        }

    }
}

package com.github.sheikh1309.streamingconsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class StreamingTwitterData implements Runnable{

    private RestHighLevelClient restHighLevelClient;
    ElasticSearchClient elasticSearchClient;
    private static Logger logger = LoggerFactory.getLogger(StreamingTwitterData.class.getName());
    private KafkaConsumer<String, String> kafkaConsumer;
    private CountDownLatch countDownLatch;

    public StreamingTwitterData(String elasticHostName,
                                int elasticPort,
                                String elasticConnectionSchema,
                                String streamingServer,
                                String streamingTopic,
                                CountDownLatch countDownLatch){

        this.elasticSearchClient = new ElasticSearchClient(elasticHostName, elasticPort, elasticConnectionSchema);
        StreamingConsumer streamingConsumer = new StreamingConsumer(streamingServer);
        this.kafkaConsumer = streamingConsumer.getKafkaConsumer();
        kafkaConsumer.subscribe(Arrays.asList(streamingTopic));
        this.countDownLatch = countDownLatch;

    }

    @Override
    public void run() {
        String index = "twitter", type = "tweets";
        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = this.kafkaConsumer.poll(Duration.ofMillis(100));
                for( ConsumerRecord<String, String> record : consumerRecords) {
                    this.elasticSearchClient.addDataToBulkRequest(index, type, record.value());
                }
                if (consumerRecords.count() > 0) {
                    this.elasticSearchClient.pushAllDataFromBulk();
                    this.kafkaConsumer.commitAsync();
                }
            }
        } catch (WakeupException e) {
            logger.error("Error WakeupException " + e);
        } finally {
            kafkaConsumer.close();
            countDownLatch.countDown();
        }
    }

    public void shutdown() {
        this.kafkaConsumer.wakeup();
    }
}

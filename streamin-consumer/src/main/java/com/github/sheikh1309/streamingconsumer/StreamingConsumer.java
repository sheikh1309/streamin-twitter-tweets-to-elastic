package com.github.sheikh1309.streamingconsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public class StreamingConsumer {

    private KafkaConsumer<String, String> kafkaConsumer;

    public StreamingConsumer(String streamingServerUrl) {
        this.createStreamingConsumer(streamingServerUrl);
    }

    private void createStreamingConsumer(String streamingServerUrl) {
        Properties properties = this.getProperties(streamingServerUrl);
        this.kafkaConsumer = new KafkaConsumer<String, String>(properties);
    }

    private Properties getProperties(String streamingServerUrl) {
        Properties properties = new Properties();
        String groupID = "first-group-id";
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, streamingServerUrl);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return properties;
    }

    public KafkaConsumer<String, String> getKafkaConsumer() {
        return this.kafkaConsumer;
    }
}

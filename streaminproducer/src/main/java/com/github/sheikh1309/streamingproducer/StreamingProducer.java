package com.github.sheikh1309.streamingproducer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class StreamingProducer {

    private KafkaProducer<String, String> streamingProducer;

    public StreamingProducer(String streamingServerUrl) {
        this.createStreamingProducer(streamingServerUrl);
    }

    private void createStreamingProducer(String streamingServerUrl) {
        Properties properties = this.getProperties(streamingServerUrl);
        this.streamingProducer = new KafkaProducer<String, String>(properties);
    }

    private Properties getProperties(String streamingServerUrl) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, streamingServerUrl);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024)); // 32kb batch size
        return properties;
    }

    public ProducerRecord<String, String> getProducerRecord(String topic, String recordContent ) {
        return new ProducerRecord<String, String>(topic, recordContent);
    }

    public KafkaProducer<String, String> getStreamingProducer() {
        return streamingProducer;
    }
}

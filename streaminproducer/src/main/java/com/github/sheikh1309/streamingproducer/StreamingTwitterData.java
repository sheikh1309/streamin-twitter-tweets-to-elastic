package com.github.sheikh1309.streamingproducer;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.Client;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class StreamingTwitterData {

    private static Logger logger = LoggerFactory.getLogger(StreamingTwitterData.class.getName());
    private Dotenv dotenv = Dotenv.load();
    private StreamingProducer streamingProducer;
    private Client client;
    private BlockingQueue contentQueue;
    private KafkaProducer<String ,String> producer;

    public StreamingTwitterData() {
        this.streamingProducer = new StreamingProducer(this.dotenv.get("streamingServer"));
        List<String> terms = Lists.newArrayList("twitter", "api");
        TwitterClient twitterClient = new TwitterClient(terms);
        this.client = twitterClient.getClient();
        this.client.connect();
        this.contentQueue = twitterClient.getMsgQueue();
        this.producer = this.streamingProducer.getStreamingProducer();
        this.shutDownHook();
    }

    public void startStreaming() {

        while (!this.client.isDone()) {
            String content = null;
            try {
                content = (String) this.contentQueue.poll(5 , TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.client.stop();
            }
            logger.info(content);
            this.produceDataToStreamingServer(content);
        }
    }

    private void produceDataToStreamingServer(String content) {
        this.producer.send(streamingProducer.getProducerRecord(dotenv.get("streamingTopic"), content), getCallBack());
    }

    private static Callback getCallBack() {
        return (RecordMetadata recordMetadata, Exception e) -> {
            if(e != null) {
                logger.error("Error ", e);
            }
        };
    }

    private void shutDownHook() {
        Runtime.getRuntime().addShutdownHook( new Thread(() -> {
            logger.info("Stopping Application");
            logger.info("Shutting Down Twitter Client");
            this.client.stop();
            logger.info("Shutting Down Streaming Producer");
            this.producer.close();
        } ));
    }
}

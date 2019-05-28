import com.github.sheikh1309.streamingconsumer.StreamingConsumer;
import com.github.sheikh1309.streamingconsumer.StreamingTwitterData;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args){
        Dotenv dotenv = Dotenv.load();

        String elasticHostName = dotenv.get("elasticHostName");
        String elasticConnectionSchema = dotenv.get("elasticConnectionSchema");
        int elasticPort = Integer.parseInt(Objects.requireNonNull(dotenv.get("elasticPort")));
        String streamingServer = dotenv.get("streamingServer");
        String streamingTopic =  dotenv.get("streamingTopic");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Runnable streamingTwitterData = new StreamingTwitterData(elasticHostName,
                                                                 elasticPort,
                                                                 elasticConnectionSchema,
                                                                 streamingServer,
                                                                 streamingTopic,
                                                                 countDownLatch);
        shutDownHook((StreamingTwitterData) streamingTwitterData);
        Thread thread = new Thread(streamingTwitterData);
        thread.start();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void shutDownHook(StreamingTwitterData streamingTwitterData) {
        Runtime.getRuntime().addShutdownHook( new Thread(() -> {
            ((StreamingTwitterData) streamingTwitterData).shutdown();
        } ));
    }
}

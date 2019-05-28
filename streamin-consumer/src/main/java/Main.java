import com.github.sheikh1309.streamingconsumer.StreamingTwitterData;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();

        String elasticHostName = dotenv.get("elasticHostName");
        String elasticConnectionSchema = dotenv.get("elasticConnectionSchema");
        int elasticPort = Integer.parseInt(Objects.requireNonNull(dotenv.get("elasticPort")));
        String streamingServer = dotenv.get("streamingServer");
        String streamingTopic =  dotenv.get("streamingTopic");

        new StreamingTwitterData(elasticHostName, elasticPort, elasticConnectionSchema, streamingServer, streamingTopic);
    }

}

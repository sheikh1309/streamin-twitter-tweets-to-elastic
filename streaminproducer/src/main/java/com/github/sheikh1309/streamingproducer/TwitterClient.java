package com.github.sheikh1309.streamingproducer;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterClient {

    private String consumerKey;
    private String consumerSecret;
    private String token;
    private String tokenSecret;
    private List<String> terms;
    private Client client;
    private BlockingQueue<String> msgQueue;

    public TwitterClient(List<String> terms) {

        Dotenv dotenv = Dotenv.load();
        this.consumerKey = dotenv.get("consumerKey");
        this.consumerSecret = dotenv.get("consumerSecret");
        this.token = dotenv.get("token");
        this.tokenSecret = dotenv.get("tokenSecret");
        this.terms = terms;
        this.client = getTwitterClient();

    }

    private Client getTwitterClient() {
        this.msgQueue = new LinkedBlockingQueue<String>(100000);
        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(this.terms);

        Authentication authentication = new OAuth1(this.consumerKey, this.consumerSecret, this.token, this.tokenSecret);
        ClientBuilder builder = getClientBuilder(hosts, authentication, endpoint);
        return builder.build();
    }

    private ClientBuilder getClientBuilder(Hosts hosts,
                                           Authentication authentication,
                                           StatusesFilterEndpoint endpoint) {
        return new ClientBuilder()
                .hosts(hosts)
                .authentication(authentication)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(this.msgQueue));
    }

    public Client getClient() {
        return this.client;
    }

    public BlockingQueue<String> getMsgQueue() {
        return this.msgQueue;
    }
}

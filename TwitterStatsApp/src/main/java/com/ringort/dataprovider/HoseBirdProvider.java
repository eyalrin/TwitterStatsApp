package com.ringort.dataprovider;

import com.google.common.collect.Lists;
import com.ringort.consts.ConnectionConsts;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class HoseBirdProvider extends AbstractProvider {

    @Override
    public void startReading(List<String> cities) {
        Thread thread = new Thread(() -> {
            // Create an appropriately sized blocking queue
            BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);

            // Define our endpoint: By default, delimited=length is set (we need this for our processor)
            // and stall warnings are on.
            StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
            List<String> terms = Lists.newArrayList("twitter", "api"); // I have to give some filtering, otherwise the API returns no messages
            endpoint.trackTerms(terms);
            endpoint.languages(Lists.newArrayList("en"));

            ArrayList<Location> locations = getLocations(cities);
            if (locations.size() > 0) {
                endpoint.locations(locations);
            }

            endpoint.stallWarnings(false);

            Authentication auth = new OAuth1(ConnectionConsts.oAuthConsumerKey, ConnectionConsts.oAuthConsumerSecret,
                    ConnectionConsts.oAuthAccessToken, ConnectionConsts.oAuthAccessTokenSecret);

            // Create a new BasicClient. By default gzip is enabled.
            BasicClient client = new ClientBuilder()
                    .name("TwitterStatsApp")
                    .hosts(Constants.STREAM_HOST)
                    .endpoint(endpoint)
                    .authentication(auth)
                    .processor(new StringDelimitedProcessor(queue))
                    .build();

            // Establish a connection
            client.connect();

            while (!client.isDone()) {
                String msg = null;
                try {
                    msg = queue.poll(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (msg != null) {
                    handleTweet(msg);
                }
            }

            client.stop();
        });

        thread.start();
    }

    public void handleTweet(String tweet) {

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(tweet);
            Object text = jsonObject.get("text");
            handleText(text);
        } catch (ParseException e) {
            System.out.println("Response from provider is missing needed data");
        }
    }
}

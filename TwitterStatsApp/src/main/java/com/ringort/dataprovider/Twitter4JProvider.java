package com.ringort.dataprovider;

import com.ringort.consts.ConnectionConsts;
import com.twitter.hbc.core.endpoint.Location;
import twitter4j.*;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class Twitter4JProvider extends AbstractProvider {

    @Override
    public void startReading(List<String> cities) {

        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice arg) {
            }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
            }
            @Override
            public void onStallWarning(StallWarning warning) {
            }
            @Override
            public void onStatus(Status status) {
                handleText(status.getText());
            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }
        };

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(ConnectionConsts.oAuthConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(ConnectionConsts.oAuthConsumerSecret);
        configurationBuilder.setOAuthAccessToken(ConnectionConsts.oAuthAccessToken);
        configurationBuilder.setOAuthAccessTokenSecret(ConnectionConsts.oAuthAccessTokenSecret);
        OAuthAuthorization auth = new OAuthAuthorization(configurationBuilder.build());
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance(auth);

        FilterQuery tweetFilterQuery = null;
        ArrayList<Location> locations = getLocations(cities);
        double[][] locArr = new  double[locations.size() * 2][];
        if (locations.size() > 0) {
            int i = 0;
            tweetFilterQuery = new FilterQuery();
            tweetFilterQuery.language("en");
            for (Location location : locations) {
                double[] locSouth = {location.southwestCoordinate().longitude(), location.southwestCoordinate().latitude()};
                double[] locNorth = {location.northeastCoordinate().longitude(), location.northeastCoordinate().latitude()};
                locArr[i] = locSouth;
                i++;
                locArr[i] = locNorth;
                i++;
            }
            tweetFilterQuery.locations(locArr);
        }

        twitterStream.addListener(listener);
        if (locations.size() > 0) {
            twitterStream.filter(tweetFilterQuery);
        } else {
            twitterStream.sample("en");
        }
    }
}

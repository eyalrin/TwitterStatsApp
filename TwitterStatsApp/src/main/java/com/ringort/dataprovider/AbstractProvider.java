package com.ringort.dataprovider;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.endpoint.Location;
import javafx.util.Pair;

import java.util.*;

public abstract class AbstractProvider {
    protected Map<String, Pair<Location.Coordinate, Location.Coordinate>> citiesCoordinates;

    private long odd;
    private long even;


    public AbstractProvider() {
        // Note - can be replaced with an external source lke google maps, used consts for simplicity
        citiesCoordinates = new HashMap<>();
        citiesCoordinates.put("San Francisco", new Pair<>(new Location.Coordinate(-122.75, 36.8), new Location.Coordinate(-121.75, 37.8)));
        citiesCoordinates.put("New York", new Pair<>(new Location.Coordinate(-74, 40), new Location.Coordinate(-73, 41)));
        citiesCoordinates.put("Chicago", new Pair<>(new Location.Coordinate(-88.41, 41), new Location.Coordinate(-86.87, 42.21)));
    }

    public abstract void startReading(List<String> cities);

    public void handleText(Object text) {
        if (text != null) {
            String tweetText = text.toString();
            if (tweetText != null && tweetText.length() > 0) {
                StringTokenizer tokens = new StringTokenizer(tweetText);
                if (tokens.countTokens() % 2 == 0) {
                    even++;
                } else {
                    odd++;
                }
            }
        }
    }

    public float calcPercentage() {
        if (even > 0 || odd > 0) {
            long total = even + odd;
            return ((float) odd / ((float) total)) * 100;
        }
        return 0;
    }

    public void printPercentage() {
        float percentage = calcPercentage();
        System.out.println(String.format("\nOdd percentage: %.2f", percentage));
    }

    protected ArrayList<Location> getLocations(List<String> selectedLocations) {
        ArrayList<Location> locations = Lists.newArrayList();
        for (String selectedLocation : selectedLocations) {
            Pair<Location.Coordinate, Location.Coordinate> pair = citiesCoordinates.get(selectedLocation);
            if (pair != null) {
                locations.add(new Location(pair.getKey(), pair.getValue()));
            }
        }
        return locations;
    }
}

package com.ringort.application;

import com.google.common.collect.Lists;
import com.ringort.dataprovider.AbstractProvider;
import com.ringort.dataprovider.Twitter4JProvider;

public class StatisticsApplication {
    public static void main(String[] args) {
        AbstractProvider provider = new Twitter4JProvider();
        provider.startReading(Lists.newArrayList("San Francisco", "New York", "Chicago"));

        while (true) {
            try {
                Thread.sleep(2000);
                provider.printPercentage();
            } catch (InterruptedException e) {
                System.out.println("A General Error has occurred");
            }
        }
    }
}

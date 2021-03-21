package com.ringort.daatprovider.unittest;

import com.google.common.collect.Lists;
import com.ringort.dataprovider.AbstractProvider;
import com.ringort.dataprovider.HoseBirdProvider;
import org.junit.Assert;
import org.junit.Test;

public class HoseBirdTests {

    @Test
    public void testPercentageCalcNormal() {
        AbstractProvider provider = new HoseBirdProvider();
        provider.handleText("Trump is president");
        provider.handleText("Trump is a good president");
        provider.handleText("Trump is a bad president");
        provider.handleText("Clinton was a president");
        provider.handleText("Bush was a president");
        provider.handleText("Washington was a president");
        provider.handleText("Outbrain is in Natanya");

        Assert.assertEquals(42.857143, provider.calcPercentage(), 0.000001);
    }

    @Test
    public void testPercentageCalcNoTweets() {
        AbstractProvider provider = new HoseBirdProvider();
        Assert.assertEquals(0.0, provider.calcPercentage(), 0.000001);
    }

    // Not asserting, just showing that no failure
    @Test
    public void testNoCities() {
        AbstractProvider provider = new HoseBirdProvider();
        provider.startReading(Lists.newArrayList());

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(2000);
                provider.printPercentage();
            } catch (InterruptedException e) {
                System.out.println("A General Error has occurred");
            }

        }
    }

    // Not asserting, just showing that no failure
    @Test
    public void testInvalidCity() {
        AbstractProvider provider = new HoseBirdProvider();
        provider.startReading(Lists.newArrayList("Fdsfcsd", "New York", "Chicago"));

        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(2000);
                provider.printPercentage();
            } catch (InterruptedException e) {
                System.out.println("A General Error has occurred");
            }

        }
    }
}

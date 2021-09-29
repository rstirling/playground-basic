package org.example.fhir.intermediate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SampleClientIntermediateTest {

    private SampleClientIntermediate sampleClientIntermediate;

    @Before
    public void setup() {
        sampleClientIntermediate = new SampleClientIntermediate();
    }

    @Test
    public void testGetCached() {
        final long time = sampleClientIntermediate.getPatientResponseTime("SMITH", true);
        Assert.assertTrue("response time should be bigger than 0", time > 0);
    }

    @Test
    public void testGetNoCached() {
        final long time = sampleClientIntermediate.getPatientResponseTime("SMITH", false);
        Assert.assertTrue("response time should be bigger than 0", time > 0);
    }

    @Test
    public void testGetCachedMultiple() throws IOException {

        final List<String> names = sampleClientIntermediate.getNamesFromFile();

        Assert.assertEquals(20, names.size());

        //For the sake of simplicity the "isPresent()" was not called from Optional average().
        final double firstRound = names.stream()
                .mapToLong(n -> sampleClientIntermediate.getPatientResponseTime(n, true))
                .average()
                .getAsDouble();

        final double secondRound = names.stream()
                .mapToLong(n -> sampleClientIntermediate.getPatientResponseTime(n, true))
                .average()
                .getAsDouble();

        final double thirdRound = names.stream()
                .mapToLong(n -> sampleClientIntermediate.getPatientResponseTime(n, false))
                .average()
                .getAsDouble();

        Assert.assertTrue("firstRound should be bigger than secondRound", firstRound > secondRound);
        Assert.assertTrue("thirdRound should be bigger than secondRound", thirdRound > secondRound);
    }

    @Test
    public void testGetNamesFromFile() throws IOException {
        final List<String> namesFromFile = sampleClientIntermediate.getNamesFromFile();
        Assert.assertFalse(namesFromFile.isEmpty());
        Assert.assertEquals(20, namesFromFile.size());
    }
}
package org.example.fhir.basic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.example.fhir.intermediate.TimeInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheControlTest {

    private TimeInterceptor timeInterceptor;
    private final FhirContext fhirContext = FhirContext.forR4();
    private final IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

    @Before
    public void setup() {
        timeInterceptor = new TimeInterceptor();
        client.registerInterceptor(new LoggingInterceptor(false));
        client.registerInterceptor(timeInterceptor);
    }

    //Assert that no-cached responses are slower than cached
    @Test
    public void testNoCachePerformance() throws IOException {

        final Path path = Paths.get("src/test/resources/Names.csv");
        final List<String> names = Arrays.asList(Files.readAllLines(path).get(0).split(","));

        final Map<Integer, Long> results = new HashMap<>();

        int round = 0;

        CacheControlDirective cacheControl = new CacheControlDirective().setNoCache(false);

        while (round < 3) {

            if (round == 2) {
                cacheControl = new CacheControlDirective().setNoCache(true);
            }

            client.search()
                    .forResource("Patient")
                    .where(Patient.FAMILY.contains().values(names))
                    .returnBundle(Bundle.class)
                    .cacheControl(cacheControl)
                    .execute();

            final long elapsedTime = timeInterceptor.getElapsedTimeAndRestart();
            results.put(round, elapsedTime);
            round++;
        }

        Assert.assertTrue("Round[0] is longer than Round[1]", results.get(0) > results.get(1));
        Assert.assertTrue("Round[2] is longer than Round[1]", results.get(2) > results.get(1));
    }

    //Assert that no-cached responses are slower than cached
    @Test
    public void testNoCachePerformanceWith20Loops() throws IOException {

        final Path path = Paths.get("src/test/resources/Names.csv");
        final List<String> names = Arrays.asList(Files.readAllLines(path).get(0).split(","));

        final Map<Integer, Double> results = new HashMap<>();

        int round = 0;

        while (round < 3) {

            final CacheControlDirective cache = new CacheControlDirective().setNoCache(false);
            double average = names.stream()
                    .mapToLong(n -> {
                        client.search()
                                .forResource("Patient")
                                .where(Patient.FAMILY.matches().values(n))
                                .returnBundle(Bundle.class)
                                .cacheControl(cache)
                                .execute();
                        return timeInterceptor.getElapsedTimeAndRestart();
                    })
                    .average()
                    .orElseThrow(IllegalStateException::new);

            if (round == 2) {
                final CacheControlDirective noCache = new CacheControlDirective().setNoCache(true);
                average = names.stream()
                        .mapToLong(n -> {
                            client.search()
                                    .forResource("Patient")
                                    .where(Patient.FAMILY.matches().values(n))
                                    .returnBundle(Bundle.class)
                                    .cacheControl(noCache)
                                    .execute();
                            return timeInterceptor.getElapsedTimeAndRestart();
                        })
                        .average()
                        .orElseThrow(IllegalStateException::new);
            }

            results.put(round, average);
            round++;
        }

        Assert.assertTrue("Round[0] is longer than Round[1]", results.get(0) > results.get(1));
        Assert.assertTrue("Round[2] is longer than Round[1]", results.get(2) > results.get(1));
    }

}
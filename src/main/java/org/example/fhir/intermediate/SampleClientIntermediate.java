package org.example.fhir.intermediate;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class SampleClientIntermediate {

    private final FhirContext fhirContext = FhirContext.forR4();
        private final IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
//    private final IGenericClient client = fhirContext.newRestfulGenericClient("https://lforms-fhir.nlm.nih.gov/baseR4"); //Alternative
    private final TimeInterceptor timeInterceptor;

    public SampleClientIntermediate() {
        client.registerInterceptor(new LoggingInterceptor(false));
        timeInterceptor = new TimeInterceptor();
        client.registerInterceptor(timeInterceptor);
    }

    /**
     * Gets a list of patient family names from a CVS file - ref. resources/Names.cvs
     *
     * @return list of Names
     * @throws IOException if there is an error reading file
     */
    public List<String> getNamesFromFile() throws IOException {
        final Path path = Paths.get("src/main/resources/Names.csv");
        return Arrays.asList(Files.readAllLines(path).get(0).split(","));
    }

    /**
     * Gets the response time for a patient search call.
     *
     * @param patientFamilyName the patient family name to be searched
     * @param cached            defines if no-cache header parameter should be used on requests
     * @return time in millis
     */
    public long getPatientResponseTime(String patientFamilyName, boolean cached) {

        final CacheControlDirective cache = new CacheControlDirective().setNoCache(!cached);

        client.search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().values(patientFamilyName))
                .returnBundle(Bundle.class)
                .cacheControl(cache)
                .execute();

        return timeInterceptor.getElapsedTimeAndRestart();
    }

}
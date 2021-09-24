package org.example.fhir.basic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class SampleClientBasic {

    private static final Logger log = LoggerFactory.getLogger(SampleClientBasic.class);

    private final FhirContext fhirContext = FhirContext.forR4();
    //    private final IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
    private final IGenericClient client = fhirContext.newRestfulGenericClient("https://lforms-fhir.nlm.nih.gov/baseR4");

    public SampleClientBasic() {
        client.registerInterceptor(new LoggingInterceptor(false));
    }

    /**
     * Returns a list of patients filtered by family name and sorted by given name
     *
     * @param familyName
     * @return list of Patients
     */
    public List<Patient> getPatients(String familyName) {

        // Search for Patient resources
        final SortSpec familyNameSortSpec = new SortSpec(Patient.GIVEN.getParamName());

        final Bundle response = client
                .search()
                .forResource("Patient")
                .where(Patient.FAMILY.matches().value(familyName))
                .sort(familyNameSortSpec)
                .returnBundle(Bundle.class)
                .execute();

        //Printing Patients info
        response.getEntry()
                .stream()
                .map(e -> (Patient) e.getResource())
                .forEach(p -> log.info("Patient - fistName[{}], lastName[{}] and DoB[{}]",
                        p.getName().get(0).getGiven().get(0).getValue(),
                        p.getName().get(0).getFamily(),
                        DateTimeFormatter.ofPattern("yyyy").format(p.getBirthDate().toInstant().atZone(ZoneId.systemDefault()))
                ));

        return response.getEntry().stream()
                .map(e -> (Patient) e.getResource())
                .collect(Collectors.toList());
    }

}
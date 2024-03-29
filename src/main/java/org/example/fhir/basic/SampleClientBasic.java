package org.example.fhir.basic;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SampleClientBasic {

    private static final Logger log = LoggerFactory.getLogger(SampleClientBasic.class);

    private final FhirContext fhirContext = FhirContext.forR4();
    private final IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");
//    private final IGenericClient client = fhirContext.newRestfulGenericClient("https://lforms-fhir.nlm.nih.gov/baseR4");

    public SampleClientBasic() {
        client.registerInterceptor(new LoggingInterceptor(false));
    }

    /**
     * Returns a list of patients filtered by family name and sorted by given name
     *
     * @param familyName patient's family name
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

        return response.getEntry()
                .stream()
                .map(e -> (Patient) e.getResource())
                .peek(p -> log.info("Patient - fistName[{}], lastName[{}] and DoB[{}]",
                        p.getNameFirstRep().getGivenAsSingleString(),
                        p.getNameFirstRep().getFamily(),
                        getDoB(p)))
                .collect(Collectors.toList());
    }

    /**
     * Sorts patients by their given names using HumanName.getGivenAsSingleString()
     *
     * @param patients List of Patients
     * @return list of Patients sorted by given names
     */
    public List<Patient> getPatientsSortedByGivenNames(List<Patient> patients) {

        return patients.stream()
                .sorted(Comparator.comparing(Patient::getNameFirstRep,
                        Comparator.comparing(HumanName::getGivenAsSingleString,
                                Comparator.comparing(String::toLowerCase))))
                .collect(Collectors.toList());
    }

    private String getDoB(Patient p) {
        return p.getBirthDate() != null ? DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .format(p.getBirthDate().toInstant().atZone(ZoneId.systemDefault())) : "Unknown";
    }

}
package org.example.fhir.basic;

import org.hl7.fhir.r4.model.Patient;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SampleClientBasicTest {

    private SampleClientBasic sampleClientBasic;

    @Before
    public void setup() {
        sampleClientBasic = new SampleClientBasic();
    }

    @Test
    public void getPatientsTest() {

        final List<Patient> patients = sampleClientBasic.getPatients("SMITH");

        assertFalse(patients.isEmpty());

        final String firstPatientGivenName = patients.get(0).getNameFirstRep().getGivenAsSingleString();
        final String secondPatientGivenName = patients.get(1).getNameFirstRep().getGivenAsSingleString();

        /*
         * I noticed that the backend is returning a [James A] before [Aaliyah]. James A has an array of given names
         * that are not being considered by the sorting spec. e.g. "given": [ "James", "A" ]. The test should work
         * once the sorting issue is solved.
         */
        assertTrue("Should be negative to ensure proper ordering - Sorting is not correct",
                firstPatientGivenName.compareTo(secondPatientGivenName) < 0);

    }

}
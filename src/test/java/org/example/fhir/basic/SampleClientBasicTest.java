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

//        final List<Patient> patients = sampleClientBasic.getPatients("SMITH");
        final List<Patient> patients = sampleClientBasic.getPatients("MCINTOSH");

        assertFalse(patients.isEmpty());

        final String firstPatientGivenName = patients.get(0).getName().get(0).getGivenAsSingleString();
        final String secondPatientGivenName = patients.get(1).getName().get(0).getGivenAsSingleString();

        assertTrue("Should be negative to ensure proper ordering",
                firstPatientGivenName.compareTo(secondPatientGivenName) < 0);

    }

}
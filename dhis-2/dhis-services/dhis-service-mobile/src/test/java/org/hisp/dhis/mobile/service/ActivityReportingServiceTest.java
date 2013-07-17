package org.hisp.dhis.mobile.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.api.mobile.ActivityReportingService;
import org.hisp.dhis.api.mobile.NotAllowedException;
import org.hisp.dhis.api.mobile.model.PatientAttribute;
import org.hisp.dhis.api.mobile.model.PatientIdentifier;
import org.hisp.dhis.api.mobile.model.LWUITmodel.Patient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ActivityReportingServiceTest
    extends DhisSpringTest
{
    @Autowired
    private ActivityReportingService activityReportingService;

    @Test
    public void testSavePatient()
    {
        Patient patientA = this.createLWUITPatient( 'A' );
        Patient patientB = this.createLWUITPatient( 'B' );
        try
        {
            assertNotNull( activityReportingService.savePatient( patientA, 779 ) );
            assertNotNull( activityReportingService.savePatient( patientB, 779 ) );
        }
        catch ( NotAllowedException e )
        {
            e.printStackTrace();
        }

    }


    private Patient createLWUITPatient( char uniqueCharacter )
    {
        Patient patient = new Patient();
        patient.setAge( 1 );
        patient.setBirthDate( new Date() );
        patient.setFirstName( "Firstname" + uniqueCharacter );
        patient.setMiddleName( "Middlename" + uniqueCharacter );
        patient.setLastName( "LastName" + uniqueCharacter );
        patient.setGender( "male" );
        patient.setOrganisationUnitName( "OrgUnitName" );
        patient.setPhoneNumber( "095678943" );
        patient.setRegistrationDate( new Date() );
        patient.setIdentifiers( new ArrayList<PatientIdentifier>() );
        patient.setPatientAttValues( new ArrayList<PatientAttribute>() );
        return patient;
    }
}

package org.hisp.dhis.patient;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.system.deletion.DeletionHandler;

public class PatientDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    protected String getClassName()
    {
        return Patient.class.getSimpleName();
    }

    @Override
    public void deleteSource( Source source )
    {
        for ( Patient patient : patientService.getPatients( (OrganisationUnit) source ) )
        {
            patientService.deletePatient( patient );
        }
    }

}

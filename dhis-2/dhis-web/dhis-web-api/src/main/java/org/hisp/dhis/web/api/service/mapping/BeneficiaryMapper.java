package org.hisp.dhis.web.api.service.mapping;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.web.api.model.Beneficiary;

public class BeneficiaryMapper
    implements BeanMapper<Patient, Beneficiary>
{

    @Override
    public Beneficiary getModel( Patient patient )
    {
        if ( patient == null )
        {
            return null;
        }

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setId( patient.getId() );
        beneficiary.setFirstName( patient.getFirstName() );
        beneficiary.setLastName( patient.getLastName() );
        beneficiary.setMiddleName( patient.getMiddleName() );

        return beneficiary;
    }

}

package org.hisp.dhis.patient.api.service;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.api.model.Beneficiary;

public class BeneficiaryMapper
    extends AbstractEntitiyModelBeanMapper<Patient, Beneficiary>
{

    @Override
    public Beneficiary getModel( Patient patient, MappingManager mappingManager )
    {
        if (patient == null) {
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

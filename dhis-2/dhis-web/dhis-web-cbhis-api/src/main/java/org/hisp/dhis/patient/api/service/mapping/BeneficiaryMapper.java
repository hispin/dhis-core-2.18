package org.hisp.dhis.patient.api.service.mapping;

import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.api.model.Beneficiary;
import org.hisp.dhis.patient.api.service.MappingFactory;

public class BeneficiaryMapper
    implements BeanMapper<Patient, Beneficiary>
{

    @Override
    public Beneficiary getModel( Patient patient, MappingFactory mappingFactory, UriInfo uriInfo )
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

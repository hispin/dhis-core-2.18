package org.hisp.dhis.patient;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;

public interface PatientIdentifierTypeStore extends GenericIdentifiableObjectStore<PatientIdentifierType>
{
    Collection<PatientIdentifierType> get ( boolean mandatory );

}

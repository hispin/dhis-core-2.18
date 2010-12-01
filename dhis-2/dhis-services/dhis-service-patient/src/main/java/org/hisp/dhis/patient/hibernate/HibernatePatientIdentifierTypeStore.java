package org.hisp.dhis.patient.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeStore;

public class HibernatePatientIdentifierTypeStore
    extends HibernateGenericStore<PatientIdentifierType>
    implements PatientIdentifierTypeStore
{

    @SuppressWarnings( "unchecked" )
    public Collection<PatientIdentifierType> get( boolean mandatory )
    {
        return getCriteria( Restrictions.eq( "mandatory", mandatory ) ).list();
    }

}

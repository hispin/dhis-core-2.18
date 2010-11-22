package org.hisp.dhis.patient.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.patient.PatientMobileSetting;
import org.hisp.dhis.patient.PatientMobileSettingStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernatePatientMobileSettingStore
    extends HibernateGenericStore<PatientMobileSetting>
    implements PatientMobileSettingStore
{

    @SuppressWarnings( "unchecked" )
    public Collection<PatientMobileSetting> getCurrentSetting()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PatientMobileSetting.class );
        criteria.setCacheable( true );

        System.out.println(criteria.list().size());
        
        return criteria.list();
    }
}

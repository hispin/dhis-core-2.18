package org.hisp.dhis.mapping.hibernate;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.mapping.Map;
import org.hisp.dhis.mapping.MapStore;
import org.hisp.dhis.user.User;

public class HibernateMapStore
    extends HibernateIdentifiableObjectStore<Map> implements MapStore
{
    @SuppressWarnings("unchecked")
    public Collection<Map> getSystemAndUserMaps( User user )
    {
        return getCriteria( Restrictions.or( 
            Restrictions.eq( "user", user ),
            Restrictions.isNull( "user" ) ) ).list();
    }
}

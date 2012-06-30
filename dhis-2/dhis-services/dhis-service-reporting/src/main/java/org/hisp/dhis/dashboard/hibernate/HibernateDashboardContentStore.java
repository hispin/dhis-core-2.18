package org.hisp.dhis.dashboard.hibernate;

import org.hisp.dhis.dashboard.DashboardContent;
import org.hisp.dhis.dashboard.DashboardContentStore;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.user.User;

public class HibernateDashboardContentStore
    extends HibernateGenericStore<DashboardContent> implements DashboardContentStore
{
    @Override
    public void delete( User user )
    {
        String hql = "delete from DashboardContent d where d.user = :user";
        
        getQuery( hql ).setEntity( "user", user ).executeUpdate();        
    }
}

package org.hisp.dhis.useraudit.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.useraudit.FailedLogin;
import org.hisp.dhis.useraudit.UserAuditStore;

public class HibernateUserAuditStore
    implements UserAuditStore
{
    private SessionFactory sessionFactory;
    
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    public void saveFailedLogin( FailedLogin login )
    {
        sessionFactory.getCurrentSession().save( login );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<FailedLogin> getAllFailedLogins()
    {
        return sessionFactory.getCurrentSession().createCriteria( FailedLogin.class ).list();
    }
    
    public int getFailedLogins( String username, Date date )
    {
        Session session = sessionFactory.getCurrentSession();
        
        String hql = "delete from FailedLogin where date < :date";
        
        session.createQuery( hql ).setDate( "date", date ).executeUpdate();
        
        hql = "select count(*) from FailedLogin where username = :username";
        
        Long no = (Long) session.createQuery( hql ).setString( "username", username ).uniqueResult();
        
        return no.intValue();
    }
}

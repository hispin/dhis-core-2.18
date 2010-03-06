package org.hisp.dhis.useraudit.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.useraudit.LoginFailure;
import org.hisp.dhis.useraudit.UserAuditStore;

public class HibernateUserAuditStore
    implements UserAuditStore
{
    private SessionFactory sessionFactory;
    
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    public void saveLoginFailure( LoginFailure login )
    {
        sessionFactory.getCurrentSession().save( login );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<LoginFailure> getAllLoginFailures()
    {
        return sessionFactory.getCurrentSession().createCriteria( LoginFailure.class ).list();
    }
    
    public void deleteLoginFailures( String username )
    {
        String hql = "delete from LoginFailure where username = :username";
        
        sessionFactory.getCurrentSession().createQuery( hql ).setString( "username", username ).executeUpdate();
    }
        
    public int getLoginFailures( String username, Date date )
    {
        Session session = sessionFactory.getCurrentSession();
        
        String hql = "delete from LoginFailure where date < :date";
        
        session.createQuery( hql ).setDate( "date", date ).executeUpdate();
        
        hql = "select count(*) from LoginFailure where username = :username";
        
        Long no = (Long) session.createQuery( hql ).setString( "username", username ).uniqueResult();
        
        return no.intValue();
    }
}

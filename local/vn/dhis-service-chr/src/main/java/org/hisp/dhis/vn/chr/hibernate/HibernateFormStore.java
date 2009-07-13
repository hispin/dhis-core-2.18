package org.hisp.dhis.vn.chr.hibernate;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateFormStore
    implements FormStore
{
    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -----------------------------------------------------------------------------------------------
    // Implements
    // -----------------------------------------------------------------------------------------------

    public int addForm( Form form )
    {
        Session session = sessionFactory.getCurrentSession();

        String name = form.getName().toLowerCase();

        form.setName( name );

        return (Integer) session.save( form );
    }

    public void deleteForm( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( id );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getAllForms()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Form.class );

        return criteria.list();
    }

    public Form getForm( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Form) session.get( Form.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getVisibleForms( String visible )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.visible = :visible" );

        query.setString( "visible", visible );

        return query.list();
    }

    public void updateForm( Form form )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( form );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getFormsByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.name = :name" );

        query.setString( "name", name );

        return query.list();
    }
}

package org.hisp.dhis.vn.chr.hibernate;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormStore;
import org.hisp.dhis.vn.chr.comparator.FormNameComparator;
import org.hisp.dhis.hibernate.HibernateSessionManager;

public class HibernateFormStore
    implements FormStore
{

    // -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

    private HibernateSessionManager hibernateSessionManager;

    // -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------
    public void setHibernateSessionManager( HibernateSessionManager hibernateSessionManager )
    {
        this.hibernateSessionManager = hibernateSessionManager;
    }

    // -----------------------------------------------------------------------------------------------
    // Implements
    // -----------------------------------------------------------------------------------------------

    public int addForm( Form form )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        String name = form.getName().toLowerCase();

        form.setName( name );

        return (Integer) session.save( form );
    }

    public void deleteForm( int id )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        session.delete( id );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getAllForms()
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Criteria criteria = session.createCriteria( Form.class );

        List<Form> list = criteria.list();

        Collections.sort( list, new FormNameComparator() );

        return list;
    }

    public Form getForm( int id )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        return (Form) session.get( Form.class, id );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getVisibleForms( boolean visible )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.visible = :visible" );

        query.setBoolean( "visible", visible );

        return query.list();
    }

    public void updateForm( Form form )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        session.update( form );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getFormsByName( String name )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.name = :name" );

        query.setString( "name", name );

        return query.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<Form> getCreatedForms()
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.created = :created" );

        query.setBoolean( "created", true );

        return query.list();
    }

    public Form getFormByName( String name )
    {

        Session session = hibernateSessionManager.getCurrentSession();

        Query query = session.createQuery( "from Form c where c.name = :name" );

        query.setString( "name", name );

        return (Form) query.uniqueResult();
    }

}

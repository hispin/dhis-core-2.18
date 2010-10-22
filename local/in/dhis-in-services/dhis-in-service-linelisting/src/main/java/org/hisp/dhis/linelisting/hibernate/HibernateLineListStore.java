package org.hisp.dhis.linelisting.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.hisp.dhis.linelisting.LineListStore;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public class HibernateLineListStore
    implements LineListStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    // -------------------------------------------------------------------------
    // Line List Group
    // -------------------------------------------------------------------------

    public int addLineListGroup( LineListGroup lineListGroup )
    {
        PeriodType periodType = periodStore.getPeriodType( lineListGroup.getPeriodType().getClass() );

        lineListGroup.setPeriodType( periodType );

        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListGroup );
    }

    public void deleteLineListGroup( LineListGroup lineListGroup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListGroup );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getAllLineListGroups()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListGroup.class ).list();

    }

    public LineListGroup getLineListGroup( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListGroup) session.get( LineListGroup.class, id );

    }

    public LineListGroup getLineListGroupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListGroup) criteria.uniqueResult();
    }

    public LineListGroup getLineListGroupByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (LineListGroup) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getLineListGroupsBySource( Source source )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.createAlias( "sources", "s" );
        criteria.add( Restrictions.eq( "s.id", source.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListGroup> getLineListGroupsByElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListGroup.class );
        criteria.createAlias( "lineListElement", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListElement.getId() ) );

        return criteria.list();
    }

    public void updateLineListGroup( LineListGroup lineListGroup )
    {
        PeriodType periodType = periodStore.getPeriodType( lineListGroup.getPeriodType().getClass() );

        lineListGroup.setPeriodType( periodType );
        
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListGroup );

    }

    // -------------------------------------------------------------------------
    // Line List Element
    // -------------------------------------------------------------------------

    public int addLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListElement );
    }

    public void deleteLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListElement );

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getAllLineListElements()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListElement.class ).list();

    }

    public LineListElement getLineListElement( int id )
    {
        Session session = sessionFactory.getCurrentSession();
        return (LineListElement) session.get( LineListElement.class, id );

    }

    public LineListElement getLineListElementByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListElement) criteria.uniqueResult();
    }

    public LineListElement getLineListElementByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );
        
        return (LineListElement) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getLineListElementsByOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.createAlias( "lineListOption", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListOption.getId() ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListElement> getLineListElementsBySortOrder (LineListGroup lineListGroup)
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListElement.class );
        criteria.createAlias( "lineListGroup", "l" );

        criteria.add( Restrictions.eq( "l.id", lineListGroup.getId() ) );

        return criteria.list();
    }

    public void updateLineListElement( LineListElement lineListElement )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListElement );

    }

    // -------------------------------------------------------------------------
    // Line List Option
    // -------------------------------------------------------------------------

    public int addLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( lineListOption );
    }

    public void deleteLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lineListOption );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LineListOption> getAllLineListOptions()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( LineListOption.class ).list();

    }

    public LineListOption getLineListOption( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        return (LineListOption) session.get( LineListOption.class, id );

    }

    public LineListOption getLineListOptionByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListOption.class );
        criteria.add( Restrictions.eq( "name", name ) );

        return (LineListOption) criteria.uniqueResult();
    }

    public LineListOption getLineListOptionByShortName( String shortName )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LineListOption.class );
        criteria.add( Restrictions.eq( "shortName", shortName ) );

        return (LineListOption) criteria.uniqueResult();
    }

    public void updateLineListOption( LineListOption lineListOption )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( lineListOption );
    }
    
}

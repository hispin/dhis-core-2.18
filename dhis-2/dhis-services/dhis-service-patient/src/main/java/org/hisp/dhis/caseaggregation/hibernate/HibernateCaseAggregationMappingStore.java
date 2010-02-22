package org.hisp.dhis.caseaggregation.hibernate;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.caseaggregation.CaseAggregationMapping;
import org.hisp.dhis.caseaggregation.CaseAggregationMappingStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.patientdatavalue.PatientDataValue;

public class HibernateCaseAggregationMappingStore
    implements CaseAggregationMappingStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    // -------------------------------------------------------------------------
    // CaseAggregationMapping
    // -------------------------------------------------------------------------

    @Override
    public void addCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( caseAggregationMapping );
    }

    @Override
    public void deleteCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( caseAggregationMapping );
    }

    @Override
    public void updateCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( caseAggregationMapping );
    }

    @Override
    public CaseAggregationMapping getCaseAggregationMappingByOptionCombo( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( CaseAggregationMapping.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return (CaseAggregationMapping) criteria.uniqueResult();
    }

    public int executeMappingQuery( String sQuery )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( sQuery );
        
        Iterator  it = query.iterate(); 
        int rs = 0;
        while( it.hasNext() )
        {
            Object obj = it.next();
            System.out.println("result object: "+obj);
            rs = NumberUtils.toInt( obj.toString(), 0 );
            
        }
        return rs;
    }

    public List<PatientDataValue> executeMappingQueryForListPatientDataValue( String squery )
    {
        Session session = sessionFactory.getCurrentSession();
        Query query = session.createQuery( squery );
        
        return query.list(); 
    }

}

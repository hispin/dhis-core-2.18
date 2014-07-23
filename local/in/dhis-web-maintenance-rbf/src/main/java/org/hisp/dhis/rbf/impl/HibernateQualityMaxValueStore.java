package org.hisp.dhis.rbf.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class HibernateQualityMaxValueStore implements QualityMaxValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // QualityMaxValue
    // -------------------------------------------------------------------------
    
	@Override
	public void addQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.save( qualityMaxValue );
	}

	@Override
	public void updateQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.update( qualityMaxValue );
	}

	@Override
	public void deleteQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.delete( qualityMaxValue );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<QualityMaxValue> getAllQuanlityMaxValues() {
		
		Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( QualityMaxValue.class );

        return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnit organisationUnit, DataSet dataSet) 
	{
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataSet dataSet) 
	{
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
	}
	
	@Override
	public QualityMaxValue getQualityMaxValue(
			OrganisationUnit organisationUnit, DataElement dataElement,
			DataSet dataSet,Date startDate ,Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (QualityMaxValue) criteria.uniqueResult();
	}

	@Override
	public QualityMaxValue getQualityMaxValue( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet,Date startDate ,Date endDate) 
	{
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (QualityMaxValue) criteria.uniqueResult();
	}
	
	@SuppressWarnings( "unchecked" )
    @Override
	public Collection<QualityMaxValue> getQuanlityMaxValues(
			OrganisationUnit organisationUnit, DataElement dataElement) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
	}
	
	@SuppressWarnings( "unchecked" )
    @Override
	public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement) 
	{
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );

        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
	}
	
	public Map<Integer, Double> getQualityMaxValues( OrganisationUnitGroup orgUnitGroup, String orgUnitBranchIds, DataSet dataSet, Period period )
    {
        Map<Integer, Double> qualityMaxValueMap = new HashMap<Integer, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        try
        {                       
            String query = "select td.dataelementid, td.value from "+
                            "( " +
                                "select max(asd.level) as level,asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                " from " +
                                    "( "+
                                        " select td.orgunitgroupid,td.organisationunitid,td.datasetid,td.dataelementid,os.level,td.value " +
                                            " from qualitymaxvalue td inner join _orgunitstructure os on os.organisationunitid = td.organisationunitid "+
                                            " where '" + curPeriod + "'  between date(td.startdate) and date(td.enddate) " +
                                                " and orgunitgroupid in ( " + orgUnitGroup.getId() + ") " +
                                                " and datasetid in ( " +dataSet.getId() + ") "+
                                                " )asd "+
                                                " group by asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                                " )sag1 " +
                                                " inner join qualitymaxvalue td on td.dataelementid=sag1.dataelementid " +
                                                " where td.orgunitgroupid=sag1.orgunitgroupid " + 
                                                " and td.datasetid=sag1.datasetid " +
                                                " and td.organisationunitid in ("+ orgUnitBranchIds +") ";
            
            System.out.println("Query: " + query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                qualityMaxValueMap.put( dataElementId, value );
                System.out.println( dataElementId + " : " + value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getQualityMaxValues Exception :"+ e.getMessage() );
        }
        
        return qualityMaxValueMap;
    }
    

}

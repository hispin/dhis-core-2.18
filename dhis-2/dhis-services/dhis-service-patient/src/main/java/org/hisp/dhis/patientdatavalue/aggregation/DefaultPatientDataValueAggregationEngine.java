package org.hisp.dhis.patientdatavalue.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;

// Work in progress!

@SuppressWarnings( "unused" )
public class DefaultPatientDataValueAggregationEngine
    implements PatientDataValueAggregationEngine
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    /*
     * interface: startDate, endDate, organisationUnit
     * 
     * dimensions: data element, category option combo, period, organisation unit,
     * 
     * process:
     * 
     * get distinct dataelement
     * get patients for organisation unit
     * 
     * int - sum: ("Number of condoms") 
     * 
     * int - count: ("Need for referral")
     * 
     * bool - sum: ("Baby weight")
     * 
     */
    
    // -------------------------------------------------------------------------
    // PatientDataValueAggregationEngine implementation
    // -------------------------------------------------------------------------

    public void aggregate( Date startDate, Date endDate, OrganisationUnit organisationUnit )
    {
        Collection<DataElement> dataElements = null; // patientDataValueService.getDistinctDataElements();
        
        Collection<Integer> patients = null; // patientService.getPatients( OrganisationUnit organisationUnit );

        Collection<Period> periods = periodService.getPeriodsBetweenDates( startDate, endDate );            
    }
    
    private void aggregate( DataElement dataElement, Collection<Integer> categoryOptionCombos, Period period, Collection<Integer> patients )
    {
        // sum - int
        
        String sql = 
            "SELECT sum( value ) " + 
            "FROM patientdatavalue " + 
            "WHERE dataelementid = '" + dataElement.getId() + "' " +
            "AND datetime > '" + DateUtils.getMediumDateString( period.getStartDate() ) + " " +
            "AND datetime <= " + DateUtils.getMediumDateString( period.getEndDate() ) + " " +
            "AND patientid IN (" + TextUtils.getCommaDelimitedString( patients ) + ")";
        
        // sum - bool
        
        sql = 
            "SELECT count( * ) " +
            "FROM patientdatavalue " + 
            "WHERE dataelementid = '" + dataElement.getId() + "' " +
            "AND datetime > '" + DateUtils.getMediumDateString( period.getStartDate() ) + " " +
            "AND datetime <= " + DateUtils.getMediumDateString( period.getEndDate() ) + " " +
            "AND patientid IN (" + TextUtils.getCommaDelimitedString( patients ) + ") " +
            "AND value='T'";
        
        // count - int
        
        for ( Integer categoryOptionComboId : categoryOptionCombos )
        {
            sql =
                "SELECT count( * ) " +
                "FROM patientdatavalue " + 
                "WHERE dataelementid = '" + dataElement.getId() + "' " +
                "AND categoryoptioncomboid = '" + categoryOptionComboId + " " +
                "AND datetime > '" + DateUtils.getMediumDateString( period.getStartDate() ) + " " +
                "AND datetime <= " + DateUtils.getMediumDateString( period.getEndDate() ) + " " +
                "AND patientid IN (" + TextUtils.getCommaDelimitedString( patients ) + ")";
        }
    }
    
    private Collection<Period> filterPeriods( Collection<Period> periods, PeriodType periodType )
    {
        Collection<Period> filteredPeriods = new ArrayList<Period>();
        
        for ( Period period : periods )
        {
            if ( period.getPeriodType().equals( periodType ) )
            {
                filteredPeriods.add( period );
            }
        }
        
        return filteredPeriods;
    }
}

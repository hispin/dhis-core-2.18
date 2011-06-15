package org.hisp.dhis.dataanalyser.ds.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataentrystatus.DataEntryStatus;
import org.hisp.dhis.dataentrystatus.DataEntryStatusService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class DataEntryStatusSaveAction implements Action
{
    Collection<Period> periods = new ArrayList<Period>();

    Collection<DataSet> dataSets = new ArrayList<DataSet>();
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private DataEntryStatusService dataEntryStatusService;
    
    public void setDataEntryStatusService( DataEntryStatusService dataEntryStatusService )
    {
        this.dataEntryStatusService = dataEntryStatusService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------


    private Collection<Integer> selectedPeriods = new ArrayList<Integer>();

    public void setSelectedPeriods( Collection<Integer> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    private Collection<Integer> selectedDataSets = new ArrayList<Integer>();

    public void setSelectedDataSets( Collection<Integer> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }
    
    private String includeZeros;

    public void setIncludeZeros( String includeZeros )
    {
        this.includeZeros = includeZeros;
    }
    
    public String getIncludeZeros()
    {
        return includeZeros;
    }
    
    private int dataSetMemberCount;
    
    private String deInfo;
    
    private String orgUnitInfo;
    
    private  String periodInfo;
    
    private String message;

    public String getMessage()
    {
        return message;
    }

    
    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        System.out.println( "Data Entry Status Mart Start Time  : " + new Date() );
        String currentUserName = currentUserService.getCurrentUsername();
        
        for ( Integer periodId : selectedPeriods )
        {
            periods.add( periodService.getPeriod( periodId.intValue() ) );
        }

        for ( Integer dataSetId : selectedDataSets )
        {
            dataSets.add( dataSetService.getDataSet( dataSetId.intValue() ) );
        }
        
        Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
        
        String  includeZero = ""; 
        if ( includeZeros == null )
        {
            includeZero = "N";
        }
        else
        {
            includeZero = "Y";
        }
        String query2 = "";
        Double dataStatusPercentatge = 0.0;
        
        for ( DataSet dataSet : dataSets )
        {
            Set<OrganisationUnit> dataSetOrganisationUnits = dataSet.getSources();
            Set<OrganisationUnit> selOrgUnitSource = new HashSet<OrganisationUnit>();

            selOrgUnitSource.addAll( selectedOrganisationUnits );
            selOrgUnitSource.retainAll( dataSetOrganisationUnits );
            
            dataSetMemberCount = 0;
            for ( DataElement de : dataSet.getDataElements() )
            {
                deInfo += "," + de.getId();
                dataSetMemberCount += de.getCategoryCombo().getOptionCombos().size();
            }
            
            Iterator<OrganisationUnit> orgUnitListIterator = selOrgUnitSource.iterator();
            while ( orgUnitListIterator.hasNext() )
            {
                OrganisationUnit orgUnit = orgUnitListIterator.next();
                
                orgUnitInfo = "" + orgUnit.getId();
                
                Period p;
                Iterator<Period> periodIterator = periods.iterator();
                while ( periodIterator.hasNext() )
                {
                    p = (Period) periodIterator.next();
                    periodInfo = "" + p.getId();
                    
                    if ( includeZeros == null )
                    {
                        query2 = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ") and value <> 0";
                    }
                    else
                    {
                        query2 = "SELECT COUNT(*) FROM datavalue WHERE dataelementid IN (" + deInfo
                            + ") AND sourceid IN (" + orgUnitInfo + ") AND periodid IN (" + periodInfo + ")";
                    }

                    SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query2 );
                    if ( sqlResultSet.next() )
                    {
                        try
                        {
                            dataStatusPercentatge = ((double) sqlResultSet.getInt( 1 ) / (double) dataSetMemberCount) * 100.0;
                        }
                        catch ( Exception e )
                        {
                            dataStatusPercentatge = 0.0;
                        }
                    }
                    dataStatusPercentatge = Math.round( dataStatusPercentatge * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                    
                    DataEntryStatus dataEntryStatus = dataEntryStatusService.getDataEntryStatusValue( dataSet, orgUnit, p , includeZero );
                    
                    if ( dataEntryStatus != null )
                    {
                        if ( includeZeros == null )
                        {
                            //includeZero = "N";
                            dataEntryStatus.setDataset( dataSet );
                            dataEntryStatus.setOrganisationunit( orgUnit );
                            dataEntryStatus.setPeriod( p );
                            dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                            dataEntryStatus.setTimestamp( new Date() );
                            dataEntryStatus.setStoredBy( currentUserName );
                            dataEntryStatus.setIncludeZero( includeZero );
                            dataEntryStatusService.updateDataEntryStatus( dataEntryStatus );
                        }
                        else
                        {
                           // includeZero = "Y";
                            dataEntryStatus.setDataset( dataSet );
                            dataEntryStatus.setOrganisationunit( orgUnit );
                            dataEntryStatus.setPeriod( p );
                            dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                            dataEntryStatus.setTimestamp( new Date() );
                            dataEntryStatus.setStoredBy( currentUserName );
                            dataEntryStatus.setIncludeZero( includeZero );
                            dataEntryStatusService.updateDataEntryStatus( dataEntryStatus );
                        }
                    }
                    else
                    {
                        if ( includeZeros == null )
                        {
                            //includeZero = "N";
                            dataEntryStatus = new DataEntryStatus();
                            dataEntryStatus.setDataset( dataSet );
                            dataEntryStatus.setOrganisationunit( orgUnit );
                            dataEntryStatus.setPeriod( p );
                            dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                            dataEntryStatus.setTimestamp( new Date() );
                            dataEntryStatus.setStoredBy( currentUserName );
                            dataEntryStatus.setIncludeZero( includeZero );
                            dataEntryStatusService.addDataEntryStatus( dataEntryStatus );
                        }
                        else
                        {
                            //includeZero = "Y";
                            dataEntryStatus = new DataEntryStatus();
                            dataEntryStatus.setDataset( dataSet );
                            dataEntryStatus.setOrganisationunit( orgUnit );
                            dataEntryStatus.setPeriod( p );
                            dataEntryStatus.setValue( dataStatusPercentatge.toString() );
                            dataEntryStatus.setTimestamp( new Date() );
                            dataEntryStatus.setStoredBy( currentUserName );
                            dataEntryStatus.setIncludeZero( includeZero );
                            dataEntryStatusService.addDataEntryStatus( dataEntryStatus );
                        }
                    }
                }
            }
            
 
        }
        System.out.println( "Data Entry Status Mart End Time  : " + new Date() );
        message = i18n.getString( "information_successfully_saved" );
        return SUCCESS;
    }
}

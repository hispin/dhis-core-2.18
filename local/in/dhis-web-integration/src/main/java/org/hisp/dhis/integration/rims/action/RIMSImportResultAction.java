package org.hisp.dhis.integration.rims.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.HibernateException;
import org.hisp.dhis.aggregation.batch.statement.StatementManager;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.integration.rims.api.RIMSService;
import org.hisp.dhis.integration.rims.api.RIMSTable;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_DataElement;
import org.hisp.dhis.integration.rims.api.RIMS_Mapping_Orgunit;
import org.hisp.dhis.integration.rims.api.tables.DistrictTable;
import org.hisp.dhis.integration.rims.api.tables.PHCTable;
import org.hisp.dhis.integration.rims.api.tables.RIMSDistrict;
import org.hisp.dhis.integration.rims.api.tables.RIMSOrgUnit;
import org.hisp.dhis.integration.rims.api.tables.RIMS_PHC;
import org.hisp.dhis.integration.rims.util.Configuration;
import org.hisp.dhis.integration.rims.util.ImportExportError;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

import com.opensymphony.xwork.ActionSupport;

import static java.util.Calendar.YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;

public class RIMSImportResultAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private RIMSService rimsService;

    public void setRimsService( RIMSService rimsService )
    {
        this.rimsService = rimsService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // --------------------------------------------------------------------------
    // Parameters
    // --------------------------------------------------------------------------

    private Date sDate;

    private Date eDate;

    private List<String> selectedOrgunits;

    public void setSelectedOrgunits( List<String> selectedOrgunits )
    {
        this.selectedOrgunits = selectedOrgunits;
    }

    private List<String> rimsDEGroups;

    public void setRimsDEGroups( List<String> rimsDEGroups )
    {
        this.rimsDEGroups = rimsDEGroups;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private String rimsDistricts;
    
    private String includeDistrict;
    
    private String connection;

    private int totalRecCount;

    private Collection<ImportExportError> skipped = new ArrayList<ImportExportError>();

    private boolean abort;

    public int getTotalRecCount()
    {
        return totalRecCount;
    }

    // --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        // TODO Useful for testing, turn off for production use
        boolean includeTotals = false;
        
        Configuration config = new Configuration( connection );
        statementManager.initialise();

        // ---------------------------------------------------------------------
        // OrganisationUnit Info
        // ---------------------------------------------------------------------
        List<Object> rimsSelectedOus = new ArrayList<Object>();
        
        if ( includeDistrict != null )
        {
            // Get selected district and add it to the list of selected OUs
            rimsSelectedOus.add( rimsService.getDistrict( rimsDistricts, connection ) );
        }
        else if ( selectedOrgunits == null )
        {
            throw new IllegalArgumentException( "No orgunits selected" );
        }

        if ( selectedOrgunits != null )
        {
            for ( String rimsOU: selectedOrgunits )
            {
                RIMS_PHC rimsOUObj = rimsService.getPHC( rimsOU, connection );
                rimsSelectedOus.add( rimsOUObj );
            }
        }

        // DataElement Info
        List<RIMS_Mapping_DataElement> rimsSelectedMappingDEs = new ArrayList<RIMS_Mapping_DataElement>(
            getSelectedMappingDEs() );

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );
        if ( eDate == null )
        {
            throw new IllegalArgumentException( endDate +" is not a valid date" );
        }
        // Add periods if necessary
        checkAllPeriods( sDate, eDate );
        List<Period> selectedPeriods = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
    
        int orgUnitI = 0;
all:
        // TODO Figure out this generics stuff
        for ( Object o: rimsSelectedOus)
        {
            RIMSOrgUnit rimsOrgUnit = (RIMSOrgUnit) o;
            int percentage = (int) (orgUnitI++ / (double) rimsSelectedOus.size() * 100);
            System.out.print( "("+ percentage +" %) Importing "+ rimsOrgUnit.getName() );
            
            RIMS_Mapping_Orgunit mappingOrgUnit = rimsService
            .getMappingOrgUnit( rimsOrgUnit.getCode() ); 
            
            for ( RIMS_Mapping_DataElement mappingDataElement: rimsSelectedMappingDEs)
            {
                System.out.print( '.' );
                if ( mappingDataElement.getDhisExpression().equalsIgnoreCase( "NA" )
                    || mappingDataElement.getDhisExpression().equalsIgnoreCase( "NO_SUCH_DE" ) )
                {
                    System.out.println( "Skipped dataelement \""+ mappingDataElement.getDeName() +"\": marked as "+ mappingDataElement.getDhisExpression() );
                    continue;
                }
                
                if ( mappingDataElement.isTotal() && !includeTotals )
                {
                    continue;
                }

                for ( Period period: selectedPeriods )
                {
                    // Flip this using the debugger to abort.
                    // Eventually the progress indicator will have an abort button.                    
                    if ( abort )
                    {
                        break all;
                    }
                    try
                    {

                        if ( period.getPeriodType().getName().equalsIgnoreCase(
                            "monthly" ) )
                        {
                            if ( !config.getMonthlyFields().contains(
                                mappingDataElement.getRimsColumn() ) )
                            {
                                continue;
                            }
                        }
                        else if ( period.getPeriodType().getName()
                            .equalsIgnoreCase( "yearly" ) )
                        {
                            if ( !config.getYearlyFields().contains(
                                mappingDataElement.getRimsColumn() ) )
                            {
                                continue;
                            }
                        }
                        else
                        {
                            continue;
                        }

                        // -----------------------------------------------------
                        // Skip if mixing district/PHC data
                        // -----------------------------------------------------
                        RIMSTable table = config.getTable( mappingDataElement.getTableName() );
                        
                        // Prevent superfluous "dataelement not found" errors
                        if ( ( table instanceof DistrictTable
                            && rimsOrgUnit instanceof RIMS_PHC ) 
                          || ( table instanceof PHCTable
                            && rimsOrgUnit instanceof RIMSDistrict ) )
                        {
                            continue;
                        }
                        
                        // -----------------------------------------------------
                        // Get data value
                        // -----------------------------------------------------
    
                        OrganisationUnit dhisOrgUnit = organisationUnitService
                            .getOrganisationUnit( Integer.parseInt( 
                                mappingOrgUnit.getDhisid() ) );
                        if ( !dhisFormulaIsPlain( mappingDataElement
                            .getDhisExpression() ) )
                        {
                            skipped.add( new ImportExportError( mappingDataElement, mappingOrgUnit,
                                    "Only plain formulas (no operators) are supported for import" ) );
                            continue;
                        }
                        // Get value from RIMS database
                        String rimsValue = getRIMSDataValue(
                            mappingDataElement, period, rimsOrgUnit, config );

                        if ( rimsValue == null )
                        {
                            // Log and skip
                            DataElement dataElement = dataElementService
                                .getDataElement( mappingDataElement
                                    .getDhisDataElementId() );
                            skipped.add( new ImportExportError( dataElement, mappingOrgUnit, period,
                                "Data value not found in RIMS" ) );
                            continue;
                        }

                        // ---------------------------------------------------------
                        // Actual import
                        // ---------------------------------------------------------

                        // Data element
                        DataElement dataElement = dataElementService
                            .getDataElement( mappingDataElement
                                .getDhisDataElementId() );
                        if ( dataElement == null )
                        {
                            skipped.add( new ImportExportError( dataElement, mappingOrgUnit,
                                "Data element not found in DHIS" ) );
                            continue;
                        }

                        // Option combo
                        DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService
                            .getDataElementCategoryOptionCombo( mappingDataElement
                                .getDhisOptionCombo() );

                        // If data value exists, replace it, otherwise create
                        // it.
                        DataValue dataValue = dataValueService.getDataValue(
                            dhisOrgUnit, dataElement, period, optionCombo );

                        if ( dataValue == null )
                        {
                            dataValue = new DataValue( dataElement, period,
                                dhisOrgUnit, rimsValue, config.getStoredBy(),
                                new Date(), null, optionCombo );
                            dataValueService.addDataValue( dataValue );
                        }
                        else
                        {
                            dataValue.setValue( rimsValue );
                            dataValue.setTimestamp( new Date() );
                            dataValue.setStoredBy( config.getStoredBy() );

                            dataValueService.updateDataValue( dataValue );
                        }
                        totalRecCount++;
                    }
                    catch ( HibernateException hbe )
                    {
                        throw new Exception( "Database-access error", hbe );
                    }
                    catch ( Exception e )
                    {
                        // Log dataelement and reason
                        try 
                        {
                            DataElement dataElement = dataElementService
                                .getDataElement( mappingDataElement
                                    .getDhisDataElementId() );
                            skipped.add( new ImportExportError( dataElement,
                                mappingOrgUnit, period, e ) );
                            // TODO DEBUG: Beep added
                            System.err.println( e.getClass().getSimpleName() +": "+ e.getMessage() );                            
                        }
                        catch ( NumberFormatException f )
                        {
                            skipped.add( new ImportExportError( mappingDataElement, 
                                mappingOrgUnit, period, e ) );
                        }
                        continue;
                    }
                }
            }
            System.out.println( "done" );
        }

        statementManager.destroy();
        return SUCCESS;
    }

    // TODO A candidate for the DHIS core.
    /**
     * Check that all required periods are present, and add them if necessary.
     *  
     * @param start check periods after this.
     * @param end check periods before this.
     */
    private void checkAllPeriods( Date start, Date end )
    {
        PeriodType monthly = new MonthlyPeriodType();
        PeriodType yearly = new YearlyPeriodType();
        
        // ---------------------------------------------------------------------
        // Monthly
        // ---------------------------------------------------------------------
        
        // These calendars keep track of the first and last of each month as we iterate.
        Calendar firstOfMonth = Calendar.getInstance();
        Calendar lastOfMonth = Calendar.getInstance();
        
        // Start with the month which startDate is in. 
        firstOfMonth.setTime( start );
        firstOfMonth.set( DAY_OF_MONTH, 1 );
        
        lastOfMonth.setTime( start );
        lastOfMonth.set( DAY_OF_MONTH, lastOfMonth.getActualMaximum( DAY_OF_MONTH ) );
        
        // Stop when firstOfMonth has passed endDate.
        while( firstOfMonth.getTime().compareTo( end ) < 0 )
        {
            // Is the period present?
            Period checkPeriod = periodService.getPeriod( 
                firstOfMonth.getTime(), lastOfMonth.getTime(), monthly );
            if ( checkPeriod == null )
            {
                // Add it.
                periodService.addPeriod( new Period( 
                    monthly, firstOfMonth.getTime(), lastOfMonth.getTime() ) );
            }

            // Increment by one month.
            firstOfMonth.add( MONTH, 1 );
            lastOfMonth.add( MONTH, 1 );
            lastOfMonth.set( DAY_OF_MONTH, lastOfMonth.getActualMaximum( DAY_OF_MONTH ) );
        }
        
        // ---------------------------------------------------------------------
        // Yearly
        // ---------------------------------------------------------------------
        // These calendars keep track of the first and last of each month as we iterate.
        Calendar firstOfYear = Calendar.getInstance();
        Calendar lastOfYear = Calendar.getInstance();
        
        // Start with the month which startDate is in. 
        firstOfYear.setTime( start );
        firstOfYear.set( DAY_OF_YEAR, 1 );
        
        lastOfYear.setTime( start );
        lastOfYear.set( DAY_OF_YEAR, lastOfYear.getActualMaximum( DAY_OF_YEAR ) );
        
        // Stop when firstOfYear has passed endDate.
        while( firstOfYear.getTime().compareTo( end ) < 0 )
        {
            // Is the period present?
            Period checkPeriod = periodService.getPeriod( 
                firstOfYear.getTime(), lastOfYear.getTime(), yearly);
            if ( checkPeriod == null )
            {
                // Add it.
                periodService.addPeriod( new Period( 
                    yearly, firstOfYear.getTime(), lastOfYear.getTime() ) );
            }

            // Increment by one year.
            firstOfYear.add( YEAR, 1 );
            lastOfYear.add( YEAR, 1 );
            lastOfYear.set( DAY_OF_YEAR, lastOfYear.getActualMaximum( DAY_OF_YEAR ) );
        }
    }

    private String getRIMSDataValue(
        RIMS_Mapping_DataElement mappingDataElement, Period period,
        RIMSOrgUnit orgUnit, Configuration config ) throws SQLException
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime( period.getStartDate() );
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );
        month = month + 1;

        RIMSTable table = config.getTable( mappingDataElement.getTableName() );
        return table.getDataValue( mappingDataElement, orgUnit, month, year );
    }

    /**
     * Retrieve all selected dataelements for mapping from each dataelement group.
     *  
     * @return a list of all dataelements from each group.
     */
    public List<RIMS_Mapping_DataElement> getSelectedMappingDEs() throws Exception
    {
        List<RIMS_Mapping_DataElement> rimsSelectedMappingsDes = new ArrayList<RIMS_Mapping_DataElement>();

        for ( String sectionName: rimsDEGroups )
        {
            rimsSelectedMappingsDes.addAll( rimsService.getRIMSDataElementsByDEGroup( sectionName ) );
        }
        return rimsSelectedMappingsDes;
    }

    /*
     * Returns the PeriodType Object for selected DataElement, If no PeriodType
     * is found then by default returns Monthly Period type
     */
    public PeriodType getDataElementPeriodType( DataElement de )
    {
        List<DataSet> dataSetList = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        Iterator<DataSet> it = dataSetList.iterator();
        while ( it.hasNext() )
        {
            DataSet ds = (DataSet) it.next();
            List<DataElement> dataElementList = new ArrayList<DataElement>( ds.getDataElements() );
            if ( dataElementList.contains( de ) )
            {
                return ds.getPeriodType();
            }
        }

        return periodService.getPeriodTypeByName( "Monthly" );
    } // getDataElementPeriodType end

    private boolean dhisFormulaIsPlain( String formula )
    {
        // First check for the plain syntax (no operators)
        String plain = "^\\[\\d+\\.\\d+\\]$";
        if ( Pattern.matches( plain, formula ) )
        {
            return true;
        }
        // Then check that the formula has at least one data-element/option combo
        String legal = "\\[\\d+\\.\\d+\\]";
        if ( Pattern.matches( legal, formula ) )
        {
            return false;
        }
        throw new IllegalArgumentException( formula +" is not a valid dataelement expression" );
    }

    public Collection<ImportExportError> getSkipped()
    {
        return skipped;
    }

    public void setIncludeDistrict( String includeDistrict )
    {
        this.includeDistrict = includeDistrict;
    }

    public void setRimsDistricts( String rimsDistricts )
    {
        this.rimsDistricts = rimsDistricts;
    }

    public void setConnection( String connection )
    {
        this.connection = connection;
    }

}

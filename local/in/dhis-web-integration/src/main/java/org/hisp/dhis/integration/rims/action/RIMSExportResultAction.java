package org.hisp.dhis.integration.rims.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.aggregation.AggregationService;
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
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.MathUtils;
import com.opensymphony.xwork.ActionSupport;

public class RIMSExportResultAction
    extends ActionSupport
{
    private static final String NULL_REPLACEMENT = "0";

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

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
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

    public int getTotalRecCount()
    {
        return totalRecCount;
    }
    
    private Collection<ImportExportError> skipped = new ArrayList<ImportExportError>();

    private boolean abort;

    // --------------------------------------------------------------------------
    // Action Implementation
    // --------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        // TODO Useful for testing, turn off for production use
        boolean includeTotals = false;
        
        if ( connection == null )
        {
            throw new IllegalArgumentException( "Must choose connection" );
        }

        Configuration config = new Configuration( connection );
        statementManager.initialise();

        // ---------------------------------------------------------------------
        // OrganisationUnit Info
        // ---------------------------------------------------------------------
        // TODO Figure out this generics stuff
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
            getSelctedMappingDes() );

        // Period Info
        sDate = format.parseDate( startDate );
        eDate = format.parseDate( endDate );

        List<Period> selectedPeriods = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        
        int orgUnitI = 0;
all:
        // TODO Figure out this generics stuff (see above)
        for ( Object o: rimsSelectedOus )
        {
            RIMSOrgUnit rimsOrgUnit = (RIMSOrgUnit) o;
            // Initialize tables
//            for( RIMSTable table: config.getTables() )
//            {
//                table.beginTransaction();
//            }
            
            int percentage = (int) (orgUnitI++ / (double) rimsSelectedOus.size() * 100);
            System.out.print( "("+ percentage +" %) Importing "+ rimsOrgUnit.getName() );
            
            RIMS_Mapping_Orgunit rimsMappingOrgUnit = rimsService.getMappingOrgUnit( rimsOrgUnit.getCode() );
            for ( RIMS_Mapping_DataElement mappingDataElement: rimsSelectedMappingDEs )
            {
                System.out.print( '.' );

                if ( mappingDataElement.getDhisExpression().equalsIgnoreCase( "NA" )
                    || mappingDataElement.getDhisExpression().equalsIgnoreCase( "NO_SUCH_DE" ) )
                {
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
                    try {
                        if ( period.getPeriodType().getName().equalsIgnoreCase( "monthly" ) )
                        {
                            if ( !config.getMonthlyFields().contains( mappingDataElement.getRimsColumn() ) )
                            {
                                continue;
                            }
                        }
                        else if ( period.getPeriodType().getName().equalsIgnoreCase( "yearly" ) )
                        {
                            if ( !config.getYearlyFields().contains( mappingDataElement.getRimsColumn() ) )
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
    
                        Calendar cal = Calendar.getInstance();
                        cal.setTime( period.getStartDate() );
                        int year = cal.get( Calendar.YEAR );
                        int month = cal.get( Calendar.MONTH );
                        month = month + 1;
                        
                        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer
                            .parseInt( rimsMappingOrgUnit.getDhisid() ) );
                        String dhisValue = getDHISDataValue( mappingDataElement.getDhisExpression(), period, orgUnit );
                        
                        if ( dhisValue == null )
                        {
                            skipped.add( new ImportExportError( mappingDataElement, rimsMappingOrgUnit, period, "No value found for dataelement" ) );
                            continue;
                        }
                        
                        // ---------------------------------------------------------
                        // Call actual export code
                        // ---------------------------------------------------------
                        
                        if ( table.isData( rimsOrgUnit, month, year, mappingDataElement ) )
                        {
                            totalRecCount += table.updateData( rimsOrgUnit, month, year,
                                mappingDataElement, dhisValue );
                        }
                        else
                        {
                            totalRecCount += table.insertData( rimsOrgUnit, month, year,
                                mappingDataElement, dhisValue );    
                        }
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
                                rimsMappingOrgUnit, period, e ) );
                            // TODO DEBUG: Beep added
                            System.err.println( e.getClass().getSimpleName() +": "+ e.getMessage() );
                        }
                        catch ( NumberFormatException f )
                        {
                            skipped.add( new ImportExportError( mappingDataElement, 
                                rimsMappingOrgUnit, period, e ) );
                        }
                        continue;
                    }
                }
            }
            // ---------------------------------------------------------------------
            // Final fixup of the exported data
            // ---------------------------------------------------------------------
            for( RIMSTable table: config.getTables() )
            {
                table.fillInTotals();
                table.markIfComplete();
                table.commit();
            }
            System.out.println( "done" );
        }

        statementManager.destroy();
        
        return SUCCESS;
    }

    public List<RIMS_Mapping_DataElement> getSelctedMappingDes() throws Exception
    {
        List<RIMS_Mapping_DataElement> rimsSelctedMappingsDes = new ArrayList<RIMS_Mapping_DataElement>();

        Iterator<String> iterator1 = rimsDEGroups.iterator();
        while ( iterator1.hasNext() )
        {
            String sectionName = (String) iterator1.next();
            rimsSelctedMappingsDes.addAll( rimsService.getRIMSDataElementsByDEGroup( sectionName ) );

        }
        return rimsSelctedMappingsDes;
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

    private String getDHISDataValue( String formula, Period period, OrganisationUnit organisationUnit )
    {
        try
        {
            boolean typeNotInt = false;
            boolean valueFound = false;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString
                    .length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService
                    .getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                if ( dataElement.getType().equalsIgnoreCase( "int" ) )
                {

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, 
                        period, optionCombo );

                    if ( dataValue == null )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = dataValue.getValue();
                        valueFound = true;
                    }
                    
                    // Aggregated data values are not what we need when exporting
                    // to RIMS.
                    /*
                    double aggregatedValue = aggregationService.getAggregatedDataValue( dataElement, optionCombo,
                        theStartDate, theEndDate, organisationUnit );

                    if ( aggregatedValue == AggregationService.NO_VALUES_REGISTERED )
                    {
                        replaceString = NULL_REPLACEMENT;
                    }
                    else
                    {
                        replaceString = String.valueOf( aggregatedValue );
                        deFlag2 = 1;
                    }
                    */
                }
                else
                {
                    typeNotInt = true;
                    PeriodType dePeriodType = getDataElementPeriodType( dataElement );
                    List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType(
                        dePeriodType, period.getStartDate(), period.getEndDate() ) );
                    Period tempPeriod = new Period();
                    if ( periodList == null || periodList.isEmpty() )
                    {
                        replaceString = "";
                        matcher.appendReplacement( buffer, replaceString );
                        continue;
                    }
                    else
                    {
                        tempPeriod = (Period) periodList.get( 0 );
                    }

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, tempPeriod,
                        optionCombo );

                    if ( dataValue != null )
                        replaceString = dataValue.getValue();
                    else
                        replaceString = "";

                }
                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            if ( !typeNotInt )
            {
                double d = 0.0;
                try
                {
                    if ( buffer.toString().contains( "/0.0" ) || buffer.toString().contains( "/0" )
                        || buffer.toString().contains( "/((0.0" ) || buffer.toString().contains( "/((0" ) )
                        d = 0.0;
                    else
                        d = MathUtils.calculateExpression( buffer.toString() );
                }
                catch ( ArithmeticException e )
                {
                    d = 0.0;
                    System.out.println( "Divide By Zero " );
                }
                catch ( Exception e )
                {
                    d = 0.0;
                    System.out.println( "Divide By Zero " );
                }
                if ( d == -1 )
                    d = 0.0;
                else
                {
                    d = Math.round( d * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                    resultValue = "" + (int) d;
                }

                if ( !valueFound )
                {
                    resultValue = null;
                }
            }
            else
            {
                resultValue = buffer.toString();
            }
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }

    public Collection<ImportExportError> getSkipped()
    {
        return skipped;
    }

    public void setSkipped( Collection<ImportExportError> skipped )
    {
        this.skipped = skipped;
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

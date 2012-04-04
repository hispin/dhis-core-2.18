package org.hisp.dhis.alert.util;

import static org.hisp.dhis.dataentryform.DataEntryFormService.DATAELEMENT_TOTAL_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.IDENTIFIER_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INDICATOR_PATTERN;
import static org.hisp.dhis.dataentryform.DataEntryFormService.INPUT_PATTERN;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;

import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.system.util.FilterUtils;


public class AlertUtility
{
    private static final String NULL_REPLACEMENT = "";
    private static final String SEPARATOR = ".";
    
    public static final String GENERATEAGGDATA = "generateaggdata";
    public static final String USEEXISTINGAGGDATA = "useexistingaggdata";
    public static final String USECAPTUREDDATA = "usecaptureddata";

    // ---------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }


    // ---------------------------------------------------------------
    // Supporting Methods
    // ---------------------------------------------------------------
    public String getCustomDataSetReport( DataSet dataSet, OrganisationUnit unit, String periodIdsByComma,
        String aggOption, I18nFormat format )
    {
        //Map<String, String> aggregatedDataValueMap = getAggregatedValueMap( dataSet, unit, period, selectedUnitOnly,
        //    format );

        Map<String, String> aggregatedDataValueMap = getAggregatedValueMap( dataSet, unit, periodIdsByComma, aggOption );
        
        //Map<Integer, String> aggregatedIndicatorMap = getAggregatedIndicatorValueMap( dataSet, unit, period, format );
        
        Map<Integer, String> aggregatedIndicatorMap = getAggregatedIndicatorValueMap( dataSet, unit, periodIdsByComma, aggOption );

        return prepareReportContent( dataSet.getDataEntryForm(), aggregatedDataValueMap, aggregatedIndicatorMap );
    }

	/*
    private Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, Period period,
        boolean selectedUnitOnly, I18nFormat format )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY,
            DEFAULT_AGGREGATION_STRATEGY );

        Collection<DataElement> dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );

        FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );

        Map<String, String> map = new TreeMap<String, String>();

        for ( DataElement dataElement : dataElements )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getOptionCombos() )
            {
                String value;

                if ( selectedUnitOnly )
                {
                    DataValue dataValue = dataValueService.getDataValue( unit, dataElement, period, categoryOptionCombo );
                    value = (dataValue != null) ? dataValue.getValue() : null;
                }
                else
                {
                    Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                        aggregationService.getAggregatedDataValue( dataElement, categoryOptionCombo, period.getStartDate(), period.getEndDate(), unit ) : 
                        aggregatedDataValueService.getAggregatedValue( dataElement, categoryOptionCombo, period, unit );

                    value = format.formatValue( aggregatedValue );
                }

                if ( value != null )
                {
                    map.put( dataElement.getId() + SEPARATOR + categoryOptionCombo.getId(), value );
                }
            }
            
            Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                aggregationService.getAggregatedDataValue( dataElement, null, period.getStartDate(), period.getEndDate(), unit ) : 
                aggregatedDataValueService.getAggregatedValue( dataElement, period, unit );
                
            String value = format.formatValue( aggregatedValue );
            
            if ( value != null )
            {
                map.put( String.valueOf( dataElement.getId() ), value );
            }
        }

        return map;
    }
*/

/*	
    private Map<Integer, String> getAggregatedIndicatorValueMap( DataSet dataSet, OrganisationUnit unit, Period period,
        I18nFormat format )
    {
        String aggregationStrategy = (String) systemSettingManager.getSystemSetting( KEY_AGGREGATION_STRATEGY,
            DEFAULT_AGGREGATION_STRATEGY );

        Map<Integer, String> map = new TreeMap<Integer, String>();

        for ( Indicator indicator : dataSet.getIndicators() )
        {
            Double aggregatedValue = aggregationStrategy.equals( AGGREGATION_STRATEGY_REAL_TIME ) ? 
                aggregationService.getAggregatedIndicatorValue( indicator, period.getStartDate(), period.getEndDate(), unit ) : 
                aggregatedDataValueService.getAggregatedValue( indicator, period, unit );

            String value = format.formatValue( aggregatedValue );

            if ( value != null )
            {
                map.put( indicator.getId(), value );
            }
        }

        return map;
    }
*/

    private String prepareReportContent( DataEntryForm dataEntryForm, Map<String, String> dataValues,
        Map<Integer, String> indicatorValues )
    {
        StringBuffer buffer = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( dataEntryForm.getHtmlCode() );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------

            String inputHtml = inputMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher dataElementTotalMatcher = DATAELEMENT_TOTAL_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            // -----------------------------------------------------------------
            // Find existing data or indicator value and replace input tag
            // -----------------------------------------------------------------

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                Integer dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                Integer optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );

                String dataValue = dataValues.get( dataElementId + SEPARATOR + optionComboId );

                dataValue = dataValue != null ? dataValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, dataValue );
            }
            else if ( dataElementTotalMatcher.find() && dataElementTotalMatcher.groupCount() > 0 )
            {
                Integer dataElementId = Integer.parseInt( dataElementTotalMatcher.group( 1 ) );
                
                String dataValue = dataValues.get( String.valueOf( dataElementId ) );

                dataValue = dataValue != null ? dataValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, dataValue );
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                Integer indicatorId = Integer.parseInt( indicatorMatcher.group( 1 ) );

                String indicatorValue = indicatorValues.get( indicatorId );

                indicatorValue = indicatorValue != null ? indicatorValue : NULL_REPLACEMENT;

                inputMatcher.appendReplacement( buffer, indicatorValue );
            }
        }

        inputMatcher.appendTail( buffer );

        return buffer.toString();
    }


   
    
    private Map<Integer, String> getAggregatedIndicatorValueMap( DataSet dataSet, OrganisationUnit unit, String periodIdsByComma, String aggOption )
    {
        Map<Integer, String> aggMap = new HashMap<Integer, String>();
        
        List<Indicator> indicatorList = new ArrayList<Indicator>( dataSet.getIndicators() );
        String dataElmentIdsByComma = reportService.getDataelementIdsAsString( indicatorList );
        
        Map<String, String> aggDeMap = new HashMap<String, String>();
        if( aggOption.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
        {
            aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( unit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
        }
        else if( aggOption.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( unit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
        }
        else if( aggOption.equalsIgnoreCase( USECAPTUREDDATA ) )
        {
            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+unit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
        }
        
        for ( Indicator indicator : indicatorList )
        {
            Double numValue = 0.0;
            Double denValue = 0.0;
            Double indValue = 0.0;
            
            try
            {
                numValue = Double.parseDouble( reportService.getAggVal( indicator.getNumerator(), aggDeMap ) );
            }
            catch( Exception e )
            {
                numValue = 0.0;
            }
            
            try
            {
                denValue = Double.parseDouble( reportService.getAggVal( indicator.getDenominator(), aggDeMap ) );    
            }
            catch( Exception e )
            {
                denValue = 0.0;
            }

            try
            {
                if( denValue != 0.0 )
                {
                    indValue = ( numValue / denValue ) * indicator.getIndicatorType().getFactor();
                }
                else
                {
                    indValue = 0.0;
                }
            }
            catch( Exception e )
            {
                indValue = 0.0;
            }
            
            indValue = Math.round( indValue * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
            
            aggMap.put( indicator.getId(), indValue.toString() );
        }
        
        return aggMap;
    }
    
    private  Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, String periodIdsByComma, String aggOption )
    {
        Map<String, String> aggDeMap = new HashMap<String, String>();
        
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        Collection<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers(DataElement.class, dataElementList ) );
        String dataElmentIdsByComma = getCommaDelimitedString( dataElementIds );
        
        if( aggOption.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
        {
            aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( unit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
        }
        else if( aggOption.equalsIgnoreCase( GENERATEAGGDATA ) )
        {
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( unit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
        }
        else if( aggOption.equalsIgnoreCase( USECAPTUREDDATA ) )
        {
            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+unit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
        }
        
        return aggDeMap;
    }
    
}

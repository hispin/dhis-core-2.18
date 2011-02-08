package org.hisp.dhis.reporttable;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.RelativePeriods;

/**
 * The ReportTable object represents a customizable database table. It has features
 * like crosstabulation, relative periods, parameters, and display columns.
 * 
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTable
    extends IdentifiableObject
{
    public static final String DATAELEMENT_ID = "dataelementid";
    public static final String DATAELEMENT_NAME = "dataelementname";
    public static final String CATEGORYCOMBO_ID = "categoryoptioncomboid";
    public static final String CATEGORYCOMBO_NAME = "categoryoptioncomboname";
    public static final String INDICATOR_ID = "indicatorid";
    public static final String INDICATOR_NAME = "indicatorname";
    public static final String DATASET_ID = "datasetid";
    public static final String DATASET_NAME = "datasetname";
    public static final String PERIOD_ID = "periodid";
    public static final String PERIOD_NAME = "periodname";
    public static final String ORGANISATIONUNIT_ID = "organisationunitid";
    public static final String ORGANISATIONUNIT_NAME = "organisationunitname";

    public static final String REPORTING_MONTH_COLUMN_NAME = "reporting_month_name";
    public static final String PARAM_ORGANISATIONUNIT_COLUMN_NAME = "param_organisationunit_name";
    public static final String ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME = "organisation_unit_is_parent";
    
    public static final String SEPARATOR = "_";
    public static final String SPACE = " ";
    
    public static final String MODE_DATAELEMENTS = "dataelements";
    public static final String MODE_INDICATORS = "indicators";
    public static final String MODE_DATASETS = "datasets";

    public static final String TOTAL_COLUMN_NAME = "total";
    public static final String TOTAL_COLUMN_PREFIX = "total_";
    public static final String TOTAL_COLUMN_PRETTY_PREFIX = "Total ";
    
    public static final String REGRESSION_COLUMN_PREFIX = "regression_";
    
    public static final Map<String, String> PRETTY_COLUMNS = new HashMap<String, String>() { {
        put( DATAELEMENT_ID, "Data element ID" );
        put( DATAELEMENT_NAME, "Data element" );
        put( CATEGORYCOMBO_ID, "Category combination ID" );
        put( CATEGORYCOMBO_NAME, "Category combination" );
        put( INDICATOR_ID, "Indicator ID" );
        put( INDICATOR_NAME, "Indicator" );
        put( DATASET_ID, "Data set ID" );
        put( DATASET_NAME, "Data set" );
        put( PERIOD_ID, "Period ID" );
        put( PERIOD_NAME, "Period" );        
        put( ORGANISATIONUNIT_ID, "Organisation unit ID" );
        put( ORGANISATIONUNIT_NAME, "Organisation unit" );        
        put( REPORTING_MONTH_COLUMN_NAME, "Reporting month" );
        put( PARAM_ORGANISATIONUNIT_COLUMN_NAME, "Organisation unit parameter" );        
        put( ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME, "Organisation unit is parent" );
    } };
    
    private static final String EMPTY_REPLACEMENT = "_";
    private static final String EMPTY = "";    
    private static final String TABLE_PREFIX = "_report_";
    private static final String REGEX_NUMERIC = "([0-9]*)";

    private static final Map<String, String> MODE_ID_MAP = new HashMap<String, String>() { {
        put( MODE_INDICATORS, INDICATOR_ID );
        put( MODE_DATAELEMENTS, DATAELEMENT_ID );
        put( MODE_DATASETS, DATASET_ID );
    } };
    
    private static final Map<String, String> MODE_NAME_MAP = new HashMap<String, String>() { {
        put( MODE_INDICATORS, INDICATOR_NAME );
        put( MODE_DATAELEMENTS, DATAELEMENT_NAME );
        put( MODE_DATASETS, DATASET_NAME );
    } };
        
    // -------------------------------------------------------------------------
    // Persisted properties
    // -------------------------------------------------------------------------

    /**
     * The name of the database table corresponding to the ReportTable object name.
     */
    private String tableName;
    
    /**
     * The name of the existing database table.
     */
    private String existingTableName;
    
    /**
     * The ReportTable mode, can be dataelement, indicators, datasets.
     */
    private String mode;
    
    /**
     * Whether the ReportTable contains regression columns.
     */
    private Boolean regression;

    /**
     * The list of DataElements the ReportTable contains.
     */
    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    /**
     * The list of Indicators the ReportTable contains.
     */
    private List<Indicator> indicators = new ArrayList<Indicator>();
    
    /**
     * The list of DataSets the ReportTable contains.
     */
    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    /**
     * The list of Periods the ReportTable contains.
     */
    private List<Period> periods = new ArrayList<Period>();
    
    /**
     * The list of OrganisationUnits the ReportTable contains.
     */
    private List<OrganisationUnit> units = new ArrayList<OrganisationUnit>();
    
    /**
     * The DataElementCategoryCombo for the ReportTable.
     */
    private DataElementCategoryCombo categoryCombo;
    
    /**
     * Whether to crosstabulate on the Indicator dimension, which also represents DataElements and DataSets.
     */
    private Boolean doIndicators;
    
    /**
     * Whether to crosstabulate on the Period dimension.
     */
    private Boolean doPeriods;
    
    /**
     * Whether to crosstabulate on the OrganisationUnit dimension.
     */
    private Boolean doUnits;

    /**
     * The RelativePeriods of the ReportTable.
     */
    private RelativePeriods relatives;

    /**
     * The ReportParams of the ReportTable.
     */
    private ReportParams reportParams;

    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------
    
    /**
     * Periods relative to the reporting month.
     */
    private List<Period> relativePeriods = new ArrayList<Period>();
    
    /**
     * Static Periods and relative Periods.
     */
    private List<Period> allPeriods = new ArrayList<Period>();
    
    /**
     * OrganisationUnits relative to a parent unit or current unit.
     */
    private List<OrganisationUnit> relativeUnits = new ArrayList<OrganisationUnit>();
    
    /**
     * Static OrganisationUnits and relative OrganisationUnits.
     */
    private List<OrganisationUnit> allUnits = new ArrayList<OrganisationUnit>();
    
    /**
     * Indicators that will be crosstabulated on the columns axis. Indicators 
     * comprises dataelements, indicators, datasets.
     */
    private List<IdentifiableObject> crossTabIndicators = new ArrayList<IdentifiableObject>();

    /**
     * CategoryCombos that will be crosstabulated on the columns axis. Optional dimension.
     */
    private List<DataElementCategoryOptionCombo> crossTabCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    /**
     * Periods that will be crosstabulated on the columns axis. Mandatory dimension.
     */
    private List<Period> crossTabPeriods = new ArrayList<Period>();
    
    /**
     * OrganisationUnits that will be crosstabulated on the columns axis. Mandatory dimension.
     */
    private List<OrganisationUnit> crossTabUnits = new ArrayList<OrganisationUnit>();
    
    /**
     * Indicators that will be present on the rows axis.
     */
    private List<IdentifiableObject> reportIndicators = new ArrayList<IdentifiableObject>();

    /**
     * CategoryOptionCombos that will be present on the rows axis. Optional dimension.
     */
    private List<DataElementCategoryOptionCombo> reportCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    /**
     * Periods that will be present on the rows axis. Mandatory dimension.
     */
    private List<Period> reportPeriods = new ArrayList<Period>();
    
    /**
     * OrganisationUnits that will be present on the rows axis. Mandatory dimension.
     */
    private List<OrganisationUnit> reportUnits = new ArrayList<OrganisationUnit>();
    
    /**
     * Names of the columns used to query the datavalue table and as index columns
     * in the report table.
     */
    private List<String> indexColumns = new ArrayList<String>();
    
    /**
     * Names of the columns holding entry names used to query the datavalue table.
     */
    private List<String> indexNameColumns = new ArrayList<String>();
    
    /**
     * Names of the columns which should be retrieved from the datavalue table.
     */
    private List<String> selectColumns = new ArrayList<String>();
    
    /**
     * Generated names for crosstabulated columns in the report table.
     */
    private List<String> crossTabColumns = new ArrayList<String>();
    
    /**
     * Generated pretty-print names for crosstabulated columns in the report table,
     * where key is column name and value is pretty column name.
     */
    private Map<String, String> prettyCrossTabColumns = new LinkedHashMap<String, String>();
    
    /**
     * Generated unique identifiers used to retrieve the corresponding value from the datavalue table. 
     */
    private List<String> crossTabIdentifiers = new ArrayList<String>();
    
    /**
     * The I18nFormat used for internationalization of ie. periods.
     */
    private transient I18nFormat i18nFormat;
    
    /**
     * The name of the reporting month based on the report param.
     */
    private String reportingMonthName;
    
    /**
     * The name of the (parent) organisation unit based on the report param. 
     */
    private String organisationUnitName;

    /**
     * The category option combos derived from the dimension set.
     */
    private List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    /**
     * The dimension options.
     */
    private List<DataElementCategoryOption> categoryOptions = new ArrayList<DataElementCategoryOption>();

    /**
     * The dimension option column names.
     */
    private List<String> categoryOptionColumns = new ArrayList<String>();
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Constructor for persistence purposes.
     */
    public ReportTable()
    {   
    }
    
    /**
     * Constructor for testing purposes.
     * 
     * @param name the name.
     * @param tableName the table name.
     */
    public ReportTable( String name, String tableName )
    {
        this.name = name;
        this.tableName = tableName;
    }

    /**
     * Default constructor.
     * 
     * @param name the name.
     * @param mode the mode.
     * @param regression include regression columns.
     * @param dataElements the data elements.
     * @param indicators the indicators.
     * @param dataSets the datasets.
     * @param periods the periods. These periods cannot have the name property set.
     * @param relativePeriods the relative periods. These periods must have the name property set. Not persisted.
     * @param units the organisation units.
     * @param relativeUnits the organisation units. Not persisted.
     * @param dimensionSet the dimension set. Not persisted.
     * @param doIndicators indicating whether indicators should be crosstabulated.
     * @param doCategoryOptionCombos indicating whether category option combos should be crosstabulated.
     * @param doPeriods indicating whether periods should be crosstabulated.
     * @param doUnits indicating whether organisation units should be crosstabulated.
     * @param relatives the relative periods.
     * @param i18nFormat the i18n format. Not persisted.
     * @param reportingMonthName the reporting month name. Not persisted.
     */
    public ReportTable( String name,
        String mode,
        boolean regression,
        List<DataElement> dataElements,
        List<Indicator> indicators,
        List<DataSet> dataSets,
        List<Period> periods,
        List<Period> relativePeriods,
        List<OrganisationUnit> units,
        List<OrganisationUnit> relativeUnits,
        DataElementCategoryCombo categoryCombo,
        boolean doIndicators,
        boolean doPeriods,
        boolean doUnits,
        RelativePeriods relatives,
        ReportParams reportParams,
        I18nFormat i18nFormat,
        String reportingMonthName )
    {
        this.name = name;
        this.tableName = generateTableName( name );
        this.existingTableName = generateTableName( name );
        this.mode = mode;
        this.regression = regression;
        this.dataElements = dataElements;
        this.indicators = indicators;
        this.dataSets = dataSets;
        this.periods = periods;
        this.relativePeriods = relativePeriods;
        this.units = units;
        this.relativeUnits = relativeUnits;
        this.categoryCombo = categoryCombo;
        this.doIndicators = doIndicators;
        this.doPeriods = doPeriods;
        this.doUnits = doUnits;
        this.relatives = relatives;
        this.reportParams = reportParams;
        this.i18nFormat = i18nFormat;
        this.reportingMonthName = reportingMonthName;
    }

    // -------------------------------------------------------------------------
    // Init
    // -------------------------------------------------------------------------

    public void init()
    {
        verify( nonEmptyLists( dataElements, indicators, dataSets ) == 1, "One of dataelements, indicators, and datasets size must be larger than 0" );
        verify( !( doIndicators && doPeriods && doUnits ), "Cannot crosstab on all dimensions" );
        verify( i18nFormat != null, "I18n format must be set" );
        
        // ---------------------------------------------------------------------
        // Init tableName, allPeriods and allUnits
        // ---------------------------------------------------------------------

        this.tableName = generateTableName( name );
        
        allPeriods.addAll( periods );
        allPeriods.addAll( relativePeriods );
        
        allUnits.addAll( units );
        allUnits.addAll( relativeUnits );

        // ---------------------------------------------------------------------
        // Set name on periods
        // ---------------------------------------------------------------------

        for ( Period period : allPeriods )
        {
            if ( period.getName() == null ) // Crosstabulated relative periods
            {
                period.setName( i18nFormat.formatPeriod( period ) ); // Static periods + indexed relative periods
                period.setShortName( i18nFormat.formatPeriod( period ) );
            }
        }
        
        // ---------------------------------------------------------------------
        // Init dimensions
        // ---------------------------------------------------------------------

        if ( isDimensional() )
        {
            categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>( categoryCombo.getOptionCombos() );
            
            verify( nonEmptyLists( categoryOptionCombos ) == 1, "Category option combos size must be larger than 0" );
        }
        
        // ---------------------------------------------------------------------
        // Init indexColumns and selectColumns
        // ---------------------------------------------------------------------

        if ( isDoIndicators() )
        {
            crossTabIndicators = new ArrayList<IdentifiableObject>();
            crossTabIndicators.addAll( indicators );
            crossTabIndicators.addAll( dataElements );
            crossTabIndicators.addAll( dataSets );
            reportIndicators.add( null );
            selectColumns.add( MODE_ID_MAP.get( mode ) );
        }
        else
        {
            crossTabIndicators.add( null );
            reportIndicators = new ArrayList<IdentifiableObject>();
            reportIndicators.addAll( indicators );
            reportIndicators.addAll( dataElements );
            reportIndicators.addAll( dataSets );
            indexColumns.add( MODE_ID_MAP.get( mode ) );
            indexNameColumns.add( MODE_NAME_MAP.get( mode ) );
        }
        
        if ( isDimensional() ) // Category options will be crosstab if dimensional
        {
            reportCategoryOptionCombos.add( null );
            
            if ( listIsNonEmpty( categoryOptionCombos ) ) // Optional dimension
            {
                crossTabCategoryOptionCombos = categoryOptionCombos;
                selectColumns.add( CATEGORYCOMBO_ID );
            }
            else
            {
                crossTabCategoryOptionCombos.add( null );
            }
        }
        else
        {
            crossTabCategoryOptionCombos.add( null );
            
            if ( listIsNonEmpty( categoryOptionCombos ) ) // Optional dimension
            {
                reportCategoryOptionCombos = categoryOptionCombos;
                indexColumns.add( CATEGORYCOMBO_ID );
                indexNameColumns.add( CATEGORYCOMBO_NAME );
            }
            else
            {
                reportCategoryOptionCombos.add( null );
            }
        }
        
        if ( isDoPeriods() )
        {
            crossTabPeriods = new ArrayList<Period>( allPeriods );
            reportPeriods.add( null );
            selectColumns.add( PERIOD_ID );
        }
        else
        {
            crossTabPeriods.add( null );
            reportPeriods = new ArrayList<Period>( removeDuplicates( allPeriods ) ); // Remove potential duplicates from relative periods / params
            indexColumns.add( PERIOD_ID );
            indexNameColumns.add( PERIOD_NAME );
        }
        
        if ( isDoUnits() )
        {
            crossTabUnits = new ArrayList<OrganisationUnit>( allUnits );
            reportUnits.add( null );
            selectColumns.add( ORGANISATIONUNIT_ID );
        }
        else
        {
            crossTabUnits.add( null );
            reportUnits = new ArrayList<OrganisationUnit>( removeDuplicates( allUnits ) ); // Remove potential duplicates from params
            indexColumns.add( ORGANISATIONUNIT_ID );
            indexNameColumns.add( ORGANISATIONUNIT_NAME );
        }

        // ---------------------------------------------------------------------
        // Init crossTabColumns and crossTabIdentifiers
        // ---------------------------------------------------------------------

        for ( IdentifiableObject indicator : crossTabIndicators )
        {
            for ( DataElementCategoryOptionCombo categoryOptionCombo : crossTabCategoryOptionCombos )
            {
                for ( Period period : crossTabPeriods )
                {
                    for ( OrganisationUnit unit : crossTabUnits )
                    {
                        String columnName = getColumnName( indicator, categoryOptionCombo, period, unit, i18nFormat );
                        String prettyColumnName = getPrettyColumnName( indicator, categoryOptionCombo, period, unit );
                        String columnIdentifier = getColumnIdentifier( indicator, categoryOptionCombo, period, unit );
                        
                        if ( columnName != null && !columnName.isEmpty() )
                        {
                            crossTabColumns.add( columnName );
                            prettyCrossTabColumns.put( columnName, prettyColumnName );    
                        }
                        
                        crossTabIdentifiers.add( columnIdentifier );
                    }
                }
            }
        }

        // ---------------------------------------------------------------------
        // Init dimensionOptions and dimensionOptionColumns
        // ---------------------------------------------------------------------

        if ( doTotal() )
        {
            verify ( nonEmptyLists( categoryCombo.getCategories() ) == 1, "Category combo categories size must be larger than 0" );
            
            for ( DataElementCategory category : categoryCombo.getCategories() )
            {
                for ( DataElementCategoryOption categoryOption : category.getCategoryOptions() )
                {
                    String columnName = databaseEncode( TOTAL_COLUMN_PREFIX + categoryOption.getName() );
                    String prettyColumnName = TOTAL_COLUMN_PRETTY_PREFIX + categoryOption.getName();
                    
                    categoryOptions.add( categoryOption );
                    categoryOptionColumns.add( columnName );
                    prettyCrossTabColumns.put( columnName, prettyColumnName );
                }
            }
            
            verify( nonEmptyLists( categoryOptions, categoryOptionColumns ) == 2, "Category options size must be larger than 0" );
        }
    }

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------
    
    /**
     * Returns a list of names of all columns for this ReportTable.
     */
    public List<String> getAllColumns()
    {
        List<String> columns = new ArrayList<String>();
        
        columns.addAll( getIndexColumns() );
        columns.addAll( getIndexNameColumns() );
        columns.add( ReportTable.REPORTING_MONTH_COLUMN_NAME );
        columns.add( ReportTable.PARAM_ORGANISATIONUNIT_COLUMN_NAME );
        columns.addAll( getCrossTabColumns() );
        columns.addAll( getCategoryOptionColumns() );
        
        if ( doTotal() )
        {
            columns.add( TOTAL_COLUMN_NAME );
        }
        
        if ( isRegression() )
        {
            for ( String regressionColumn : getCrossTabColumns() )
            {
                columns.add( REGRESSION_COLUMN_PREFIX + regressionColumn );
            }
        }
        
        return columns;
    }
    
    /**
     * Generates a pretty-print name of the argument column name.
     */
    public String prettyPrintColumn( String column )
    {
        column = column.replaceAll( "_", " " );
        return column.substring( 0, 1 ).toUpperCase() + column.substring( 1, column.length() );
    }
    
    /**
     * Tests whether this ReportTable contains regression columns.
     */
    public boolean isRegression()
    {
        return regression != null && regression;
    }
    
    /**
     * Updates the existing table name with the current name.
     */
    public void updateExistingTableName()
    {
        this.existingTableName = generateTableName( name );
    }
    
    /**
     * Tests whether the Indicator dimension will be crosstabulated for this ReportTable.
     */
    public boolean isDoIndicators()
    {
        return doIndicators != null && doIndicators;
    }

    /**
     * Tests whether the Period dimension will be crosstabulated for this ReportTable.
     */
    public boolean isDoPeriods()
    {
        return doPeriods != null && doPeriods;
    }

    /**
     * Tests whether the OrganisationUnit dimension will be crosstabulated for this ReportTable.
     */
    public boolean isDoUnits()
    {
        return doUnits != null && doUnits;
    }

    /**
     * Tests whether this ReportTable is multi-dimensional.
     */
    public boolean isDimensional()
    {
        return categoryCombo != null;
    }
        
    /**
     * Tests whether a total column should be included.
     */
    public boolean doTotal()
    {
        return !isDoIndicators() && !isDoPeriods() && !isDoUnits() && 
            isDimensional() && mode.equals( MODE_DATAELEMENTS );
    }
    
    /**
     * Returns a List containing index and select columns.
     */
    public List<String> getIndexAndSelectColumns()
    {
        List<String> columns = new ArrayList<String>( indexColumns );
        columns.addAll( selectColumns );
        return columns;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Generates a prefixed, database encoded name.
     */
    private static String generateTableName( String name )
    {
        return TABLE_PREFIX + databaseEncode( name );
    }
    
    /**
     * Returns the number of empty lists among the argument lists.
     */
    private static int nonEmptyLists( List<?>... lists )
    {
        int nonEmpty = 0;
        
        for ( List<?> list : lists )
        {
            if ( list != null && list.size() > 0 )
            {
                ++nonEmpty;
            }
        }
        
        return nonEmpty;
    }
    
    /**
     * Tests whether the argument list is not null and has no elements.
     */
    private static boolean listIsNonEmpty( List<?> list )
    {
        return list != null && list.size() > 0;
    }
    
    private static String getPrettyColumnName( IdentifiableObject... objects )
    {
        StringBuffer buffer = new StringBuffer();
        
        for ( IdentifiableObject object : objects )
        {
            buffer.append( object != null ? ( object.getShortName() + SPACE ) : EMPTY );
        }
        
        return buffer.length() > 0 ? buffer.substring( 0, buffer.lastIndexOf( SPACE ) ) : buffer.toString();
    }
    
    /**
     * Generates a column name based on short-names of the argument objects. Null 
     * arguments are ignored in the name.
     */
    private static String getColumnName( IdentifiableObject metaObject, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit unit, I18nFormat format )
    {
        StringBuffer buffer = new StringBuffer();
        
        if ( metaObject != null )
        {
            buffer.append( metaObject.getShortName() + SEPARATOR );
        }
        if ( categoryOptionCombo != null )
        {
            buffer.append( categoryOptionCombo.getShortName() + SEPARATOR );
        }
        if ( period != null )
        {
            buffer.append( period.getName() + SEPARATOR );
        }
        if ( unit != null )
        {
            buffer.append( unit.getShortName() + SEPARATOR );
        }

        String column = databaseEncode( buffer.toString() );
        
        return column.length() > 0 ? column.substring( 0, column.lastIndexOf( SEPARATOR ) ) : column;
    }
        
    /**
     * Generates a column identifier based on the internal identifiers of the
     * argument objects. Null arguments are ignored in the identifier. 
     */
    private static String getColumnIdentifier( IdentifiableObject... objects )
    {
        StringBuffer buffer = new StringBuffer();

        for ( IdentifiableObject object : objects )
        {
            buffer.append( object != null ? ( object.getId() + SEPARATOR ) : EMPTY );
        }
        
        return buffer.length() > 0 ? buffer.substring( 0, buffer.lastIndexOf( SEPARATOR ) ) : buffer.toString();
    }
    
    /**
     * Database encodes the argument string. Remove non-character data from the
     * string, prefixes the string if it starts with a numeric character and
     * truncates the string if it is longer than 255 characters.
     */
    private static String databaseEncode( String string )
    {
        if ( string != null )
        {
            string = string.toLowerCase();
            
            string = string.replaceAll( " ", EMPTY_REPLACEMENT );
            string = string.replaceAll( "-", EMPTY );
            string = string.replaceAll( "<", EMPTY_REPLACEMENT + "lt" + EMPTY_REPLACEMENT );
            string = string.replaceAll( ">", EMPTY_REPLACEMENT + "gt" + EMPTY_REPLACEMENT );
            
            StringBuffer buffer = new StringBuffer();
            
            Pattern pattern = Pattern.compile( "[a-zA-Z0-9_]" );
            
            Matcher matcher = pattern.matcher( string );
            
            while ( matcher.find() )
            {
                buffer.append( matcher.group() );
            }
            
            string = buffer.toString();
            
            string = string.replaceAll( EMPTY_REPLACEMENT + "+", EMPTY_REPLACEMENT );

            // -----------------------------------------------------------------
            // Cannot start with numeric character
            // -----------------------------------------------------------------

            if ( string.length() > 0 && string.substring( 0, 1 ).matches( REGEX_NUMERIC ) )
            {
                string = SEPARATOR + string;
            }

            // -----------------------------------------------------------------
            // Cannot be longer than 255 characters
            // -----------------------------------------------------------------

            if ( string.length() > 255 )
            {
                string = string.substring( 0, 255 );
            }
        }
        
        return string;
    }

    /**
     * Removes duplicates from the given list while maintaining the order.
     */
    private static <T> List<T> removeDuplicates( List<T> list )
    {
        final List<T> temp = new ArrayList<T>( list );
        Collections.reverse( temp );        
        list.clear();
        
        for ( T object : temp )
        {
            if ( !list.contains( object ) )
            {
                list.add( object );
            }
        }
        
        return list;
    }
    
    /**
     * Supportive method.
     */
    private static void verify( boolean expression, String falseMessage )
    {
        if ( !expression )
        {
            throw new IllegalStateException( falseMessage );
        }   
    }
    
    // -------------------------------------------------------------------------
    // Equals and hashCode
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        
        int result = 1;
        
        result = PRIME * result + ( ( name == null ) ? 0 : name.hashCode() );
        
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final ReportTable other = (ReportTable) object;
        
        return name.equals( other.getName() );
    }
    
    // -------------------------------------------------------------------------
    // Get- and set-methods for persisted properties
    // -------------------------------------------------------------------------

    public String getTableName()
    {
        return tableName;
    }
    
    public void setTableName( String tableName )
    {
        this.tableName = tableName;
    }
    
    public String getExistingTableName()
    {
        return existingTableName;
    }

    public void setExistingTableName( String existingTableName )
    {
        this.existingTableName = existingTableName;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public Boolean getRegression()
    {
        return regression;
    }

    public void setRegression( Boolean regression )
    {
        this.regression = regression;
    }
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<DataElement> dataElements )
    {
        this.dataElements = dataElements;
    }

    public List<DataElementCategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    public void setCategoryOptionCombos( List<DataElementCategoryOptionCombo> categoryOptionCombos )
    {
        this.categoryOptionCombos = categoryOptionCombos;
    }
    
    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<Indicator> indicators )
    {
        this.indicators = indicators;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    public void setDataSets( List<DataSet> dataSets )
    {
        this.dataSets = dataSets;
    }

    public void setPeriods( List<Period> periods )
    {
        this.periods = periods;
    }

    public List<OrganisationUnit> getUnits()
    {
        return units;
    }

    public void setUnits( List<OrganisationUnit> units )
    {
        this.units = units;
    }

    public DataElementCategoryCombo getCategoryCombo()
    {
        return categoryCombo;
    }

    public void setCategoryCombo( DataElementCategoryCombo categoryCombo )
    {
        this.categoryCombo = categoryCombo;
    }

    public Boolean getDoIndicators()
    {
        return doIndicators;
    }

    public void setDoIndicators( Boolean doIndicators )
    {
        this.doIndicators = doIndicators;
    }
    
    public Boolean getDoPeriods()
    {
        return doPeriods;
    }

    public void setDoPeriods( Boolean doPeriods )
    {
        this.doPeriods = doPeriods;
    }

    public Boolean getDoUnits()
    {
        return doUnits;
    }

    public void setDoUnits( Boolean doUnits )
    {
        this.doUnits = doUnits;
    }
    
    public RelativePeriods getRelatives()
    {
        return relatives;
    }

    public void setRelatives( RelativePeriods relatives )
    {
        this.relatives = relatives;
    }

    public ReportParams getReportParams()
    {
        return reportParams;
    }

    public void setReportParams( ReportParams reportParams )
    {
        this.reportParams = reportParams;
    }
    
    // -------------------------------------------------------------------------
    // Get- and set-methods for transient properties
    // -------------------------------------------------------------------------

    public List<Period> getRelativePeriods()
    {
        return relativePeriods;
    }

    public void setRelativePeriods( List<Period> relativePeriods )
    {
        this.relativePeriods = relativePeriods;
    }

    public List<Period> getAllPeriods()
    {
        return allPeriods;
    }

    public List<OrganisationUnit> getRelativeUnits()
    {
        return relativeUnits;
    }

    public void setRelativeUnits( List<OrganisationUnit> relativeUnits )
    {
        this.relativeUnits = relativeUnits;
    }

    public List<OrganisationUnit> getAllUnits()
    {
        return allUnits;
    }
    
    public I18nFormat getI18nFormat()
    {
        return i18nFormat;
    }

    public void setI18nFormat( I18nFormat format )
    {
        i18nFormat = format;
    }

    public List<IdentifiableObject> getReportIndicators()
    {
        return reportIndicators;
    }

    public List<DataElementCategoryOptionCombo> getReportCategoryOptionCombos()
    {
        return reportCategoryOptionCombos;
    }

    public List<Period> getReportPeriods()
    {
        return reportPeriods;
    }

    public List<OrganisationUnit> getReportUnits()
    {
        return reportUnits;
    }
    
    public List<String> getIndexColumns()
    {
        return indexColumns;
    }

    public List<String> getIndexNameColumns()
    {
        return indexNameColumns;
    }

    public List<String> getSelectColumns()
    {
        return selectColumns;
    }

    public List<String> getCrossTabColumns()
    {
        return crossTabColumns;
    }

    public Map<String, String> getPrettyCrossTabColumns()
    {
        return prettyCrossTabColumns;
    }

    public List<String> getCrossTabIdentifiers()
    {
        return crossTabIdentifiers;
    }

    public String getReportingMonthName()
    {
        return reportingMonthName;
    }

    public void setReportingMonthName( String reportingMonthName )
    {
        this.reportingMonthName = reportingMonthName;
    }
    
    public String getOrganisationUnitName()
    {
        return organisationUnitName;
    }

    public void setOrganisationUnitName( String organisationUnitName )
    {
        this.organisationUnitName = organisationUnitName;
    }

    public List<DataElementCategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }
    
    public List<String> getCategoryOptionColumns()
    {
        return categoryOptionColumns;
    }
}

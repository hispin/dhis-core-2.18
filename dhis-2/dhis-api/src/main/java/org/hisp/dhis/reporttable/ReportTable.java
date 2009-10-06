package org.hisp.dhis.reporttable;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTable
    implements Serializable
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
    
    public static final String SEPARATOR = "_";
    public static final String SPACE = " ";
    
    public static final String MODE_DATAELEMENTS = "dataelements";
    public static final String MODE_INDICATORS = "indicators";
    public static final String MODE_DATASETS = "datasets";
    
    public static final String REGRESSION_COLUMN_PREFIX = "regression_";
    
    private static final String EMPTY_REPLACEMENT = "_";    
    private static final String TABLE_PREFIX = "_report_";
    private static final String REGEX_NUMERIC = "([0-9]*)";

    // -------------------------------------------------------------------------
    // Persisted properties
    // -------------------------------------------------------------------------

    private int id;
    
    private String name;

    private String tableName;
    
    private String existingTableName;
    
    private String mode;
    
    private Boolean regression;

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    private List<DataElementCategoryOptionCombo> categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
    
    private List<Indicator> indicators = new ArrayList<Indicator>();
    
    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    private List<Period> periods = new ArrayList<Period>();
    
    private List<OrganisationUnit> units = new ArrayList<OrganisationUnit>();

    private Boolean doIndicators;
    
    private Boolean doCategoryOptionCombos;
    
    private Boolean doPeriods;
    
    private Boolean doUnits;

    private RelativePeriods relatives;

    private ReportParams reportParams;

    private List<ReportTableColumn> displayColumns = new ArrayList<ReportTableColumn>();
    
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
    private Map<String, String> prettyCrossTabColumns = new HashMap<String, String>();
    
    /**
     * Generated unique identifiers used to retrieve the corresponding value from the datavalue table. 
     */
    private List<String> crossTabIdentifiers = new ArrayList<String>();
    
    /**
     * The I18nFormat used for internationalization of ie. periods.
     */
    private I18nFormat i18nFormat;
    
    /**
     * The name of the reporting month.
     */
    private String reportingMonthName;

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
     * @param categoryOptionCombos the category option combos.
     * @param periods the periods. These periods cannot have the name property set.
     * @param relativePeriods the relative periods. These periods must have the name property set. Not persisted.
     * @param units the organisation units.
     * @param relativeUnits the organisation units. Not persisted.
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
        List<DataElementCategoryOptionCombo> categoryOptionCombos,
        List<Period> periods,
        List<Period> relativePeriods,
        List<OrganisationUnit> units,
        List<OrganisationUnit> relativeUnits,
        boolean doIndicators,
        boolean doCategoryOptionCombos,
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
        this.categoryOptionCombos = categoryOptionCombos;
        this.periods = periods;
        this.relativePeriods = relativePeriods;
        this.units = units;
        this.relativeUnits = relativeUnits;
        this.doIndicators = doIndicators;
        this.doCategoryOptionCombos = doCategoryOptionCombos;
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
        if ( nonEmptyLists( dataElements, indicators, dataSets ) > 1 )
        {
            throw new IllegalArgumentException( "ReportTable cannot contain more than one out of dataelements, indicators, and datasets" );
        }
        
        if ( listIsNonEmpty( categoryOptionCombos ) && ( mode != null && !mode.equalsIgnoreCase( MODE_DATAELEMENTS ) ) )
        {
            throw new IllegalArgumentException( "ReportTable cannot contain category option combos when not in dataelement mode" );
        }
        
        // ---------------------------------------------------------------------
        // Init tableName, allPeriods and allUnits
        // ---------------------------------------------------------------------

        this.tableName = generateTableName( name );
        
        allPeriods.addAll( periods );
        allPeriods.addAll( relativePeriods );
        
        allUnits.addAll( units );
        allUnits.addAll( relativeUnits );
        
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
            selectColumns.add( getIdentifier( mode ) );
        }
        else
        {
            crossTabIndicators.add( null );
            reportIndicators = new ArrayList<IdentifiableObject>();
            reportIndicators.addAll( indicators );
            reportIndicators.addAll( dataElements );
            reportIndicators.addAll( dataSets );
            indexColumns.add( getIdentifier( mode ) );
            indexNameColumns.add( getName( mode ) );
        }
        
        if ( isDoCategoryOptionCombos() )
        {
            reportCategoryOptionCombos.add( null );
            
            if ( listIsNonEmpty( categoryOptionCombos ) ) // Optional dimension
            {
                crossTabCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>( categoryOptionCombos );
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
                reportCategoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>( categoryOptionCombos );
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
            reportPeriods = new ArrayList<Period>( allPeriods );
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
            reportUnits = new ArrayList<OrganisationUnit>( allUnits );
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
                        String columnName = getColumnName( indicator, categoryOptionCombo, period, unit );
                        String prettyColumnName = getPrettyColumnName( indicator, categoryOptionCombo, period, unit );
                        String columnIdentifier = getColumnIdentifier( indicator, categoryOptionCombo, period, unit );
                        
                        crossTabColumns.add( columnName );
                        prettyCrossTabColumns.put( columnName, prettyColumnName );
                        crossTabIdentifiers.add( columnIdentifier );
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Public methods
    // -------------------------------------------------------------------------

    public List<ReportTableColumn> getFilledDisplayColumns()
    {
        List<String> columns = getAllColumns();
        
        List<ReportTableColumn> displayColumns = new ArrayList<ReportTableColumn>( getDisplayColumns() );
        
        for ( String column : columns )
        {
            if ( !hasDisplayColumn( column ) )
            {
                String prettyColumn = prettyCrossTabColumns.get( column ) != null ? 
                    prettyCrossTabColumns.get( column ) : prettyPrintColumn( column );
                    
                ReportTableColumn displayColumn = new ReportTableColumn();
                
                displayColumn.setName( column );
                displayColumn.setHeader( prettyColumn );
                displayColumn.setHidden( false );
                
                displayColumns.add( displayColumn );
            }
        }
        
        return displayColumns;
    }
    
    public List<String> getAllColumns()
    {
        List<String> columns = new ArrayList<String>();
        
        columns.addAll( getIndexColumns() );
        columns.addAll( getIndexNameColumns() );
        columns.add( ReportTable.REPORTING_MONTH_COLUMN_NAME );
        columns.addAll( getCrossTabColumns() );
        
        if ( isRegression() )
        {
            for ( String regressionColumn : getCrossTabColumns() )
            {
                columns.add( REGRESSION_COLUMN_PREFIX + regressionColumn );
            }
        }
        
        return columns;
    }
    
    public boolean hasDisplayColumn( String name )
    {
        for ( ReportTableColumn column : displayColumns )
        {
            if ( column.getName().equals( name ) && column.getHeader() != null && column.getHeader().trim().length() > 0 )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public String prettyPrintColumn( String column )
    {
        column = column.replaceAll( "_", " " );
        return column.substring( 0, 1 ).toUpperCase() + column.substring( 1, column.length() );
    }
    
    public boolean isRegression()
    {
        return regression != null && regression;
    }
    
    public void updateExistingTableName()
    {
        this.existingTableName = generateTableName( name );
    }
    
    public boolean hasCategoryOptionCombos()
    {
        return categoryOptionCombos != null && categoryOptionCombos.size() > 0;
    }
    
    public boolean isDoIndicators()
    {
        return doIndicators != null && doIndicators;
    }
    
    public boolean isDoCategoryOptionCombos()
    {
        return doCategoryOptionCombos != null && doCategoryOptionCombos;
    }
    
    public boolean isDoPeriods()
    {
        return doPeriods != null && doPeriods;
    }
    
    public boolean isDoUnits()
    {
        return doUnits != null && doUnits;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String generateTableName( String name )
    {
        return TABLE_PREFIX + databaseEncode( name );
    }

    private String getIdentifier( String mode )
    {
        if ( mode == null || mode.equals( MODE_INDICATORS ) )
        {
            return INDICATOR_ID;
        }
        else if ( mode.equals( MODE_DATAELEMENTS ) )
        {
            return DATAELEMENT_ID;
        }
        else if ( mode.equals( MODE_DATASETS ) )
        {
            return DATASET_ID;
        }
        
        return null;
    }
    
    private String getName( String mode )
    {
        if ( mode == null || mode.equals( MODE_INDICATORS ) )
        {
            return INDICATOR_NAME;
        }
        else if ( mode.equals( MODE_DATAELEMENTS ) )
        {
            return DATAELEMENT_NAME;
        }
        else if ( mode.equals( MODE_DATASETS ) )
        {
            return DATASET_NAME;
        }
        
        return null;
    }
    
    private int nonEmptyLists( List<?>... lists )
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
    
    private boolean listIsNonEmpty( List<?> list )
    {
        return list != null && list.size() > 0;
    }
    
    private String getPrettyColumnName( IdentifiableObject metaObject, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit unit )
    {
        StringBuffer buffer = new StringBuffer();
        
        if ( metaObject != null )
        {
            buffer.append( metaObject.getShortName() + SPACE );
        }
        if ( categoryOptionCombo != null )
        {
            buffer.append( categoryOptionCombo.getShortName() + SPACE );
        }
        if ( period != null )
        {
            String periodName = period.getName() != null ? period.getName() : i18nFormat.formatPeriod( period );
            
            buffer.append( periodName + SPACE );
        }
        if ( unit != null )
        {
            buffer.append( unit.getShortName() + SPACE );
        }

        return buffer.length() > 0 ? buffer.substring( 0, buffer.lastIndexOf( SPACE ) ) : buffer.toString();
    }
    
    private String getColumnName( IdentifiableObject metaObject, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit unit )
    {
        StringBuffer buffer = new StringBuffer();
        
        if ( metaObject != null )
        {
            buffer.append( databaseEncode( metaObject.getShortName() ) + SEPARATOR );
        }
        if ( categoryOptionCombo != null )
        {
            buffer.append( databaseEncode( categoryOptionCombo.getShortName() ) + SEPARATOR );
        }
        if ( period != null )
        {
            String periodName = period.getName() != null ? period.getName() : i18nFormat.formatPeriod( period );
            
            buffer.append( databaseEncode( periodName ) + SEPARATOR );
        }
        if ( unit != null )
        {
            buffer.append( databaseEncode( unit.getShortName() ) + SEPARATOR );
        }

        // ---------------------------------------------------------------------
        // Columns cannot start with numeric character
        // ---------------------------------------------------------------------

        if ( buffer.length() > 0 && buffer.substring( 0, 1 ).matches( REGEX_NUMERIC ) )
        {
            buffer.insert( 0, SEPARATOR );
        }
        
        return buffer.length() > 0 ? buffer.substring( 0, buffer.lastIndexOf( SEPARATOR ) ) : buffer.toString();
    }
    
    private String getColumnIdentifier( IdentifiableObject metaObject, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit unit )
    {
        StringBuffer buffer = new StringBuffer();

        if ( metaObject != null )
        {
            buffer.append( metaObject.getId() + SEPARATOR );
        }
        if ( categoryOptionCombo != null )
        {
            buffer.append( categoryOptionCombo.getId() + SEPARATOR );
        }
        if ( period != null )
        {
            buffer.append( period.getId() + SEPARATOR );
        }
        if ( unit != null )
        {
            buffer.append( unit.getId() + SEPARATOR );
        }

        return buffer.length() > 0 ? buffer.substring( 0, buffer.lastIndexOf( SEPARATOR ) ) : buffer.toString();
    }
    
    private String databaseEncode( String string )
    {
        if ( string != null )
        {
            string = string.toLowerCase();
            
            string = string.replaceAll( " ", EMPTY_REPLACEMENT );
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
            
            if ( string.length() > 255 )
            {
                string = string.substring( 0, 255 );
            }
        }
        
        return string;
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

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName( String name )
    {
        this.name = name;
    }

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

    public Boolean getDoIndicators()
    {
        return doIndicators;
    }

    public void setDoIndicators( Boolean doIndicators )
    {
        this.doIndicators = doIndicators;
    }

    public Boolean getDoCategoryOptionCombos()
    {
        return doCategoryOptionCombos;
    }

    public void setDoCategoryOptionCombos( Boolean doCategoryOptionCombos )
    {
        this.doCategoryOptionCombos = doCategoryOptionCombos;
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

    public List<ReportTableColumn> getDisplayColumns()
    {
        return displayColumns;
    }

    public void setDisplayColumns( List<ReportTableColumn> displayColumns )
    {
        this.displayColumns = displayColumns;
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
}

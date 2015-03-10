package org.hisp.dhis.resourcetable;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.dataapproval.DataApprovalLevelService.APPROVAL_LEVEL_UNAPPROVED;
import static org.hisp.dhis.resourcetable.ResourceTableStore.TABLE_NAME_CATEGORY_OPTION_COMBO_NAME;
import static org.hisp.dhis.resourcetable.ResourceTableStore.TABLE_NAME_DATA_ELEMENT_STRUCTURE;
import static org.hisp.dhis.resourcetable.ResourceTableStore.TABLE_NAME_DATE_PERIOD_STRUCTURE;
import static org.hisp.dhis.resourcetable.ResourceTableStore.TABLE_NAME_ORGANISATION_UNIT_STRUCTURE;
import static org.hisp.dhis.resourcetable.ResourceTableStore.TABLE_NAME_PERIOD_STRUCTURE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.resourcetable.statement.CreateCategoryOptionGroupSetTableStatement;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.sqlview.SqlViewService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 */
public class DefaultResourceTableService
    implements ResourceTableService
{
    private static final Log log = LogFactory.getLog( DefaultResourceTableService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ResourceTableStore resourceTableStore;

    public void setResourceTableStore( ResourceTableStore resourceTableStore )
    {
        this.resourceTableStore = resourceTableStore;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private SqlViewService sqlViewService;

    public void setSqlViewService( SqlViewService sqlViewService )
    {
        this.sqlViewService = sqlViewService;
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateOrganisationUnitStructures()
    {
        int maxLevel = organisationUnitService.getMaxOfOrganisationUnitLevels();

        log.info( "Using " + maxLevel + " organisation unit levels for org unit structure table" );
        
        resourceTableStore.createOrganisationUnitStructure( maxLevel );

        List<Object[]> batchArgs = new ArrayList<>();

        for ( int i = 0; i < maxLevel; i++ )
        {
            int level = i + 1;

            Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnitsAtLevel( level );

            for ( OrganisationUnit unit : units )
            {
                List<Object> values = new ArrayList<>();

                values.add( unit.getId() );
                values.add( unit.getUid() );
                values.add( level );

                Map<Integer, Integer> identifiers = new HashMap<>();
                Map<Integer, String> uids = new HashMap<>();

                for ( int j = level; j > 0; j-- )
                {
                    identifiers.put( j, unit.getId() );
                    uids.put( j, unit.getUid() );

                    unit = unit.getParent();
                }

                for ( int k = 1; k <= maxLevel; k++ )
                {
                    values.add( identifiers.get( k ) != null ? identifiers.get( k ) : null );
                    values.add( uids.get( k ) );
                }

                batchArgs.add( values.toArray() );
            }
        }

        resourceTableStore.batchUpdate( (maxLevel * 2) + 3, TABLE_NAME_ORGANISATION_UNIT_STRUCTURE, batchArgs );

        log.info( "Organisation unit structure table generated" );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateCategoryOptionComboNames()
    {
        resourceTableStore.createDataElementCategoryOptionComboName();

        Collection<DataElementCategoryCombo> combos = categoryService.getAllDataElementCategoryCombos();

        List<Object[]> batchArgs = new ArrayList<>();

        for ( DataElementCategoryCombo combo : combos )
        {
            for ( DataElementCategoryOptionCombo coc : combo.getSortedOptionCombos() )
            {
                List<Object> values = new ArrayList<>();

                values.add( coc.getId() );
                values.add( coc.getName() );

                batchArgs.add( values.toArray() );
            }
        }

        resourceTableStore.batchUpdate( 2, TABLE_NAME_CATEGORY_OPTION_COMBO_NAME, batchArgs );

        log.info( "Category option combo name table generated" );
    }

    @Override
    @Transactional
    public void generateCategoryOptionGroupSetTable()
    {
        // ---------------------------------------------------------------------
        // Create table
        // ---------------------------------------------------------------------

        List<DataElementCategoryOptionCombo> categoryOptionCombos =
            new ArrayList<>( categoryService.getAllDataElementCategoryOptionCombos() );

        List<CategoryOptionGroupSet> groupSets = new ArrayList<>( categoryService.getAllCategoryOptionGroupSets() );

        Collections.sort( groupSets, IdentifiableObjectNameComparator.INSTANCE );

        resourceTableStore.createCategoryOptionGroupSetStructure( groupSets );

        // ---------------------------------------------------------------------
        // Populate table
        // ---------------------------------------------------------------------

        List<Object[]> batchArgs = new ArrayList<>();

        for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
        {
            List<Object> values = new ArrayList<>();

            values.add( categoryOptionCombo.getId() );

            for ( CategoryOptionGroupSet groupSet : groupSets )
            {
                CategoryOptionGroup group = groupSet.getGroup( categoryOptionCombo );

                values.add( group != null ? group.getName() : null );
                values.add( group != null ? group.getUid() : null );
            }

            batchArgs.add( values.toArray() );
        }

        resourceTableStore.batchUpdate( (groupSets.size() * 2) + 1, CreateCategoryOptionGroupSetTableStatement.TABLE_NAME, batchArgs );

        log.info( "Category option group set table generated" );
    }

    // -------------------------------------------------------------------------
    // DataElementGroupSetTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateDataElementGroupSetTable()
    {
        List<DataElementGroupSet> groupSets = new ArrayList<>( dataElementService.getDataDimensionDataElementGroupSets() );

        Collections.sort( groupSets, IdentifiableObjectNameComparator.INSTANCE );

        resourceTableStore.createDataElementGroupSetStructure( groupSets );

        resourceTableStore.populateDataElementGroupSetStructure( groupSets );

        log.info( "Data element group set table generated" );
    }

    // -------------------------------------------------------------------------
    // IndicatorGroupSetTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateIndicatorGroupSetTable()
    {
        List<IndicatorGroupSet> groupSets = new ArrayList<>( indicatorService.getAllIndicatorGroupSets() );

        Collections.sort( groupSets, IdentifiableObjectNameComparator.INSTANCE );

        resourceTableStore.createIndicatorGroupSetStructure( groupSets );

        resourceTableStore.populateIndicatorGroupSetStructure( groupSets );

        log.info( "Indicator group set table generated" );
    }

    // -------------------------------------------------------------------------
    // OrganisationUnitGroupSetTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateOrganisationUnitGroupSetTable()
    {
        List<OrganisationUnitGroupSet> groupSets = new ArrayList<>(
            organisationUnitGroupService.getDataDimensionOrganisationUnitGroupSets() );

        Collections.sort( groupSets, IdentifiableObjectNameComparator.INSTANCE );

        resourceTableStore.createOrganisationUnitGroupSetStructure( groupSets );

        resourceTableStore.populateOrganisationUnitGroupSetStructure( groupSets );

        log.info( "Organisation unit group set table generated" );
    }

    // -------------------------------------------------------------------------
    // CategoryTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateCategoryTable()
    {
        // ---------------------------------------------------------------------
        // Create table - only using data dimension categories
        // ---------------------------------------------------------------------

        List<DataElementCategory> categories = new ArrayList<>( categoryService.getDataDimensionDataElementCategories() );

        Collections.sort( categories, IdentifiableObjectNameComparator.INSTANCE );

        resourceTableStore.createCategoryStructure( categories );

        resourceTableStore.populateCategoryStructure( categories );

        log.info( "Category table generated" );
    }

    // -------------------------------------------------------------------------
    // DataElementTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateDataElementTable()
    {
        // ---------------------------------------------------------------------
        // Create table
        // ---------------------------------------------------------------------

        Collection<DataElement> dataElements = dataElementService.getAllDataElements();

        resourceTableStore.createDataElementStructure();

        // ---------------------------------------------------------------------
        // Populate table
        // ---------------------------------------------------------------------

        List<Object[]> batchArgs = new ArrayList<>();

        for ( DataElement dataElement : dataElements )
        {
            List<Object> values = new ArrayList<>();

            final DataSet dataSet = dataElement.getDataSet();
            final PeriodType periodType = dataElement.getPeriodType();

            values.add( dataElement.getId() );
            values.add( dataElement.getUid() );
            values.add( dataElement.getName() );
            values.add( dataSet != null ? dataSet.getId() : null );
            values.add( dataSet != null ? dataSet.getUid() : null );
            values.add( dataSet != null ? dataSet.getName() : null );
            values.add( dataSet != null && dataSet.isApproveData() ? APPROVAL_LEVEL_UNAPPROVED : 0 );
            values.add( periodType != null ? periodType.getId() : null );
            values.add( periodType != null ? periodType.getName() : null );

            batchArgs.add( values.toArray() );
        }

        resourceTableStore.batchUpdate( 9, TABLE_NAME_DATA_ELEMENT_STRUCTURE, batchArgs );

        log.info( "Data element table generated" );
    }

    // -------------------------------------------------------------------------
    // PeriodTable
    // -------------------------------------------------------------------------

    @Override
    public void generateDatePeriodTable()
    {
        // ---------------------------------------------------------------------
        // Create table
        // ---------------------------------------------------------------------

        resourceTableStore.createDatePeriodStructure();

        // ---------------------------------------------------------------------
        // Populate table
        // ---------------------------------------------------------------------

        List<PeriodType> periodTypes = PeriodType.getAvailablePeriodTypes();

        List<Object[]> batchArgs = new ArrayList<>();

        Date startDate = new Cal( 1975, 1, 1, true ).time(); //TODO
        Date endDate = new Cal( 2025, 1, 1, true ).time();

        List<Period> days = new DailyPeriodType().generatePeriods( startDate, endDate );

        Calendar calendar = PeriodType.getCalendar();

        for ( Period day : days )
        {
            List<Object> values = new ArrayList<>();

            values.add( day.getStartDate() );

            for ( PeriodType periodType : periodTypes )
            {
                values.add( periodType.createPeriod( day.getStartDate(), calendar ).getIsoDate() );
            }

            batchArgs.add( values.toArray() );
        }

        resourceTableStore.batchUpdate( PeriodType.PERIOD_TYPES.size() + 1, TABLE_NAME_DATE_PERIOD_STRUCTURE, batchArgs );

        log.info( "Period table generated" );
    }

    @Override
    @Transactional
    public void generatePeriodTable()
    {
        // ---------------------------------------------------------------------
        // Create table
        // ---------------------------------------------------------------------

        Collection<Period> periods = periodService.getAllPeriods();

        resourceTableStore.createPeriodStructure();

        // ---------------------------------------------------------------------
        // Populate table
        // ---------------------------------------------------------------------

        Calendar calendar = PeriodType.getCalendar();

        List<Object[]> batchArgs = new ArrayList<>();

        for ( Period period : periods )
        {
            if ( period != null && period.isValid() )
            {
                final Date startDate = period.getStartDate();
                final PeriodType rowType = period.getPeriodType();

                List<Object> values = new ArrayList<>();

                values.add( period.getId() );
                values.add( period.getIsoDate() );
                values.add( period.getDaysInPeriod() );

                for ( PeriodType periodType : PeriodType.PERIOD_TYPES )
                {
                    if ( rowType.getFrequencyOrder() <= periodType.getFrequencyOrder() )
                    {
                        values.add( IdentifiableObjectUtils.getLocalPeriodIdentifier( startDate, periodType, calendar ) );
                    }
                    else
                    {
                        values.add( null );
                    }
                }

                batchArgs.add( values.toArray() );
            }
        }

        resourceTableStore.batchUpdate( PeriodType.PERIOD_TYPES.size() + 3, TABLE_NAME_PERIOD_STRUCTURE, batchArgs );

        log.info( "Date period table generated" );
    }

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboTable
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public void generateDataElementCategoryOptionComboTable()
    {
        resourceTableStore.createAndGenerateDataElementCategoryOptionCombo();

        log.info( "Data element category option combo table generated" );
    }
    
    // -------------------------------------------------------------------------
    // SQL Views. Each view is created/dropped in separate transactions so that
    // process continues even if individual operations fail.
    // -------------------------------------------------------------------------

    @Override
    public void createAllSqlViews()
    {
        List<SqlView> views = new ArrayList<>( sqlViewService.getAllSqlViewsNoAcl() );
        Collections.sort( views, IdentifiableObjectNameComparator.INSTANCE );
        
        for ( SqlView view : views )
        {
            if ( !view.isQuery() )
            {
                sqlViewService.createViewTable( view );
            }
        }
    }

    @Override
    public void dropAllSqlViews()
    {
        List<SqlView> views = new ArrayList<>( sqlViewService.getAllSqlViewsNoAcl() );
        Collections.sort( views, IdentifiableObjectNameComparator.INSTANCE );
        Collections.reverse( views );

        for ( SqlView view : views )
        {
            if ( !view.isQuery() )
            {
                sqlViewService.dropViewTable( view );
            }
        }
    }
}

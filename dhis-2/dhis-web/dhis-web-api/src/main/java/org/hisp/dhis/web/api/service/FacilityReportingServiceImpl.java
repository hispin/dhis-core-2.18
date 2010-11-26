package org.hisp.dhis.web.api.service;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.comparator.DataElementSortOrderComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.web.api.model.DataElement;
import org.hisp.dhis.web.api.model.DataSet;
import org.hisp.dhis.web.api.model.DataSetValue;
import org.hisp.dhis.web.api.model.DataValue;
import org.hisp.dhis.web.api.model.Model;
import org.hisp.dhis.web.api.model.ModelList;
import org.hisp.dhis.web.api.model.Section;
import org.hisp.dhis.web.api.utils.LocaleUtil;
import org.springframework.beans.factory.annotation.Required;

public class FacilityReportingServiceImpl
    implements FacilityReportingService
{

    private DataElementSortOrderComparator dataElementComparator = new DataElementSortOrderComparator();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    private org.hisp.dhis.dataelement.DataElementCategoryService categoryService;

    private org.hisp.dhis.datavalue.DataValueService dataValueService;

    private org.hisp.dhis.dataset.DataSetService dataSetService;

    private org.hisp.dhis.i18n.I18nService i18nService;

    // -------------------------------------------------------------------------
    // Service methods
    // -------------------------------------------------------------------------

    public List<DataSet> getMobileDataSetsForUnit( OrganisationUnit unit, String localeString )
    {

        List<DataSet> datasets = new ArrayList<DataSet>();
        Locale locale = LocaleUtil.getLocale( localeString );

        for ( org.hisp.dhis.dataset.DataSet dataSet : dataSetService.getDataSetsForMobile( unit ) )
        {
            if ( dataSet.getPeriodType().getName().equals( "Daily" )
                || dataSet.getPeriodType().getName().equals( "Weekly" )
                || dataSet.getPeriodType().getName().equals( "Monthly" )
                || dataSet.getPeriodType().getName().equals( "Yearly" )
                || dataSet.getPeriodType().getName().equals( "Quarterly" ) )
            {
                datasets.add( getDataSetForLocale( dataSet.getId(), locale ) );
            }
        }

        return datasets;
    }

    public DataSet getDataSetForLocale( int dataSetId, Locale locale )
    {
        org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetId );
        dataSet = i18n( i18nService, locale, dataSet );
        Set<org.hisp.dhis.dataset.Section> sections = dataSet.getSections();

        // Collection<org.hisp.dhis.dataelement.DataElement> dataElements =
        // dataSet.getDataElements();

        // Mobile
        DataSet ds = new DataSet();

        ds.setId( dataSet.getId() );
        ds.setName( dataSet.getName() );
        ds.setPeriodType( dataSet.getPeriodType().getName() );

        // Mobile
        List<Section> sectionList = new ArrayList<Section>();
        ds.setSections( sectionList );

        if ( sections == null || sections.size() == 0 )
        {
            // Collection<org.hisp.dhis.dataelement.DataElement> dataElements =
            // new ArrayList<org.hisp.dhis.dataelement.DataElement>();
            List<org.hisp.dhis.dataelement.DataElement> dataElements = new ArrayList<org.hisp.dhis.dataelement.DataElement>(
                dataSet.getDataElements() );

            Collections.sort( dataElements, dataElementComparator );

            // Fake Section to store Data Elements
            Section section = new Section();

            sectionList.add( section );
            section.setId( 0 );
            section.setName( "" );

            List<DataElement> dataElementList = new ArrayList<DataElement>();
            section.setDataElements( dataElementList );

            for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
            {
                // Server DataElement
                dataElement = i18n( i18nService, locale, dataElement );
                Set<DataElementCategoryOptionCombo> deCatOptCombs = dataElement.getCategoryCombo().getOptionCombos();
                // Client DataElement
                ModelList deCateOptCombo = new ModelList();
                List<Model> listCateOptCombo = new ArrayList<Model>();
                deCateOptCombo.setModels( listCateOptCombo );

                for ( DataElementCategoryOptionCombo oneCatOptCombo : deCatOptCombs )
                {
                    Model oneCateOptCombo = new Model();
                    oneCateOptCombo.setId( oneCatOptCombo.getId() );
                    oneCateOptCombo.setName( oneCatOptCombo.getName() );
                    listCateOptCombo.add( oneCateOptCombo );
                }

                DataElement de = new DataElement();
                de.setId( dataElement.getId() );
                de.setName( dataElement.getName() );
                de.setType( dataElement.getType() );
                
                //For facility Reporting, all data element are mandetory
                de.setCompulsory( true );
                de.setCategoryOptionCombos( deCateOptCombo );
                dataElementList.add( de );
            }
        }
        else
        {
            for ( org.hisp.dhis.dataset.Section each : sections )
            {
                List<org.hisp.dhis.dataelement.DataElement> dataElements = each.getDataElements();

                Section section = new Section();
                section.setId( each.getId() );
                section.setName( each.getName() );
                // Mobile
                List<DataElement> dataElementList = new ArrayList<DataElement>();
                section.setDataElements( dataElementList );

                for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
                {
                    // Server DataElement
                    dataElement = i18n( i18nService, locale, dataElement );
                    Set<DataElementCategoryOptionCombo> deCatOptCombs = dataElement.getCategoryCombo()
                        .getOptionCombos();

                    // Client DataElement
                    ModelList deCateOptCombo = new ModelList();
                    List<Model> listCateOptCombo = new ArrayList<Model>();
                    deCateOptCombo.setModels( listCateOptCombo );

                    for ( DataElementCategoryOptionCombo oneCatOptCombo : deCatOptCombs )
                    {
                        Model oneCateOptCombo = new Model();
                        oneCateOptCombo.setId( oneCatOptCombo.getId() );
                        oneCateOptCombo.setName( oneCatOptCombo.getName() );
                        listCateOptCombo.add( oneCateOptCombo );
                    }

                    DataElement de = new DataElement();
                    de.setId( dataElement.getId() );
                    de.setName( dataElement.getName() );
                    de.setType( dataElement.getType() );
                    de.setCategoryOptionCombos( deCateOptCombo );
                    dataElementList.add( de );
                }
                sectionList.add( section );
            }
        }

        return ds;
    }

    @Override
    public String saveDataSetValues( OrganisationUnit unit, DataSetValue dataSetValue )
    {

        org.hisp.dhis.dataset.DataSet dataSet = dataSetService.getDataSet( dataSetValue.getId() );

        if ( !dataSetService.getDataSetsBySource( unit ).contains( dataSet ) )
        {
            return "INVALID_DATASET_ASSOCIATION";
        }

        org.hisp.dhis.period.Period selectedPeriod = getPeriod( dataSetValue.getpName(), dataSet );

        if ( selectedPeriod == null )
        {
            return "INVALID_PERIOD";
        }

        Collection<org.hisp.dhis.dataelement.DataElement> dataElements = dataSet.getDataElements();
        Collection<Integer> dataElementIds = new ArrayList<Integer>( dataSetValue.getDataValues().size() );

        for ( DataValue dv : dataSetValue.getDataValues() )
        {
            dataElementIds.add( dv.getId() );
        }

        // if ( dataElements.size() != dataElementIds.size() )
        // {
        // return "INVALID_DATASET";
        // }

        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap = new HashMap<Integer, org.hisp.dhis.dataelement.DataElement>();
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            if ( !dataElementIds.contains( dataElement.getId() ) )
            {
                return "INVALID_DATASET";
            }
            dataElementMap.put( dataElement.getId(), dataElement );
        }

        // Everything is fine, hence save
        saveDataValues( dataSetValue, dataElementMap, selectedPeriod, unit,
            categoryService.getDefaultDataElementCategoryOptionCombo() );

        return "SUCCESS";

    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private void saveDataValues( DataSetValue dataSetValue,
        Map<Integer, org.hisp.dhis.dataelement.DataElement> dataElementMap, Period period, OrganisationUnit orgUnit,
        DataElementCategoryOptionCombo optionCombo )
    {

        org.hisp.dhis.dataelement.DataElement dataElement;
        String value;

        for ( DataValue dv : dataSetValue.getDataValues() )
        {
            value = dv.getVal();
            DataElementCategoryOptionCombo cateOptCombo = categoryService.getDataElementCategoryOptionCombo( dv
                .getCategoryOptComboID() );
            if ( value != null && value.trim().length() == 0 )
            {
                value = null;
            }

            if ( value != null )
            {
                value = value.trim();
            }

            dataElement = dataElementMap.get( dv.getId() );
            org.hisp.dhis.datavalue.DataValue dataValue = dataValueService.getDataValue( orgUnit, dataElement, period,
                cateOptCombo );

            if ( dataValue == null )
            {
                if ( value != null )
                {
                    dataValue = new org.hisp.dhis.datavalue.DataValue( dataElement, period, orgUnit, value, "",
                        new Date(), "", cateOptCombo );
                    dataValueService.addDataValue( dataValue );
                }
            }
            else
            {
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValueService.updateDataValue( dataValue );
            }

        }
    }

    public Period getPeriod( String periodName, org.hisp.dhis.dataset.DataSet dataSet )
    {
        org.hisp.dhis.period.Period period = null;
        org.hisp.dhis.period.Period persistedPeriod = null;
        if ( dataSet.getPeriodType().getName().equals( "Daily" ) )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date = new Date();

            try
            {

                date = formatter.parse( periodName );
                DailyPeriodType dailyPeriodType = new DailyPeriodType();
                period = dailyPeriodType.createPeriod( date );

            }
            catch ( ParseException e )
            {
                e.printStackTrace();
            }
        }
        else if ( dataSet.getPeriodType().getName().equals( "Weekly" ) )
        {
            try
            {
                int week = Integer.parseInt( periodName.substring( 0, periodName.indexOf( '-' ) ) );
                int year = Integer
                    .parseInt( periodName.substring( periodName.indexOf( '-' ) + 1, periodName.length() ) );

                Calendar cal = Calendar.getInstance();
                cal.set( Calendar.YEAR, year );
                cal.set( Calendar.WEEK_OF_YEAR, week );
                cal.setFirstDayOfWeek( Calendar.MONDAY );

                WeeklyPeriodType weeklyPeriodType = new WeeklyPeriodType();
                period = weeklyPeriodType.createPeriod( cal.getTime() );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        else if ( dataSet.getPeriodType().getName().equals( "Monthly" ) )
        {
            try
            {
                int month = Integer.parseInt( periodName.substring( 0, periodName.indexOf( '-' ) ) );
                int year = Integer
                    .parseInt( periodName.substring( periodName.indexOf( '-' ) + 1, periodName.length() ) );

                Calendar cal = Calendar.getInstance();
                cal.set( Calendar.YEAR, year );
                cal.set( Calendar.MONTH, month );

                MonthlyPeriodType monthlyPeriodType = new MonthlyPeriodType();
                period = monthlyPeriodType.createPeriod( cal.getTime() );

            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        else if ( dataSet.getPeriodType().getName().equals( "Yearly" ) )
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, Integer.parseInt( periodName ) );

            YearlyPeriodType yearlyPeriodType = new YearlyPeriodType();

            period = yearlyPeriodType.createPeriod( cal.getTime() );
        }
        else if ( dataSet.getPeriodType().getName().equals( "Quarterly" ) )
        {
            Calendar cal = Calendar.getInstance();

            int month = 0;
            if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jan" ) )
            {
                month = 1;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Apr" ) )
            {
                month = 4;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jul" ) )
            {
                month = 6;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Oct" ) )
            {
                month = 10;
            }

            int year = Integer.parseInt( periodName.substring( periodName.lastIndexOf( " " ) + 1 ) );

            cal.set( Calendar.MONTH, month );
            cal.set( Calendar.YEAR, year );

            QuarterlyPeriodType quarterlyPeriodType = new QuarterlyPeriodType();
            if ( month != 0 )
            {
                period = quarterlyPeriodType.createPeriod( cal.getTime() );
            }

        }

        if ( period != null )
        {
            persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(),
                dataSet.getPeriodType() );

            if ( persistedPeriod == null )
            {
                periodService.addPeriod( period );
                persistedPeriod = periodService.getPeriod( period.getStartDate(), period.getEndDate(),
                    dataSet.getPeriodType() );
            }
        }

        return persistedPeriod;
    }

    // -------------------------------------------------------------------------
    // Dependency setters
    // -------------------------------------------------------------------------

    @Required
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    @Required
    public void setCategoryService( org.hisp.dhis.dataelement.DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @Required
    public void setDataValueService( org.hisp.dhis.datavalue.DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    @Required
    public void setDataSetService( org.hisp.dhis.dataset.DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    @Required
    public void setI18nService( org.hisp.dhis.i18n.I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

}

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

package org.hisp.dhis.light.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.minmax.validation.MinMaxValuesGenerationService;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ListUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetSectionFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataAnalysisService stdDevOutlierAnalysisService;

    public void setStdDevOutlierAnalysisService( DataAnalysisService stdDevOutlierAnalysisService )
    {
        this.stdDevOutlierAnalysisService = stdDevOutlierAnalysisService;
    }

    private DataAnalysisService minMaxOutlierAnalysisService;

    public void setMinMaxOutlierAnalysisService( DataAnalysisService minMaxOutlierAnalysisService )
    {
        this.minMaxOutlierAnalysisService = minMaxOutlierAnalysisService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private MinMaxValuesGenerationService minMaxValuesGenerationService;

    public void setMinMaxValuesGenerationService( MinMaxValuesGenerationService minMaxValuesGenerationService )
    {
        this.minMaxValuesGenerationService = minMaxValuesGenerationService;
    }

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    public Integer getOrganisationUnitId()
    {
        return organisationUnitId;
    }

    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    public String getPeriodId()
    {
        return periodId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private Map<String, String> dataValues = new HashMap<String, String>();

    public Map<String, String> getDataValues()
    {
        return dataValues;
    }

    private Map<String, String> validationErrors = new HashMap<String, String>();

    public Map<String, String> getValidationErrors()
    {
        return validationErrors;
    }

    private Boolean complete = false;

    public void setComplete( Boolean complete )
    {
        this.complete = complete;
    }

    public Boolean getComplete()
    {
        return complete;
    }

    private String page;

    public String getPage()
    {
        return page;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        Period period = periodService.getPeriodByExternalId( periodId );

        dataSet = dataSetService.getDataSet( dataSetId );

        for ( Section section : dataSet.getSections() )
        {
            for ( DataElement dataElement : section.getDataElements() )
            {
                for ( DataElementCategoryOptionCombo optionCombo : dataElement.getCategoryCombo().getOptionCombos() )
                {
                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period,
                        optionCombo );

                    String key = String.format( "DE%dOC%d", dataElement.getId(), optionCombo.getId() );
                    String value = "";

                    if ( dataValue != null )
                    {
                        value = dataValue.getValue();
                        validateDataElement( organisationUnit, dataElement, optionCombo, period, value );
                    }

                    dataValues.put( key, value );
                }
            }
        }

        CompleteDataSetRegistration registration = registrationService.getCompleteDataSetRegistration( dataSet, period,
            organisationUnit );

        complete = registration != null ? true : false;

        return SUCCESS;
    }

    @SuppressWarnings( "unchecked" )
    public void validateDataElement( OrganisationUnit organisationUnit, DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo, Period period, String value )
    {
        Collection<DeflatedDataValue> outliers;

        MinMaxDataElement minMaxDataElement = minMaxDataElementService.getMinMaxDataElement( organisationUnit,
            dataElement, optionCombo );

        if ( minMaxDataElement == null )
        {
            Double factor = (Double) systemSettingManager.getSystemSetting(
                SystemSettingManager.KEY_FACTOR_OF_DEVIATION, 2.0 );

            Collection<DeflatedDataValue> stdDevs = stdDevOutlierAnalysisService.analyse( organisationUnit,
                ListUtils.getCollection( dataElement ), ListUtils.getCollection( period ), factor );

            Collection<DeflatedDataValue> minMaxs = minMaxOutlierAnalysisService.analyse( organisationUnit,
                ListUtils.getCollection( dataElement ), ListUtils.getCollection( period ), null );

            outliers = CollectionUtils.union( stdDevs, minMaxs );
        }
        else
        {
            outliers = minMaxValuesGenerationService.findOutliers( organisationUnit, ListUtils.getCollection( period ),
                ListUtils.getCollection( minMaxDataElement ) );
        }

        for ( DeflatedDataValue deflatedDataValue : outliers )
        {
            System.err.println( "max: " + deflatedDataValue.getMax() );
            System.err.println( "min: " + deflatedDataValue.getMin() );
        }
    }
}

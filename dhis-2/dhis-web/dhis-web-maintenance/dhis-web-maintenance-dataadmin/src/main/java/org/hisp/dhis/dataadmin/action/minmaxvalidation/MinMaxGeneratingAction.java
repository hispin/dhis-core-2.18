package org.hisp.dhis.dataadmin.action.minmaxvalidation;

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

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class MinMaxGeneratingAction
    implements Action
{
    // -------------------------------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------------------------------

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private DataSetService dataSetService;

    private PeriodService periodService;

    private DataAnalysisService minMaxValuesGeneratingService;

    private MinMaxDataElementService minMaxDataElementService;

    private SystemSettingManager systemSettingManager;

    // -------------------------------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------------------------------

    private Integer[] dataSetIds;

    private String startDateStr;

    private String endDateStr;

    private String message;

    private I18nFormat format;

    private I18n i18n;

    // -------------------------------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------------------------------

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setMinMaxValuesGeneratingService( DataAnalysisService minMaxValuesGeneratingService )
    {
        this.minMaxValuesGeneratingService = minMaxValuesGeneratingService;
    }

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    public void setDataSetIds( Integer[] dataSetIds )
    {
        this.dataSetIds = dataSetIds;
    }

    public void setStartDateStr( String startDateStr )
    {
        this.startDateStr = startDateStr;
    }

    public void setEndDateStr( String endDateStr )
    {
        this.endDateStr = endDateStr;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> orgUnits = organisationUnitSelectionManager.getSelectedOrganisationUnits();

        if ( orgUnits == null )
        {
            message = i18n.getString( "not_choose_organisation" );
            return INPUT;
        }

        if ( startDateStr == null || endDateStr == null )
        {
            return INPUT;
        }

        // Get startDate and endDate
        Date startDate = format.parseDate( startDateStr.trim() );
        ;
        Date endDate = format.parseDate( endDateStr.trim() );

        // Get factor
        Double factor = (Double) systemSettingManager.getSystemSetting( SystemSettingManager.KEY_FACTOR_OF_DEVIATION );

        for ( Integer dataSetId : dataSetIds )
        {
            // Get dataset
            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            for ( OrganisationUnit orgUnit : orgUnits )
            {
                if ( orgUnit.getDataSets().contains( dataSet ) )
                {
                    // Get periods
                    Collection<Period> periods = periodService.getPeriodsBetweenDates( dataSet.getPeriodType(),
                        startDate, endDate );

                    // Get min/max values for dataelements into dataset
                    Collection<MinMaxDataElement> minMaxDataElements = (Collection<MinMaxDataElement>) minMaxValuesGeneratingService
                        .analyse( orgUnit, dataSet.getDataElements(), periods, factor );

                    // Save min / max value
                    for ( MinMaxDataElement minMaxDataElement : minMaxDataElements )
                    {
                        MinMaxDataElement minMaxValue = minMaxDataElementService.getMinMaxDataElement(
                            minMaxDataElement.getSource(), minMaxDataElement.getDataElement(), minMaxDataElement
                                .getOptionCombo() );

                        if ( minMaxValue != null )
                        {
                            minMaxValue.setMax( minMaxDataElement.getMax() );
                            minMaxValue.setMin( minMaxDataElement.getMin() );
                            minMaxDataElementService.updateMinMaxDataElement( minMaxValue );
                        }
                        else
                        {
                            minMaxDataElement.setGenerated( true );
                            minMaxDataElementService.addMinMaxDataElement( minMaxDataElement );
                        }
                    }
                }
            }
        }

        message = i18n.getString( "generate_values_success" );

        return SUCCESS;
    }
}

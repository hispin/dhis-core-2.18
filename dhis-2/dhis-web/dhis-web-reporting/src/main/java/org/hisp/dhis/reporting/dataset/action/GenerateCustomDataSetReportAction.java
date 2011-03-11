package org.hisp.dhis.reporting.dataset.action;

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

import java.util.Map;

import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datasetreport.DataSetReportService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GenerateCustomDataSetReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetReportService dataSetReportService;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private OrganisationUnit selectedOrgunit;

    private DataSet selectedDataSet;

    private Period selectedPeriod;

    private boolean selectedUnitOnly;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String customDataEntryFormCode;

    private String reportingUnit;

    private String reportingPeriod;

    // -----------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------
    
    public void setDataSetReportService( DataSetReportService dataSetReportService )
    {
        this.dataSetReportService = dataSetReportService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    public String getReportingUnit()
    {
        return reportingUnit;
    }

    public String getReportingPeriod()
    {
        return reportingPeriod;
    }

    public void setSelectedOrgunit( OrganisationUnit selectedOrgunit )
    {
        this.selectedOrgunit = selectedOrgunit;
    }

    public void setSelectedDataSet( DataSet selectedDataSet )
    {
        this.selectedDataSet = selectedDataSet;
    }

    public void setSelectedPeriod( Period selectedPeriod )
    {
        this.selectedPeriod = selectedPeriod;
    }

    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }

    // -----------------------------------------------------------------------
    // Action implementation
    // -----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Map<String, String> aggregatedDataValueMap = dataSetReportService.getAggregatedValueMap( selectedDataSet, selectedOrgunit, selectedPeriod,
            selectedUnitOnly );

        DataEntryForm dataEntryForm = selectedDataSet.getDataEntryForm();

        customDataEntryFormCode = dataSetReportService.prepareReportContent( dataEntryForm.getHtmlCode(),
            aggregatedDataValueMap );

        reportingUnit = selectedOrgunit.getName();

        reportingPeriod = format.formatPeriod( selectedPeriod );

        return SUCCESS;
    }
}

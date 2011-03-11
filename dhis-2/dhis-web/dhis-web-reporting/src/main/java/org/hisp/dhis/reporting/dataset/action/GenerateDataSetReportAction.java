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

package org.hisp.dhis.reporting.dataset.action;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id GenerateDataSetReportAction.java Mar 09, 2011 9:02:43 AM $
 */
public class GenerateDataSetReportAction
    implements Action
{
    private final static String RESULT_CUSTOM = "customDataSetReport";
    private final static String RESULT_SECTION = "sectionDataSetReport";
    private final static String RESULT_DEFAULT = "defaultDataSetReport";
    
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    private DataSetService dataSetService;

    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    private String periodId;

    private boolean selectedUnitOnly;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit selectedOrgunit;

    private DataSet selectedDataSet;

    private Period selectedPeriod;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public Integer getDataSetId()
    {
        return dataSetId;
    }

    public boolean isSelectedUnitOnly()
    {
        return selectedUnitOnly;
    }

    public void setSelectedUnitOnly( boolean selectedUnitOnly )
    {
        this.selectedUnitOnly = selectedUnitOnly;
    }

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public OrganisationUnit getSelectedOrgunit()
    {
        return selectedOrgunit;
    }

    public DataSet getSelectedDataSet()
    {
        return selectedDataSet;
    }

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        selectedOrgunit = selectionTreeManager.getSelectedOrganisationUnit();

        if ( dataSetId == null || periodId == null || selectedOrgunit == null )
        {
            return ERROR;
        }

        selectedDataSet = dataSetService.getDataSet( dataSetId );

        selectedPeriod = periodService.getPeriodByExternalId( periodId );

        if ( selectedDataSet.hasDataEntryForm() )
        {
            return RESULT_CUSTOM;
        }

        return selectedDataSet.hasSections() ? RESULT_SECTION : RESULT_DEFAULT;

    }
}

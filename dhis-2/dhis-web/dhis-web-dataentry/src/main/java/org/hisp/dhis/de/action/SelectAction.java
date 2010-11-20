package org.hisp.dhis.de.action;

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

import static org.hisp.dhis.de.state.SelectedStateManager.ALLOWED_FORM_TYPES;
import static org.hisp.dhis.de.state.SelectedStateManager.CUSTOM_FORM;
import static org.hisp.dhis.de.state.SelectedStateManager.DEFAULT_FORM;
import static org.hisp.dhis.de.state.SelectedStateManager.SECTION_FORM;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.de.screen.DataEntryScreenManager;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SelectAction.java 5930 2008-10-15 03:30:52Z tri $
 */
public class SelectAction
    extends ActionSupport
{
    private static final Log log = LogFactory.getLog( SelectAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private DataEntryScreenManager dataEntryScreenManager;

    public void setDataEntryScreenManager( DataEntryScreenManager dataEntryScreenManager )
    {
        this.dataEntryScreenManager = dataEntryScreenManager;
    }

    private CompleteDataSetRegistrationService registrationService;

    public void setRegistrationService( CompleteDataSetRegistrationService registrationService )
    {
        this.registrationService = registrationService;
    }

    private DataSetLockService dataSetLockService;

    public void setDataSetLockService( DataSetLockService dataSetLockService )
    {
        this.dataSetLockService = dataSetLockService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Period period;

    public Period getPeriod()
    {
        return period;
    }

    private boolean locked;

    public boolean isLocked()
    {
        return locked;
    }

    private Collection<Integer> calculatedDataElementIds;

    public Collection<Integer> getCalculatedDataElementIds()
    {
        return calculatedDataElementIds;
    }

    private Map<CalculatedDataElement, Map<DataElement, Integer>> calculatedDataElementMap;

    public Map<CalculatedDataElement, Map<DataElement, Integer>> getCalculatedDataElementMap()
    {
        return calculatedDataElementMap;
    }

    private CompleteDataSetRegistration registration;

    public CompleteDataSetRegistration getRegistration()
    {
        return registration;
    }

    private Date registrationDate;

    public Date getRegistrationDate()
    {
        return registrationDate;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String displayMode;

    public void setDisplayMode( String displayMode )
    {
        this.displayMode = displayMode;
    }

    private Integer selectedPeriodIndex;

    public void setSelectedPeriodIndex( Integer selectedPeriodIndex )
    {
        this.selectedPeriodIndex = selectedPeriodIndex;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        DataSet selectedDataSet = selectedStateManager.getSelectedDataSet();
        
        // ---------------------------------------------------------------------
        // Validate selected period
        // ---------------------------------------------------------------------

        if ( selectedPeriodIndex == null )
        {
            selectedPeriodIndex = selectedStateManager.getSelectedPeriodIndex();
        }

        if ( selectedPeriodIndex != null && selectedPeriodIndex >= 0 )
        {
            selectedStateManager.setSelectedPeriodIndex( selectedPeriodIndex );
            
            period = selectedStateManager.getSelectedPeriod();
        }
        else
        {
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Get data locking info
        // ---------------------------------------------------------------------

        if ( selectedDataSet != null )
        {
            period = selectedStateManager.getSelectedPeriod();

            DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( selectedDataSet, period );

            if ( dataSetLock != null && dataSetLock.getSources().contains( organisationUnit ) )
            {
                locked = true;
                
                log.info( "Dataset '" + selectedDataSet.getName() + "' is locked " );
            }
        }
        
        // ---------------------------------------------------------------------
        // Get calculated data element info
        // ---------------------------------------------------------------------

        calculatedDataElementIds = dataEntryScreenManager.getAllCalculatedDataElements( selectedDataSet );
        calculatedDataElementMap = dataEntryScreenManager.getNonSavedCalculatedDataElements( selectedDataSet );

        // ---------------------------------------------------------------------
        // Get data set completeness info
        // ---------------------------------------------------------------------

        if ( selectedDataSet != null && period != null && organisationUnit != null )
        {
            registration = registrationService.getCompleteDataSetRegistration( selectedDataSet, period,
                organisationUnit );

            registrationDate = registration != null ? registration.getDate() : new Date();
        }

        // ---------------------------------------------------------------------
        // Get display mode
        // ---------------------------------------------------------------------

        if ( displayMode == null || !ALLOWED_FORM_TYPES.contains( displayMode ) )
        {
            displayMode = selectedStateManager.getSelectedDisplayMode();
        }
        
        boolean customDataEntryFormExists = selectedDataSet.getDataEntryForm() != null;

        boolean hasSection = selectedDataSet.getSections() != null && selectedDataSet.getSections().size() > 0;
        
        if ( displayMode == null || !ALLOWED_FORM_TYPES.contains( displayMode ) )
        {
            if ( customDataEntryFormExists )
            {
                displayMode = CUSTOM_FORM;
            }
            else if ( hasSection )
            {
                displayMode = SECTION_FORM;
            }
            else
            {
                displayMode = DEFAULT_FORM;
            }
        }
        
        selectedStateManager.setSelectedDisplayMode( displayMode );
        
        return displayMode;
    }
}

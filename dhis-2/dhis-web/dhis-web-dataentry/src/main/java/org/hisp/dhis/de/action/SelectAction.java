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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.de.screen.DataEntryScreenManager;
import org.hisp.dhis.de.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork2.ActionSupport;

import static org.hisp.dhis.de.state.SelectedStateManager.*;

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

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public Collection<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private boolean locked;

    public boolean isLocked()
    {
        return locked;
    }

    private Boolean hasSection;

    public Boolean getHasSection()
    {
        return hasSection;
    }

    private Boolean customDataEntryFormExists;

    public Boolean getCustomDataEntryFormExists()
    {
        return this.customDataEntryFormExists;
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

    public String getDisplayMode()
    {
        return displayMode;
    }

    public void setDisplayMode( String displayMode )
    {
        this.displayMode = displayMode;
    }

    private Integer selectedDataSetId;

    public Integer getSelectedDataSetId()
    {
        return selectedDataSetId;
    }

    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }

    private Integer selectedPeriodIndex;

    public Integer getSelectedPeriodIndex()
    {
        return selectedPeriodIndex;
    }

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
        /*
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load and sort DataSets
        // ---------------------------------------------------------------------

        dataSets = selectedStateManager.loadDataSetsForSelectedOrgUnit();

        Collections.sort( dataSets, new DataSetNameComparator() );

        // ---------------------------------------------------------------------
        // Validate selected DataSet
        // ---------------------------------------------------------------------

        DataSet selectedDataSet;

        if ( selectedDataSetId != null )
        {
            selectedDataSet = dataSetService.getDataSet( selectedDataSetId );
        }
        else
        {
            selectedDataSet = selectedStateManager.getSelectedDataSet();
        }

        if ( selectedDataSet != null && dataSets.contains( selectedDataSet ) )
        {
            selectedDataSetId = selectedDataSet.getId();
            selectedStateManager.setSelectedDataSet( selectedDataSet );
        }
        else
        {
            selectedStateManager.clearSelectedDataSet();
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }
*/
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        DataSet selectedDataSet = selectedStateManager.getSelectedDataSet();
        
        // ---------------------------------------------------------------------
        // Generate Periods
        // ---------------------------------------------------------------------

        periods = selectedStateManager.getPeriodList();

        // ---------------------------------------------------------------------
        // Validate selected Period
        // ---------------------------------------------------------------------

        if ( selectedPeriodIndex == null )
        {
            selectedPeriodIndex = selectedStateManager.getSelectedPeriodIndex();
        }

        if ( selectedPeriodIndex != null && selectedPeriodIndex >= 0 && selectedPeriodIndex < periods.size() )
        {
            selectedStateManager.setSelectedPeriodIndex( selectedPeriodIndex );            
            period = selectedStateManager.getSelectedPeriod();
        }
        else
        {
            selectedStateManager.clearSelectedPeriod();

            return SUCCESS;
        }

        // -----------------------------------------------------------------------
        // For Data Locking
        // -----------------------------------------------------------------------

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
        // Get CalculatedDataElement info
        // ---------------------------------------------------------------------

        calculatedDataElementIds = dataEntryScreenManager.getAllCalculatedDataElements( selectedDataSet );
        calculatedDataElementMap = dataEntryScreenManager.getNonSavedCalculatedDataElements( selectedDataSet );

        // ---------------------------------------------------------------------
        // Get display info
        // ---------------------------------------------------------------------

        hasSection = selectedDataSet.getSections() != null && selectedDataSet.getSections().size() > 0;

        customDataEntryFormExists = selectedDataSet.getDataEntryForm() != null;

        // ---------------------------------------------------------------------
        // Make available information about dataSet completeness
        // ---------------------------------------------------------------------

        if ( selectedDataSetId != null && selectedPeriodIndex != null && organisationUnit != null )
        {
            registration = registrationService.getCompleteDataSetRegistration( selectedDataSet, period,
                organisationUnit );

            registrationDate = registration != null ? registration.getDate() : new Date();
        }

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

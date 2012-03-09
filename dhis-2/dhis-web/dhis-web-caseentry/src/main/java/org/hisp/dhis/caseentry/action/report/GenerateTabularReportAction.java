/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseentry.action.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;

/**
 * @author Chau Thu Tran
 * 
 * @version $GenerateTabularReportAction.java Feb 29, 2012 10:15:05 AM$
 */
public class GenerateTabularReportAction
    extends ActionPagingSupport<ProgramStageInstance>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programStageId;

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    private List<Integer> dataElementIds = new ArrayList<Integer>();

    public void setDataElementIds( List<Integer> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    private Set<String> searchingValues = new HashSet<String>();

    public void setSearchingValues( Set<String> searchingValues )
    {
        this.searchingValues = searchingValues;
    }

    private Grid grid;

    public Grid getGrid()
    {
        return grid;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String facilityLB;

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get orgunitIds
        // ---------------------------------------------------------------------

        OrganisationUnit selectedOrgunit = selectedStateManager.getSelectedOrganisationUnit();

        Set<Integer> orgunitIds = new HashSet<Integer>();

        if ( facilityLB.equals( "selected" ) )
        {
            orgunitIds.add( selectedOrgunit.getId() );
        }
        else
        {
            OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy();

            Set<Integer> childOrgUnitIdentifiers = hierarchy.getChildren( selectedOrgunit.getId() );

            orgunitIds.addAll( childOrgUnitIdentifiers );

            if ( facilityLB.equals( "childrenOnly" ) )
            {
                orgunitIds.remove( selectedOrgunit.getId() );
            }
        }

        // ---------------------------------------------------------------------
        // Get program-stage, start-date, end-date
        // ---------------------------------------------------------------------

        ProgramStage programStage = programStageService.getProgramStage( programStageId );

        Date startValue = format.parseDate( startDate );

        Date endValue = format.parseDate( endDate );

        dataElements = new ArrayList<DataElement>();

        // ---------------------------------------------------------------------
        // Get selected dataelements
        // ---------------------------------------------------------------------

        for ( Integer dataElementId : dataElementIds )
        {
            dataElements.add( dataElementService.getDataElement( dataElementId ) );
        }

        // ---------------------------------------------------------------------
        // Get searching-keys
        // ---------------------------------------------------------------------

        Map<Integer, String> searchingKeys = new HashMap<Integer, String>();

        for ( String searchingValue : searchingValues )
        {
            String[] infor = searchingValue.split( "_" );

            searchingKeys.put( Integer.parseInt( infor[0] ), infor[1] );
        }

        // ---------------------------------------------------------------------
        // Generate tabular report
        // ---------------------------------------------------------------------

        if ( type == null )
        {
            total = programStageInstanceService.countProgramStageInstances( programStage, searchingKeys,
                orgunitIds, startValue, endValue );

            this.paging = createPaging( total );

            grid = programStageInstanceService.getTabularReport( programStage, dataElements, searchingKeys,
                orgunitIds, startValue, endValue, paging.getStartPos(), paging.getPageSize(), format, i18n );
            return SUCCESS;
        }

        grid = programStageInstanceService.getTabularReport( programStage, dataElements, searchingKeys,
            orgunitIds, startValue, endValue, format, i18n );

        return type;
    }
}

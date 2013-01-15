/*
 * Copyright (c) 2004-2009, University of Oslo
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientreport.PatientAggregateReport;
import org.hisp.dhis.patientreport.PatientAggregateReportService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version SaveAggregateReportAction.java 1:16:10 PM Jan 14, 2013 $
 */
public class SaveAggregateReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientAggregateReportService aggregateReportService;

    private ProgramStageService programStageService;

    private OrganisationUnitService organisationUnitService;

    private DataElementService dataElementService;

    private CurrentUserService currentUserService;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    private int programStageId;

    // Date period range

    private String periodTypeName;

    private String startDate;

    private String endDate;

    // Relative periods

    private Set<String> relativePeriods = new HashSet<String>();

    // Fixed periods

    private List<String> fixedPeriod = new ArrayList<String>();

    // Organisation units

    private Set<Integer> orgunitIds;

    // Data element filter values

    private Collection<String> filterValues;

    // Option

    private String facilityLB;

    private Integer limitRecords;

    private int position;

    private Integer deGroupBy;

    private String aggregateType;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setAggregateReportService( PatientAggregateReportService aggregateReportService )
    {
        this.aggregateReportService = aggregateReportService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setProgramStageId( int programStageId )
    {
        this.programStageId = programStageId;
    }

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public void setRelativePeriods( Set<String> relativePeriods )
    {
        this.relativePeriods = relativePeriods;
    }

    public void setFixedPeriod( List<String> fixedPeriod )
    {
        this.fixedPeriod = fixedPeriod;
    }

    public void setOrgunitIds( Set<Integer> orgunitIds )
    {
        this.orgunitIds = orgunitIds;
    }

    public void setFilterValues( Collection<String> filterValues )
    {
        this.filterValues = filterValues;
    }

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    public void setLimitRecords( Integer limitRecords )
    {
        this.limitRecords = limitRecords;
    }

    public void setPosition( int position )
    {
        this.position = position;
    }

    public void setDeGroupBy( Integer deGroupBy )
    {
        this.deGroupBy = deGroupBy;
    }

    public void setAggregateType( String aggregateType )
    {
        this.aggregateType = aggregateType;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        PatientAggregateReport aggregateReport = new PatientAggregateReport();

        aggregateReport.setName( name );
        aggregateReport.setProgramStage( programStageService.getProgramStage( programStageId ) );

        if ( periodTypeName != null )
        {
            aggregateReport.setPeriodTypeName( periodTypeName );
            aggregateReport.setStartDate( format.parseDate( startDate ) );
            aggregateReport.setEndDate( format.parseDate( endDate ) );
        }

        aggregateReport.setRelativePeriods( relativePeriods );
        aggregateReport.setFixedPeriods( fixedPeriod );
        aggregateReport.setOrganisationUnits( new HashSet<OrganisationUnit>( organisationUnitService
            .getOrganisationUnits( orgunitIds ) ) );

        aggregateReport.setFilterValues( filterValues );
        aggregateReport.setFacilityLB( facilityLB );
        aggregateReport.setLimitRecords( limitRecords );
        aggregateReport.setPosition( position );
        aggregateReport.setDeGroupBy( dataElementService.getDataElement( deGroupBy ) );
        aggregateReport.setAggregateType( aggregateType );
        aggregateReport.setUser( currentUserService.getCurrentUser() );

        aggregateReportService.saveOrUpdate( aggregateReport );

        return SUCCESS;
    }
}

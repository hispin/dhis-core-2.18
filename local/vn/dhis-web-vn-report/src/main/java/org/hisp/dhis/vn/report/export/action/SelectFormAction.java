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
package org.hisp.dhis.vn.report.export.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;
import org.hisp.dhis.vn.report.*;
import org.hisp.dhis.vn.report.comparator.ReportNameComparator;
import org.hisp.dhis.vn.report.state.SelectionManager;
import org.hisp.dhis.vn.report.utils.DateUtils;

import com.opensymphony.xwork.Action;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class SelectFormAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------
    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private ReportExcelService reportService;

    private PeriodService periodService;

    private SelectionManager selectionManager;

    private CurrentUserService currentUserService;

    private UserStore userStore;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private List<ReportExcelInterface> reports;

    private List<Period> periods;

    private OrganisationUnit organisationUnit;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public List<ReportExcelInterface> getReports()
    {
        return reports;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public String execute()
        throws Exception
    {

        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( organisationUnit== null)  return SUCCESS;

        User currentUser = currentUserService.getCurrentUser();

        if ( currentUserService.currentUserIsSuper() )
        {

            reports = new ArrayList<ReportExcelInterface>( reportService.getALLReport() );

        }
        else
        {

            reports = new ArrayList<ReportExcelInterface>();

            UserCredentials credentials = userStore.getUserCredentials( currentUser );

            for ( UserAuthorityGroup group : credentials.getUserAuthorityGroups() )
            {
                reports.addAll( group.getReportExcels() );
            }

        }

        Collection<ReportExcelInterface> reportAssociation = reportService
            .getReportsByOrganisationUnit( organisationUnit );

        reports.retainAll( reportAssociation );

        Collections.sort( reports, new ReportNameComparator() );

        PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );

        Date firstDateOfThisYear = DateUtils.getFirstDayOfYear( DateUtils.getCurrentYear() );

        Date endDateOfThisMonth = DateUtils.getEndDate( DateUtils.getCurrentMonth(), DateUtils.getCurrentYear() );

        periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType,
            firstDateOfThisYear, endDateOfThisMonth ) );

        Collections.sort( periods, new PeriodComparator() );

        selectionManager.setSeletedYear( DateUtils.getCurrentYear() );

        return SUCCESS;
    }
}

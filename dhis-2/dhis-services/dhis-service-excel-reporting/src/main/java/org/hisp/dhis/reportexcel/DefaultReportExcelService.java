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
package org.hisp.dhis.reportexcel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.status.DataEntryStatus;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
@Transactional
public class DefaultReportExcelService
    implements ReportExcelService
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private ReportExcelStore reportStore;

    public void setReportStore( ReportExcelStore reportStore )
    {
        this.reportStore = reportStore;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    // --------------------------------------
    // Service of Report
    // --------------------------------------

    public int addReportExcel( ReportExcel report )
    {
        return reportStore.addReportExcel( report );
    }

    public void updateReportExcel( ReportExcel report )
    {
        reportStore.updateReportExcel( report );
    }

    public void deleteReportExcel( int id )
    {
        reportStore.deleteReportExcel( id );
    }

    public ReportExcel getReportExcel( int id )
    {
        return reportStore.getReportExcel( id );
    }

    public ReportExcel getReportExcel(  String name )
    {
        return reportStore.getReportExcel( name );
    }

    public Collection<ReportExcel> getReportExcelsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return reportStore.getReportExcelsByOrganisationUnit( organisationUnit );
    }

    public Collection<ReportExcel> getALLReportExcel()
    {
        return reportStore.getALLReportExcel();
    }

    public Collection<ReportExcel> getReportExcels( User user, boolean superUser, String group )
    {
        if ( user == null || (user != null && superUser) )
        {
            return this.getReportsByGroup( group );
        }
        else
        {
            Set<UserAuthorityGroup> userRoles = userStore.getUserCredentials( user ).getUserAuthorityGroups();

            Collection<ReportExcel> reports = new ArrayList<ReportExcel>();

            for ( ReportExcel report : this.getReportsByGroup( group ) )
            {
                if ( CollectionUtils.intersection( report.getUserRoles(), userRoles ).size() > 0 )
                {
                    reports.add( report );
                }
            }

            return reports;
        }
    }

    public Collection<String> getReportExcelGroups()
    {
        return reportStore.getReportExcelGroups();
    }

    public Collection<ReportExcel> getReportsByGroup( String group )
    {
        return reportStore.getReportsByGroup( group );
    }

    // --------------------------------------
    // Service of Report Item
    // --------------------------------------

    public void addReportExcelItem( ReportExcelItem reportItem )
    {
        reportStore.addReportExcelItem( reportItem );
    }

    public void updateReportExcelItem( ReportExcelItem reportItem )
    {
        reportStore.updateReportExcelItem( reportItem );
    }

    public void deleteReportExcelItem( int id )
    {
        reportStore.deleteReportExcelItem( id );
    }

    public ReportExcelItem getReportExcelItem( int id )
    {
        return reportStore.getReportExcelItem( id );
    }   

    public Collection<ReportExcelItem> getALLReportExcelItem()
    {
        return reportStore.getALLReportExcelItem();
    }

    public Collection<ReportExcelItem> getReportExcelItem( int sheetNo, Integer reportId )
    {
        return reportStore.getReportExcelItem( sheetNo, reportId );
    }

    public Collection<Integer> getSheets( Integer reportId )
    {
        return reportStore.getSheets( reportId );
    }

    // --------------------------------------
    // Report DataElement Order
    // --------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id )
    {
        return reportStore.getDataElementGroupOrder( id );
    }

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder )
    {
        reportStore.updateDataElementGroupOrder( dataElementGroupOrder );
    }

    public void deleteDataElementGroupOrder( Integer id )
    {
        reportStore.deleteDataElementGroupOrder( id );
    }

    // -------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------

    public int countDataValueOfDataSet( DataSet arg0, OrganisationUnit arg1, Period arg2 )
    {
        return reportStore.countDataValueOfDataSet( arg0, arg1, arg2 );
    }

    public void deleteDataEntryStatus( int arg0 )
    {
        reportStore.deleteDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getALLDataEntryStatus()
    {
        return reportStore.getALLDataEntryStatus();
    }

    public DataEntryStatus getDataEntryStatus( int arg0 )
    {
        return reportStore.getDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefault()
    {
        return reportStore.getDataEntryStatusDefault();
    }

    public int saveDataEntryStatus( DataEntryStatus arg0 )
    {
        return reportStore.saveDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> arg0 )
    {
        return reportStore.getDataEntryStatusDefaultByDataSets( arg0 );

    }

    public void updateDataEntryStatus( DataEntryStatus arg0 )    {
        
        reportStore.updateDataEntryStatus( arg0 );
    }   

}

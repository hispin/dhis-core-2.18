package org.hisp.dhis.reportexcel;

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

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.status.DataEntryStatus;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Tran Thanh Tri
 * @version $Id: DefaultExportReportService.java 2010-03-11 11:52:20Z Chau Thu
 *          Tran $ $
 */
@Transactional
public class DefaultExportReportService
    implements ExportReportService
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private ExportReportStore exportReportStore;

    public void setExportReportStore( ExportReportStore exportReportStore )
    {
        this.exportReportStore = exportReportStore;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService i18nService )
    {
        this.i18nService = i18nService;
    }

    // --------------------------------------
    // Service of Report
    // --------------------------------------

    public int addExportReport( ReportExcel report )
    {
        int id = exportReportStore.addExportReport( report );

        i18nService.addObject( report );

        return id;
    }

    public void updateExportReport( ReportExcel report )
    {
        exportReportStore.updateExportReport( report );

        i18nService.verify( report );
    }

    public void deleteExportReport( int id )
    {
        i18nService.removeObject( exportReportStore.getExportReport( id ) );

        exportReportStore.deleteExportReport( id );
    }

    public ReportExcel getExportReport( int id )
    {
        return i18n( i18nService, exportReportStore.getExportReport( id ) );
    }

    public ReportExcel getExportReport( String name )
    {
        return i18n( i18nService, exportReportStore.getExportReport( name ) );
    }

    public Collection<ReportExcel> getExportReportsByOrganisationUnit( OrganisationUnit organisationUnit )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByOrganisationUnit( organisationUnit ) );
    }

    public Collection<ReportExcel> getAllExportReport()
    {
        return i18n( i18nService, exportReportStore.getAllExportReport() );
    }

    public Collection<ReportExcel> getExportReports( User user, boolean superUser, String group )
    {

        if ( user == null || superUser )
        {
            return i18n( i18nService, this.getExportReportsByGroup( group ) );
        }

        else
        {
            Set<UserAuthorityGroup> userRoles = userStore.getUserCredentials( user ).getUserAuthorityGroups();

            Collection<ReportExcel> reports = new ArrayList<ReportExcel>();

            for ( ReportExcel report : this.getExportReportsByGroup( group ) )
            {
                if ( CollectionUtils.intersection( report.getUserRoles(), userRoles ).size() > 0 )
                {
                    reports.add( report );
                }
            }

            return i18n( i18nService, reports );
        }
    }

    public Collection<String> getExportReportGroups()
    {
        return i18n( i18nService, exportReportStore.getExportReportGroups() );
    }

    public Collection<ReportExcel> getExportReportsByGroup( String group )
    {
        return i18n( i18nService, exportReportStore.getExportReportsByGroup( group ) );
    }

    public Collection<String> getAllExportReportTemplates()
    {
        return exportReportStore.getAllExportReportTemplates();
    }

    public void deleteMultiExportItem( Collection<Integer> ids )
    {
        if ( !ids.isEmpty() )
        {
            exportReportStore.deleteMultiExportItem( ids );
        }
    }

    @Override
    public void updateExportReportSystemByTemplate( String curName, String newName )
    {
        exportReportStore.updateReportWithExcelTemplate( curName, newName );

    }

    // --------------------------------------
    // Service of Report Item
    // --------------------------------------

    public void addExportItem( ReportExcelItem reportItem )
    {
        exportReportStore.addExportItem( reportItem );

        i18nService.addObject( reportItem );
    }

    public void updateExportItem( ReportExcelItem reportItem )
    {
        exportReportStore.updateExportItem( reportItem );

        i18nService.verify( reportItem );
    }

    public void deleteExportItem( int id )
    {
        i18nService.removeObject( exportReportStore.getExportItem( id ) );

        exportReportStore.deleteExportItem( id );
    }

    public ReportExcelItem getExportItem( int id )
    {
        return i18n( i18nService, exportReportStore.getExportItem( id ) );
    }

    public Collection<ReportExcelItem> getAllExportItem()
    {
        return i18n( i18nService, exportReportStore.getAllExportItem() );
    }

    public Collection<ReportExcelItem> getExportItem( int sheetNo, Integer reportId )
    {
        return i18n( i18nService, exportReportStore.getExportItem( sheetNo, reportId ) );
    }

    public Collection<Integer> getSheets( Integer reportId )
    {
        return exportReportStore.getSheets( reportId );
    }

    // --------------------------------------
    // Report DataElement Order
    // --------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id )
    {
        return exportReportStore.getDataElementGroupOrder( id );
    }

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder )
    {
        exportReportStore.updateDataElementGroupOrder( dataElementGroupOrder );
    }

    public void deleteDataElementGroupOrder( Integer id )
    {
        exportReportStore.deleteDataElementGroupOrder( id );
    }

    // -------------------------------------------------
    // Data Entry Status
    // -------------------------------------------------

    public int countDataValueOfDataSet( DataSet arg0, OrganisationUnit arg1, Period arg2 )
    {
        return exportReportStore.countDataValueOfDataSet( arg0, arg1, arg2 );
    }

    public void deleteDataEntryStatus( int arg0 )
    {
        exportReportStore.deleteDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getALLDataEntryStatus()
    {
        return exportReportStore.getALLDataEntryStatus();
    }

    public DataEntryStatus getDataEntryStatus( int arg0 )
    {
        return exportReportStore.getDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefault()
    {
        return exportReportStore.getDataEntryStatusDefault();
    }

    public int saveDataEntryStatus( DataEntryStatus arg0 )
    {
        return exportReportStore.saveDataEntryStatus( arg0 );
    }

    public Collection<DataEntryStatus> getDataEntryStatusDefaultByDataSets( Collection<DataSet> arg0 )
    {
        return exportReportStore.getDataEntryStatusDefaultByDataSets( arg0 );

    }

    public void updateDataEntryStatus( DataEntryStatus arg0 )
    {
        exportReportStore.updateDataEntryStatus( arg0 );
    }

    @Override
    public PeriodColumn getPeriodColumn( Integer id )
    {
        return exportReportStore.getPeriodColumn( id );
    }

    @Override
    public void updatePeriodColumn( PeriodColumn periodColumn )
    {
        exportReportStore.updatePeriodColumn( periodColumn );
    }

}

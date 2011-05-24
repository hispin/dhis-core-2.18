package org.hisp.dhis.reportexcel.importitem;

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

import java.util.Collection;

import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

@Transactional
public class DefaultImportItemService
    implements ImportItemService
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ImportItemStore importItemStore;

    public void setImportItemStore( ImportItemStore importItemStore )
    {
        this.importItemStore = importItemStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // Excelitem group Services
    // -------------------------------------------------------------------------

    public int addImportReport( ExcelItemGroup importReport )
    {
        int id = importItemStore.addImportReport( importReport );

        i18nService.addObject( importReport );

        return id;
    }

    public void deleteImportReport( int id )
    {
        i18nService.removeObject( importItemStore.getImportReport( id ) );

        importItemStore.deleteImportReport( id );
    }

    public Collection<ExcelItemGroup> getAllImportReport()
    {
        return i18n( i18nService, importItemStore.getAllImportReport() );
    }

    public ExcelItemGroup getImportReport( int id )
    {
        return i18n( i18nService, importItemStore.getImportReport( id ) );
    }

    public void updateImportReport( ExcelItemGroup importReport )
    {
        importItemStore.updateImportReport( importReport );

        i18nService.verify( importReport );
    }

    public Collection<ExcelItemGroup> getImportReports( OrganisationUnit organisationUnit )
    {
        return i18n( i18nService, importItemStore.getImportReports( organisationUnit ) );
    }

    // -------------------------------------------------------------------------
    // Import Item Services
    // -------------------------------------------------------------------------

    public int addImportItem( ExcelItem excelItem )
    {
        int id = importItemStore.addImportItem( excelItem );

        i18nService.addObject( excelItem );

        return id;
    }

    public void deleteImportItem( int id )
    {

        i18nService.removeObject( importItemStore.getImportItem( id ) );

        importItemStore.deleteImportItem( id );
    }

    public Collection<ExcelItem> getAllImportItem()
    {

        return i18n( i18nService, importItemStore.getAllImportItem() );
    }

    public void updateImportItem( ExcelItem excelItem )
    {

        importItemStore.updateImportItem( excelItem );

        i18nService.verify( excelItem );
    }

    public ExcelItem getImportItem( int id )
    {

        return i18n( i18nService, importItemStore.getImportItem( id ) );
    }

    // -------------------------------------------------------------------------
    // DataElement Order
    // -------------------------------------------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id )
    {
        return importItemStore.getDataElementGroupOrder( id );
    }

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder )
    {
        importItemStore.updateDataElementGroupOrder( dataElementGroupOrder );
    }

    public void deleteDataElementGroupOrder( Integer id )
    {
        importItemStore.deleteDataElementGroupOrder( id );
    }

    public ExcelItem getImportItem( String name )
    {
        return importItemStore.getImportItem( name );
    }

    public ExcelItemGroup getImportReport( String name )
    {
        return importItemStore.getImportReport( name );
    }

}

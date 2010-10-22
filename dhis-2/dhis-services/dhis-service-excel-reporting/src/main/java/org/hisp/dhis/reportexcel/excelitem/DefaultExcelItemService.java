package org.hisp.dhis.reportexcel.excelitem;

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
public class DefaultExcelItemService
    implements ExcelItemService
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private ExcelItemStore excelItemStore;

    public void setExcelItemStore( ExcelItemStore excelItemStore )
    {
        this.excelItemStore = excelItemStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // --------------------------------------
    // Excelitem group Services
    // --------------------------------------

    public int addExcelItemGroup( ExcelItemGroup excelItemGroup )
    {
        int id = excelItemStore.addExcelItemGroup( excelItemGroup );

        i18nService.addObject( excelItemGroup );

        return id;
    }

    public void deleteExcelItemGroup( int id )
    {
        i18nService.removeObject( excelItemStore.getExcelItemGroup( id ) );

        excelItemStore.deleteExcelItemGroup( id );
    }

    public Collection<ExcelItemGroup> getAllExcelItemGroup()
    {
        return i18n( i18nService, excelItemStore.getAllExcelItemGroup() );
    }

    public ExcelItemGroup getExcelItemGroup( int id )
    {
        return i18n( i18nService, excelItemStore.getExcelItemGroup( id ) );
    }

    public void updateExcelItemGroup( ExcelItemGroup excelItemGroup )
    {
        excelItemStore.updateExcelItemGroup( excelItemGroup );
        
        i18nService.verify( excelItemGroup );
    }

    public Collection<ExcelItemGroup> getExcelItemGroups( OrganisationUnit organisationUnit )
    {
        return i18n( i18nService, excelItemStore.getExcelItemGroups( organisationUnit ) );
    }

    // --------------------------------------
    // Excelitem Services
    // --------------------------------------

    public int addExcelItem( ExcelItem excelItem )
    {

        int id = excelItemStore.addExcelItem( excelItem );
        
        i18nService.addObject( excelItem );
         
        return id;
    }

    public void deleteExcelItem( int id )
    {

        i18nService.removeObject( excelItemStore.getExcelItem( id ) );
        
        excelItemStore.deleteExcelItem( id );
    }

    public Collection<ExcelItem> getAllExcelItem()
    {

        return i18n( i18nService, excelItemStore.getAllExcelItem() );
    }

    public void updateExcelItem( ExcelItem excelItem )
    {

        excelItemStore.updateExcelItem( excelItem );
        
        i18nService.verify( excelItem );
    }

    public ExcelItem getExcelItem( int id )
    {

        return i18n( i18nService, excelItemStore.getExcelItem( id ) );
    }

    // --------------------------------------
    // DataElement Order
    // --------------------------------------

    public DataElementGroupOrder getDataElementGroupOrder( Integer id )
    {
        return excelItemStore.getDataElementGroupOrder( id );
    }

    public void updateDataElementGroupOrder( DataElementGroupOrder dataElementGroupOrder )
    {
        excelItemStore.updateDataElementGroupOrder( dataElementGroupOrder );
    }

    public void deleteDataElementGroupOrder( Integer id )
    {
        excelItemStore.deleteDataElementGroupOrder( id );
    }

    public ExcelItem getExcelItem( String name )
    {
        return excelItemStore.getExcelItem( name );
    }

    public ExcelItemGroup getExcelItemGroup( String name )
    {
        return excelItemStore.getExcelItemGroup( name );
    }

}

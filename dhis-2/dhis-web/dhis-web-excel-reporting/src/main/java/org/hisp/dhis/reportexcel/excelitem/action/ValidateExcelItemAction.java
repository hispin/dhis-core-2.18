package org.hisp.dhis.reportexcel.excelitem.action;

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

import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;

/**
 * @author Chau Thu Tran
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ValidateExcelItemAction
    extends ActionSupport
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ExcelItemService excelItemService;

    // -------------------------------------------------------------------------
    // Inputs
    // -------------------------------------------------------------------------

    private String name;

    private String expression;

    private Integer row;

    private Integer column;

    private Integer sheetNo;

    private Integer excelItemGroupId;

    private Integer id;

    // -------------------------------------------------------------------------
    // Setters
    // -------------------------------------------------------------------------

    public void setExcelItemService( ExcelItemService excelItemService )
    {
        this.excelItemService = excelItemService;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public void setRow( Integer row )
    {
        this.row = row;
    }

    public void setColumn( Integer column )
    {
        this.column = column;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public void setExcelItemGroupId( Integer excelItemGroupId )
    {
        this.excelItemGroupId = excelItemGroupId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExcelItemGroup excelItemGroup = excelItemService.getExcelItemGroup( excelItemGroupId );

        if ( name == null || name.length() == 0 )
        {
            message = i18n.getString( "name" ) + " " + i18n.getString( "not_null" );

            return ERROR;
        }

        if ( expression == null || expression.length() == 0 )
        {
            message = i18n.getString( "expression" ) + " " + i18n.getString( "not_null" );

            return ERROR;
        }

        if ( sheetNo == null )
        {
            message = i18n.getString( "sheetNo" ) + " " + i18n.getString( "not_null" );

            return ERROR;
        }

        if ( row == null )
        {
            message = i18n.getString( "row" ) + " " + i18n.getString( "not_null" );

            return ERROR;
        }

        if ( column == null )
        {
            message = i18n.getString( "column" ) + " " + i18n.getString( "not_null" );

            return ERROR;
        }

        if ( id == null )
        {

            if ( excelItemGroup.excelItemIsExist( name ) )
            {
                message = i18n.getString( "name_ready_exist" );

                return ERROR;
            }

            if ( excelItemGroup.rowAndColumnIsExist( sheetNo, row, column ) )
            {
                message = i18n.getString( "cell_exist" );

                return ERROR;
            }
        }
        else
        {
            ExcelItem excelItem = excelItemService.getExcelItem( id );

            ExcelItem temp = excelItemGroup.getExcelItemByName( name );

            if ( excelItem != temp )
            {
                message = i18n.getString( "name_ready_exist" );

                return ERROR;
            }

            temp = excelItemGroup.getExcelItemBySheetRowColumn( sheetNo, row, column );

            if ( temp != null && excelItem != temp )
            {
                message = i18n.getString( "cell_exist" );

                return ERROR;
            }

        }

        return SUCCESS;
    }
}

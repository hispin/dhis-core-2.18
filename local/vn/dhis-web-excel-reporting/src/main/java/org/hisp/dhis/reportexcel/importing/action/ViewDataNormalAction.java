package org.hisp.dhis.reportexcel.importing.action;

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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.importing.ExcelItemValue;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id
 */

public class ViewDataNormalAction
    implements Action
{
    // --------------------------------------------------------------------
    // Inputs && Outputs
    // --------------------------------------------------------------------

    private File upload;

    private List<ExcelItemValue> excelItemValues;

    private ArrayList<ExcelItem> excelItems;
    
    private String message;
    
    private I18n i18n;

    // --------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public void setExcelItems( ArrayList<ExcelItem> excelItems )
    {
        this.excelItems = excelItems;
    }

    public List<ExcelItemValue> getExcelItemValues()
    {
        return excelItemValues;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
    
    // --------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------

    public String execute()
    {
        try
        {
            FileInputStream inputStream = new FileInputStream( upload );

            Workbook wb = new HSSFWorkbook( inputStream );

            excelItemValues = new ArrayList<ExcelItemValue>();

            if ( excelItems == null || !excelItems.isEmpty() )
            {
                message = i18n.getString( "import_excel_items_cannot_be_empty" );
                
                return ERROR;
            }
            
            for ( ExcelItem excelItem : excelItems )
            {
                Sheet sheet = wb.getSheetAt( excelItem.getSheetNo() - 1 );

                String value = ExcelUtils.readValueImportingByPOI( excelItem.getRow(), excelItem.getColumn(), sheet );

                ExcelItemValue excelItemvalue = new ExcelItemValue( excelItem, value.trim() );

                excelItemValues.add( excelItemvalue );
            }

            return SUCCESS;

        }
        catch ( Exception ex )
        {
            throw new RuntimeException("Error while previewing the imported value", ex);
        }
    }
}

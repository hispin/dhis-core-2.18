package org.hisp.dhis.reportexcel.importing.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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
import java.util.Collections;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.importing.ImportItemValue;
import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.importitem.ImportItemService;
import org.hisp.dhis.reportexcel.importitem.comparator.ImportItemComparator;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ViewDataCategoryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportItemService importItemService;

    public void setImportItemService( ImportItemService importItemService )
    {
        this.importItemService = importItemService;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Inputs && Outputs
    // -------------------------------------------------------------------------

    private List<ImportItemValue> importItemValues = new ArrayList<ImportItemValue>();

    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public List<ImportItemValue> getImportItemValues()
    {
        return importItemValues;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        try
        {
            FileInputStream inputStream = new FileInputStream( new File( selectionManager.getUploadFilePath() ) );

            Workbook wb = new HSSFWorkbook( inputStream );

            ExcelItemGroup importReport = importItemService.getImportReport( selectionManager.getSelectedReportId() );

            List<ExcelItem> importItems = new ArrayList<ExcelItem>( importReport.getExcelItems() );

            if ( importItems == null || importItems.isEmpty() )
            {
                message = i18n.getString( "import_excel_items_cannot_be_empty" );

                return ERROR;
            }

            Collections.sort( importItems, new ImportItemComparator() );

            for ( ExcelItem importItem : importItems )
            {
                Sheet sheet = wb.getSheetAt( importItem.getSheetNo() - 1 );

                int rowBegin = importItem.getRow();

                for ( DataElementGroupOrder dataElementGroup : importReport.getDataElementOrders() )
                {
                    for ( DataElement dataElement : dataElementGroup.getDataElements() )
                    {
                        String value = ExcelUtils.readValueImportingByPOI( rowBegin, importItem.getColumn(), sheet );

                        ExcelItem item = new ExcelItem();

                        item.setId( importItem.getId() );

                        item.setExpression( importItem.getExpression().replace( "*",
                            String.valueOf( dataElement.getId() ) ) );

                        item.setName( importItem.getName() + " - " + dataElement.getName() );

                        item.setRow( rowBegin );

                        ImportItemValue importItemValue = new ImportItemValue( item, value );

                        importItemValues.add( importItemValue );

                        rowBegin++;

                    } // for (DataElementGroupOrder ...

                } // for (DataElement ...

            }// end for (ImportItem ...

            return SUCCESS;

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return ERROR;
    }

}

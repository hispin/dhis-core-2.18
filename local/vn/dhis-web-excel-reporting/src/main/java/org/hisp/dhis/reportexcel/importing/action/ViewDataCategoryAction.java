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
import java.util.Collections;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.comparator.ExcelItemComparator;
import org.hisp.dhis.reportexcel.importing.ExcelItemValue;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $Id
 */

public class ViewDataCategoryAction
    implements Action
{

    // --------------------------------------------------------------------
    // Inputs && Outputs
    // --------------------------------------------------------------------

    private ExcelItemGroup excelItemGroup;

    private ArrayList<ExcelItemValue> excelItemValues;

    private File upload;

    public String[] excelItemIds;

    // --------------------------------------------------------------------
    // Getters and Setters
    // --------------------------------------------------------------------

    public void setUpload( File upload )
    {
        this.upload = upload;
    }

    public void setExcelItemGroup( ExcelItemGroup excelItemGroup )
    {
        this.excelItemGroup = excelItemGroup;
    }

    public void setExcelItemIds( String[] excelItemIds )
    {
        this.excelItemIds = excelItemIds;
    }

    public ArrayList<ExcelItemValue> getExcelItemValues()
    {
        return excelItemValues;
    }

    // --------------------------------------------------------------------
    // Action implementation
    // --------------------------------------------------------------------

    public String execute()
    {
        try
        {
            FileInputStream inputStream = new FileInputStream( upload );

            HSSFWorkbook wb = new HSSFWorkbook( inputStream );

            ArrayList<ExcelItem> excelItems = new ArrayList<ExcelItem>( excelItemGroup.getExcelItems() );

            Collections.sort( excelItems, new ExcelItemComparator() );

            excelItemValues = new ArrayList<ExcelItemValue>();

            for ( ExcelItem excelItem : excelItems )
            {

                HSSFSheet sheet = wb.getSheetAt( excelItem.getSheetNo() - 1 );

                int rowBegin = excelItem.getRow();

                for ( DataElementGroupOrder dataElementGroup : excelItemGroup.getDataElementOrders() )
                {

                    for ( DataElement dataElement : dataElementGroup.getDataElements() )
                    {

                        String value = ExcelUtils.readValueImportingByPOI( rowBegin, excelItem.getColumn(), sheet );

                        ExcelItem item = new ExcelItem();

                        item.setId( excelItem.getId() );

                        item.setExpression( excelItem.getExpression().replace( "*",
                            String.valueOf( dataElement.getId() ) ) );

                        item.setName( excelItem.getName() + " - " + dataElement.getName() );

                        item.setRow( rowBegin );

                        ExcelItemValue excelItemValue = new ExcelItemValue( item, value );

                        excelItemValues.add( excelItemValue );

                        rowBegin++;

                    } // for (DataElementGroupOrder ...

                } // for (DataElement ...

            }// end for (ExcelItem ...

            return SUCCESS;

        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }

        return ERROR;
    }

}

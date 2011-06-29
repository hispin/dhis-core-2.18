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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;
import org.hisp.dhis.reportexcel.importing.ViewDataGeneric;
import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.preview.action.XMLStructureResponseImport;

/**
 * @author Dang Duy Hieu
 * @version $Id
 */

public class ViewDataCategoryAction
    extends ViewDataGeneric
{
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public void executeViewData( ExcelItemGroup importReport, List<ExcelItem> importItems )
    {
        try
        {
            List<ExcelItem> categoryImportItems = new ArrayList<ExcelItem>();

            setUpImportItems( importReport, importItems, categoryImportItems );

            xmlStructureResponse = new XMLStructureResponseImport( selectionManager.getUploadFilePath(),
                importItemService.getAllSheet(), categoryImportItems, true, ExcelItemGroup.TYPE.CATEGORY ).getXml();
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private void setUpImportItems( ExcelItemGroup importReport, List<ExcelItem> importItemsSource,
        List<ExcelItem> importItemsDest )
    {
        for ( ExcelItem importItem : importItemsSource )
        {
            int rowBegin = importItem.getRow();

            for ( DataElementGroupOrder dataElementGroup : importReport.getDataElementOrders() )
            {
                for ( DataElement dataElement : dataElementGroup.getDataElements() )
                {
                    ExcelItem item = new ExcelItem();

                    item.setId( importItem.getId() );

                    item.setExpression( importItem.getExpression().replace( "*", dataElement.getId() + "" ) );

                    item.setRow( rowBegin++ );

                    importItemsDest.add( item );
                }
            }
        }
    }
}

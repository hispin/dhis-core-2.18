package org.hisp.dhis.databrowser;

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

import java.io.OutputStream;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.system.util.ExcelUtils;

import com.lowagie.text.DocumentException;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DefaultDataBrowserXLSService
    implements DataBrowserXLSService
{
    public void writeDataBrowserResult( String dataBrowserTitleName, String dataBrowserFromDate,
        String dataBrowserToDate, String dataBrowserPeriodType, int fontSize, DataBrowserTable dataBrowserTable,
        OutputStream out, I18n i18n )
    {
        WritableCellFormat FORMAT_MAIN_TITLE = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, fontSize + 5, WritableFont.BOLD ) );

        WritableCellFormat FORMAT_SUB_TITLE = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, fontSize - 2 ) );

        WritableCellFormat FORMAT_HEADER = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, fontSize + 2,
            WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE ) );

        WritableCellFormat FORMAT_PAR_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, fontSize ) );

        WritableCellFormat FORMAT_ODD_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL, fontSize ) );

        try
        {
            WritableWorkbook workbook = ExcelUtils.openWorkbook( out );

            WritableSheet sheet = workbook.createSheet( i18n.getString( "drill_down_data" ), 1 );

            FORMAT_MAIN_TITLE.setShrinkToFit( true );
            FORMAT_SUB_TITLE.setShrinkToFit( true );
            
            ExcelUtils.setUpFormat( FORMAT_MAIN_TITLE, Alignment.GENERAL, Border.NONE, BorderLineStyle.THIN, Colour.WHITE );
            ExcelUtils.setUpFormat( FORMAT_SUB_TITLE, Alignment.GENERAL, Border.NONE, BorderLineStyle.THIN, Colour.WHITE );
            ExcelUtils.setUpFormat( FORMAT_HEADER, Alignment.CENTRE, Border.ALL, BorderLineStyle.NONE, Colour.BLUE_GREY );
            ExcelUtils.setUpFormat( FORMAT_PAR_TEXT, Alignment.GENERAL, Border.NONE, BorderLineStyle.THIN, Colour.GRAY_25 );
            ExcelUtils.setUpFormat( FORMAT_ODD_TEXT, Alignment.GENERAL, Border.NONE, BorderLineStyle.THIN, Colour.WHITE );

            // Title information
            ExcelUtils.writeDataBrowserTitle( sheet, FORMAT_MAIN_TITLE, FORMAT_SUB_TITLE, dataBrowserTitleName,
                dataBrowserFromDate, dataBrowserToDate, dataBrowserPeriodType, i18n );

            // Header information
            ExcelUtils.writeDataBrowserHeaders( sheet, FORMAT_HEADER, dataBrowserTable, i18n );

            // Data information
            ExcelUtils.writeDataBrowserResults( sheet, FORMAT_PAR_TEXT, FORMAT_ODD_TEXT, fontSize, dataBrowserTable );

            ExcelUtils.writeAndCloseWorkbook( workbook );
        }
        catch ( WriteException e )
        {
            e.printStackTrace();
        }
        catch ( DocumentException e )
        {
            e.printStackTrace();
        }
    }

}

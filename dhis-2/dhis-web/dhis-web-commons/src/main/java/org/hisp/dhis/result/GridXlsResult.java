package org.hisp.dhis.result;

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
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * @author Lars Helge Overland
 */
public class GridXlsResult
    implements Result
{
    private static final String DEFAULT_SHEET_NAME = "Sheet 1";
    private static final String DEFAULT_FILENAME = "Grid.xls";
    private static final String EMPTY = "";
    
    private static final WritableCellFormat FORMAT_TTTLE = new WritableCellFormat( new WritableFont(
        WritableFont.TAHOMA, 13, WritableFont.NO_BOLD, false ) );

    private static final WritableCellFormat FORMAT_LABEL = new WritableCellFormat( new WritableFont(
        WritableFont.ARIAL, 11, WritableFont.NO_BOLD, true ) );

    private static final WritableCellFormat FORMAT_TEXT = new WritableCellFormat( new WritableFont( WritableFont.ARIAL,
        11, WritableFont.NO_BOLD, false ) );

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Grid grid;
    
    public void setGrid( Grid grid )
    {
        this.grid = grid;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get grid
        // ---------------------------------------------------------------------

        Grid _grid = (Grid) invocation.getStack().findValue( "grid" );
        
        grid = _grid != null ? _grid : grid; 

        // ---------------------------------------------------------------------
        // Configure response
        // ---------------------------------------------------------------------

        HttpServletResponse response = ServletActionContext.getResponse();

        OutputStream out = response.getOutputStream();

        String filename = CodecUtils.filenameEncode( StringUtils.defaultIfEmpty( grid.getTitle(), DEFAULT_FILENAME ) ) + ".xls";
                
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, true, filename, true );
        
        // ---------------------------------------------------------------------
        // Create workbook and write to output stream
        // ---------------------------------------------------------------------

        WritableWorkbook workbook = Workbook.createWorkbook( out );

        String sheetName = CodecUtils.filenameEncode( StringUtils.defaultIfEmpty( grid.getTitle(), DEFAULT_SHEET_NAME ) );
        
        WritableSheet sheet = workbook.createSheet( sheetName, 0 );

        int rowNumber = 1;

        int columnIndex = 0;

        sheet.addCell( new Label( 0, rowNumber++, grid.getTitle(), FORMAT_TTTLE ) );

        rowNumber++;

        for ( GridHeader header : grid.getVisibleHeaders() )
        {
            sheet.addCell( new Label( columnIndex++, rowNumber, header.getName(), FORMAT_LABEL ) );
        }

        rowNumber++;

        for ( List<Object> row : grid.getVisibleRows() )
        {
            columnIndex = 0;

            for ( Object column : row )
            {
                if ( column != null && MathUtils.isNumeric( String.valueOf( column ) ) )
                {
                    sheet.addCell( new Number( columnIndex++, rowNumber, Double.valueOf( String.valueOf( column ) ), FORMAT_TEXT ) );
                }
                else
                {
                    String content = column != null ? String.valueOf( column ) : EMPTY;
                    
                    sheet.addCell( new Label( columnIndex++, rowNumber, content, FORMAT_TEXT ) );
                }
            }

            rowNumber++;
        }

        workbook.write();

        workbook.close();        
    }
}

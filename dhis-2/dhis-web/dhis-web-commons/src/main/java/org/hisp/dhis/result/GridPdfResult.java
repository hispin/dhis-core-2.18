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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

import static org.hisp.dhis.system.util.PDFUtils.*;

/**
 * @author Lars Helge Overland
 */
public class GridPdfResult
    implements Result
{
    private static final String DEFAULT_FILENAME = "Grid";

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

        String filename = CodecUtils.filenameEncode( StringUtils.defaultIfEmpty( grid.getTitle(), DEFAULT_FILENAME ) ) + ".pdf";
        
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, true, filename, false );

        // ---------------------------------------------------------------------
        // Write PDF to output stream
        // ---------------------------------------------------------------------
        
        Document document = openDocument( out );
        
        PdfPTable table = new PdfPTable( grid.getVisibleWidth() );
        
        table.setHeaderRows( 1 );
        table.setWidthPercentage( 100f );

        table.addCell( getTitleCell( grid.getTitle(), grid.getVisibleWidth() ) );
        table.addCell( getEmptyCell( grid.getVisibleWidth(), 30 ) );
        table.addCell( getSubtitleCell( grid.getSubtitle(), grid.getVisibleWidth() ) );
        table.addCell( getEmptyCell( grid.getVisibleWidth(), 30 ) );
        
        for ( GridHeader header : grid.getVisibleHeaders() )
        {
            table.addCell( getItalicCell( header.getName() ) );
        }

        table.addCell( getEmptyCell( grid.getVisibleWidth(), 10 ) );
        
        for ( List<Object> row : grid.getVisibleRows() )
        {
            for ( Object col : row )
            {
                table.addCell( getTextCell( col ) );
            }
        }

        addTableToDocument( document, table );

        closeDocument( document );
    }
}

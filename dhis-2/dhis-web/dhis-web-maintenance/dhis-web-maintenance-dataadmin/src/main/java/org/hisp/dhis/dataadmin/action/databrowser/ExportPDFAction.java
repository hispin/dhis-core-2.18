package org.hisp.dhis.dataadmin.action.databrowser;

/*
 * Copyright (c) 2004-${year}, University of Oslo
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

import static org.hisp.dhis.system.util.PDFUtils.addTableToDocument;
import static org.hisp.dhis.system.util.PDFUtils.getHeader5Cell;
import static org.hisp.dhis.system.util.PDFUtils.getHeader6Cell;
import static org.hisp.dhis.system.util.PDFUtils.getHeader7Cell;
import static org.hisp.dhis.system.util.PDFUtils.getText5Cell;
import static org.hisp.dhis.system.util.PDFUtils.getText6Cell;
import static org.hisp.dhis.system.util.PDFUtils.getText7Cell;
import static org.hisp.dhis.system.util.PDFUtils.openDocument;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.databrowser.DataBrowserTable;
import org.hisp.dhis.databrowser.MetaValue;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.PDFUtils;
import org.hisp.dhis.util.SessionUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.opensymphony.xwork2.Action;

/**
 * @author briane, eivinhb
 * @version $Id$
 */
public class ExportPDFAction
    implements Action
{    
    private static final String KEY_DATABROWSERTITLENAME  = "dataBrowserTitleName";    
    private static final String KEY_DATABROWSERFROMDATE   = "dataBrowserFromDate";
    private static final String KEY_DATABROWSERTODATE     = "dataBrowserToDate";
    private static final String KEY_DATABROWSERPERIODTYPE = "dataBrowserPeriodType";
    private static final String KEY_DATABROWSERTABLE      = "dataBrowserTableResults";

    // -------------------------------------------------------------------------
    // Input / output
    // -------------------------------------------------------------------------

    private DataBrowserTable dataBrowserTable;

    public List<MetaValue> getAllColumns()
    {
        return dataBrowserTable.getColumns();
    }

    public DataBrowserTable getDataBrowserTable()
    {
        return dataBrowserTable;
    }

    public List<List<Integer>> getAllCounts()
    {
        return dataBrowserTable.getCounts();
    }

    public Iterator<MetaValue> getRowNamesIterator()
    {
        return dataBrowserTable.getRows().iterator();
    }

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public void setFileName( String fileName )
    {
        if ( fileName.endsWith( ".pdf" ) )
        {
            this.fileName = fileName.substring( 0, fileName.length() - 4 );
        }
        else
        {
            this.fileName = fileName;
        }
    }

    public String getFileName()
    {
        return this.fileName;
    }
    
    private int fontSize;
    
    public void setFontSize( int fontSize )
    {
        this.fontSize = fontSize;
    }
    
    private String pageLayout;
    
    public void setPageLayout( String pageLayout )
    {
        this.pageLayout = pageLayout;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Manage save to file
        HttpServletResponse response = ServletActionContext.getResponse();
        response.reset();
        response.setContentType( "text/plain" );
        response.setHeader( "Content-Disposition", "attachment;filename=" + fileName + ".pdf" );
        response.setHeader( "Pragma", "anytextexeptno-cache,true" );
        response.setHeader( "Cache-Control", "max-arg=0" );

        // Get session variables set by SearchAction
        String dataBrowserTitleName = (String) SessionUtils.getSessionVar( KEY_DATABROWSERTITLENAME );
        String dataBrowserFromDate = (String) SessionUtils.getSessionVar( KEY_DATABROWSERFROMDATE );
        String dataBrowserToDate = (String) SessionUtils.getSessionVar( KEY_DATABROWSERTODATE );
        String dataBrowserPeriodType = (String) SessionUtils.getSessionVar( KEY_DATABROWSERPERIODTYPE );
        DataBrowserTable dataBrowserTable = (DataBrowserTable) SessionUtils.getSessionVar( KEY_DATABROWSERTABLE );

        // Set row colors matching the displayed Web search result
        Color headColor = new Color( 0xC0D9D9 ); // Blueish
        Color parColor = new Color( 0xDDDDDD ); // Greyish
        Color oddColor = new Color( 0xFCFCFC ); // Whiteish

        // Export to PDF
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Set initial inputStream for Velocity
            inputStream = new ByteArrayInputStream( baos.toByteArray() );
           
            // There is a problem with IText regarding setting of landscape. The
            // problem is that the first page is always Portrait
            // as newPage() does not create a new page when the page is empty.
            // This can be fixed by creating a document as follows:
            // Document document = new Document(PageSize.A4.rotate());
            // The openDocument in PDFUtils has been extended to support passing
            // of the PageSize parameter;
            
            Document document;
            
            // Set document page size (layout)
            if ( pageLayout.equals( "Landscape" ) )
            {
                document = openDocument( baos, PageSize.A4.rotate() );
            }
            else if ( pageLayout.equals( "Portrait" ) )
            {
                document = openDocument( baos, PageSize.A4 );
            }
            else // Default is landscape
            {
                document = openDocument( baos, PageSize.A4.rotate() );
            }
           
            // Heading information
            Paragraph titleParagraph = new Paragraph( "Export results for " + dataBrowserTitleName, FontFactory
                .getFont( FontFactory.HELVETICA, 16, Font.NORMAL, Color.BLACK ) );

            String fromDate = dataBrowserFromDate;
            if ( dataBrowserFromDate.length() == 0 )
            {
                fromDate = "earliest";
            }

            String toDate = dataBrowserToDate;
            if ( dataBrowserToDate.length() == 0 )
            {
                toDate = "latest";
            }
            
            Paragraph periodParagraph = new Paragraph( "From date: " + fromDate + "  To date: " + toDate
                + "  Period type: " + dataBrowserPeriodType, FontFactory.getFont( FontFactory.HELVETICA, 8,
                Font.NORMAL, Color.BLACK ) );

            // DataBrowser table
            PdfPTable table = new PdfPTable( dataBrowserTable.getColumns().size() );
            table.setWidthPercentage( 100f );
            table.setKeepTogether( false );
                       
            // Header row
            for ( MetaValue col : dataBrowserTable.getColumns() )
            {
                // Convert to new date format
                String colName = DateUtils.convertDate( col.getName() );

                PdfPCell cell;

                // Set font size for header cell
                if (fontSize == 4)
                {
                    cell = new PdfPCell( getHeader7Cell( colName, 1 ) );
                }
                else if (fontSize == 6)
                {
                    cell = new PdfPCell( getHeader6Cell( colName, 1 ) );
                }
                else // Default is 8
                {
                    cell = new PdfPCell( getHeader5Cell( colName, 1 ) );
                }
                cell.setMinimumHeight( fontSize );

                cell.setBorder( Rectangle.BOX );
                cell.setBackgroundColor( headColor );
                table.addCell( cell );
            }

            // Data rows
            Iterator<MetaValue> rowIt = dataBrowserTable.getRows().iterator();
            int i = 0;
            for ( List<Integer> col : dataBrowserTable.getCounts() )
            {
                i = i + 1;
                MetaValue rowMeta = rowIt.next();

                Color color;
                if ( i % 2 == 1 )
                {
                    color = oddColor;
                }
                else
                {
                    color = parColor;
                }

                PdfPCell cell;

                // Set font size for text cell
                if (fontSize == 4)
                {
                    cell = new PdfPCell( getText7Cell( rowMeta.getName() ) );
                }
                else if (fontSize == 6)
                {
                    cell = new PdfPCell( getText6Cell( rowMeta.getName() ) );
                }
                else // Default is 8
                {
                    cell = new PdfPCell( getText5Cell( rowMeta.getName() ) );
                }
                cell.setMinimumHeight( fontSize );
                
                cell.setBorder( Rectangle.BOX );
                cell.setBackgroundColor( color );
                table.addCell( cell );

                for ( int rowItem : col )
                {
                    Phrase phrase = new Phrase( new Integer( rowItem ).toString(), FontFactory.getFont(
                        FontFactory.HELVETICA, fontSize, Font.NORMAL, Color.BLACK ) );
              
                    // Color zero values as bold red
                    if ( rowItem == 0 )
                    {
                        phrase.getFont().setStyle( Font.BOLD );
                        phrase.getFont().setColor( Color.RED );
                    }

                    cell.setPhrase( phrase );
                    cell.setBorder( Rectangle.BOX );
                    cell.setBackgroundColor( color );
                    table.addCell( cell );
                }
                
                // Set first row as header row. This will be repeated for each
                // new page.
                table.setHeaderRows( 1 );
            }
            
            // Add heading information
            document.add( titleParagraph );
            document.add( periodParagraph );
            document.add( new Paragraph(" ") ); // Paragraph break
                        
            // Adjust column widths so that first column is a bit wider than the
            // rest.
            int numColumns = dataBrowserTable.getColumns().size();
            float[] widths = new float[numColumns];
            widths[0] = 2;
            for ( i = 1; i < numColumns; i++ )
            {
                widths[i] = 1;
            }
            table.setWidths( widths );
            
            // Add DataBrowser table
            addTableToDocument( document, table );

            PDFUtils.closeDocument( document );
            
            // Set final inputStream for Velocity 
            inputStream = new ByteArrayInputStream( baos.toByteArray() );
        }
        catch ( Exception ex )
        {
            // Close the inputStream after it has been managed by the browser
            inputStream.close();
        }

        return SUCCESS;
    }
}

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

import static org.hisp.dhis.system.util.PDFUtils.addTableToDocument;
import static org.hisp.dhis.system.util.PDFUtils.closeDocument;
import static org.hisp.dhis.system.util.PDFUtils.getHeaderCell;
import static org.hisp.dhis.system.util.PDFUtils.getTextCell;
import static org.hisp.dhis.system.util.PDFUtils.openDocument;

import java.awt.Color;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.PDFUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DefaultDataBrowserPdfService
    implements DataBrowserPdfService
{
    private static final Color headColor = new Color( 0xC0D9D9 ); // Blueish

    private static final Color parColor = new Color( 0xDDDDDD ); // Greyish

    private static final Color oddColor = new Color( 0xFCFCFC ); // Whiteish

    public void writeDataBrowserResult( String dataBrowserTitleName, String dataBrowserFromDate,
        String dataBrowserToDate, String dataBrowserPeriodType, String pageLayout, int fontSize,
        DataBrowserTable dataBrowserTable, OutputStream out, I18n i18n )
    {
        // There is a problem with IText regarding setting of landscape. The
        // problem is that the first page is always Portrait
        // as newPage() does not create a new page when the page is empty.
        // This can be fixed by creating a document as follows:
        // Document document = new Document(PageSize.A4.rotate());
        // The openDocument in PDFUtils has been extended to support passing
        // of the PageSize parameter;

        try
        {
            Document document;

            // DataBrowser table
            PdfPTable table = new PdfPTable( dataBrowserTable.getColumns().size() );
            table.setWidthPercentage( 100f );
            table.setKeepTogether( false );

            // Set document page size (layout)
            document = this.initDocumentByLayout( out, pageLayout );

            // Heading information
            this.writeHeader( document, dataBrowserTitleName, dataBrowserFromDate, dataBrowserToDate,
                dataBrowserPeriodType, i18n );

            this.writeLines( document, table, dataBrowserTable, fontSize, i18n );

            // Add DataBrowser table
            addTableToDocument( document, table );

            closeDocument( document );
        }
        catch ( DocumentException e )
        {
            e.printStackTrace();
        }

    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    private Document initDocumentByLayout( OutputStream out, String pageLayout )
    {
        if ( (pageLayout == null) || pageLayout.isEmpty() || pageLayout.equals( "Landscape" ) )
        {
            return openDocument( out, PageSize.A4.rotate() );
        }
        else if ( pageLayout.equals( "Portrait" ) )
        {
            return openDocument( out, PageSize.A4 );
        }

        return openDocument( out );
    }

    private void writeHeader( Document document, String dataBrowserTitleName, String dataBrowserFromDate,
        String dataBrowserToDate, String dataBrowserPeriodType, I18n i18n )
        throws DocumentException
    {
        Paragraph titleParagraph = new Paragraph( i18n.getString( "export_results_for" ) + " " + dataBrowserTitleName,
            PDFUtils.getBoldFont( 12 ) );

        if ( dataBrowserFromDate.length() == 0 )
        {
            dataBrowserFromDate = i18n.getString( "earliest" );
        }

        if ( dataBrowserToDate.length() == 0 )
        {
            dataBrowserToDate = i18n.getString( "latest" );
        }

        Paragraph periodParagraph = new Paragraph( i18n.getString( "from_date" ) + ": " + dataBrowserFromDate + " "
            + i18n.getString( "to_date" ) + ": " + dataBrowserToDate + ", " + i18n.getString( "period_type" ) + ": "
            + i18n.getString( dataBrowserPeriodType ), PDFUtils.getFont( 8 ) );

        if ( i18n != null )
        {
            // Add heading information
            document.add( titleParagraph );
            document.add( periodParagraph );
            document.add( new Paragraph( " " ) ); // Paragraph break
        }
    }

    private void writeLines( Document document, PdfPTable table, DataBrowserTable dataBrowserTable, int fontSize,
        I18n i18n )
        throws DocumentException
    {
        // Header row
        this.writeHeaderRow( dataBrowserTable, table, fontSize, i18n );

        // Data rows
        this.writeDataRows( dataBrowserTable, table, fontSize );
    }

    private void writeHeaderRow( DataBrowserTable dataBrowserTable, PdfPTable table, int fontSize, I18n i18n )
    {
        for ( MetaValue col : dataBrowserTable.getColumns() )
        {
            // Convert to new date format
            String colName = i18n.getString( DateUtils.convertDate( col.getName() ) );

            table.addCell( this.createHeaderCell( colName, fontSize, headColor ) );
        }
    }

    private void writeDataRows( DataBrowserTable dataBrowserTable, PdfPTable table, int fontSize )
        throws DocumentException
    {
        // Data rows
        int i = 0;
        Color color;
        Iterator<MetaValue> rowIt = dataBrowserTable.getRows().iterator();

        for ( List<String> col : dataBrowserTable.getCounts() )
        {
            i = i + 1;
            MetaValue rowMeta = rowIt.next();

            color = ( i % 2 == 1 ) ? oddColor : parColor;

            PdfPCell cell = this.createTextCell( rowMeta.getName(), fontSize, color );

            table.addCell( cell );

            for ( String rowItem : col )
            {
                Phrase phrase = new Phrase( new Integer( rowItem ).toString(), FontFactory.getFont(
                    FontFactory.HELVETICA, fontSize, Font.NORMAL, Color.BLACK ) );

                // Color zero values as bold red
                if ( rowItem.trim().matches( "0" ) )
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

        // Adjust column widths so that first column
        // is a bit wider than the rest.
        this.adjustColumnWitdh( dataBrowserTable, table, i );

    }

    private PdfPCell createHeaderCell( String columnName, int fontSize, Color color )
    {
        PdfPCell cell = getHeaderCell( columnName, 1 );

        cell.setMinimumHeight( fontSize );
        cell.setBorder( Rectangle.BOX );
        cell.setBackgroundColor( color );

        return cell;
    }

    private PdfPCell createTextCell( String columnName, int fontSize, Color color )
    {
        PdfPCell cell = getTextCell( columnName );

        cell.setMinimumHeight( fontSize );
        cell.setBorder( Rectangle.BOX );
        cell.setBackgroundColor( color );

        return cell;
    }

    private void adjustColumnWitdh( DataBrowserTable dataBrowserTable, PdfPTable table, int i )
        throws DocumentException
    {
        int numColumns = dataBrowserTable.getColumns().size();
        float[] widths = new float[numColumns];
        widths[0] = 2;

        for ( i = 1; i < numColumns; i++ )
        {
            widths[i] = 1;
        }
        table.setWidths( widths );
    }
}

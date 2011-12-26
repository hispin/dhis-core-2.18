package org.hisp.dhis.tallysheet;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.system.util.PDFUtils;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben
 *         Wangberg
 * @version $Id$
 */
public class DefaultTallySheetPdfService
    implements TallySheetPdfService
{
    //TODO this class must be improved and use PdfUtils
    
    private static Font headerFont;

    private static Font tableFont;

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public InputStream createTallySheetPdf( TallySheet tallySheet, I18n i18n )
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = null;
        Document document = new Document();
        String facilityName = "";

        boolean a3Format = tallySheet.isA3Format();

        if ( tallySheet.isDisplayFacilityName() )
        {
            facilityName = tallySheet.getFacilityName();
        }

        try
        {
            document.setPageSize( a3Format ? PageSize.A3 : PageSize.A4 );
            PdfWriter.getInstance( document, outputStream );

            writeMetadata( document, tallySheet.getTallySheetName(), i18n );

            document.open();
            initFont();
            writeHeader( document, tallySheet.getTallySheetName(), facilityName, i18n );
            writeLines( document, tallySheet.getTallySheetTuples(), a3Format, tallySheet.getRowWidth() );
            document.close();

            inputStream = new ByteArrayInputStream( outputStream.toByteArray() );
        }
        catch ( DocumentException e )
        {
            e.printStackTrace();
        }

        return inputStream;
    }

    private void writeMetadata( Document document, String title, I18n i18n )
    {
        document.addTitle( title );
        document.addAuthor( "DHIS 2" );

        if ( i18n != null )
        {
            document.addSubject( i18n.getString( "tally_sheet_report" ) );
        }

        document.addKeywords( "tallysheet, health, data, tally sheet" );
        document.addCreator( "DHIS 2" );
    }

    private void writeHeader( Document document, String header, String facilityName, I18n i18n )
        throws DocumentException
    {
        document.add( new Paragraph( header, headerFont ) );

        if ( i18n != null )
        {
            document.add( new Paragraph( i18n.getString( "facility" ) + ": " + facilityName, headerFont ) );
            document.add( new Paragraph( i18n.getString( "month" ) + ": ", headerFont ) );
            document.add( new Paragraph( i18n.getString( "year" ) + ": ", headerFont ) );

            Paragraph totalParagraph = new Paragraph( i18n.getString( "total" ) + ": ", headerFont );
            totalParagraph.setAlignment( "right" );
            totalParagraph.setIndentationRight( 10 );
            totalParagraph.setSpacingAfter( 2 );
            document.add( totalParagraph );
        }
    }

    private void writeLines( Document document, List<TallySheetTuple> tallySheetTuples, boolean a3Format, int rowWidth )
        throws DocumentException
    {
        double a4Multiplier = (PageSize.A3.getWidth() / PageSize.A4.getWidth()) * 1.1;
        float[] widths = { 0.2f, 0.55f, 0.05f };

        if ( !a3Format )
        {
            widths[0] = (float) (widths[0] * a4Multiplier);
            widths[2] = (float) (widths[2] * a4Multiplier);
            widths[1] = 1f - widths[0] - widths[2];
        }

        PdfPTable table = new PdfPTable( widths );
        table.setWidthPercentage( 100 );

        DataElement dataElement;

        for ( TallySheetTuple tallySheetTuple : tallySheetTuples )
        {
            dataElement = tallySheetTuple.getDataElement();
            int rows = tallySheetTuple.getNumberOfRows();
            if ( tallySheetTuple.isChecked() && rows > 0 )
            {
                table.addCell( createNameCell( dataElement.getName(), tableFont ) );
                table.addCell( createCellsCell( rows, tableFont, rowWidth ) );
                table.addCell( createTotalBoxCell( tableFont ) );
            }
        }
        document.add( table );
    }

    private PdfPCell createNameCell( String elementName, Font font )
    {
        PdfPCell nameCell = new PdfPCell( new Paragraph( elementName, font ) );
        nameCell.setBorderWidth( 0 );
        nameCell.setBorderWidthTop( (float) 0.5 );
        nameCell.setHorizontalAlignment( Element.ALIGN_LEFT );
        return nameCell;
    }

    private PdfPCell createCellsCell( int rows, Font font, int rowWidth )
    {
        StringBuilder cellRows = new StringBuilder();

        for ( int i = 0; i < rows; i++ )
        {
            if ( i > 0 )
            {
                cellRows.append( "\n" );
            }

            for ( int j = 0; j < rowWidth; j++ )
            {
                if ( j % 5 == 0 )
                {
                    cellRows.append( " " );
                }

                if ( j != 0 && j % 25 == 0 )
                {
                    cellRows.append( "    " );
                }

                cellRows.append( "0" );
            }
        }

        Paragraph cellParagraph = new Paragraph( cellRows.toString(), font );
        PdfPCell cellCell = new PdfPCell( cellParagraph );
        cellCell.setBorderWidth( 0 );
        cellCell.setBorderWidthTop( (float) 0.5 );
        cellCell.setHorizontalAlignment( Element.ALIGN_RIGHT );
        cellCell.setPaddingBottom( 4 );
        return cellCell;
    }

    private PdfPCell createTotalBoxCell( Font font )
    {
        PdfPCell boxCell = new PdfPCell();
        PdfPTable totalTable = new PdfPTable( 1 );
        PdfPCell totalCell = new PdfPCell();
        totalCell.setBorderWidth( (float) 0.5 );
        totalCell.setFixedHeight( (float) (font.getCalculatedSize() * 1.2) );
        totalTable.addCell( totalCell );
        boxCell.addElement( totalTable );
        boxCell.setBorderWidth( 0 );
        boxCell.setBorderWidthTop( (float) 0.5 );
        return boxCell;
    }

    private void initFont()
    {
        headerFont = PDFUtils.getBoldFont( 12 );
        tableFont = PDFUtils.getFont( 8 );
    }
}

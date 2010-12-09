package org.hisp.dhis.system.util;

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

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.validation.ValidationRule;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Lars Helge Overland
 * @version $Id$
 * @modifier Dang Duy Hieu
 * @since 2010-05-19
 */
public class PDFUtils
{
    public static final int ALIGN_CENTER = PdfPCell.ALIGN_CENTER;

    public static final int ALIGN_LEFT = PdfPCell.ALIGN_LEFT;

    public static final int ALIGN_RIGHT = PdfPCell.ALIGN_RIGHT;

    private static final Font TEXT = new Font( Font.HELVETICA, 9, Font.NORMAL );

    private static final Font TEXT5 = new Font( Font.HELVETICA, 8, Font.NORMAL );

    private static final Font TEXT6 = new Font( Font.HELVETICA, 6, Font.NORMAL );

    private static final Font TEXT7 = new Font( Font.HELVETICA, 4, Font.NORMAL );

    private static final Font ITALIC = new Font( Font.HELVETICA, 9, Font.ITALIC );

    private static final Font HEADER1 = new Font( Font.HELVETICA, 20, Font.BOLD );

    private static final Font HEADER2 = new Font( Font.HELVETICA, 16, Font.BOLD );

    private static final Font HEADER3 = new Font( Font.HELVETICA, 12, Font.BOLD );

    private static final Font HEADER4 = new Font( Font.HELVETICA, 9, Font.BOLD );

    private static final Font HEADER5 = new Font( Font.HELVETICA, 8, Font.BOLD );

    private static final Font HEADER6 = new Font( Font.HELVETICA, 6, Font.BOLD );

    private static final Font HEADER7 = new Font( Font.HELVETICA, 4, Font.BOLD );

    public static final String PDF_ARIAL_FONT = "arial.ttf";

    /**
     * Creates a document.
     * 
     * @param outputStream The output stream to write the document content.
     * @return A Document.
     */
    public static Document openDocument( OutputStream outputStream )
    {
        return openDocument( outputStream, PageSize.A4 );
    }

    /**
     * Creates a document.
     * 
     * @param outputStream The output stream to write the document content.
     * @param pageSize the page size.
     * @return A Document.
     */
    public static Document openDocument( OutputStream outputStream, Rectangle pageSize )
    {
        try
        {
            Document document = new Document( pageSize );

            PdfWriter.getInstance( document, outputStream );

            document.open();

            return document;
        }
        catch ( DocumentException ex )
        {
            throw new RuntimeException( "Failed to open PDF document", ex );
        }
    }

    /**
     * Starts a new page in the document.
     * 
     * @param document The document to start a new page in.
     */
    public static void startNewPage( Document document )
    {
        document.newPage();
    }

    /**
     * <p>
     * Creates a table. Specify the columns and widths by providing one<br>
     * float per column with a percentage value. For instance
     * </p>
     * 
     * <p>
     * getPdfPTable( 0.35f, 0.65f )
     * </p>
     * 
     * <p>
     * will give you a table with two columns where the first covers 35 %<br>
     * of the page while the second covers 65 %.
     * </p>
     * 
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     * @return
     */
    public static PdfPTable getPdfPTable( boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = new PdfPTable( columnWidths );

        table.setWidthPercentage( 100f );
        table.setKeepTogether( keepTogether );

        return table;
    }

    /**
     * Adds a table to a document.
     * 
     * @param document The document to add the table to.
     * @param table The table to add to the document.
     */
    public static void addTableToDocument( Document document, PdfPTable table )
    {
        try
        {
            document.add( table );
        }
        catch ( DocumentException ex )
        {
            throw new RuntimeException( "Failed to add table to document", ex );
        }
    }

    /**
     * Moves the cursor to the next page in the document.
     * 
     * @param document The document.
     */
    public static void moveToNewPage( Document document )
    {
        document.newPage();
    }

    /**
     * Closes the document if it is open.
     * 
     * @param document The document to close.
     */
    public static void closeDocument( Document document )
    {
        if ( document.isOpen() )
        {
            document.close();
        }
    }

    /**
     * Creates a cell.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font of the cell text.
     * @param verticalAlign The vertical alignment of the text in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getCell( String text, int colspan, Font font, int verticalAlign )
    {
        Paragraph paragraph = new Paragraph( text, font );

        PdfPCell cell = new PdfPCell( paragraph );

        cell.setColspan( colspan );
        cell.setBorder( 0 );
        cell.setMinimumHeight( 15 );
        cell.setHorizontalAlignment( verticalAlign );

        return cell;
    }

    /**
     * Creates an empty cell.
     * 
     * @param colspan The column span of the cell.
     * @param height The height of the column.
     * @return A PdfCell.
     */
    public static PdfPCell getCell( int colSpan, int height )
    {
        PdfPCell cell = new PdfPCell();

        cell.setColspan( colSpan );
        cell.setBorder( 0 );
        cell.setMinimumHeight( height );

        return cell;
    }

    /**
     * Creates a cell spanning one column.
     * 
     * @param text The text to include in the cell.
     * @param font The font of the cell content.
     * @return A PdfCell.
     */
    public static PdfPCell getCell( String text, Font font )
    {
        return getCell( text, 1, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getTextCell( String text, int colspan )
    {
        return getCell( text, colspan, TEXT, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font for text in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getTextCell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param horizontalAlign The horizontal align, either ALIGN_LEFT,
     *        ALIGN_RIGHT, or ALIGN_CENTER.
     * @return A PdfCell.
     */
    public static PdfPCell getTextCell( String text, int colspan, int horizontalAlign )
    {
        return getCell( text, colspan, TEXT, horizontalAlign );
    }

    /**
     * Creates a cell with text font spanning one column.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getTextCell( String text )
    {
        return getCell( text, 1, TEXT, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text font spanning one column.
     * 
     * @param text The text to include in the cell.
     * @param font The font for text in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getTextCell( String text, Font font )
    {
        return getCell( text, 1, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 5 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getText5Cell( String text )
    {
        return getCell( text, 1, TEXT5, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 5 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getText5Cell( String text, Font font )
    {
        return getCell( text, 1, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 6 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getText6Cell( String text )
    {
        return getCell( text, 1, TEXT6, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 5 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getText6Cell( String text, Font font )
    {
        return getCell( text, 1, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 7 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getText7Cell( String text )
    {
        return getCell( text, 1, TEXT7, ALIGN_LEFT );
    }

    /**
     * Creates a cell with text 5 font.
     * 
     * @param text The text to include in the cell.
     * @return A PdfCell.
     */

    public static PdfPCell getText7Cell( String text, Font font )
    {
        return getCell( text, 1, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with italic text font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getItalicCell( String text, int colspan )
    {
        return getCell( text, colspan, ITALIC, ALIGN_LEFT );
    }

    /**
     * Creates a cell with italic text font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font to embed in text of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getItalicCell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 1 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader1Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER1, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 2 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader2Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER2, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 3 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader3Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER3, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 3 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font of the cell content.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader3Cell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 3 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader4Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER4, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 3 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The font of the cell content.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader4Cell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 5 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader5Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER5, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 5 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The customize font of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader5Cell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 6 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader6Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER6, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 6 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The customize font of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader6Cell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 7 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader7Cell( String text, int colspan )
    {
        return getCell( text, colspan, HEADER7, ALIGN_LEFT );
    }

    /**
     * Creates a cell with header 7 font.
     * 
     * @param text The text to include in the cell.
     * @param colspan The column span of the cell.
     * @param font The customize font of the cell.
     * @return A PdfCell.
     */
    public static PdfPCell getHeader7Cell( String text, int colspan, Font font )
    {
        return getCell( text, colspan, font, ALIGN_LEFT );
    }

    /**
     * Creates a BaseFont with the given dimension
     * 
     * @param dimension whether horizontal or vertical
     * @return A BaseFont.
     */
    public static BaseFont getTrueTypeFontByDimension( String dimension )
    {
        try
        {
            return BaseFont.createFont( PDF_ARIAL_FONT, dimension, BaseFont.EMBEDDED );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Error occurred in creating a BaseFont instance by the given dimension" );
        }

    }

    /**
     * Writes a "Data Elements" title in front of page
     * 
     * @param dataElementIds the identifier list of Data
     * @param i18n The i18n object
     * @param format The i18nFormat object
     * 
     */
    public static void printObjectFrontPage( Document document, Collection<?> objectIds, I18n i18n,
        I18nFormat format, String frontPageLabel )
    {
        if ( objectIds == null || objectIds.size() > 0 )
        {
            String title = i18n.getString( frontPageLabel );

            printFrontPage( document, title, i18n, format );
        }
    }

    /**
     * Writes a "Data dictionary" title in front of page
     * 
     * @param document The document
     * @param i18n The i18n object
     * @param format The i18nFormat object
     * 
     */
    public static void printDocumentFrontPage( Document document, I18n i18n, I18nFormat format )
    {
        String title = i18n.getString( "data_dictionary" );

        printFrontPage( document, title, i18n, format );
    }

    /**
     * Writes a DHIS2.0 title in front of page
     * 
     * @param document The document
     * @param exportParams the exporting params
     * 
     */
    private static void printFrontPage( Document document, String title, I18n i18n, I18nFormat format )
    {
        BaseFont bf = getTrueTypeFontByDimension( BaseFont.IDENTITY_H );

        Font TEXT = new Font( bf, 9, Font.NORMAL );
        Font HEADER2 = new Font( bf, 16, Font.BOLD );

        PdfPTable table = getPdfPTable( true, 1.00f );

        table.addCell( getCell( i18n.getString( "district_health_information_software" ), 1, TEXT, ALIGN_CENTER ) );

        table.addCell( getCell( 1, 40 ) );

        table.addCell( getCell( title, 1, HEADER2, ALIGN_CENTER ) );

        table.addCell( getCell( 1, 40 ) );

        String date = format.formatDate( Calendar.getInstance().getTime() );

        table.addCell( getCell( date, 1, TEXT, ALIGN_CENTER ) );

        addTableToDocument( document, table );

        moveToNewPage( document );
    }

    /**
     * Creates a table with the given data element
     * 
     * @param element The data element
     * @param i18n i18n object
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printDataElement( DataElement element, I18n i18n, Font HEADER3, Font ITALIC, Font TEXT,
        boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeader3Cell( element.getName(), 2, HEADER3 ) );

        table.addCell( getCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
        table.addCell( getTextCell( element.getShortName(), TEXT ) );

        if ( nullIfEmpty( element.getAlternativeName() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "alternative_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getAlternativeName(), TEXT ) );
        }
        if ( nullIfEmpty( element.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getCode() ) );
        }
        if ( nullIfEmpty( element.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( element.getDescription(), TEXT ) );
        }

        table.addCell( getItalicCell( i18n.getString( "active" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( element.isActive() ) ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "type" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getType().get( element.getType() ) ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "aggregation_operator" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getAggregationOperator().get( element.getAggregationOperator() ) ),
            TEXT ) );

        table.addCell( getCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given indicator
     * 
     * @param indicator The indicator
     * @param i18n i18n object
     * @param expressionService The expression service
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printIndicator( Indicator indicator, I18n i18n, ExpressionService expressionService,
        Font HEADER3, Font ITALIC, Font TEXT, boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeader3Cell( indicator.getName(), 2, HEADER3 ) );

        table.addCell( getCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
        table.addCell( getTextCell( indicator.getShortName(), TEXT ) );

        if ( nullIfEmpty( indicator.getAlternativeName() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "alternative_name" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getAlternativeName(), TEXT ) );
        }
        if ( nullIfEmpty( indicator.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getCode() ) );
        }
        if ( nullIfEmpty( indicator.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( indicator.getDescription(), TEXT ) );
        }

        table.addCell( getItalicCell( i18n.getString( "annualized" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( indicator.getAnnualized() ) ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "indicator_type" ), 1, ITALIC ) );
        table.addCell( getTextCell( indicator.getIndicatorType().getName(), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "numerator_description" ), 1, ITALIC ) );
        table.addCell( getTextCell( indicator.getNumeratorDescription(), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "numerator_aggregation_operator" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getAggregationOperator().get(
            indicator.getNumeratorAggregationOperator() ) ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "numerator_formula" ), 1, ITALIC ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( indicator.getNumerator() ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "denominator_description" ), 1, ITALIC ) );
        table.addCell( getTextCell( indicator.getDenominatorDescription(), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "denominator_aggregation_operator" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getAggregationOperator().get(
            indicator.getDenominatorAggregationOperator() ) ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "denominator_formula" ), 1, ITALIC ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( indicator.getDenominator() ), TEXT ) );

        table.addCell( getCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given unit
     * 
     * @param unit The organization unit
     * @param i18n i18n object
     * @param format
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printOrganisationUnit( OrganisationUnit unit, I18n i18n, I18nFormat format, Font HEADER3,
        Font ITALIC, Font TEXT, boolean keepTogether, float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeader3Cell( unit.getName(), 2, HEADER3 ) );

        table.addCell( getCell( 2, 15 ) );

        table.addCell( getItalicCell( i18n.getString( "short_name" ), 1, ITALIC ) );
        table.addCell( getTextCell( unit.getShortName(), TEXT ) );

        if ( nullIfEmpty( unit.getCode() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "code" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getCode() ) );
        }

        table.addCell( getItalicCell( i18n.getString( "opening_date" ), 1, ITALIC ) );
        table.addCell( getTextCell( unit.getOpeningDate() != null ? format.formatDate( unit.getOpeningDate() ) : "" ) );

        if ( nullIfEmpty( unit.getClosedDate().toString() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "closed_date" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getClosedDate() != null ? format.formatDate( unit.getClosedDate() ) : "" ) );
        }

        table.addCell( getItalicCell( i18n.getString( "active" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( getBoolean().get( unit.isActive() ) ), TEXT ) );

        if ( nullIfEmpty( unit.getComment() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "comment" ), 1, ITALIC ) );
            table.addCell( getTextCell( unit.getComment(), TEXT ) );
        }

        table.addCell( getCell( 2, 30 ) );

        return table;
    }

    /**
     * Creates a table with the given validation rule
     * 
     * @param validationRule The validation rule
     * @param i18n i18n object
     * @param expressionService The expression service
     * @param HEADER3 The header3 font
     * @param ITALIC The italic font
     * @param TEXT The text font
     * @param keepTogether Indicates whether the table could be broken across
     *        multiple pages or should be kept at one page.
     * @param columnWidths The column widths.
     */
    public static PdfPTable printValidationRule( ValidationRule validationRule, I18n i18n,
        ExpressionService expressionService, Font HEADER3, Font ITALIC, Font TEXT, boolean keepTogether,
        float... columnWidths )
    {
        PdfPTable table = getPdfPTable( keepTogether, columnWidths );

        table.addCell( getHeader3Cell( validationRule.getName(), 2, HEADER3 ) );

        table.addCell( getCell( 2, 15 ) );

        if ( nullIfEmpty( validationRule.getDescription() ) != null )
        {
            table.addCell( getItalicCell( i18n.getString( "description" ), 1, ITALIC ) );
            table.addCell( getTextCell( validationRule.getDescription(), TEXT ) );
        }

        table.addCell( getItalicCell( i18n.getString( "type" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getType() ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "operator" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getOperator() ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "left_side_of_expression" ), 1, ITALIC ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( validationRule.getLeftSide()
            .getExpression() ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "left_side_description" ), 1, ITALIC ) );
        table.addCell( getTextCell( validationRule.getLeftSide().getDescription(), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "right_side_of_expression" ), 1, ITALIC ) );
        table.addCell( getTextCell( expressionService.getExpressionDescription( validationRule.getRightSide()
            .getExpression() ), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "right_side_description" ), 1, ITALIC ) );
        table.addCell( getTextCell( validationRule.getRightSide().getDescription(), TEXT ) );

        table.addCell( getItalicCell( i18n.getString( "period_type" ), 1, ITALIC ) );
        table.addCell( getTextCell( i18n.getString( validationRule.getPeriodType().getName() ), TEXT ) );

        table.addCell( getCell( 2, 30 ) );

        return table;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static Map<Boolean, String> getBoolean()
    {
        Map<Boolean, String> map = new HashMap<Boolean, String>();
        map.put( true, "yes" );
        map.put( false, "no" );
        return map;
    }

    private static Map<String, String> getType()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.VALUE_TYPE_STRING, "text" );
        map.put( DataElement.VALUE_TYPE_INT, "number" );
        map.put( DataElement.VALUE_TYPE_BOOL, "yes_no" );
        return map;
    }

    private static Map<String, String> getAggregationOperator()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put( DataElement.AGGREGATION_OPERATOR_SUM, "sum" );
        map.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, "average" );
        map.put( DataElement.AGGREGATION_OPERATOR_COUNT, "count" );
        return map;
    }

}

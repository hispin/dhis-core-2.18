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

import java.io.OutputStream;

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
}

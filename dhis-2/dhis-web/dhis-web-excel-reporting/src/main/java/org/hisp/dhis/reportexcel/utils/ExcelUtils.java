package org.hisp.dhis.reportexcel.utils;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import jxl.Cell;
import jxl.Sheet;
import jxl.write.Blank;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * @author Tran Thanh Tri
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExcelUtils
{
    public static final String TEXT = "TEXT";

    public static final String NUMBER = "NUMBER";

    public static final String NUMBER_OF_ZERO = "0";

    private final static Integer NUMBER_OF_LETTER = new Integer( 26 );

    public static void writeValue( int row, int column, String value, String type, WritableSheet sheet,
        WritableCellFormat format )
        throws RowsExceededException, WriteException
    {

        if ( row > 0 && column > 0 )
        {
            if ( type.equalsIgnoreCase( TEXT ) )
            {
                sheet.addCell( new Label( column - 1, row - 1, value==null?"":value , format ) );
            }
            if ( type.equalsIgnoreCase( NUMBER ) )
            {
                double v = Double.parseDouble( value );
                if ( v != 0 )
                {
                    sheet.addCell( new Number( column - 1, row - 1, v, format ) );

                }
                else
                {

                    sheet.addCell( new Blank( column - 1, row - 1, format ) );
                }
            }
        }

    }

    public static Cell getValue( int row, int column, Sheet sheet )
    {
        return sheet.getCell( column - 1, row - 1 );
    }

    public static String convertColNumberToColName( int column )
    {

        String ConvertToLetter = "";

        int iAlpha = column / 27;
        int iRemainder = column - (iAlpha * 26);

        if ( iAlpha > 0 )
        {
            ConvertToLetter = String.valueOf( ((char) (iAlpha + 64)) );
        }
        if ( iRemainder > 0 )
        {
            ConvertToLetter += String.valueOf( ((char) (iRemainder + 64)) );
        }

        return ConvertToLetter;
    }

    public static void writeFormula( int row, int column, String formula, WritableSheet sheet, WritableCellFormat format )
        throws RowsExceededException, WriteException
    {
        if ( row > 0 && column > 0 )
        {
            sheet.addCell( new Formula( column - 1, row - 1, formula, format ) );
        }
    }

    public static String readValue( int row, int column, Sheet sheet )
    {
        Cell cell = sheet.getCell( column - 1, row - 1 );
        return cell.getContents();
    }

	/* POI methods */
    public static void writeValueByPOI( int row, int column, String value, String type, HSSFSheet sheet,
        HSSFCellStyle cellStyle )
    {
        if ( row > 0 && column > 0 )
        {
            HSSFRow rowPOI = sheet.getRow( row - 1 );
            HSSFCell cellPOI = rowPOI.createCell( column - 1 );

            cellPOI.setCellStyle( cellStyle );

            if ( type.equalsIgnoreCase( ExcelUtils.TEXT ) )
            {
                cellPOI.setCellValue( new HSSFRichTextString( value ) );
            }
            if ( type.equalsIgnoreCase( ExcelUtils.NUMBER ) )
            {
                double v = Double.parseDouble( value );

                if ( v != 0 )
                {
                    cellPOI.setCellValue( new HSSFRichTextString( value ) );

                }
                else
                {
                    cellPOI.setCellValue( new HSSFRichTextString( ExcelUtils.NUMBER_OF_ZERO ) );
                }
            }
        }
    }

    public static HSSFCell getValueByPOI( int row, int column, HSSFSheet sheet )
    {
        return sheet.getRow( row - 1 ).getCell( column - 1 );
    }

    public static void writeFormulaByPOI( int row, int column, String formula, HSSFSheet sheet, HSSFCellStyle cellStyle )
    {
        if ( row > 0 && column > 0 )
        {
            HSSFRow rowPOI = sheet.getRow( row - 1 );
            HSSFCell cellPOI = rowPOI.createCell( column - 1 );

            cellPOI.setCellStyle( cellStyle );
            cellPOI.setCellFormula( formula );
        }
    }
	

    public static int convertExcelColumnNameToNumber( String columnName )
    {
        try
        {
            int iCol = 0;

            if ( columnName.length() > 0 )
            {
                char[] characters = columnName.toUpperCase().toCharArray();

                for ( int i = 0; i < characters.length; i++ )
                {
                    iCol *= NUMBER_OF_LETTER;
                    iCol += (characters[i] - 'A' + 1);
                }
            }
            return iCol;
        }
        catch ( Exception e )
        {
            return -1;
        }
    }
}

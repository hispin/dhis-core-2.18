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

/**
 * @author Tran Thanh Tri
 * @author Chau Thu Tran
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class ExcelUtils
{
    public static final String ZERO = "0.0";

    public static final String TEXT = "TEXT";

    public static final String NUMBER = "NUMBER";

    public static final String EXTENSION_XLS = ".xls";

    private static final Integer NUMBER_OF_LETTER = new Integer( 26 );

    private static final Integer POI_CELLSTYLE_BLANK = new Integer( org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK );

    private static final Byte POI_CELLERROR_NAN = (byte) org.apache.poi.ss.usermodel.ErrorConstants.ERROR_NA;

    private static final Byte POI_CELLERROR_INFINITE = (byte) org.apache.poi.ss.usermodel.ErrorConstants.ERROR_NUM;

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    /* JXL - Get the specified cell */
    public static Cell getCell( int row, int column, Sheet sheet )
    {
        return sheet.getCell( column - 1, row - 1 );
    }

    /* JXL - Read the value of specified cell */
    public static String readValue( int row, int column, Sheet sheet )
    {
        return sheet.getCell( column - 1, row - 1 ).getContents();
    }

    /* JXL - Write the value with customize format */
    public static void writeValue( int row, int column, String value, String type, WritableSheet sheet,
        WritableCellFormat format )
        throws RowsExceededException, WriteException
    {
        if ( row > 0 && column > 0 )
        {
            if ( type.equalsIgnoreCase( TEXT ) )
            {
                sheet.addCell( new Label( column - 1, row - 1, (value == null ? "" : value), format ) );
            }
            else if ( type.equalsIgnoreCase( NUMBER ) )
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

    /* JXL - Write formula with customize format */
    public static void writeFormula( int row, int column, String formula, WritableSheet sheet, WritableCellFormat format )
        throws RowsExceededException, WriteException
    {
        if ( row > 0 && column > 0 )
        {
            sheet.addCell( new Formula( column - 1, row - 1, formula, format ) );
        }
    }

    /* POI - Get the specified cell */
    public static org.apache.poi.ss.usermodel.Cell getCellByPOI( int row, int column,
        org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        return sheetPOI.getRow( row - 1 ).getCell( column - 1 );
    }

    /* POI - Read the value of specified cell */
    public static String readValuePOI( int row, int column, org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        org.apache.poi.ss.usermodel.Cell cellPOI = getCellByPOI( row - 1, column - 1, sheetPOI );

        String value = "";

        if ( cellPOI != null )
        {
            switch ( cellPOI.getCellType() )
            {
            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                value = cellPOI.getRichStringCellValue().toString();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                value = String.valueOf( cellPOI.getBooleanCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR:
                value = String.valueOf( cellPOI.getErrorCellValue() );
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA:
                value = cellPOI.getCellFormula();
                break;

            case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                value = String.valueOf( cellPOI.getNumericCellValue() );
                break;

            default:
                value = cellPOI.getStringCellValue();
                break;
            }
        }

        return value;

    }

    /**
     * USING FOR XLS-XLSX EXTENSION
     */

    /* POI - Write value without CellStyle */
    public static void writeValueByPOI( int row, int column, String value, String type,
        org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        if ( row > 0 && column > 0 )
        {

            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );
            org.apache.poi.ss.usermodel.CellStyle cellStylePOI = sheetPOI.getColumnStyle( column - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.getCell( column - 1 );

            if ( cellPOI == null )
            {
                cellPOI = rowPOI.createCell( column - 1 );
            }
            else
            {
                cellStylePOI = cellPOI.getCellStyle();
            }

            cellPOI.setCellStyle( cellStylePOI );

            if ( type.equalsIgnoreCase( ExcelUtils.TEXT ) )
            {
                cellPOI.setCellValue( value );
            }
            else if ( type.equalsIgnoreCase( ExcelUtils.NUMBER ) )
            {
                if ( value.equals( ZERO ) )
                {
                    cellPOI.setCellType( POI_CELLSTYLE_BLANK );
                }
                else if ( Double.isNaN( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_NAN );
                }
                else if ( Double.isInfinite( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_INFINITE );
                }
                else
                {
                    cellPOI.setCellValue( Double.parseDouble( value ) );
                }
            }
        }
    }

    /* POI - Write value with customized CellStyle */
    public static void writeValueByPOI( int row, int column, String value, String type,
        org.apache.poi.ss.usermodel.Sheet sheetPOI, org.apache.poi.ss.usermodel.CellStyle cellStyle )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.createCell( column - 1 );

            cellPOI.setCellStyle( cellStyle );

            if ( type.equalsIgnoreCase( ExcelUtils.TEXT ) )
            {
                cellPOI.setCellValue( value );
            }
            else if ( type.equalsIgnoreCase( ExcelUtils.NUMBER ) )
            {
                if ( value.equals( ZERO ) )
                {
                    cellPOI.setCellType( POI_CELLSTYLE_BLANK );
                }
                else if ( Double.isNaN( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_NAN );
                }
                else if ( Double.isInfinite( Double.valueOf( value ) ) )
                {
                    cellPOI.setCellErrorValue( POI_CELLERROR_INFINITE );
                }
                else
                {
                    cellPOI.setCellValue( Double.parseDouble( value ) );
                }
            }
        }
    }

    /* POI - Write formula without CellStyle */
    public static void writeFormulaByPOI( int row, int column, String formula,
        org.apache.poi.ss.usermodel.Sheet sheetPOI )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );
            org.apache.poi.ss.usermodel.CellStyle cellStylePOI = sheetPOI.getColumnStyle( column - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.getCell( column - 1 );

            if ( cellPOI == null )
            {
                cellPOI = rowPOI.createCell( column - 1 );
            }
            else
            {
                cellStylePOI = cellPOI.getCellStyle();
            }

            cellPOI.setCellStyle( cellStylePOI );
            cellPOI.setCellFormula( formula );
        }
    }

    /* POI - Write formula with customize CellStyle */
    public static void writeFormulaByPOI( int row, int column, String formula,
        org.apache.poi.ss.usermodel.Sheet sheetPOI, org.apache.poi.ss.usermodel.CellStyle cellStyle )
    {
        if ( row > 0 && column > 0 )
        {
            org.apache.poi.ss.usermodel.Row rowPOI = sheetPOI.getRow( row - 1 );

            if ( rowPOI == null )
            {
                rowPOI = sheetPOI.createRow( row - 1 );
            }

            org.apache.poi.ss.usermodel.Cell cellPOI = rowPOI.createCell( column - 1 );
            cellPOI.setCellStyle( cellStyle );
            cellPOI.setCellFormula( formula );
        }
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

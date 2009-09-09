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
package org.hisp.dhis.vn.report.utils;

import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import jxl.write.Blank;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ExcelUtils
{
    public static final String TEXT = "TEXT";

    public static final String NUMBER = "NUMBER";

    public static void writeValue( int row, int column, String value, String type, WritableSheet sheet,
        WritableCellFormat format )
        throws RowsExceededException, WriteException
    {

        if ( row > 0 && column > 0 )
        {
            if ( type.equalsIgnoreCase( TEXT ) )
            {
                sheet.addCell( new Label( column - 1, row - 1, value, format ) );
            }
            if ( type.equalsIgnoreCase( NUMBER ) )
            {
                double v = Double.parseDouble( value );
                if ( v != 0 )
                {
                    sheet.addCell( new Number( column - 1, row - 1, v, format ) );
                    
                }else{
                    
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
    
    public static void writeFormula( int row, int column, String formula ,WritableSheet sheet,
        WritableCellFormat format ) throws RowsExceededException, WriteException{
        if ( row > 0 && column > 0 )
        {
            sheet.addCell( new Formula( column - 1, row - 1, formula, format ) );
        }        
    }
    
    public static String readValue( int row, int column, Sheet sheet )
    {
//
//        NumberCell _cell =(NumberCell) sheet.getCell(column,row);
// System.out.println("\n\n\n number value = " + _cell.getValue());
//        
        Cell cell = sheet.getCell( column - 1, row - 1 );
 System.out.println("\n\n\n string value = " + cell.getContents() + 
     "\n column = " + column + "\n row = " + row);
              
        return cell.getContents();
        
//        return cell.getValue();
    }

}

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

package org.hisp.dhis.patient.action.patientimport;

/**
 * @author Chau Thu Tran
 * 
 * @version ExcelItem.java Nov 12, 2010 1:07:17 PM
 */
public class XMLItem
{
    public static String ORGUNIT_TYPE = "orgunit";

    public static String START_ROW_TYPE = "startRow";

    public static String END_ROW_TYPE = "endRow";

    public static String PROPERTY_TYPE = "property";

    public static String ATTRIBUTE_TYPE = "attribute";

    public static String IDENTIFIER_TYPE = "identifier";
    
    public static String AGE_TYPE = "age-type";
    
    public static String BIRTH_DATE_FROM_VALUE = "birthDateFromAge";
    
    public static String PROGRAM_TYPE = "program";
    
    public static String PROGRAM_ATTRIBUTE_TYPE = "programAttr";
    
    public static String PROGRAM_STAGE_TYPE = "programStage";

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private int row;

    private int column;

    private int sheet;

    private String type;

    private String value;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public XMLItem()
    {

    }

    public XMLItem( int row, int column, int sheet, String type, String value )
    {
        this.row = row;
        this.column = column;
        this.type = type;
        this.value = value;
        this.sheet = sheet;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public boolean isType( String type )
    {
       return this.type.equalsIgnoreCase( type );
    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public int getRow()
    {
        return row;
    }

    public void setRow( int row )
    {
        this.row = row;
    }

    public int getColumn()
    {
        return column;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setColumn( int column )
    {
        this.column = column;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public int getSheet()
    {
        return sheet;
    }

    public void setSheet( int sheet )
    {
        this.sheet = sheet;
    }
}

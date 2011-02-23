package org.hisp.dhis.reportexcel;

import java.util.ArrayList;
import java.util.List;

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

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ReportExcelItem
{

    private int id;

    private String name;

    private String itemType;

    private int row;

    private int column;

    private String expression;

    private String periodType;

    private int sheetNo;

    private ReportExcel reportExcel;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ReportExcelItem()
    {
    }

    public ReportExcelItem( String name, String itemType, int row, int column, String expression )
    {
        this.itemType = itemType;
        this.row = row;
        this.column = column;
        this.expression = expression;
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public ReportExcel getReportExcel()
    {
        return reportExcel;
    }

    public void setReportExcel( ReportExcel reportExcel )
    {
        this.reportExcel = reportExcel;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getItemType()
    {
        return itemType;
    }

    public void setItemType( String itemType )
    {
        this.itemType = itemType;
    }

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

    public void setColumn( int column )
    {
        this.column = column;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public int getSheetNo()
    {
        return sheetNo;
    }

    public void setSheetNo( int sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ReportExcelItem other = (ReportExcelItem) obj;
        if ( id != other.id )
            return false;
        return true;
    }

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String DATAELEMENT = "dataelement";

        public static final String ORGANISATION = "organisation";

        public static final String INDICATOR = "indicator";       

        public static final String DATAELEMENT_CODE = "dataelement_code";

        public static final String DATAELEMENT_NAME = "dataelement_name";

        public static final String SERIAL = "serial";

        public static final String FORMULA_EXCEL = "formulaexcel";
    }

    public static class PERIODTYPE
    {
        public static final String SELECTED_MONTH = "selected_month";

        public static final String LAST_3_MONTH = "last_3_month";

        public static final String SO_FAR_THIS_YEAR = "so_far_this_year";

        public static final String LAST_6_MONTH = "last_6_month";

        public static final String QUARTERLY = "quaterly";

        public static final String SIX_MONTH = "6_month";

        public static final String YEARLY = "yealy";
        
        public static List<String> getPeriodTypes()
        {
            List<String> list = new ArrayList<String>();
            list.add( SELECTED_MONTH );
            list.add( LAST_3_MONTH );
            list.add( SO_FAR_THIS_YEAR );
            list.add( LAST_6_MONTH );
            list.add( QUARTERLY );
            list.add( SIX_MONTH );
            list.add( YEARLY );
            
            return list;
        }
    }
    
    
}

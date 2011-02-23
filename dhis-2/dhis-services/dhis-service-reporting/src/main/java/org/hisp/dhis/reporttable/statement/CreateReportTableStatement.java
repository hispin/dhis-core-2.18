package org.hisp.dhis.reporttable.statement;

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

import static org.hisp.dhis.reporttable.ReportTable.*;

import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.reporttable.ReportTable;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CreateReportTableStatement
    extends ReportTableStatement
{    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CreateReportTableStatement( ReportTable reportTable, StatementBuilder statementBuilder )
    {        
        super( reportTable, statementBuilder );
        
        this.statementBuilder = statementBuilder;
    }

    // -------------------------------------------------------------------------
    // ReportTableStatement implementation
    // -------------------------------------------------------------------------

    @Override
    protected void init( ReportTable reportTable )
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append( "CREATE TABLE " + reportTable.getTableName() + " ( " );

        // ---------------------------------------------------------------------
        // Identifier
        // ---------------------------------------------------------------------

        for ( String column : reportTable.getIndexColumns() )
        {
            buffer.append( column + SPACE + NUMERIC_COLUMN_TYPE + SEPARATOR );
        }

        // ---------------------------------------------------------------------
        // Name
        // ---------------------------------------------------------------------

        for ( String column : reportTable.getIndexNameColumns() )
        {
            buffer.append( column + SPACE + LONG_TEXT_COLUMN_TYPE + SEPARATOR );
        }

        // ---------------------------------------------------------------------
        // Param reporting month name
        // ---------------------------------------------------------------------

        buffer.append( ReportTable.REPORTING_MONTH_COLUMN_NAME + SPACE + LONG_TEXT_COLUMN_TYPE + SEPARATOR );

        // ---------------------------------------------------------------------
        // Param organisation unit name
        // ---------------------------------------------------------------------

        buffer.append( ReportTable.PARAM_ORGANISATIONUNIT_COLUMN_NAME + SPACE + LONG_TEXT_COLUMN_TYPE + SEPARATOR );

        // ---------------------------------------------------------------------
        // Organisation unit is parent
        // ---------------------------------------------------------------------

        buffer.append( ReportTable.ORGANISATION_UNIT_IS_PARENT_COLUMN_NAME + SPACE + SHORT_TEXT_COLUMN_TYPE + SEPARATOR );
        
        // ---------------------------------------------------------------------
        // Crosstab
        // ---------------------------------------------------------------------

        for ( List<IdentifiableObject> column : reportTable.getColumns() )
        {
            buffer.append( getColumnName( column ) + SPACE + statementBuilder.getDoubleColumnType() + SEPARATOR );
        }

        // ---------------------------------------------------------------------
        // Total
        // ---------------------------------------------------------------------

        if ( reportTable.doTotal() )
        {
            for ( DataElementCategoryOption categoryOption : reportTable.getCategoryCombo().getCategoryOptions() )
            {
                buffer.append( databaseEncode( categoryOption.getShortName() ) + SPACE + statementBuilder.getDoubleColumnType() + SEPARATOR );
            }
            
            buffer.append( ReportTable.TOTAL_COLUMN_NAME + SPACE + statementBuilder.getDoubleColumnType() + SEPARATOR );
        }
        
        // ---------------------------------------------------------------------
        // Regression
        // ---------------------------------------------------------------------

        if ( reportTable.isRegression() )
        {
            for ( List<IdentifiableObject> column : reportTable.getColumns() )
            {
                buffer.append( REGRESSION_COLUMN_PREFIX + getColumnName( column ) + SPACE + statementBuilder.getDoubleColumnType() + SEPARATOR );
            }            
        }

        // ---------------------------------------------------------------------
        // Primary key
        // ---------------------------------------------------------------------

        buffer.append( "PRIMARY KEY ( " );
        
        Iterator<String> indexColumns = reportTable.getIndexColumns().iterator();
        
        while ( indexColumns.hasNext() )
        {
            buffer.append( indexColumns.next() + ( indexColumns.hasNext() ? SEPARATOR : SPACE ) );
        }
        
        buffer.append( ") )" );
        
        statement = buffer.toString();
    }
}

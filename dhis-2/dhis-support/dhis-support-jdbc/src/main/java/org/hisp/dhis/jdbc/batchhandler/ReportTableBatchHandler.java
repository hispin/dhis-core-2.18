package org.hisp.dhis.jdbc.batchhandler;

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

import org.amplecode.quick.JdbcConfiguration;
import org.amplecode.quick.batchhandler.AbstractBatchHandler;
import org.hisp.dhis.reporttable.ReportTable;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableBatchHandler
    extends AbstractBatchHandler<ReportTable>
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    public ReportTableBatchHandler( JdbcConfiguration config )
    {
        super( config, false, false );
    }

    // -------------------------------------------------------------------------
    // AbstractBatchHandler implementation
    // -------------------------------------------------------------------------

    protected void setTableName()
    {
        statementBuilder.setTableName( "reporttable" );
    }
    
    @Override
    protected void setAutoIncrementColumn()
    {
        statementBuilder.setAutoIncrementColumn( "reporttableid" );
    }
    
    @Override
    protected void setIdentifierColumns()
    {
        statementBuilder.setIdentifierColumn( "reporttableid" );
    }
    
    @Override
    protected void setIdentifierValues( ReportTable reportTable )
    {        
        statementBuilder.setIdentifierValue( reportTable.getId() );
    }
    
    protected void setUniqueColumns()
    {
        statementBuilder.setUniqueColumn( "name" );
    }
    
    protected void setUniqueValues( ReportTable reportTable )
    {        
        statementBuilder.setUniqueValue( reportTable.getName() );
    }
    
    protected void setColumns()
    {
        statementBuilder.setColumn( "name" );
        statementBuilder.setColumn( "tablename" );
        statementBuilder.setColumn( "existingtablename" );
        statementBuilder.setColumn( "mode" );
        statementBuilder.setColumn( "regression" );
        statementBuilder.setColumn( "doindicators" );
        statementBuilder.setColumn( "doperiods" );
        statementBuilder.setColumn( "dounits" );
        
        statementBuilder.setColumn( "reportingmonth" );
        statementBuilder.setColumn( "last3months" );
        statementBuilder.setColumn( "last6months" );
        statementBuilder.setColumn( "last12months" );
        statementBuilder.setColumn( "sofarthisyear" );
        statementBuilder.setColumn( "last3to6months" );
        statementBuilder.setColumn( "last6to9months" );
        statementBuilder.setColumn( "last9to12months" );
        statementBuilder.setColumn( "last12individualmonths" );
        statementBuilder.setColumn( "individualmonthsthisyear" );
        statementBuilder.setColumn( "individualquartersthisyear" );
        
        statementBuilder.setColumn( "paramreportingmonth" );
        statementBuilder.setColumn( "paramparentorganisationunit" );
        statementBuilder.setColumn( "paramorganisationunit" );
    }
    
    protected void setValues( ReportTable reportTable )
    {        
        statementBuilder.setValue( reportTable.getName() );
        statementBuilder.setValue( reportTable.getTableName() );
        statementBuilder.setValue( reportTable.getExistingTableName() );
        statementBuilder.setValue( reportTable.getMode() );
        statementBuilder.setValue( reportTable.isRegression() );
        statementBuilder.setValue( reportTable.isDoIndicators() );
        statementBuilder.setValue( reportTable.isDoPeriods() );
        statementBuilder.setValue( reportTable.isDoUnits() );
        
        statementBuilder.setValue( reportTable.getRelatives().isReportingMonth() );
        statementBuilder.setValue( reportTable.getRelatives().isLast3Months() );
        statementBuilder.setValue( reportTable.getRelatives().isLast6Months() );
        statementBuilder.setValue( reportTable.getRelatives().isLast12Months() );
        statementBuilder.setValue( reportTable.getRelatives().isSoFarThisYear() );
        statementBuilder.setValue( reportTable.getRelatives().isLast3To6Months() );
        statementBuilder.setValue( reportTable.getRelatives().isLast6To9Months() );
        statementBuilder.setValue( reportTable.getRelatives().isLast9To12Months() );
        statementBuilder.setValue( reportTable.getRelatives().isLast12IndividualMonths() );
        statementBuilder.setValue( reportTable.getRelatives().isIndividualMonthsThisYear() );
        statementBuilder.setValue( reportTable.getRelatives().isIndividualQuartersThisYear() );

        statementBuilder.setValue( reportTable.getReportParams().isParamReportingMonth() );
        statementBuilder.setValue( reportTable.getReportParams().isParamParentOrganisationUnit() );
        statementBuilder.setValue( reportTable.getReportParams().isParamOrganisationUnit() );        
    }
}

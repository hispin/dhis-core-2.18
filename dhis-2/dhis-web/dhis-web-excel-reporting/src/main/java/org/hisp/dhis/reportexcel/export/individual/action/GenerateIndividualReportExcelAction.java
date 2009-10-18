package org.hisp.dhis.reportexcel.export.individual.action;

/* Copyright (c) 2004-2007, University of Oslo
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

import java.io.File;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.export.action.GenerateReportExcelSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class GenerateIndividualReportExcelAction
    extends GenerateReportExcelSupport
{

    // ---------------------------------------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------------------------------------

    private DataValueService dataValueService;

    private ExpressionService expressionService;

    // ---------------------------------------------------------------------------------------------------
    // Inputs && Outputs
    // ---------------------------------------------------------------------------------------------------

    private String[] operands;

    private Integer[] periods;

    // ---------------------------------------------------------------------------------------------------
    // Getters && Setters
    // ---------------------------------------------------------------------------------------------------

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    public void setOperands( String[] operands )
    {
        this.operands = operands;
    }

    public void setPeriods( Integer[] periods )
    {
        this.periods = periods;
    }

    // ---------------------------------------------------------------------------------------------------
    // Action implementation
    // ---------------------------------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        WorkbookSettings ws = new WorkbookSettings();

        ws.setLocale( new Locale( "en", "EN" ) );

        File tempFile = new File( reportLocationManager.getReportExcelTempDirectory() + File.separator
            + System.currentTimeMillis() + ".xls" );

        WritableWorkbook tempWorkbook = Workbook.createWorkbook( tempFile, ws );

        WritableSheet sheet = tempWorkbook.createSheet( "Sheet1", 0 );

        for ( int i = 0; i < operands.length; i++ )
        {
            Operand operand = expressionService.getOperandsInExpression( "[" + operands[i] + "]" ).iterator().next();

            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

            DataElementCategoryOptionCombo optionCombo = categoryService
                .getDataElementCategoryOptionCombo( operand.getOptionComboId() );

            // start row = 2, start column = 1 --> dataelement name
            ExcelUtils.writeValue( i + 2, 1, dataElement.getName() + " - " + optionCombo.getName(), ExcelUtils.TEXT,
                sheet, this.textLeft );

            for ( int j = 0; j < periods.length; j++ )
            {
                Period period = periodService.getPeriod( periods[j] );

                DataValue dataValue = dataValueService
                    .getDataValue( organisationUnit, dataElement, period, optionCombo );

                if ( dataValue != null )
                {
                    // start row = 2, start column = 2 --> datavalue
                    ExcelUtils.writeValue( i + 2, j + 2, dataValue.getValue(), ExcelUtils.TEXT, sheet, number );
                }

            }
        }

        for ( int j = 0; j < periods.length; j++ )
        {
            Period period = periodService.getPeriod( periods[j].intValue() );

            format.formatPeriod( period );

            ExcelUtils.writeValue( 1, j + 2, format.formatPeriod( period ), ExcelUtils.TEXT, sheet,
                this.textICDBoldJustify );
        }

        tempWorkbook.write();

        tempWorkbook.close();

        selectionManager.setReportExcelOutput( tempFile.getPath() );

        statementManager.destroy();

        return SUCCESS;
    }
}

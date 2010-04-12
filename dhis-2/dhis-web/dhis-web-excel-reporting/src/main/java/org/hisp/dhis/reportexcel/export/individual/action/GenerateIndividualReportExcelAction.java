package org.hisp.dhis.reportexcel.export.individual.action;

/* Copyright (c) 2004-2010, University of Oslo
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
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.export.action.GenerateReportSupport;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class GenerateIndividualReportExcelAction
    extends GenerateReportSupport
{

    // ---------------------------------------------------------------------------------------------------
    // Dependencies
    // ---------------------------------------------------------------------------------------------------

    private DataValueService dataValueService;

    private ExpressionService expressionService;

    public I18nFormat format;
    
    // ---------------------------------------------------------------------------------------------------
    // Inputs && Outputs
    // ---------------------------------------------------------------------------------------------------

    private String[] operands;

    private Integer[] periods;
    
    private OrganisationUnit organisationUnit;
    
    public String message;

    public I18n i18n;

    // ---------------------------------------------------------------------------------------------------
    // Getters && Setters
    // ---------------------------------------------------------------------------------------------------

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public String getMessage() {
		return message;
	}

	public void setFormat(I18nFormat format) {
		this.format = format;
	}

	public void setI18n(I18n i18n) {
		this.i18n = i18n;
	}

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
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
        statementManager.initialise();
        
        organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();
        
        if(organisationUnit == null){
        	message = i18n.getString("choose_orgUnit");
        	return ERROR;
        }
        	
        templateWorkbook = new HSSFWorkbook();
        
        Sheet sheet = templateWorkbook.createSheet( organisationUnit.getName() ); 
        
        initExcelFormat();
        
        installDefaultExcelFormat();  

        for ( int i = 0; i < operands.length; i++ )
        {
            DataElementOperand operand = expressionService.getOperandsInExpression( "[" + operands[i] + "]" ).iterator().next();

            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

            DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( operand
                .getOptionComboId() );

            // start row = 2, start column = 1 --> dataelement name

            ExcelUtils.writeValueByPOI( i + 2, 1, dataElement.getName() + " - " + optionCombo.getName(),
                ExcelUtils.TEXT, sheet, this.csText );

            for ( int j = 0; j < periods.length; j++ )
            {
                Period period = periodService.getPeriod( periods[j] );

                DataValue dataValue = dataValueService
                    .getDataValue( organisationUnit, dataElement, period, optionCombo );

                if ( dataValue != null )
                {
                    // start row = 2, start column = 2 --> datavalue

                    ExcelUtils.writeValueByPOI( i + 2, j + 2, dataValue.getValue(), ExcelUtils.NUMBER, sheet,
                        this.csNumber );

                }

            }
        }

        for ( int j = 0; j < periods.length; j++ )
        {
            Period period = periodService.getPeriod( periods[j].intValue() );

            format.formatPeriod( period );

            ExcelUtils.writeValueByPOI( 1, j + 2, format.formatPeriod( period ), ExcelUtils.TEXT, sheet,
                this.csTextICDJustify );

        }
        
        String file =  reportLocationManager.getReportExcelTempDirectory() + File.separator
        + System.currentTimeMillis() + ".xls" ;
        
        FileOutputStream out = new FileOutputStream( file );

        templateWorkbook.write( out );

        out.close();

        selectionManager.setDownloadFilePath( file );

        statementManager.destroy();

        return SUCCESS;
    }
}

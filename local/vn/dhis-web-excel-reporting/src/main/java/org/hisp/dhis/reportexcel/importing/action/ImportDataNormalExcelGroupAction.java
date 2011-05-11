package org.hisp.dhis.reportexcel.importing.action;

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

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reportexcel.period.generic.PeriodGenericManager;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class ImportDataNormalExcelGroupAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private ExpressionService expressionService;

    private DataElementService dataElementService;

    private DataElementCategoryService categoryService;

    private CurrentUserService currentUserService;

    private ExcelItemService excelItemService;

    private SelectionManager selectionManager;

    private PeriodGenericManager periodGenericManager;

    // -------------------------------------------------------------------------
    // Inputs && Outputs
    // -------------------------------------------------------------------------

    private Integer excelItemGroupId;

    public Integer[] excelItemIds;

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setPeriodGenericManager( PeriodGenericManager periodGenericManager )
    {
        this.periodGenericManager = periodGenericManager;
    }

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setExcelItemService( ExcelItemService excelItemService )
    {
        this.excelItemService = excelItemService;
    }

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    public void setExcelItemGroupId( Integer excelItemGroupId )
    {
        this.excelItemGroupId = excelItemGroupId;
    }

    public void setExcelItemIds( Integer[] excelItemIds )
    {
        this.excelItemIds = excelItemIds;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ExcelItemGroup excelItemGroup = excelItemService.getExcelItemGroup( excelItemGroupId.intValue() );

        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( excelItemIds == null )
        {
            message = i18n.getString( "choose_excelItem" );

            return ERROR;
        }

        if ( organisationUnit != null )
        {
            FileInputStream upload = new FileInputStream( selectionManager.getUploadFilePath() );

            HSSFWorkbook wb = new HSSFWorkbook( upload );

            Collection<ExcelItem> excelItems = new ArrayList<ExcelItem>();
            
            if ( excelItemIds != null )
            {
                for ( int i = 0; i < excelItemIds.length; i++ )
                {
                    excelItems.add( excelItemService.getExcelItem( excelItemIds[i] ) );
                }
            }
            else
            {
                excelItems = excelItemGroup.getExcelItems();
            }

            Period period = periodGenericManager.getSelectedPeriod();

            for ( ExcelItem excelItem : excelItems )
            {
                HSSFSheet sheet = wb.getSheetAt( excelItem.getSheetNo() - 1 );
                
                String value = ExcelUtils.readValueImportingByPOI( excelItem.getRow(), excelItem.getColumn(), sheet );
                
                if ( value.length() > 0 )
                {
                    DataElementOperand operand = expressionService.getOperandsInExpression( excelItem.getExpression() ).iterator()
                        .next();

                    DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

                    DataElementCategoryOptionCombo optionCombo = categoryService
                        .getDataElementCategoryOptionCombo( operand.getOptionComboId() );

                    String storedBy = currentUserService.getCurrentUsername();

                    DataValue dataValue = dataValueService.getDataValue( organisationUnit, dataElement, period,
                        optionCombo );

                    if ( dataValue == null )
                    {
                        dataValue = new DataValue( dataElement, period, organisationUnit, value + "", storedBy,
                            new Date(), null, optionCombo );
                        dataValueService.addDataValue( dataValue );
                    }
                    else
                    {
                        dataValue.setValue( value + "" );
                        dataValue.setTimestamp( new Date() );
                        dataValue.setStoredBy( storedBy );
                        dataValueService.updateDataValue( dataValue );

                    }
                }
            }// end for (ExcelItem ...

        }// end if (organisationUnit ...

        message = i18n.getString( "success" );

        return SUCCESS;
    }

}
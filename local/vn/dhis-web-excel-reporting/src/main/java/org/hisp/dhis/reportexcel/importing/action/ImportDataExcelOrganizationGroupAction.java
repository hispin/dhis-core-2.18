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
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.importitem.ExcelItem;
import org.hisp.dhis.reportexcel.importitem.ImportItemService;
import org.hisp.dhis.reportexcel.period.generic.PeriodGenericManager;
import org.hisp.dhis.reportexcel.state.SelectionManager;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class ImportDataExcelOrganizationGroupAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private ImportItemService importItemService;

    public void setImportItemService( ImportItemService importItemService )
    {
        this.importItemService = importItemService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    private PeriodGenericManager periodGenericManager;

    public void setPeriodGenericManager( PeriodGenericManager periodGenericManager )
    {
        this.periodGenericManager = periodGenericManager;
    }

    private SelectionManager selectionManager;

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Inputs && Outputs
    // -------------------------------------------------------------------------

    public String[] importItemIds;

    public void setExcelItemIds( String[] importItemIds )
    {
        this.importItemIds = importItemIds;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitSelectionManager.getSelectedOrganisationUnit();

        if ( importItemIds == null )
        {
            message = i18n.getString( "choose_import_item" );

            return ERROR;
        }

        if ( organisationUnit != null )
        {
            FileInputStream inputStream = new FileInputStream( selectionManager.getUploadFilePath() );

            HSSFWorkbook wb = new HSSFWorkbook( inputStream );

            Period period = periodGenericManager.getSelectedPeriod();

            for ( int i = 0; i < importItemIds.length; i++ )
            {
                int orgunitId = Integer.parseInt( importItemIds[i].split( "-" )[0] );

                OrganisationUnit o = organisationUnitService.getOrganisationUnit( orgunitId );

                int row = Integer.parseInt( importItemIds[i].split( "-" )[1] );

                int importItemId = Integer.parseInt( importItemIds[i].split( "-" )[2] );

                ExcelItem importItem = importItemService.getImportItem( importItemId );

                if ( importItem.getId() == importItemId )
                {
                    writeDataValue( importItem, wb, row, o, period );
                }// end if( importItem ...

            }// end for (int i ...

        }// end if (organisationUnit ...

        message = i18n.getString( "success" );

        return SUCCESS;
    }

    private void writeDataValue( ExcelItem importItem, HSSFWorkbook wb, int row, OrganisationUnit o, Period period )
    {
        HSSFSheet sheet = wb.getSheetAt( importItem.getSheetNo() - 1 );

        String value = ExcelUtils.readValueImportingByPOI( importItem.getRow() + row, importItem.getColumn(), sheet );

        if ( value.length() > 0 )
        {
            DataElementOperand operand = expressionService.getOperandsInExpression( importItem.getExpression() )
                .iterator().next();

            DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );

            DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( operand
                .getOptionComboId() );

            String storedBy = currentUserService.getCurrentUsername();

            DataValue dataValue = dataValueService.getDataValue( o, dataElement, period, optionCombo );

            if ( dataValue == null )
            {
                dataValue = new DataValue( dataElement, period, o, value + "", storedBy, new Date(), null, optionCombo );
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
    }

}

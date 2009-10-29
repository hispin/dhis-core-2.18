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
package org.hisp.dhis.reportexcel.importing.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItem;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reportexcel.utils.ExcelUtils;
import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */

public class ImportDataExcelOrganizationGroupAction extends ActionSupport {

	// --------------------------------------------------------------------
	// Dependencies
	// --------------------------------------------------------------------

	private DataValueService dataValueService;

	private OrganisationUnitSelectionManager organisationUnitSelectionManager;

	private ExpressionService expressionService;

	private DataElementService dataElementService;

	private DataElementCategoryService categoryService;

	private CurrentUserService currentUserService;

	private PeriodService periodService;

	private ExcelItemService excelItemService;

	// --------------------------------------------------------------------
	// Inputs && Outputs
	// --------------------------------------------------------------------

	private Integer excelItemGroupId;

	private String uploadFileName;

	private Integer periodId;

	public Integer[] excelItemIds;

	// --------------------------------------------------------------------
	// Getters and Setters
	// --------------------------------------------------------------------
	public void setOrganisationUnitSelectionManager(
			OrganisationUnitSelectionManager organisationUnitSelectionManager) {
		this.organisationUnitSelectionManager = organisationUnitSelectionManager;
	}

	public void setExpressionService(ExpressionService expressionService) {
		this.expressionService = expressionService;
	}

	public void setCategoryService(DataElementCategoryService categoryService) {
		this.categoryService = categoryService;
	}

	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}

	public void setDataValueService(DataValueService dataValueService) {
		this.dataValueService = dataValueService;
	}

	public void setExcelItemGroupId(Integer excelItemGroupId) {
		this.excelItemGroupId = excelItemGroupId;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public void setPeriodId(Integer periodId) {
		this.periodId = periodId;
	}

	public void setExcelItemIds(Integer[] excelItemIds) {
		this.excelItemIds = excelItemIds;
	}

	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}

	// --------------------------------------------------------------------
	// Action implementation
	// --------------------------------------------------------------------

	public String execute() throws Exception {

		ExcelItemGroup excelItemGroup = excelItemService
				.getExcelItemGroup(excelItemGroupId.intValue());

		OrganisationUnit organisationUnit = organisationUnitSelectionManager
				.getSelectedOrganisationUnit();

		if (organisationUnit != null) {

			File upload = new File(uploadFileName);
			WorkbookSettings ws = new WorkbookSettings();
			ws.setLocale(new Locale("en", "EN"));
			Workbook templateWorkbook = Workbook.getWorkbook(upload, ws);

			Collection<ExcelItem> excelItems = new ArrayList<ExcelItem>();
			if (excelItemIds != null) {
				for (int i = 0; i < excelItemIds.length; i++) {
					excelItems.add(excelItemService
							.getExcelItem(excelItemIds[i]));
				}
			} else {
				excelItems = excelItemGroup.getExcelItems();
			}

			
			
			for (OrganisationUnitGroup organisationUnitGroup : excelItemGroup
					.getOrganisationUnitGroups()) {

				Collection<OrganisationUnit> organisationUnits = getOrganisationUnits(
						organisationUnitGroup, organisationUnit);

				int row = 0;
				
				for (OrganisationUnit o : organisationUnits) {

					for (ExcelItem exelItem : excelItems) {

						Sheet sheet = templateWorkbook.getSheet(exelItem
								.getSheetNo() - 1);

						String value = ExcelUtils.readValue(exelItem.getRow()
								+ row, exelItem.getColumn(), sheet);

						if (value.length() > 0) {

							Period period = periodService.getPeriod(periodId
									.intValue());

							Operand operand = expressionService
									.getOperandsInExpression(
											exelItem.getExpression())
									.iterator().next();

							DataElement dataElement = dataElementService
									.getDataElement(operand.getDataElementId());

							DataElementCategoryOptionCombo optionCombo = categoryService
									.getDataElementCategoryOptionCombo(operand
											.getOptionComboId());

							String storedBy = currentUserService
									.getCurrentUsername();

							DataValue dataValue = dataValueService
									.getDataValue(o, dataElement, period,
											optionCombo);

							if (dataValue == null) {
								dataValue = new DataValue(dataElement, period,
										o, value + "", storedBy, new Date(),
										null, optionCombo);
								dataValueService.addDataValue(dataValue);
							} else {
								dataValue.setValue(value + "");
								dataValue.setTimestamp(new Date());
								dataValue.setStoredBy(storedBy);

								dataValueService.updateDataValue(dataValue);

							}

						} // if (value.length() ...

					}// end for (ExcelItem ...
					
					row ++;
					
				}// end for (OrganisationUnit ...

			} // end for ( OrganisationUnitGroup ...

		}// end if (organisationUnit ...

		message = i18n.getString("success");

		return SUCCESS;
	}

	private Collection<OrganisationUnit> getOrganisationUnits(
			OrganisationUnitGroup group, OrganisationUnit parentUnit) {

		List<OrganisationUnit> childrenOrganisationUnits = new ArrayList<OrganisationUnit>(
				parentUnit.getChildren());

		Collection<OrganisationUnit> organisationUnits = group.getMembers();

		organisationUnits.retainAll(childrenOrganisationUnits);

		return organisationUnits;
	}

}

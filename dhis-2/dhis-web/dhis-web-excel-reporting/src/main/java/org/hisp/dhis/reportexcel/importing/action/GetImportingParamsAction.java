package org.hisp.dhis.reportexcel.importing.action;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reportexcel.importing.period.action.SelectedStateManager;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public class GetImportingParamsAction extends ActionSupport {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private SelectedStateManager selectedStateManager;

	private ExcelItemService excelItemService;

	// -------------------------------------------------------------------------
	// Inputs && Outputs
	// -------------------------------------------------------------------------

	private OrganisationUnit organisationUnit;

	private Collection<ExcelItemGroup> excelItemGroups = new ArrayList<ExcelItemGroup>();

	private List<Period> periods = new ArrayList<Period>();

	private boolean locked;

	// -------------------------------------------------------------------------
	// Input/output
	// -------------------------------------------------------------------------

	private Integer selectedExcelItemGroupId;

	private Integer selectedPeriodIndex;

	// -------------------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------------------

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public void setSelectedStateManager(
			SelectedStateManager selectedStateManager) {
		this.selectedStateManager = selectedStateManager;
	}

	public void setSelectedPeriodIndex(Integer selectedPeriodIndex) {
		this.selectedPeriodIndex = selectedPeriodIndex;
	}

	public Integer getSelectedPeriodIndex() {
		return selectedPeriodIndex;
	}

	public void setSelectedExcelItemGroupId(Integer selectedExcelItemGroupId) {
		this.selectedExcelItemGroupId = selectedExcelItemGroupId;
	}

	public Integer getSelectedExcelItemGroupId() {
		return selectedExcelItemGroupId;
	}

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}

	public Collection<ExcelItemGroup> getExcelItemGroups() {
		return excelItemGroups;
	}

	public Collection<Period> getPeriods() {
		return periods;
	}

	public boolean isLocked() {
		return locked;
	}

	private File fileExcel;

	public File getFileExcel() {
		return fileExcel;
	}

	public void setFileExcel(File fileExcel) {
		this.fileExcel = fileExcel;
	}

	// -------------------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------------------

	public String execute() throws Exception {

		// ---------------------------------------------------------
		// Get File Excel
		// ---------------------------------------------------------
		
		if (fileExcel != null) {
			message = i18n.getString("upload_file") + " "
					+ i18n.getString("success") + " <br>      ' "
					+ fileExcel.getName() + " '";
		}

		// ---------------------------------------------------------------------
		// Validate selected OrganisationUnit
		// ---------------------------------------------------------------------

		organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

		if (organisationUnit == null) {
			selectedExcelItemGroupId = null;
			selectedPeriodIndex = null;

			selectedStateManager.clearSelectedPeriod();

			return SUCCESS;
		}

		// ---------------------------------------------------------------------
		// Load and sort ExcelItemGroups
		// ---------------------------------------------------------------------

		excelItemGroups = selectedStateManager
				.loadExcelItemGroupsForSelectedOrgUnit(organisationUnit);

		// ---------------------------------------------------------------------
		// Validate selected ExcelItemGroup
		// ---------------------------------------------------------------------

		ExcelItemGroup selectedExcelItemGroup;

		if (selectedExcelItemGroupId != null) {
			selectedExcelItemGroup = excelItemService
					.getExcelItemGroup(selectedExcelItemGroupId);
		} else {
			selectedExcelItemGroup = selectedStateManager
					.getSelectedExcelItemGroup();
		}

		if (selectedExcelItemGroup != null
				&& excelItemGroups.contains(selectedExcelItemGroup)) {
			selectedExcelItemGroupId = selectedExcelItemGroup.getId();
			selectedStateManager
					.setSelectedExcelItemGroup(selectedExcelItemGroup);
		} else {
			selectedExcelItemGroupId = null;
			selectedPeriodIndex = null;

			selectedStateManager.clearSelectedExcelItemGroup();
			selectedStateManager.clearSelectedPeriod();

			return SUCCESS;
		}

		// ---------------------------------------------------------------------
		// Generate Periods
		// ---------------------------------------------------------------------

		periods = selectedStateManager.getPeriodList();

		return SUCCESS;
	}
}

package org.hisp.dhis.reportexcel.importing.action;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;

public class GetImportingParamsAction extends ActionSupport {

	// -------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------

	private OrganisationUnitSelectionManager organisationUnitSelectionManager;

	private ExcelItemService excelItemService;


	// -------------------------------------------------------------
	// Inputs && Outputs
	// -------------------------------------------------------------

	private OrganisationUnit organisationUnit;

	private Collection<ExcelItemGroup> excelItemGroups;

	private List<Period> periods;

	private File fileExcel;

	private Collection<ReportExcel> categoryGroups;

	// -------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------
	
	public Collection<ReportExcel> getCategoryGroups() {
		return categoryGroups;
	}

	public void setOrganisationUnitSelectionManager(
			OrganisationUnitSelectionManager organisationUnitSelectionManager) {
		this.organisationUnitSelectionManager = organisationUnitSelectionManager;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}

	public Collection<ExcelItemGroup> getExcelItemGroups() {
		return excelItemGroups;
	}

	public void setExcelItemService(ExcelItemService excelItemService) {
		this.excelItemService = excelItemService;
	}

	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}

	public File getFileExcel() {
		return fileExcel;
	}

	public void setFileExcel(File fileExcel) {
		this.fileExcel = fileExcel;
	}

	public List<Period> getPeriods() {
		return periods;
	}

	public void setPeriods(List<Period> periods) {
		this.periods = periods;
	}

	// -------------------------------------------------------------
	// Action implementation
	// -------------------------------------------------------------

	public String execute() throws Exception {

		// ---------------------------------------------------------
		// Get Excel Item Groups by the selected Organisation group
		// ---------------------------------------------------------
		organisationUnit = organisationUnitSelectionManager
				.getSelectedOrganisationUnit();

		if (organisationUnit == null) {
			return SUCCESS;
		}
		excelItemGroups = excelItemService
				.getExcelItemGroupsByOrganisationUnit(organisationUnit);
		
		// ---------------------------------------------------------
		// Get File Excel
		// ---------------------------------------------------------
        
		if (fileExcel != null) {
			message = i18n.getString("upload_file") + " "
					+ i18n.getString("success") + " <br>      ' "
					+ fileExcel.getName() + " '";
		}
		
		return SUCCESS;
	}
}

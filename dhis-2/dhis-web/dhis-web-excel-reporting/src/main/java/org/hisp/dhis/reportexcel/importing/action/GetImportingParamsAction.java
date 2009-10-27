package org.hisp.dhis.reportexcel.importing.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportexcel.action.ActionSupport;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reportexcel.export.action.SelectionManager;
import org.hisp.dhis.reportexcel.utils.DateUtils;

public class GetImportingParamsAction extends ActionSupport {

	// -------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------

	private OrganisationUnitSelectionManager organisationUnitSelectionManager;

	private ExcelItemService excelItemService;

	private PeriodService periodService;

	private SelectionManager selectionManager;

	// -------------------------------------------------------------
	// Inputs && Outputs
	// -------------------------------------------------------------

	private OrganisationUnit organisationUnit;

	private List<ExcelItemGroup> excelItemGroups;

	private List<Period> periods;

	private File fileExcel;

	// -------------------------------------------------------------
	// Getters && Setters
	// -------------------------------------------------------------
	
	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void setPeriodService(PeriodService periodService) {
		this.periodService = periodService;
	}

	public void setOrganisationUnitSelectionManager(
			OrganisationUnitSelectionManager organisationUnitSelectionManager) {
		this.organisationUnitSelectionManager = organisationUnitSelectionManager;
	}

	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}

	public List<ExcelItemGroup> getExcelItemGroups() {
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
		
		excelItemGroups = new ArrayList<ExcelItemGroup>();

		List<ExcelItemGroup> listExcelItemGroup = new ArrayList<ExcelItemGroup>(
				excelItemService.getAllExcelItemGroup());

		if (organisationUnit == null) {
			excelItemGroups.addAll(excelItemGroups);
		} else {

			for (ExcelItemGroup excelItemGroup : listExcelItemGroup) {
				excelItemGroups.add(excelItemGroup);
			}
		}

		if (fileExcel != null) {
			message = i18n.getString("upload_file") + " "
					+ i18n.getString("success") + " <br>      ' "
					+ fileExcel.getName() + " '";
		}

		// ---------------------------------------------------------
		// Get Periods
		// ---------------------------------------------------------

		PeriodType periodType = periodService
				.getPeriodTypeByClass(MonthlyPeriodType.class);

		Date firstDateOfThisYear = DateUtils.getFirstDayOfYear(DateUtils
				.getCurrentYear());

		Date endDateOfThisMonth = DateUtils.getEndDate(DateUtils
				.getCurrentMonth(), DateUtils.getCurrentYear());

		periods = new ArrayList<Period>(periodService
				.getIntersectingPeriodsByPeriodType(periodType,
						firstDateOfThisYear, endDateOfThisMonth));

		Collections.sort(periods, new PeriodComparator());

		selectionManager.setSeletedYear(DateUtils.getCurrentYear());

		return SUCCESS;
	}
}

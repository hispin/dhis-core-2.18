/**
 * @author Dang Duy Hieu
 */

package org.hisp.dhis.vn.report;

import java.util.List;

import org.hisp.dhis.dataelement.DataElementCategoryCombo;

public class ReportExcelCategory extends ReportExcel implements
		ReportExcelInterface {

	private List<DataElementOrderInGroup> dataElementOrders;

	private DataElementCategoryCombo categoryCombo;

	/**
	 * Constructors
	 */
	public ReportExcelCategory() {
		super();
	}

	public ReportExcelCategory(String name, String excelTemplateFile,
			int periodRow, int periodColumn, int organisationRow,
			int organisationColumn) {
		super(name, excelTemplateFile, periodRow, periodColumn,
				organisationRow, organisationColumn);
	}

	public List<DataElementOrderInGroup> getDataElementOrders() {
		return dataElementOrders;
	}

	public void setDataElementOrders(
			List<DataElementOrderInGroup> dataElementOrders) {
		this.dataElementOrders = dataElementOrders;
	}

	public DataElementCategoryCombo getCategoryCombo() {
		return categoryCombo;
	}

	public void setCategoryCombo(DataElementCategoryCombo categoryCombo) {
		this.categoryCombo = categoryCombo;
	}

	/**
	 * @ getReportType: gets Report Type for ReportExcelCategory
	 */
	public String getReportType() {
		return ReportExcel.TYPE.CATEGORY;
	}
}

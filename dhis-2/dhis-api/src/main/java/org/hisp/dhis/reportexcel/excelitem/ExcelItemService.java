package org.hisp.dhis.reportexcel.excelitem;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.reportexcel.DataElementGroupOrder;

public interface ExcelItemService {

	String ID = ExcelItemService.class.getName();

	// ----------------------------------------------------------------------------
	// Excelitem group services
	// ----------------------------------------------------------------------------

	public void addExcelItemGroup(ExcelItemGroup excelItemGroup);

	public void updateExcelItemGroup(ExcelItemGroup excelItemGroup);

	public void deleteExcelItemGroup(int id);

	public Collection<ExcelItemGroup> getAllExcelItemGroup();

	public ExcelItemGroup getExcelItemGroup(int id);

	public Collection<ExcelItemGroup> getExcelItemGroupsByOrganisationUnit(
			OrganisationUnit organisationUnit);

	// ----------------------------------------------------------------------------
	// Excelitem services
	// ----------------------------------------------------------------------------

	public void addExcelItem(ExcelItem excelItem);

	public void updateExcelItem(ExcelItem excelItem);

	public void deleteExcelItem(int id);

	public Collection<ExcelItem> getAllExcelItem();

	public ExcelItem getExcelItem(int id);

	// --------------------------------------
	// DataElement Order
	// --------------------------------------

	public DataElementGroupOrder getDataElementGroupOrder(Integer id);

	public void updateDataElementGroupOrder(
			DataElementGroupOrder dataElementGroupOrder);

	public void deleteDataElementGroupOrder(Integer id);

}

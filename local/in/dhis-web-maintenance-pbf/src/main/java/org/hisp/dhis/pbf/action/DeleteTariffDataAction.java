package org.hisp.dhis.pbf.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;

import com.opensymphony.xwork2.Action;

public class DeleteTariffDataAction implements Action {
	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------

	private TariffDataValueService tariffDataValueService;

	public void setTariffDataValueService(TariffDataValueService tariffDataValueService) 
	{
		this.tariffDataValueService = tariffDataValueService;
	}
	private DataElementService dataElementService;
	
	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}

	private OrganisationUnitService organisationUnitService;
	
	public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}
	
	private OrganisationUnitGroupService organisationUnitGroupService;
	
	public void setOrganisationUnitGroupService(
			OrganisationUnitGroupService organisationUnitGroupService) {
		this.organisationUnitGroupService = organisationUnitGroupService;
	}
	
	private DataElementCategoryService categoryService;
	
	public void setCategoryService(DataElementCategoryService categoryService) {
		this.categoryService = categoryService;
	}
	// -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
	
	private String organisationUnitId;
	
	private String dataElementId;
	
	private String categoryOptionComboId;
	
	private String organisationUnitGroupId;
	
	private String startDate;
	
	private String endDate;
	
	public void setOrganisationUnitId(String organisationUnitId) {
		this.organisationUnitId = organisationUnitId;
	}


	public void setDataElementId(String dataElementId) {
		this.dataElementId = dataElementId;
	}


	public void setCategoryOptionComboId(String categoryOptionComboId) {
		this.categoryOptionComboId = categoryOptionComboId;
	}


	public void setOrganisationUnitGroupId(String organisationUnitGroupId) {
		this.organisationUnitGroupId = organisationUnitGroupId;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute() throws Exception {
		
		Date sDate;
		Date eDate;
		int orgUnitId = Integer.parseInt(organisationUnitId);
		
		int deId = Integer.parseInt(dataElementId);
		
		int categoryId = Integer.parseInt(categoryOptionComboId);
		int orgUnitGrpId = Integer.parseInt(organisationUnitGroupId);
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		sDate = dateFormat.parse(startDate);
		eDate = dateFormat.parse(endDate);
		DataElement dataElement = dataElementService.getDataElement(deId);
		OrganisationUnitGroup orgUnitGrp = organisationUnitGroupService.getOrganisationUnitGroup(orgUnitGrpId);
		DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo(categoryId);
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(orgUnitId);
		
		TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue
						(organisationUnit, dataElement, optionCombo, orgUnitGrp, sDate, eDate);
				
				
		tariffDataValueService.deleteTariffDataValue(tariffDataValue);
		
		return SUCCESS;
	}
}
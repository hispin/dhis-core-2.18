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
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

public class AddTariffDataAction implements Action {
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
	
	private CurrentUserService currentUserService;
	
	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	// -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
	
	
	private String pbfType;
	private String tariff;
	private String startDate;
	private String endDate;
	
	private String dataElementId;
	private String orgUnitId;
	
	public void setDataElementId(String dataElementId) {
		this.dataElementId = dataElementId;
	}



	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}



	public void setPbfType(String pbfType) {
		this.pbfType = pbfType;
	}



	public void setTariff(String tariff) {
		this.tariff = tariff;
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
		SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
		sDate = dateFormat.parse(startDate);
		eDate = dateFormat.parse(endDate);
		
		DataElement dataElement = dataElementService.getDataElement(Integer.parseInt(dataElementId));
		
		OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit(Integer.parseInt(orgUnitId));
		
		OrganisationUnitGroup orgUnitGrp = organisationUnitGroupService.getOrganisationUnitGroup(Integer.parseInt(pbfType));
		DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
		
		TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue(orgUnit, dataElement, optionCombo, orgUnitGrp, sDate, eDate);
		
		TariffDataValue tariffDataValue1;
		
		if(tariffDataValue == null)
		{
			tariffDataValue1 = new TariffDataValue();
		}
		else
		{
			tariffDataValue1 = tariffDataValueService.getTariffDataValue(orgUnit, dataElement, optionCombo, orgUnitGrp, sDate, eDate);
		}
		
		tariffDataValue1.setValue(Double.parseDouble(tariff));
		tariffDataValue1.setStartDate(sDate);
		tariffDataValue1.setEndDate(eDate);
		tariffDataValue1.setTimestamp(new Date());
		tariffDataValue1.setStoredBy(currentUserService.getCurrentUsername());
		tariffDataValue1.setDataElement(dataElement);
		tariffDataValue1.setOptionCombo(optionCombo);
		tariffDataValue1.setOrganisationUnitGroup(orgUnitGrp);
		tariffDataValue1.setOrganisationUnit(orgUnit);
		
		if(tariffDataValue == null)
		{
			tariffDataValueService.addTariffDataValue(tariffDataValue1);
		}
		else
		{
			tariffDataValueService.updateTariffDataValue(tariffDataValue1);
		}		
		return SUCCESS;
	}
}
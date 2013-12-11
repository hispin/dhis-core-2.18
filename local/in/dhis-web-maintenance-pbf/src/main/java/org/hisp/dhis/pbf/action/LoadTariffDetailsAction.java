package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;

import com.opensymphony.xwork2.Action;

public class LoadTariffDetailsAction implements Action
{
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

	// -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
	private String dataElementName;
	
	 public void setDataElementName(String dataElementName) {
			this.dataElementName = dataElementName;
		}
	private String orgUnitId;
	
	public void setOrgUnitId(String orgUnitId) {
		this.orgUnitId = orgUnitId;
	}
	
	private List<TariffDataValue> tariffList = new ArrayList<TariffDataValue>();
	
	public List<TariffDataValue> getTariffList() {
		return tariffList;
	}
	
	private DataElement selecteddataElement;
	
	public DataElement getSelecteddataElement() {
		return selecteddataElement;
	}

	private List<OrganisationUnitGroup> orGroupList = new ArrayList<OrganisationUnitGroup>();
	
	public List<OrganisationUnitGroup> getOrGroupList() {
		return orGroupList;
	}
	
	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
	
	public String execute()
    {
		OrganisationUnitGroupSet organisationUnitGroupSet =  organisationUnitGroupService.getOrganisationUnitGroupSet(6709);
		orGroupList = new ArrayList<OrganisationUnitGroup>(organisationUnitGroupSet.getOrganisationUnitGroups()) ;
		selecteddataElement = dataElementService.getDataElementByName(dataElementName);
		OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit(Integer.parseInt(orgUnitId));
		
		tariffList = new ArrayList<TariffDataValue>(tariffDataValueService.getTariffDataValues(organisationUnit, selecteddataElement)) ;
		
        return SUCCESS;
    }
}
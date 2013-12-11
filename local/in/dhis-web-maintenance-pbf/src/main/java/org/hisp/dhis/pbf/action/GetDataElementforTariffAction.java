package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

public class GetDataElementforTariffAction implements Action
{
	private final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";
	
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	private DataElementService dataElementService;
	
	public void setDataElementService(DataElementService dataElementService) {
		this.dataElementService = dataElementService;
	}
	
	private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService(
			OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}

	// -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
	
	private List<String> dataElementList = new ArrayList<String>();
	
    public List<String> getDataElementList() {
		return dataElementList;
	}

	public void setDataElementList(List<String> dataElementList) {
		this.dataElementList = dataElementList;
	}

	private String tariff_setting_authority;
	
	public String getTariff_setting_authority() {
		return tariff_setting_authority;
	}
	
	private List<Integer> levelOrgUnitIds = new ArrayList<Integer>();
	
	public List<Integer> getLevelOrgUnitIds() {
		return levelOrgUnitIds;
	}
	
	// -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
    {
		if(constantService.getConstantByName( TARIFF_SETTING_AUTHORITY) == null)
		{
			tariff_setting_authority = "Level 1";
			List<OrganisationUnit> allLevelOrg =new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitsAtLevel(1)) ;
			for(OrganisationUnit org : allLevelOrg)
			{
				levelOrgUnitIds.add(org.getId());
			}
		}
		else
		{
			Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
			tariff_setting_authority = "Level "+Integer.parseInt(tariff_authority.getValue()+"");
			List<OrganisationUnit> allLevelOrg =new ArrayList<OrganisationUnit>(organisationUnitService.getOrganisationUnitsAtLevel(Integer.parseInt(tariff_authority.getValue()+""))) ;
			for(OrganisationUnit org : allLevelOrg)
			{
				levelOrgUnitIds.add(org.getId());
			}
		}
    	List<DataElement> dataElements = new ArrayList<DataElement>(dataElementService.getAllDataElements());
    	for(DataElement de : dataElements)
    	{
    		if(!(dataElementList.contains("\""+de.getName()+"\"")))
    		{
    			dataElementList.add("\""+de.getName()+"\"");
    		}
    	}
    	
        return SUCCESS;
    }
}
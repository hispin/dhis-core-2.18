package org.hisp.dhis.pbf.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.TariffDataValue;
import org.hisp.dhis.pbf.api.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;

import com.opensymphony.xwork2.Action;

public class LoadTariffDetailsAction implements Action
{
	private final static String PBF_ORGUNIT_GROUP_SET = "PBF_ORGUNIT_GROUP_SET";
	
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
	
	private CurrentUserService currentUserService;
	
	public void setCurrentUserService(CurrentUserService currentUserService) {
		this.currentUserService = currentUserService;
	}

	private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
	// -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
	private String dataElementName;
	
	 public void setDataElementName(String dataElementName) {
			this.dataElementName = dataElementName;
		}
	private String orgUnitUid;
	
	public void setOrgUnitUid(String orgUnitUid) {
		this.orgUnitUid = orgUnitUid;
	}

	private List<TariffDataValue> tariffList = new ArrayList<TariffDataValue>();
	
	public List<TariffDataValue> getTariffList() {
		return tariffList;
	}
	
	private String updateAuthority;
	
	public String getUpdateAuthority() {
		return updateAuthority;
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
		Constant pbf_orgunitGrpSet = constantService.getConstantByName( PBF_ORGUNIT_GROUP_SET );
		User curUser = currentUserService.getCurrentUser();
		List<UserAuthorityGroup> userAuthorityGroups = new ArrayList<UserAuthorityGroup>( curUser.getUserCredentials()
	            .getUserAuthorityGroups() );
		for ( UserAuthorityGroup userAuthorityGroup : userAuthorityGroups )
        {
            userAuthorityGroup.getUserGroupAccesses();
            if ( userAuthorityGroup.getAuthorities().contains( "F_TARIFFDATAVALUE_UPDATE" ) )
            {               
            	updateAuthority = "Yes";
            }
            else
            {                
            	updateAuthority = "No";
            }
        }
		
		OrganisationUnitGroupSet organisationUnitGroupSet =  organisationUnitGroupService.getOrganisationUnitGroupSet((int)pbf_orgunitGrpSet.getValue());
		orGroupList = new ArrayList<OrganisationUnitGroup>(organisationUnitGroupSet.getOrganisationUnitGroups()) ;
		selecteddataElement = dataElementService.getDataElementByName(dataElementName);
		List<String> orgUids = new ArrayList<String>();
		orgUids.add(orgUnitUid);
		List<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnitsByUid(orgUids);
		
		tariffList = new ArrayList<TariffDataValue>(tariffDataValueService.getTariffDataValues(organisationUnits.get(0), selecteddataElement)) ;
		
        return SUCCESS;
    }
}
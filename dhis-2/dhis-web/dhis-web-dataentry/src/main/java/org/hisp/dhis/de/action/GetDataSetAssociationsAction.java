package org.hisp.dhis.de.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.organisationunit.OrganisationUnitDataSetAssociationSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class GetDataSetAssociationsAction
    implements Action
{
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private ConfigurationService configurationService;

    private List<Set<String>> dataSetAssociationSets;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public List<Set<String>> getDataSetAssociationSets()
    {
        return dataSetAssociationSets;
    }

    private Map<String, Integer> organisationUnitAssociationSetMap;

    public Map<String, Integer> getOrganisationUnitAssociationSetMap()
    {
        return organisationUnitAssociationSetMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        OrganisationUnitLevel offlineOrgUnitLevel = configurationService.getConfiguration().getOfflineOrganisationUnitLevel();

        Integer level = offlineOrgUnitLevel != null ? offlineOrgUnitLevel.getLevel() : null;

        OrganisationUnitDataSetAssociationSet organisationUnitSet = organisationUnitService.getOrganisationUnitDataSetAssociationSet( level );

        dataSetAssociationSets = organisationUnitSet.getDataSetAssociationSets();

        organisationUnitAssociationSetMap = organisationUnitSet.getOrganisationUnitAssociationSetMap();

        return SUCCESS;
    }
}

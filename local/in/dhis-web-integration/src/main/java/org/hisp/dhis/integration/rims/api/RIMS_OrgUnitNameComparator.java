package org.hisp.dhis.integration.rims.api;

import java.util.Comparator;


public class RIMS_OrgUnitNameComparator implements Comparator<RIMS_Mapping_Orgunit> 
{

    public int compare( RIMS_Mapping_Orgunit rimsMappingOU1, RIMS_Mapping_Orgunit rimsMappingOU2 )
    {
        return rimsMappingOU1.getOuName().compareTo( rimsMappingOU2.getOuName() );
    }

}

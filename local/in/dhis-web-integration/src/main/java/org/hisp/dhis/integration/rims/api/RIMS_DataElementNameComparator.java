package org.hisp.dhis.integration.rims.api;

import java.util.Comparator;


public class RIMS_DataElementNameComparator implements Comparator<RIMS_Mapping_DataElement>
{
    public int compare( RIMS_Mapping_DataElement rimsMappingDE1, RIMS_Mapping_DataElement rimsMappingDE2 )
    {
        return rimsMappingDE1.getDeName().compareTo( rimsMappingDE2.getDeName() );
    }
}

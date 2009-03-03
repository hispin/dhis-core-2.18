package org.hisp.dhis.resourcetable;

import java.util.Collection;

public interface ResourceTableStore
{
    String ID = ResourceTableStore.class.getName();
    
    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    int addOrganisationUnitStructure( OrganisationUnitStructure structure );
    
    Collection<OrganisationUnitStructure> getOrganisationUnitStructures();
    
    int deleteOrganisationUnitStructures();

    // -------------------------------------------------------------------------
    // GroupSetStructure
    // -------------------------------------------------------------------------

    int addGroupSetStructure( GroupSetStructure structure );
    
    Collection<GroupSetStructure> getGroupSetStructures();
    
    int deleteGroupSetStructures();    

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------

    int addDataElementCategoryOptionComboName( DataElementCategoryOptionComboName name );
    
    Collection<DataElementCategoryOptionComboName> getDataElementCategoryOptionComboNames();
    
    int deleteDataElementCategoryOptionComboNames();
}

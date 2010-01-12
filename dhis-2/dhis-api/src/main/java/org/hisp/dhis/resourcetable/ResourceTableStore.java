package org.hisp.dhis.resourcetable;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

public interface ResourceTableStore
{
    String ID = ResourceTableStore.class.getName();
    
    // -------------------------------------------------------------------------
    // OrganisationUnitStructure
    // -------------------------------------------------------------------------

    /**
     * Adds an OrganisationUnitStructure.
     * 
     * @param structure the OrganisationUnitStructure to add.
     * @return the generated identifier.
     */
    int addOrganisationUnitStructure( OrganisationUnitStructure structure );
    
    /**
     * Retrieves all OrganisationUnitStructures.
     * 
     * @return a Collection of OrganisationUnitStructures.
     */
    Collection<OrganisationUnitStructure> getOrganisationUnitStructures();
    
    /**
     * Deletes all OrganisationUnitStructures.
     * 
     * @return the number of deleted objects.
     */
    int deleteOrganisationUnitStructures();

    // -------------------------------------------------------------------------
    // GroupSetStructure
    // -------------------------------------------------------------------------

    /**
     * Adds a GroupSetStructure.
     * 
     * @param structure the GroupSetStructure to add.
     * @return the generated identifier.
     */
    int addGroupSetStructure( GroupSetStructure structure );
    
    /**
     * Retrieves all GroupSetStructures.
     * 
     * @return a Collection of GroupSetStructures.
     */
    Collection<GroupSetStructure> getGroupSetStructures();
    
    /**
     * Deletes all GroupSetStructures.
     * 
     * @return the number of deleted objects.
     */
    int deleteGroupSetStructures();    

    // -------------------------------------------------------------------------
    // DataElementCategoryOptionComboName
    // -------------------------------------------------------------------------

    /**
     * Adds a DataElementCategoryOptionComboName.
     * 
     * @param name the DataElementCategoryOptionComboName to add.
     * @return the generated identifier.
     */
    int addDataElementCategoryOptionComboName( DataElementCategoryOptionComboName name );
    
    /**
     * Retrieves all DataElementCategoryOptionComboNames.
     * 
     * @return a Collection of DataElementCategoryOptionComboNames.
     */
    Collection<DataElementCategoryOptionComboName> getDataElementCategoryOptionComboNames();
    
    /**
     * Deletes all DataElementCategoryOptionComboNames.
     * 
     * @return the number of deleted objects.
     */
    int deleteDataElementCategoryOptionComboNames();

    // -------------------------------------------------------------------------
    // GroupSetStructure
    // -------------------------------------------------------------------------

    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createDataElementGroupSetStructure( List<DataElementGroupSet> groupSets );

    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createIndicatorGroupSetStructure( List<IndicatorGroupSet> groupSets );
    
    /**
     * Creates table.
     * 
     * @param groupSets the group sets.
     */
    void createOrganisationUnitGroupSetStructure( List<OrganisationUnitGroupSet> groupSets );
    
}

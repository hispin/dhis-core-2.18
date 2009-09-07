package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.util.Collection;

public interface LineListDataElementMappingStore
{
    String ID = LineListDataElementMappingStore.class.getName();
    
    /**
     * Adds a new LineListDataElementMapping to the database.
     * 
     * @param lineListDataElementMapping The new LineListDataElementMapping to add.
     * @return The generated identifier for this LineListDataElementMapping.
     */
    int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Updates an LineListDataElementMapping.
     * 
     * @param lineListDataElementMapping The LineListDataElementMapping to update.
     */
    void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Deletes an LineListDataElementMapping from the database.
     * 
     * @param id Identifier of the LineListDataElementMapping to delete.
     */
    void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping );

    /**
     * Gets the LineListDataElementMapping with the given identifier.
     * 
     * @param id The identifier.
     * @return An LineListDataElementMapping with the given identifier.
     */
    LineListDataElementMapping getLineListDataElementMapping( int id );

    /**
     * Gets all LineListDataElementMapping.
     * 
     * @return A collection with all the LineListDataElementMappings.
     */
    Collection<LineListDataElementMapping> getAllLineListDataElementMappings();
}

package org.hisp.dhis.linelisting;

import java.util.Collection;

import org.hisp.dhis.source.Source;

public interface LineListStore
{
	String ID = LineListStore.class.getName();
	
    // -------------------------------------------------------------------------
    // LineList Group
    // -------------------------------------------------------------------------

    /**
     * Adds a LineList Group.
     * 
     * @param lineListGroup The LineListGroup to add.
     * @return The generated unique identifier for this LineListGroup.
     */
    int addLineListGroup( LineListGroup lineListGroup );

    /**
     * Updates a LineListGroup.
     * 
     * @param dataSet The LineListGroup to update.
     */
    void updateLineListGroup( LineListGroup lineListGroup );

    /**
     * Deletes a LineListGroup.
     * 
     * @param dataSet The LineListGroup to delete.
     */
    void deleteLineListGroup( LineListGroup lineListGroup );

    /**
     * Get a LineListGroup
     * 
     * @param id The unique identifier for the LineListGroup to get.
     * @return The LineListGroup with the given id or null if it does not exist.
     */
    LineListGroup getLineListGroup( int id );
    
    /**
     * Returns a LineListGroups with the given name.
     * 
     * @param name The name.
     * @return A LineListGroup with the given name.
     */
    LineListGroup getLineListGroupByName( String name );

    /**
     * Returns the LineListGroup with the given short name.
     * 
     * @param shortName The short name.
     * @return The LineListGroup with the given short name.
     */
    LineListGroup getLineListGroupByShortName( String shortName );
    
    /**
     * Returns all Line List Groups associated with the specified source.
     */
    Collection<LineListGroup> getLineListGroupsBySource(Source source);
    
    /**
     * Returns all Line List Groups associated with the specified Element
     */
    Collection<LineListGroup> getLineListGroupsByElement(LineListElement lineListElement);

    /**
     * Get all LineListGroups.
     * 
     * @return A collection containing all LineListGroups.
     */
    Collection<LineListGroup> getAllLineListGroups();
       
    // -------------------------------------------------------------------------
    // LineList Element
    // -------------------------------------------------------------------------
    
    /**
     * Adds a LineList Element.
     * 
     * @param lineListElement The lineListElement to add.
     * @return The generated unique identifier for this lineListElement.
     */
    int addLineListElement( LineListElement lineListElement );

    /**
     * Updates a lineListElement.
     * 
     * @param dataSet The lineListElement to update.
     */
    void updateLineListElement( LineListElement lineListElement );

    /**
     * Deletes a lineListElement.
     * 
     * @param dataSet The lineListElement to delete.
     */
    void deleteLineListElement( LineListElement lineListElement );

    /**
     * Get a lineListElement
     * 
     * @param id The unique identifier for the lineListElement to get.
     * @return The lineListElement with the given id or null if it does not exist.
     */
    LineListElement getLineListElement( int id );
    
    /**
     * Returns a LineListElements with the given name.
     * 
     * @param name The name.
     * @return A LineListElement with the given name.
     */
    LineListElement getLineListElementByName( String name );
    
    /**
     * Returns a LineListElements with the given name.
     * 
     * @param name The name.
     * @return A LineListElement with the given name.
     */
    LineListElement getLineListElementByShortName( String shortName );
    
    
    /**
     * Returns all Line List Elements associated with the specified Option
     */
    Collection<LineListElement> getLineListElementsByOption (LineListOption lineListOption);

    
    /**
     * Get all LineListElements.
     * 
     * @return A collection containing all LineListElement.
     */
    Collection<LineListElement> getAllLineListElements();
    
    
    // -------------------------------------------------------------------------
    // LineList Option
    // -------------------------------------------------------------------------
    
    /**
     * Adds a LineList Option.
     * 
     * @param lineListOption The lineListOption to add.
     * @return The generated unique identifier for this lineListOption.
     */
    int addLineListOption( LineListOption lineListOption );

    /**
     * Updates a lineListOption.
     * 
     * @param dataSet The lineListOption to update.
     */
    void updateLineListOption( LineListOption lineListOption );

    /**
     * Deletes a lineListOption.
     * 
     * @param dataSet The lineListOption to delete.
     */
    void deleteLineListOption( LineListOption lineListOption );

    /**
     * Get a lineListOption
     * 
     * @param id The unique identifier for the lineListOption to get.
     * @return The lineListOption with the given id or null if it does not exist.
     */
    LineListOption getLineListOption( int id );


    /**
     * Get a lineListOption by using the name
     * 
     * @param name The name for the lineListOption to get.
     * @return The lineListOption with the given name or null if it does not exist.
     */
    LineListOption getLineListOptionByName (String name);
    
    /**
     * Get a lineListOption by using the short name
     * 
     * @param short Name The short name for the lineListOption to get.
     * @return The lineListOption with the given short name or null if it does not exist.
     */
    LineListOption getLineListOptionByShortName(String shortName);
    
    /**
     * Get all LineListOptions.
     * 
     * @return A collection containing all LineListOption.
     */
    Collection<LineListOption> getAllLineListOptions();
        

}

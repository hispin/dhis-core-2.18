package org.hisp.dhis.dataelement;

import java.util.Collection;

public interface CategoryOptionGroupSetService
{
    String ID = CategoryOptionGroupSetService.class.getName();

    /**
     * Adds an {@link CategoryOptionGroupSet}
     * 
     * @param groupSet The to CategoryOptionGroupSet add.
     * 
     * @return A generated unique id of the added {@link CategoryOptionGroupSet}
     *         .
     */
    int addCategoryOptionGroupSet( CategoryOptionGroupSet groupSet );

    /**
     * Deletes a {@link CategoryOptionGroupSet}.
     * 
     * @param groupSet the CategoryOptionGroupSet to delete.
     */
    void deleteCategoryOptionGroupSet( CategoryOptionGroupSet groupSet );

    /**
     * Updates an {@link CategoryOptionGroupSet}.
     * 
     * @param groupSet the CategoryOptionGroupSet to update.
     */
    void updateCategoryOptionGroupSet( CategoryOptionGroupSet groupSet );

    /**
     * Returns a {@link CategoryOptionGroupSet}.
     * 
     * @param id the id of the CategoryOptionGroupSet to return.
     * 
     * @return the CategoryOptionGroupSet with the given id
     */
    CategoryOptionGroupSet getCategoryOptionGroupSet( int id );

    /**
     * Returns the {@link CategoryOptionGroupSet} with the given UID.
     * 
     * @param uid the UID.
     * @return the CategoryOptionGroupSet with the given UID, or null if no
     *         match.
     */
    CategoryOptionGroupSet getCategoryOptionGroupSetByUid( String uid );

    /**
     * Returns a {@link CategoryOptionGroupSet} with a given name.
     * 
     * @param name the name of the CategoryOptionGroupSet to return.
     * 
     * @return the CategoryOptionGroupSet with the given name, or null if no
     *         match.
     */
    CategoryOptionGroupSet getCategoryOptionGroupSetByName( String name );

    /**
     * Returns all {@link CategoryOptionGroupSet}
     * 
     * @return a collection of all CategoryOptionGroupSet, or an empty
     *         collection if there are no CategoryOptionGroupSets.
     */
    Collection<CategoryOptionGroupSet> getAllCategoryOptionGroupSets();

    /**
     * Returns {@link CategoryOptionGroupSet} list with paging
     * 
     * @param name Keyword for searching by name
     * @param first
     * @param max
     * @return a collection of all CategoryOptionGroupSet, or an empty
     *         collection if there are no CategoryOptionGroupSets.
     */
    Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetweenByName( String name, int first, int max );

    /**
     * Returns The number of all CategoryOptionGroupSet available
     * 
     */
    int getCategoryOptionGroupSetCount();

    /**
     * Returns {@link CategoryOptionGroupSet} list with paging
     * 
     * @param first
     * @param max
     * @return a collection of all CategoryOptionGroupSet, or an empty
     *         collection if there are no CategoryOptionGroupSets.
     */
    Collection<CategoryOptionGroupSet> getCategoryOptionGroupSetsBetween( int first, int max );
   
    /**
     * Returns The number of CategoryOptionGroupSets with the key searched
     * 
     * @param name Keyword for searching by name
     * 
     * @return A number
     * 
     */
    int getCategoryOptionGroupSetCountByName( String name );
}

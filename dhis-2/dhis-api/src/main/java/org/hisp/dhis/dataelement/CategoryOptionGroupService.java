package org.hisp.dhis.dataelement;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;

public interface CategoryOptionGroupService
{
    String ID = CategoryOptionGroupService.class.getName();

    /**
     * Adds an {@link CategoryOptionGroup}
     * 
     * @param categoryOptionGroup The to CategoryOptionGroup add.
     * 
     * @return A generated unique id of the added {@link CategoryOptionGroup} .
     */
    int addCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup );

    /**
     * Deletes a {@link CategoryOptionGroup}.
     * 
     * @param categoryOptionGroup the CategoryOptionGroup to delete.
     */
    void deleteCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup );

    /**
     * Updates an {@link CategoryOptionGroup}.
     * 
     * @param categoryOptionGroup the CategoryOptionGroup to update.
     */
    void updateCategoryOptionGroup( CategoryOptionGroup categoryOptionGroup );

    /**
     * Returns a {@link CategoryOptionGroup}.
     * 
     * @param id the id of the CategoryOptionGroup to return.
     * 
     * @return the CategoryOptionGroup with the given id
     */
    CategoryOptionGroup getCategoryOptionGroup( int id );

    /**
     * Returns the {@link CategoryOptionGroup} with the given UID.
     * 
     * @param uid the UID.
     * @return the CategoryOptionGroup with the given UID, or null if no match.
     */
    CategoryOptionGroup getCategoryOptionGroupByUid( String uid );

    /**
     * Returns a {@link CategoryOptionGroup} with a given name.
     * 
     * @param name the name of the CategoryOptionGroup to return.
     * 
     * @return the CategoryOptionGroup with the given name, or null if no match.
     */
    CategoryOptionGroup getCategoryOptionGroupByName( String name );

    /**
     * Returns a {@link CategoryOptionGroup} with a given name.
     * 
     * @param shortName the code of the CategoryOptionGroup to return.
     * 
     * @return the CategoryOptionGroup with the given code, or null if no
     *         match.
     */
    CategoryOptionGroup getCategoryOptionGroupByCode( String code );

    /**
     * Returns a {@link CategoryOptionGroup} with a given name.
     * 
     * @param shortName the shortName of the CategoryOptionGroup to return.
     * 
     * @return the CategoryOptionGroup with the given shortName, or null if no
     *         match.
     */
    CategoryOptionGroup getCategoryOptionGroupByShortName( String shortName );

    /**
     * Returns all {@link CategoryOptionGroup}
     * 
     * @return a collection of all CategoryOptionGroup, or an empty collection
     *         if there are no CategoryOptionGroups.
     */
    Collection<CategoryOptionGroup> getAllCategoryOptionGroups();

    /**
     * Returns {@link CategoryOptionGroup} list with paging
     * 
     * @param name Keyword for searching by name
     * @param first
     * @param max
     * @return a collection of all CategoryOptionGroup, or an empty collection
     *         if there are no CategoryOptionGroups.
     */
    Collection<CategoryOptionGroup> getCategoryOptionGroupsBetweenByName( String name, int first, int max );

    /**
     * Returns The number of all CategoryOptionGroup available
     * 
     */
    int getCategoryOptionGroupCount();

    /**
     * Returns {@link CategoryOptionGroup} list with paging
     * 
     * @param first
     * @param max
     * @return a collection of all CategoryOptionGroup, or an empty collection
     *         if there are no CategoryOptionGroups.
     */
    Collection<CategoryOptionGroup> getCategoryOptionGroupsBetween( int first, int max );

    /**
     * Returns The number of CategoryOptionGroups with the key searched
     * 
     * @param name Keyword for searching by name
     * 
     * @return A number
     * 
     */
    int getCategoryOptionGroupCountByName( String name );
}

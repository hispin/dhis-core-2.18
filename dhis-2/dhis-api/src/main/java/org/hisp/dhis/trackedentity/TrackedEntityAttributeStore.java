package org.hisp.dhis.trackedentity;

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

import org.hisp.dhis.common.GenericNameableObjectStore;

/**
 * @author Abyot Asalefew Gizaw
 */
public interface TrackedEntityAttributeStore
    extends GenericNameableObjectStore<TrackedEntityAttribute>
{
    String ID = TrackedEntityAttributeStore.class.getName();

    /**
     * Get attributes by value type
     * 
     * @param valueType Value type
     * 
     * @return List of attributes
     */
    Collection<TrackedEntityAttribute> getByValueType( String valueType );

    /**
     * Get mandatory attributes without groups
     * 
     * @return List of attributes
     */
    Collection<TrackedEntityAttribute> getOptionalAttributesWithoutGroup();

    /**
     * Get attributes by mandatory option
     * 
     * @param mandatory True/False value
     * 
     * @return List of attributes
     */
    Collection<TrackedEntityAttribute> getByMandatory( boolean mandatory );

    /**
     * Get {@link TrackedEntityAttribute} without any group
     * 
     * @return TrackedEntityAttribute without group.
     */
    Collection<TrackedEntityAttribute> getWithoutGroup();

    /**
     * Get attributes by groupBy option
     * 
     * @return TrackedEntityAttribute with groupby as true
     */
    TrackedEntityAttribute getByGroupBy();

    /**
     * Get attributes which are displayed in visit schedule
     * 
     * @param displayOnVisitSchedule True/False value
     * 
     * @return List of attributes
     */
    Collection<TrackedEntityAttribute> getByDisplayOnVisitSchedule( boolean displayOnVisitSchedule );

    /**
     * Get attributes which are displayed in visit schedule
     * 
     * @param displayInListNoProgram True/False value
     * 
     * @return List of attributes
     */
    Collection<TrackedEntityAttribute> getDisplayedInList( boolean displayInListNoProgram );

    // -------------------------------------------------------------------------
    // TrackedEntityAttributeOption
    // -------------------------------------------------------------------------

    /**
     * Returns a {@link TrackedEntityAttributeOption} with a given name.
     * 
     * @param attribute {@link TrackedEntityAttribute}
     * 
     * @param name the name of the TrackedEntityAttributeOption to return.
     * 
     * @return the TrackedEntityAttributeOption with the given name, or null if
     * no match.
     */
    TrackedEntityAttributeOption get( TrackedEntityAttribute attribute, String name );

    /**
     * Get all {@link TrackedEntityAttributeOption} of a
     * {@link TrackedEntityAttribute}
     * 
     * @param attribute {@link TrackedEntityAttribute}
     * 
     * @return {@link TrackedEntityAttributeOption}
     */
    Collection<TrackedEntityAttributeOption> get( TrackedEntityAttribute attribute );
}

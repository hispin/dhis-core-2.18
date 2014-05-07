package org.hisp.dhis.trackedentity;

/*
 * Copyright (c) 2004-2014, University of Oslo
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
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew Gizaw
 */
public interface TrackedEntityInstanceService
{
    String ID = TrackedEntityInstanceService.class.getName();

    public static final int ERROR_NONE = 0;

    public static final int ERROR_DUPLICATE_IDENTIFIER = 1;

    public static final int ERROR_ENROLLMENT = 2;

    public static final String SAPERATOR = "_";

    /**
     * Returns a grid with tracked entity instance values based on the given
     * TrackedEntityInstanceQueryParams.
     * 
     * @param params the TrackedEntityInstanceQueryParams.
     * @return a grid.
     */
    Grid getTrackedEntityInstances( TrackedEntityInstanceQueryParams params );

    /**
     * Returns a TrackedEntityInstanceQueryParams based on the given input.
     * 
     * @param query the query string.
     * @param attribute the set of attributes.
     * @param filter the set of filters.
     * @param ou the set of organisatio unit identifiers.
     * @param ouMode the OrganisationUnitSelectionMode.
     * @param program the Program identifier.
     * @param programStatus the ProgramStatus in the given orogram.
     * @param followUp indicates follow up status in the given Program.
     * @param programStartDate the start date for enrollment in the given
     *        Program.
     * @param programEndDate the end date for enrollment in the given Program.
     * @param trackedEntity the TrackedEntity uid.
     * @param eventStatus the event status for the given Program.
     * @param eventStartDate the event start date for the given Program.
     * @param eventEndDate the event end date for the given Program.
     * @param skipMeta indicates whether to include meta data in the response.
     * @param page the page number.
     * @param pageSize the page size.
     * @return a TrackedEntityInstanceQueryParams.
     */
    TrackedEntityInstanceQueryParams getFromUrl( String query, Set<String> attribute, Set<String> filter,
        Set<String> ou, OrganisationUnitSelectionMode ouMode, String program, ProgramStatus programStatus,
        Boolean followUp, Date programStartDate, Date programEndDate, String trackedEntity, EventStatus eventStatus,
        Date eventStartDate, Date eventEndDate, boolean skipMeta, Integer page, Integer pageSize );

    /**
     * Validates the given TrackedEntityInstanceQueryParams. The params is
     * considered valid if no exception are thrown and the method returns
     * normally.
     * 
     * @param params the TrackedEntityInstanceQueryParams.
     * @throws IllegalQueryException if the given params is invalid.
     */
    void validate( TrackedEntityInstanceQueryParams params )
        throws IllegalQueryException;

    /**
     * Adds an {@link TrackedEntityInstance}
     * 
     * @param entityInstance The to TrackedEntityInstance add.
     * 
     * @return A generated unique id of the added {@link TrackedEntityInstance}.
     */
    int addTrackedEntityInstance( TrackedEntityInstance entityInstance );

    /**
     * Deletes a {@link TrackedEntityInstance}.
     * 
     * @param entityInstance the TrackedEntityInstance to delete.
     */
    void deleteTrackedEntityInstance( TrackedEntityInstance entityInstance );

    /**
     * Updates a {@link TrackedEntityInstance}.
     * 
     * @param entityInstance the TrackedEntityInstance to update.
     */
    void updateTrackedEntityInstance( TrackedEntityInstance entityInstance );

    /**
     * Returns a {@link TrackedEntityInstance}.
     * 
     * @param id the id of the TrackedEntityInstanceAttribute to return.
     * 
     * @return the TrackedEntityInstanceAttribute with the given id
     */
    TrackedEntityInstance getTrackedEntityInstance( int id );

    /**
     * Returns the {@link TrackedEntityAttribute} with the given UID.
     * 
     * @param uid the UID.
     * @return the TrackedEntityInstanceAttribute with the given UID, or null if
     *         no match.
     */
    TrackedEntityInstance getTrackedEntityInstance( String uid );

    /**
     * Returns all {@link TrackedEntityInstance}
     * 
     * @return a collection of all TrackedEntityInstance, or an empty collection
     *         if there are no TrackedEntityInstances.
     */
    //Collection<TrackedEntityInstance> getAllTrackedEntityInstances();

    /**
     * Retrieve entityInstances for mobile base on identifier value
     * 
     * @param searchText value
     * @param orgUnitId
     * 
     * @return TrackedEntityInstance List
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstancesForMobile( String searchText, int orgUnitId );

    /**
     * Retrieve entityInstances base on organization unit with result limited
     * 
     * @param organisationUnit organisationUnit
     * @param min
     * @param max
     * 
     * @return TrackedEntityInstance List
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Integer min,
        Integer max );

    /**
     * Retrieve entityInstances who enrolled into a program with active status
     * 
     * @param program Program
     * @return TrackedEntityInstance list
     */
    //Collection<TrackedEntityInstance> getTrackedEntityInstances( Program program );

    /**
     * Retrieve entityInstances registered in a orgunit and enrolled into a
     * program with active status
     * 
     * @param organisationUnit
     * @param program
     * @return
     */
    //Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Program program );

    /**
     * Retrieve entityInstances base on Attribute
     * 
     * @param attributeId
     * @param value
     * @return
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstance( Integer attributeId, String value );

    /**
     * Search entityInstances base on OrganisationUnit and Program with result
     * limited name
     * 
     * @param organisationUnit
     * @param program
     * @param min
     * @param max
     * @return
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Program program,
        Integer min, Integer max );

    /**
     * Sort the result by TrackedEntityInstanceAttribute
     * 
     * @param entityInstances
     * @param attribute
     * @return TrackedEntityInstance List
     */
    //Collection<TrackedEntityInstance> sortTrackedEntityInstancesByAttribute( Collection<TrackedEntityInstance> entityInstances, TrackedEntityAttribute attribute );

    /**
     * Get entityInstances who has the same representative
     * 
     * @params entityInstance The representatives
     * 
     * @return TrackedEntityInstance List
     * **/
    Collection<TrackedEntityInstance> getRepresentatives( TrackedEntityInstance entityInstance );

    /**
     * Register a new entityInstance
     * 
     * @param entityInstance TrackedEntityInstance
     * @param representativeId The id of entityInstance who is representative
     * @param relationshipTypeId The id of relationship type defined
     * @param attributeValues Set of attribute values
     * 
     * @return The error code after registering entityInstance
     */
    int createTrackedEntityInstance( TrackedEntityInstance entityInstance, Integer representativeId,
        Integer relationshipTypeId, Set<TrackedEntityAttributeValue> attributeValues );

    /**
     * Update information of an entityInstance existed
     * 
     * @param entityInstance TrackedEntityInstance
     * @param representativeId The id of representative of this entityInstance
     * @param relationshipTypeId The id of relationship type of this person
     * @param valuesForSave The entityInstance attribute values for adding
     * @param valuesForUpdate The entityInstance attribute values for updating
     * @param valuesForDelete The entityInstance attribute values for deleting
     * 
     */
    void updateTrackedEntityInstance( TrackedEntityInstance entityInstance, Integer representativeId,
        Integer relationshipTypeId, List<TrackedEntityAttributeValue> valuesForSave,
        List<TrackedEntityAttributeValue> valuesForUpdate, Collection<TrackedEntityAttributeValue> valuesForDelete );

    /**
     * Get the number of entityInstances who registered into an organisation
     * unit
     * 
     * @param organisationUnit Organisation Unit
     * 
     * @return The number of entityInstances
     */
    //int countGetTrackedEntityInstancesByOrgUnit( OrganisationUnit organisationUnit );

    /**
     * Get the number of entityInstances who registered into an organisation
     * unit and enrolled into a program
     * 
     * @param organisationUnit Organisation Unit
     * @param program Program
     * 
     * @return The number of entityInstances
     */
    //int countGetTrackedEntityInstancesByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

    /**
     * Cache value from String to the value type based on property
     * 
     * @param property Property name of entityInstance
     * @param value Value
     * @param format I18nFormat
     * 
     * @return An object
     */
    Object getObjectValue( String property, String value, I18nFormat format );

    /**
     * Search entityInstances by phone number (performs partial search)
     * 
     * @param phoneNumber The string for searching by phone number
     * @param min
     * @param max
     * 
     * @return List of entityInstance
     */
    //Collection<TrackedEntityInstance> getTrackedEntityInstancesByPhone( String phoneNumber, Integer min, Integer max );

    /**
     * Validate entityInstance attributes and validation criteria by program
     * before registering or updating information
     * 
     * @param entityInstance TrackedEntityInstance object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check unique attribute values of the
     *        entityInstance
     * @param format I18nFormat
     * @return Error code 0 : Validation is OK 1_<duplicate-value> : The
     *         attribute value is duplicated 2_<validation-criteria-id> :
     *         Violate validation criteria of the program
     */
    String validateTrackedEntityInstance( TrackedEntityInstance entityInstance, Program program, I18nFormat format );

    /**
     * Validate patient enrollment
     * 
     * @param entityInstance TrackedEntityInstance object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return ValidationCriteria object which is violated
     */
    ValidationCriteria validateEnrollment( TrackedEntityInstance entityInstance, Program program, I18nFormat format );

    /**
     * Retrieve entityInstances for mobile base on identifier value
     * 
     * @param searchText value
     * @param orgUnitId
     * @param attributeId
     * @return TrackedEntityInstance List
     */

    Collection<TrackedEntityInstance> searchTrackedEntityInstancesForMobile( String searchText, int orgUnitId, int attributeId );

    /**
     * Search entityInstances by entityInstance attribute value (performs
     * partial search)
     * 
     * @param entityInstance attribute value The string for searching by
     *        entityInstance attribute value
     * @param min
     * @param max
     * 
     * @return List of TrackedEntityInstance
     */
    //Collection<TrackedEntityInstance> getTrackedEntityInstancesByAttributeValue( String searchText, int attributeId, Integer min, Integer max );

    /**
     * Get entityInstances by {@link TrackedEntity}
     * 
     * @param trackedEntity {@link TrackedEntity}
     * 
     * @return List of entityInstance
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstances( TrackedEntity trackedEntity );
}

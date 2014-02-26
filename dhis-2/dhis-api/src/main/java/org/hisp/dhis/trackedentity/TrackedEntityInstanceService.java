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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */

public interface TrackedEntityInstanceService
{
    String ID = TrackedEntityInstanceService.class.getName();

    public static final int ERROR_NONE = 0;

    public static final int ERROR_DUPLICATE_IDENTIFIER = 1;

    public static final int ERROR_ENROLLMENT = 2;

    /**
     * Adds an {@link TrackedEntityInstance}
     * 
     * @param entityInstance The to TrackedEntityInstance add.
     * 
     * @return A generated unique id of the added {@link TrackedEntityInstance}.
     */
    int saveTrackedEntityInstance( TrackedEntityInstance entityInstance );

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
    Collection<TrackedEntityInstance> getAllTrackedEntityInstances();

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
    Collection<TrackedEntityInstance> getTrackedEntityInstances( Program program );

    /**
     * Retrieve entityInstances registered in a orgunit and enrolled into a
     * program with active status
     * 
     * @param organisationUnit
     * @param program
     * @return
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstances( OrganisationUnit organisationUnit, Program program );

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
    Collection<TrackedEntityInstance> sortTrackedEntityInstancesByAttribute(
        Collection<TrackedEntityInstance> entityInstances, TrackedEntityAttribute attribute );

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
    int countGetTrackedEntityInstancesByOrgUnit( OrganisationUnit organisationUnit );

    /**
     * Get the number of entityInstances who registered into an organisation
     * unit and enrolled into a program
     * 
     * @param organisationUnit Organisation Unit
     * @param program Program
     * 
     * @return The number of entityInstances
     */
    int countGetTrackedEntityInstancesByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

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
     * Search entityInstances by attribute values and/or a program which
     * entityInstances enrolled into
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values, identifiers and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param attributes The attribute values of these attribute are displayed
     *        into result
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param min
     * @param max
     * 
     * @return An object
     */
    Collection<TrackedEntityInstance> searchTrackedEntityInstances( List<String> searchKeys,
        Collection<OrganisationUnit> orgunit, Boolean followup, Collection<TrackedEntityAttribute> attributes,
        Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get the number of entityInstances who meet the criteria for searching
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * 
     * @return The number of entityInstances
     */
    int countSearchTrackedEntityInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment );

    /**
     * Get phone numbers of persons who meet the criteria for searching *
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param min
     * @param max
     * 
     * @return List of entityInstance
     */
    Collection<String> getTrackedEntityInstancePhoneNumbers( List<String> searchKeys,
        Collection<OrganisationUnit> orgunit, Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get events which meet the criteria for searching
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @parma min
     * @param max
     * 
     * @return List of entityInstance
     */
    List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Integer statusEnrollment, Integer min, Integer max );

    /**
     * Get visit schedule of person who meet the criteria for searching
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @parma min
     * @param max
     * 
     * @return Grid
     */
    Grid getScheduledEventsReport( List<String> searchKeys, Collection<OrganisationUnit> orgunits, Boolean followup,
        Integer statusEnrollment, Integer min, Integer max, I18n i18n );

    /**
     * Search entityInstances by phone number (performs partial search)
     * 
     * @param phoneNumber The string for searching by phone number
     * @param min
     * @param max
     * 
     * @return List of entityInstance
     */
    Collection<TrackedEntityInstance> getTrackedEntityInstancesByPhone( String phoneNumber, Integer min, Integer max );

    /**
     * Get events of entityInstances who meet the criteria for searching
     * 
     * @param program Program. It's is used for getting attributes of this
     *        program and put attribute values of entityInstances into the
     *        result
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
     * @param orgunit Organisation unit where entityInstances registered
     * @param followup Only getting entityInstances with program risked if this
     *        property is true. And getting entityInstances without program
     *        risked if its value is false
     * @param statusEnrollment The status of program of entityInstances. There
     *        are three status, includes Active enrollments only, Completed
     *        enrollments only and Active and completed enrollments
     * @param i18n I18n
     * 
     * @return Grid
     */
    Grid getTrackingEventsReport( Program program, List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Integer statusEnrollment, I18n i18n );

    /**
     * Validate entityInstance attributes and validation criteria by program
     * before registering or updating information
     * 
     * @param entityInstance TrackedEntityInstance object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check unique attribute values of the
     *        entityInstance
     * @param format I18nFormat
     * @return Error code 0 : Validation is OK 1 : The attribute is duplicated 2
     *         : Violate validation criteria of the program
     */
    int validateTrackedEntityInstance( TrackedEntityInstance entityInstance, Program program, I18nFormat format );

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

    Collection<TrackedEntityInstance> searchTrackedEntityInstancesForMobile( String searchText, int orgUnitId,
        int attributeId );

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
    Collection<TrackedEntityInstance> getTrackedEntityInstancesByAttributeValue( String searchText, int attributeId,
        Integer min, Integer max );
}

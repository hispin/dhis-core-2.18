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

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.validation.ValidationCriteria;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public interface TrackedEntityInstanceStore
    extends GenericIdentifiableObjectStore<TrackedEntityInstance>
{
    final String ID = TrackedEntityInstanceStore.class.getName();

    final int MAX_RESULTS = 50000;

    /**
     * Search entityInstances who registered in a certain organisation unit
     * 
     * @param organisationUnit Organisation unit where entityInstances
     *        registered
     * @param min
     * @param max
     * 
     * @return List of entityInstances
     */
    Collection<TrackedEntityInstance> getByOrgUnit( OrganisationUnit organisationUnit, Integer min, Integer max );

    /**
     * Search entityInstances registered into a certain organisation unit and
     * enrolled into a program with active status
     * 
     * @param organisationUnit Organisation unit where entityInstances
     *        registered
     * @param program Program. It's is used for getting attributes of this
     *        program and put attribute values of entityInstances into the
     *        result
     * @param min
     * @param max
     * 
     * @return List of entityInstances
     */
    Collection<TrackedEntityInstance> getByOrgUnitProgram( OrganisationUnit organisationUnit, Program program,
        Integer min, Integer max );

    List<TrackedEntityInstance> query( TrackedEntityQueryParams params );

    /**
     * Search instances who has the same representative
     * 
     * @param instances Representative
     * 
     * @return List of entityInstances
     */
    Collection<TrackedEntityInstance> getRepresentatives( TrackedEntityInstance instances );

    /**
     * Search the number of entityInstances who registered into an organisation
     * unit
     * 
     * @param organisationUnit Organisation unit
     * 
     * @return The number of entityInstances
     */
    int countListTrackedEntityInstanceByOrgunit( OrganisationUnit organisationUnit );

    /**
     * Get the number of entityInstances who registered into a certain
     * organisation unit and enrolled into a program with active status
     * 
     * @param organisationUnit Organisation unit where entityInstances
     *        registered
     * @param program Program. It's is used for getting attributes of this
     *        program and put attribute values of entityInstances into the
     *        result
     * 
     * @return The number of entityInstances
     */
    int countGetTrackedEntityInstancesByOrgUnitProgram( OrganisationUnit organisationUnit, Program program );

    /**
     * Get number of entityInstances who meet the criteria for searching
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
    int countSearch( List<String> searchKeys, Collection<OrganisationUnit> orgunit, Boolean followup,
        Integer statusEnrollment );

    /**
     * Search entityInstances by phone number (performs partial search)
     * 
     * @param phoneNumber The string for searching by phone number
     * @param min
     * @param max
     * 
     * @return List of instances
     */
    Collection<TrackedEntityInstance> getByPhoneNumber( String phoneNumber, Integer min, Integer max );

    /**
     * Search events of entityInstances who meet the criteria for searching
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
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
     * @return List of entityInstances
     */
    Collection<TrackedEntityInstance> search( List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Collection<TrackedEntityAttribute> attributes, Integer statusEnrollment, Integer min,
        Integer max );

    /**
     * Search events which meet the criteria for searching
     * 
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
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
     * @return List of entityInstances
     */
    List<Integer> getProgramStageInstances( List<String> searchKeys, Collection<OrganisationUnit> orgunits,
        Boolean followup, Collection<TrackedEntityAttribute> attributes, Integer statusEnrollment, Integer min,
        Integer max );

    /**
     * Search entityInstances who enrolled into a program with active status
     * 
     * @param program Program
     * @param min
     * @param max
     * 
     *        return List of entityInstances
     */
    Collection<TrackedEntityInstance> getByProgram( Program program, Integer min, Integer max );

    /**
     * Search events of entityInstances who meet the criteria for searching
     * 
     * @param grid Grid with headers
     * @param searchKeys The key for searching entityInstances by attribute
     *        values and/or a program
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
     * @return Grid
     */
    Grid getTrackedEntityInstanceEventReport( Grid grid, List<String> searchKeys, Collection<OrganisationUnit> orgunit,
        Boolean followup, Collection<TrackedEntityAttribute> attributes, Integer statusEnrollment, Integer min,
        Integer max );

    /**
     * Validate entity-instances attribute values and validation criteria by program
     * before registering / updating information
     * 
     * @param entityinstance TrackedEntityInstance object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check attribute values of the instances
     * @param format I18nFormat
     * 
     * @return Error code 0 : Validation is OK 1 : The attribute value is
     *         duplicated 2 : Violate validation criteria of the program
     */
    int validate( TrackedEntityInstance entityinstance, Program program, I18nFormat format );

    /**
     * Validate entity-instance enrollment
     * 
     * @param entityinstance TrackedEntityInstance object
     * @param program Program which person needs to enroll. If this parameter is
     *        null, the system check identifiers of the patient
     * @param format I18nFormat
     * 
     * @return ValidationCriteria object which is violated
     */
    ValidationCriteria validateEnrollment(TrackedEntityInstance entityinstance, Program program, I18nFormat format );

    /**
     * Validate instances attribute values and validation criteria by program
     * before registering / updating information
     * 
     * @param searchText The value of a TrackedEntityAttribute
     * @param attributeId The id of a TrackedEntityAttribute
     * 
     * @return TrackedEntityInstance list
     */
    Collection<TrackedEntityInstance> getByAttributeValue( String searchText, int attributeId, Integer min, Integer max );
}

package org.hisp.dhis.program;

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
import java.util.Date;
import java.util.List;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.sms.outbound.OutboundSms;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public interface ProgramStageInstanceStore
    extends GenericIdentifiableObjectStore<ProgramStageInstance>
{
    String ID = ProgramStageInstanceStore.class.getName();

    /**
     * Retrieve an event on a program instance and a program stage. For
     * repeatable stage, the system returns the last event
     * 
     * @param programInstance ProgramInstance
     * @param programStage ProgramStage
     * 
     * @return ProgramStageInstance
     */
    ProgramStageInstance get( ProgramInstance programInstance, ProgramStage programStage );

    /**
     * Retrieve an event list on a program instance and a program stage
     * 
     * @param programInstance ProgramInstance
     * @param programStage ProgramStage
     * 
     * @return ProgramStageInstance
     */
    Collection<ProgramStageInstance> getAll( ProgramInstance programInstance, ProgramStage programStage );

    /**
     * Retrieve an event list on a program stage
     * 
     * @param programStage ProgramStage
     * 
     * @return ProgramStageInstance
     */
    Collection<ProgramStageInstance> get( ProgramStage programStage );

    /**
     * Retrieve an event list on due-date
     * 
     * @param dueDate Due date
     * 
     * @return ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Date dueDate );

    /**
     * Retrieve an event list by status on due date
     * 
     * @param dueDate Due date
     * @param completed Optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) instances.
     * 
     * @return ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Date dueDate, Boolean completed );

    /**
     * Retrieve an event list in a due date period
     * 
     * @param after - Optional date the instance should be on or after.
     * @param before - optional date the instance should be on or before.
     * 
     * @param ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Date startDate, Date endDate );

    /**
     * Retrieve an event list by complete status in a period
     * 
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param completed Optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) instances.
     */
    Collection<ProgramStageInstance> get( Date startDate, Date endDate, Boolean completed );

    /**
     * Retrieve an event list on program instance list
     * 
     * @param programInstances ProgramInstance list
     * 
     * @return ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Collection<ProgramInstance> programInstances );

    /**
     * Retrieve an event list on program instance list with a certain status
     * 
     * @param programInstances ProgramInstance list
     * @param completed Optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) instances.
     * 
     * @return ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Collection<ProgramInstance> programInstances, boolean completed );

    /**
     * Get all {@link ProgramStageInstance program stage instances} for unit.
     * 
     * @param unit - the unit to get instances for.
     * @param after - optional date the instance should be on or after.
     * @param before - optional date the instance should be on or before.
     * @param completed - optional flag to only get completed (<code>true</code>
     *        ) or uncompleted (<code>false</code>) instances.
     * @return
     */
    List<ProgramStageInstance> get( OrganisationUnit unit, Date after, Date before, Boolean completed );

    /**
     * Get all events by patient, optionally filtering by completed.
     * 
     * @param patient Patient
     * 
     * @param completed - optional flag to only get completed (
     *        <code>true</code> ) or uncompleted (<code>false</code>) instances.
     * 
     * @return ProgramStageInstance list
     */
    List<ProgramStageInstance> get( Patient patient, Boolean completed );

    /**
     * Retrieve an event list on program stage by an orgunit
     * 
     * @param programStage ProgramStage
     * @param organisationUnit OrganisationUnit
     * 
     * @return ProgramStageInstance list
     */
    List<ProgramStageInstance> get( ProgramStage programStage, OrganisationUnit orgunit );

    /**
     * Retrieve an event list on program stage by an orgunit in a due date
     * period
     * 
     * @param programStage ProgramStage
     * @param organisationUnit OrganisationUnit
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * 
     * @return ProgramStageInstance list
     */
    List<ProgramStageInstance> get( ProgramStage programStage, OrganisationUnit orgunit, Date startDate, Date endDate,
        int min, int max );

    /**
     * Remove events without any data values
     * 
     * @param programStage Empty events belong to this program stage are removed
     * @param organisationUnit Specify an orgunit where empty events belong to
     */
    void removeEmptyEvents( ProgramStage programStage, OrganisationUnit organisationUnit );

    /**
     * Create relationship between an OutboundSms with many events.
     * 
     * @param programStageInstances Event list
     * @param outboundSms OutboundSms object
     */
    void update( Collection<Integer> programStageInstanceIds, OutboundSms outboundSms );

    /**
     * Retrieve scheduled list of patients registered
     * 
     * @return A SchedulingProgramObject list
     */
    Collection<SchedulingProgramObject> getSendMesssageEvents();

    /**
     * Get the number of events by status
     * 
     * @param programStage The program stage needs to get details
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param status The status of event. There are four statuses for events,
     *        includes COMPLETED_STATUS, VISITED_STATUS, FUTURE_VISIT_STATUS,
     *        LATE_VISIT_STATUS
     */
    int getStatisticalProgramStageReport( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate,
        Date endDate, int status );

    /**
     * Get details of events which meets the criteria in statistical report
     * 
     * @param programStage The program stage needs to get details
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param status The status of event. There are four statuses for events,
     *        includes COMPLETED_STATUS, VISITED_STATUS, FUTURE_VISIT_STATUS,
     *        LATE_VISIT_STATUS
     * @param min
     * @param max
     */
    List<ProgramStageInstance> getStatisticalProgramStageDetailsReport( ProgramStage programStage,
        Collection<Integer> orgunitIds, Date startDate, Date endDate, int status, Integer min, Integer max );

    /**
     * Get events of a program by report date
     * 
     * @param program Program
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param completed optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) or all (<code>null</code>)
     *        instances.
     * 
     * @return ProgramStageInstance list
     */
    Collection<ProgramStageInstance> get( Program program, Collection<Integer> orgunitIds, Date after, Date before,
        Boolean completed );

    /**
     * Get the number of events by completed status
     * 
     * @param program Program
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param completed Optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) instances.
     * @return A number
     */
    int count( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate, Boolean completed );

    /**
     * Get the number of events by completed status
     * 
     * @param program Program
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param completed Optional flag to only get completed (<code>true</code> )
     *        or uncompleted (<code>false</code>) instances.
     * @return A number
     */
    int count( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        Boolean completed );

    /**
     * Get the number of over due events of a program stage in a certain period
     * 
     * @param programStage ProgramStage
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * 
     * @return A number
     */
    int getOverDueCount( ProgramStage programStage, Collection<Integer> orgunitIds, Date startDate, Date endDate );

    /**
     * Get the number of program instances completed
     * 
     * @param program Program
     * @param orgunitIds The ids of orgunits where the events happened
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * @param status The status of event. There are four statuses for events,
     *        includes COMPLETED_STATUS, VISITED_STATUS, FUTURE_VISIT_STATUS,
     *        LATE_VISIT_STATUS
     * @return A number
     */
    int averageNumberCompleted( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate,
        Integer status );

    /**
     * Get ids of orgunits where events happened in a period
     * 
     * @param startDate The start date for retrieving on report date
     * @param endDate The end date for retrieving on report date
     * 
     * @return The ids of orgunits
     */
    Collection<Integer> getOrgunitIds( Date startDate, Date endDate );

    /**
     * Get/Export a report about the number of events of a program completed on
     * a orgunit
     * 
     * @param orgunitIds The ids of orgunits where the events happened
     * @param program The program needs for reporting
     * @param after Optional date the instance should be on or after.
     * @param before Optional date the instance should be on or before.
     * 
     * @return Grid
     */
    Grid getCompleteness( Collection<Integer> orgunitIds, Program program, String startDate, String endDate, I18n i18n );
}

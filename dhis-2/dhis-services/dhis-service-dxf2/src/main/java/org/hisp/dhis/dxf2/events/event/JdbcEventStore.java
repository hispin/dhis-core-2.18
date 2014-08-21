package org.hisp.dhis.dxf2.events.event;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.common.IdentifiableObjectUtils.getIdList;
import static org.hisp.dhis.system.util.DateUtils.getMediumDateString;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JdbcEventStore
    implements EventStore
{
    private static final Log log = LogFactory.getLog( JdbcEventStore.class );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Event> getAll( Program program, ProgramStage programStage, ProgramStatus programStatus,
        Boolean followUp, List<OrganisationUnit> organisationUnits, TrackedEntityInstance trackedEntityInstance,
        Date startDate, Date endDate, EventStatus status )
    {
        List<Event> events = new ArrayList<>();

        Integer trackedEntityInstanceId = null;

        if ( trackedEntityInstance != null )
        {
            org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = entityInstanceService
                .getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() );

            if ( entityInstance != null )
            {
                trackedEntityInstanceId = entityInstance.getId();
            }
        }

        String sql = buildSql( program, programStage, programStatus, followUp, getIdList( organisationUnits ),
            trackedEntityInstanceId, startDate, endDate, status );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        log.info( "Event query SQL: " + sql );

        Event event = new Event();
        event.setEvent( "not_valid" );

        Set<String> notes = new HashSet<>();

        while ( rowSet.next() )
        {
            if ( rowSet.getString( "psi_uid" ) == null || rowSet.getString( "ou_uid" ) == null )
            {
                continue;
            }

            if ( !event.getEvent().equals( rowSet.getString( "psi_uid" ) ) )
            {
                event = new Event();

                event.setEvent( rowSet.getString( "psi_uid" ) );
                event.setTrackedEntityInstance( rowSet.getString( "pa_uid" ) );
                event.setStatus( EventStatus.valueOf( rowSet.getString( "psi_status" ) ) );
                event.setProgram( rowSet.getString( "p_uid" ) );
                event.setProgramStage( rowSet.getString( "ps_uid" ) );

                if ( rowSet.getInt( "p_type" ) != 3 )
                {
                    event.setEnrollment( rowSet.getString( "pi_uid" ) );
                    event.setEnrollmentStatus( EventStatus.fromInt( rowSet.getInt( "pi_status" ) ) );
                }

                event.setStoredBy( rowSet.getString( "psi_completeduser" ) );
                event.setOrgUnit( rowSet.getString( "ou_uid" ) );
                event.setDueDate( StringUtils.defaultIfEmpty(
                    rowSet.getString( "psi_duedate" ), rowSet.getString( "psi_duedate" ) ) );
                event.setEventDate( StringUtils.defaultIfEmpty(
                    rowSet.getString( "psi_executiondate" ), rowSet.getString( "psi_executiondate" ) ) );

                if ( rowSet.getBoolean( "ps_capturecoordinates" ) )
                {
                    Double longitude = rowSet.getDouble( "psi_longitude" );
                    Double latitude = rowSet.getDouble( "psi_latitude" );

                    if ( longitude != null && latitude != null )
                    {
                        Coordinate coordinate = new Coordinate( longitude, latitude );

                        try
                        {
                            List<Double> list = objectMapper.readValue( coordinate.getCoordinateString(),
                                new TypeReference<List<Double>>()
                                {
                                } );

                            coordinate.setLongitude( list.get( 0 ) );
                            coordinate.setLatitude( list.get( 1 ) );
                        }
                        catch ( IOException ignored )
                        {
                        }

                        if ( coordinate.isValid() )
                        {
                            event.setCoordinate( coordinate );
                        }
                    }
                }

                events.add( event );
            }

            if ( rowSet.getString( "pdv_value" ) != null && rowSet.getString( "de_uid" ) != null )
            {
                DataValue dataValue = new DataValue();
                dataValue.setValue( rowSet.getString( "pdv_value" ) );
                dataValue.setProvidedElsewhere( rowSet.getBoolean( "pdv_providedelsewhere" ) );
                dataValue.setDataElement( rowSet.getString( "de_uid" ) );
                dataValue.setStoredBy( rowSet.getString( "pdv_storedby" ) );

                event.getDataValues().add( dataValue );
            }

            if ( rowSet.getString( "psinote_value" ) != null && !notes.contains( rowSet.getString( "psinote_id" ) ) )
            {
                Note note = new Note();
                note.setValue( rowSet.getString( "psinote_value" ) );
                note.setStoredDate( StringUtils.defaultIfEmpty(
                    rowSet.getString( "psinote_soreddate" ), rowSet.getString( "psinote_soreddate" ) ) );
                note.setStoredBy( rowSet.getString( "psinote_storedby" ) );

                event.getNotes().add( note );
                notes.add( rowSet.getString( "psinote_id" ) );
            }

        }

        return events;
    }

    private String buildSql( Program program, ProgramStage programStage, ProgramStatus programStatus, Boolean followUp,
        List<Integer> orgUnitIds, Integer trackedEntityInstanceId, Date startDate, Date endDate, EventStatus status )
    {
        SqlHelper hlp = new SqlHelper();

        String sql =
            "select pi.uid as pi_uid, pi.status as pi_status, p.uid as p_uid, p.type as p_type, ps.uid as ps_uid, ps.capturecoordinates as ps_capturecoordinates, pa.uid as pa_uid, psi.uid as psi_uid, psi.status as psi_status, ou.uid as ou_uid, " +
                "psi.executiondate as psi_executiondate, psi.duedate as psi_duedate, psi.completeduser as psi_completeduser, psi.longitude as psi_longitude, psi.latitude as psi_latitude, " +
                "psinote.trackedentitycommentid as psinote_id, psinote.commenttext as psinote_value, psinote.createddate as psinote_soreddate, psinote.creator as psinote_storedby, " +
                "pdv.value as pdv_value, pdv.storedby as pdv_storedby, pdv.providedelsewhere as pdv_providedelsewhere, de.uid as de_uid " +
                "from program p " +
                "left join programstage ps on ps.programid=p.programid " +
                "left join programstageinstance psi on ps.programstageid=psi.programstageid " +
                "left join programinstance pi on pi.programinstanceid=psi.programinstanceid " +
                "left join programstageinstancecomments psic on psi.programstageinstanceid=psic.programstageinstanceid " +
                "left join trackedentitycomment psinote on psic.trackedentitycommentid=psinote.trackedentitycommentid ";

        if ( status == null || EventStatus.isExistingEvent( status ) )
        {
            sql += "left join organisationunit ou on (psi.organisationunitid=ou.organisationunitid) ";
        }
        else
        {
            sql +=
                "left join trackedentityinstance tei on tei.trackedentityinstanceid=pi.trackedentityinstanceid " +
                    "left join organisationunit ou on (tei.organisationunitid=ou.organisationunitid) ";
        }

        sql +=
            "left join trackedentitydatavalue pdv on psi.programstageinstanceid=pdv.programstageinstanceid " +
                "left join dataelement de on pdv.dataelementid=de.dataelementid " +
                "left join trackedentityinstance pa on pa.trackedentityinstanceid=pi.trackedentityinstanceid ";

        if ( trackedEntityInstanceId != null )
        {
            sql += hlp.whereAnd() + " pa.trackedentityinstanceid=" + trackedEntityInstanceId + " ";
        }

        if ( program != null )
        {
            sql += hlp.whereAnd() + " p.programid = " + program.getId() + " ";
        }

        if ( programStage != null )
        {
            sql += hlp.whereAnd() + " ps.programstageid = " + programStage.getId() + " ";
        }

        if ( programStatus != null )
        {
            sql += hlp.whereAnd() + " pi.status = " + programStatus.getValue() + " ";
        }

        if ( followUp != null )
        {
            sql += hlp.whereAnd() + " pi.followup is " + (followUp ? "true" : "false") + " ";
        }

        if ( status == null || EventStatus.isExistingEvent( status ) )
        {
            if ( orgUnitIds != null && !orgUnitIds.isEmpty() )
            {
                sql += hlp.whereAnd() + " psi.organisationunitid in (" + getCommaDelimitedString( orgUnitIds ) + ") ";
            }

            if ( startDate != null )
            {
                sql += hlp.whereAnd() + " psi.executiondate >= '" + getMediumDateString( startDate ) + "' ";
            }

            if ( endDate != null )
            {
                sql += hlp.whereAnd() + " psi.executiondate <= '" + getMediumDateString( endDate ) + "' ";
            }
        }
        else
        {
            if ( orgUnitIds != null && !orgUnitIds.isEmpty() )
            {
                sql += hlp.whereAnd() + " tei.organisationunitid in (" + getCommaDelimitedString( orgUnitIds ) + ") ";
            }

            if ( startDate != null )
            {
                sql += hlp.whereAnd() + " psi.duedate >= '" + getMediumDateString( startDate ) + "' ";
            }

            if ( endDate != null )
            {
                sql += hlp.whereAnd() + " psi.duedate <= '" + getMediumDateString( endDate ) + "' ";
            }

            if ( status == EventStatus.VISITED )
            {
                sql = "and psi.status = '" + EventStatus.ACTIVE.name() + "' and psi.executiondate is not null ";
            }
            else if ( status == EventStatus.COMPLETED )
            {
                sql = "and psi.status = '" + EventStatus.COMPLETED.name() + "' ";
            }
            else if ( status == EventStatus.SCHEDULE )
            {
                sql += "and psi.executiondate is null and date(now()) <= date(psi.duedate) and psi.status = '" + EventStatus.ACTIVE.name() + "' ";
            }
            else if ( status == EventStatus.OVERDUE )
            {
                sql += "and psi.executiondate is null and date(now()) > date(psi.duedate) and psi.status = '" + EventStatus.ACTIVE.name() + "' ";
            }
            else
            {
                sql += "and psi.status = '" + status.name() + "' ";
            }
        }

        sql += " order by psi_uid;";

        return sql;
    }
}

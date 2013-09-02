package org.hisp.dhis.dxf2.event;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultEventStore implements EventStore
{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getAll( Program program, ProgramStage programStage, OrganisationUnit organisationUnit, Date startDate, Date endDate )
    {
        List<Event> events = new ArrayList<Event>();
        String sql = buildSql( programStage.getId(), organisationUnit.getId(), startDate.toString(), endDate.toString() );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        Event event = new Event();
        event.setEvent( "not_valid" );

        while ( rowSet.next() )
        {
            if ( !event.getEvent().equals( rowSet.getString( "psi_uid" ) ) )
            {
                event = new Event();

                event.setCompleted( rowSet.getBoolean( "psi_completed" ) );
                event.setEvent( rowSet.getString( "psi_uid" ) );
                event.setProgram( program.getUid() );
                event.setProgramStage( programStage.getUid() );
                event.setStoredBy( rowSet.getString( "psi_completeduser" ) );
                event.setOrgUnit( rowSet.getString( "ou_uid" ) );
                event.setEventDate( rowSet.getString( "psi_executiondate" ) );

                events.add( event );
            }

            DataValue dataValue = new DataValue();
            dataValue.setValue( rowSet.getString( "pdv_value" ) );
            dataValue.setProvidedElsewhere( rowSet.getBoolean( "pdv_providedelsewhere" ) );
            dataValue.setDataElement( rowSet.getString( "de_uid" ) );

            event.getDataValues().add( dataValue );
        }

        return events;
    }

    private String buildSql( int programStageId, int orgUnitId, String startDate, String endDate )
    {
        String sql = "select psi.uid as psi_uid, ou.uid as ou_uid, psi.executiondate as psi_executiondate," +
            " psi.completeduser as psi_completeduser, psi.completed as psi_completed," +
            " pdv.value as pdv_value, pdv.providedelsewhere as pdv_providedelsewhere, de.uid as de_uid" +
            " from programstageinstance psi" +
            " left join organisationunit ou on (psi.organisationunitid=ou.organisationunitid and ou.organisationunitid=" + orgUnitId + ")" +
            " left join patientdatavalue pdv on psi.programstageinstanceid=pdv.programstageinstanceid" +
            " left join dataelement de on pdv.dataelementid=de.dataelementid" +
            " where psi.programstageid=" + programStageId +
            " and (psi.executiondate >= '" + startDate + "' and psi.executiondate <= '" + endDate + "') order by psi_uid";

        return sql;
    }
}

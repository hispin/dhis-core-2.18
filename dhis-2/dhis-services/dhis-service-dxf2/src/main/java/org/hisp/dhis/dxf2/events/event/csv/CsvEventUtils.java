package org.hisp.dhis.dxf2.events.event.csv;

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

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.hisp.dhis.dxf2.events.event.DataValue;
import org.hisp.dhis.dxf2.events.event.Event;
import org.hisp.dhis.dxf2.events.event.Events;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public final class CsvEventUtils
{
    private static CsvMapper csvMapper = new CsvMapper();

    private static CsvSchema csvSchema = csvMapper.schemaFor( CsvEventDataValue.class );

    public static CsvMapper getCsvMapper()
    {
        return csvMapper;
    }

    public static CsvSchema getCsvSchema()
    {
        return csvSchema;
    }

    public static void writeEvents( OutputStream outputStream, Events events, boolean withHeaders ) throws IOException
    {
        ObjectWriter writer = getCsvMapper().writer( getCsvSchema().withUseHeader( withHeaders ) );

        List<CsvEventDataValue> dataValues = new ArrayList<>();

        for ( Event event : events.getEvents() )
        {
            CsvEventDataValue templateDataValue = new CsvEventDataValue();
            templateDataValue.setEvent( event.getEvent() );
            templateDataValue.setProgram( event.getProgram() == null ? events.getProgram() : event.getProgram() );
            templateDataValue.setProgramInstance( events.getProgramInstance() );
            templateDataValue.setProgramStage( event.getProgramStage() );
            templateDataValue.setEnrollment( event.getEnrollment() );
            templateDataValue.setEnrollmentStatus( event.getEnrollmentStatus() );
            templateDataValue.setOrgUnit( event.getOrgUnit() );
            templateDataValue.setTrackedEntityInstance( event.getTrackedEntityInstance() );
            templateDataValue.setEventDate( event.getEventDate() );
            templateDataValue.setDueDate( event.getDueDate() );
            templateDataValue.setStoredBy( event.getStoredBy() );

            if ( event.getCoordinate() != null )
            {
                templateDataValue.setLatitude( event.getCoordinate().getLatitude() );
                templateDataValue.setLongitude( event.getCoordinate().getLongitude() );
            }

            templateDataValue.setFollowup( event.getFollowup() );

            for ( DataValue value : event.getDataValues() )
            {
                CsvEventDataValue dataValue = new CsvEventDataValue( templateDataValue );
                dataValue.setDataElement( value.getDataElement() );
                dataValue.setValue( value.getValue() );
                dataValue.setProvidedElsewhere( value.getProvidedElsewhere() );

                if ( value.getStoredBy() != null )
                {
                    dataValue.setStoredBy( value.getStoredBy() );
                }

                dataValues.add( dataValue );
            }
        }

        writer.writeValue( outputStream, dataValues );
    }

    private CsvEventUtils()
    {
    }
}

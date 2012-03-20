package org.hisp.dhis.common.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hisp.dhis.period.PeriodType;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonPeriodTypeSerializer
    extends JsonSerializer<PeriodType>
{
    @Override
    public void serialize( PeriodType value, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException
    {
        System.err.println( "Deserialized: " + value.getName() );

        jgen.writeString( value.getName() );
    }
}

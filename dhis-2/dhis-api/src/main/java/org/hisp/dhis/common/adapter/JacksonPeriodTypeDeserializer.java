package org.hisp.dhis.common.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.hisp.dhis.period.PeriodType;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JacksonPeriodTypeDeserializer
    extends JsonDeserializer<PeriodType>
{
    @Override
    public PeriodType deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException, JsonProcessingException
    {
        String periodTypeString = jp.readValueAs( String.class );

        System.err.println( "Deserialized: " + periodTypeString );

        return PeriodType.getPeriodTypeByName( periodTypeString );
    }
}

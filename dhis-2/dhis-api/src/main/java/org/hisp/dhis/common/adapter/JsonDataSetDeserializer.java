package org.hisp.dhis.common.adapter;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.dataset.DataSet;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class JsonDataSetDeserializer extends JsonDeserializer<DataSet>
{
    @Override
    public DataSet deserialize( JsonParser jp, DeserializationContext context ) throws IOException, JsonProcessingException
    {
        DataSet dataSet = new DataSet();
        BaseNameableObject baseNameableObject = jp.readValueAs( BaseNameableObject.class );

        dataSet.setUid( baseNameableObject.getUid() );
        dataSet.setName( baseNameableObject.getName() );

        return dataSet;
    }
}

package org.hisp.dhis.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/i18n" )
public class I18nController
{
    @Autowired
    private I18nManager i18nManager;

    @RequestMapping( method = RequestMethod.POST )
    public void postI18n( OutputStream outputStream, InputStream inputStream ) throws Exception
    {
        I18n i18n = i18nManager.getI18n( "org.hisp.dhis" );
        Map<String, String> output = new HashMap<String, String>();

        List<String> input = JacksonUtils.getJsonMapper().readValue( inputStream, new TypeReference<List<String>>()
        {
        } );

        for ( String key : input )
        {
            String value = i18n.getString( key );

            if ( value != null )
            {
                output.put( key, value );
            }
        }

        JacksonUtils.getJsonMapper().writeValue( outputStream, output );
    }
}

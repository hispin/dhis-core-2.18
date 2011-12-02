package org.hisp.dhis.api.view;

import org.springframework.validation.BindingResult;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class ViewUtils
{
    public static Map<String, Object> filterModel( Map<String, Object> model )
    {
        Map<String, Object> result = new HashMap<String, Object>( model.size() );

        for ( Map.Entry<String, Object> entry : model.entrySet() )
        {
            if ( !(entry.getValue() instanceof BindingResult) )
            {
                result.put( entry.getKey(), entry.getValue() );
            }
        }

        return result;
    }
}

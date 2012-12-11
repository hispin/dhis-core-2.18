package org.hisp.dhis.system.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMap<T, V>
    extends HashMap<T, List<V>>
{
    public List<String> putValue( T key, V value )
    {
        List<V> list = this.get( key );
        list = list == null ? new ArrayList<V>() : list;        
        list.add( value );
        this.put( key, list );        
        return null;
    }
}

package org.hisp.dhis.mapping;

import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.user.User;

public interface MapStore
    extends GenericIdentifiableObjectStore<Map>
{
    Collection<Map> getSystemAndUserMaps( User user );
}

package org.hisp.dhis.web.api.provider;

import static org.junit.Assert.assertTrue;

import org.hisp.dhis.web.api.mapping.DataStreamSerializableProvider;
import org.hisp.dhis.web.api.model.OrgUnits;
import org.junit.Test;

public class DataStreamSerializableProviderTest
{

    @Test
    public void testAssigning() {
        boolean writeable = new DataStreamSerializableProvider().isWriteable( OrgUnits.class, null, null, null );
        assertTrue( writeable );
    }
}

package org.hisp.dhis.web.api.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.model.OrgUnits;
import org.junit.Test;

public class OrgUnitsTest
{

    @Test
    public void testSerialization()
        throws IOException
    {
        OrgUnit unit = createOrgUnit();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );
        
        OrgUnits units = new OrgUnits();
        units.add( unit );
        units.serialize( dos );
        dos.flush();
        OrgUnits units2 = new OrgUnits( );
        units2.deSerialize( new DataInputStream( new ByteArrayInputStream( baos.toByteArray() ) ) );
        assertEquals( 1, units2.size() );

        OrgUnit unit2 = units2.get( 0 );
        assertEquals( unit.getName(), unit2.getName() );
        assertEquals( unit.getId(), unit2.getId() );
        
    }

    private OrgUnit createOrgUnit()
    {
        OrgUnit unit = new OrgUnit();
        unit.setId( 1 );
        unit.setName( "name" );
        unit.setDownloadActivityPlanUrl( "downloadActivityPlanUrl" );
        unit.setDownloadAllUrl( "downloadAllUrl" );
        unit.setUploadActivityReportUrl( "uploadActivityReportUrl" );
        unit.setUploadFacilityReportUrl( "uploadFacilityReportUrl" );
        return unit;
    }
}

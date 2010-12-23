package org.hisp.dhis.web.api.model;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

public class OrgUnitTest
{

    @Test
    public void testSerialization()
        throws IOException
    {
        OrgUnit unit = new OrgUnit();
        unit.setId( 1 );
        unit.setName( "name" );
        unit.setDownloadActivityPlanUrl( "downloadActivityPlanUrl" );
        unit.setDownloadAllUrl( "downloadAllUrl" );
        unit.setUploadActivityReportUrl( "uploadActivityReportUrl" );
        unit.setUploadFacilityReportUrl( "uploadFacilityReportUrl" );
        unit.setUpdateDataSetUrl( "updateDataSetUrl" );
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream( baos );
        unit.serialize( dos );
        dos.flush();
        OrgUnit unit2 = new OrgUnit();
        unit2.deSerialize( new DataInputStream( new ByteArrayInputStream( baos.toByteArray() ) ) );

        assertEquals( unit.getName(), unit2.getName() );
        assertEquals( unit.getId(), unit2.getId() );
        
    }
}

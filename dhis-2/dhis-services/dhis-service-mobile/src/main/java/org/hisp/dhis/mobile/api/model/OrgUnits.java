package org.hisp.dhis.mobile.api.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OrgUnits
    implements DataStreamSerializable
{
    private List<MobileOrgUnitLinks> orgUnits = new ArrayList<MobileOrgUnitLinks>();

    public OrgUnits()
    {
    }

    public OrgUnits( List<MobileOrgUnitLinks> unitList )
    {
        this.orgUnits = unitList;
    }

    @XmlElement( name = "orgUnit" )
    public List<MobileOrgUnitLinks> getOrgUnits()
    {
        return orgUnits;
    }

    public void setOrgUnits( List<MobileOrgUnitLinks> orgUnits )
    {
        this.orgUnits = orgUnits;
    }

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( orgUnits.size() );
        for ( MobileOrgUnitLinks unit : orgUnits )
        {
            unit.serialize( dataOutputStream );
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        orgUnits = new ArrayList<MobileOrgUnitLinks>();
        int size = dataInputStream.readInt();

        for ( int i = 0; i < size; i++ )
        {
            MobileOrgUnitLinks unit = new MobileOrgUnitLinks();
            unit.deSerialize( dataInputStream );
            orgUnits.add( unit );
        }
    }

}

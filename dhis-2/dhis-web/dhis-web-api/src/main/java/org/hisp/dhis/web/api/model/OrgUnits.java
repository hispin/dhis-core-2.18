package org.hisp.dhis.web.api.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class OrgUnits implements DataStreamSerializable
{
    private List<OrgUnit> orgUnits = new ArrayList<OrgUnit>();
    
    @XmlElement(name="orgUnit")
    public List<OrgUnit> getOrgUnits() {
        return orgUnits;
    }
    
    public void setOrgUnits( List<OrgUnit> orgUnits )
    {
        this.orgUnits = orgUnits;
    }

    @Override
    public void serialize( DataOutputStream dataOutputStream )
        throws IOException
    {
        dataOutputStream.writeInt( orgUnits.size() );
        for ( OrgUnit unit : orgUnits )
        {
            unit.serialize( dataOutputStream );
        }

    }

    @Override
    public void deSerialize( DataInputStream dataInputStream )
        throws IOException
    {
        orgUnits = new ArrayList<OrgUnit>();
        int size = dataInputStream.readInt();

        for ( int i = 0; i < size; i++ )
        {
            OrgUnit unit = new OrgUnit();
            unit.deSerialize( dataInputStream );
            orgUnits.add( unit );
        }
    }

}

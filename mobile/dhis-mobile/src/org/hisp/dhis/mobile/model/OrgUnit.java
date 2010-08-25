package org.hisp.dhis.mobile.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class OrgUnit extends AbstractModel{
	
    private int id;

    private String name;

    private String programFormsLink;
    
    private String activitiesLink;

    public void setId( int id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setProgramFormsLink( String programFormsLink )
    {
        this.programFormsLink = programFormsLink;
    }

    public void setActivitiesLink( String activitiesLink )
    {
        this.activitiesLink = activitiesLink;
    }

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getProgramFormsLink() {
		return programFormsLink;
	}

	public String getActivitiesLink() {
		return activitiesLink;
	}
	
	public static OrgUnit recordToOrgUnit( byte[] rec)
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(rec);
        DataInputStream din = new DataInputStream(bin);

        OrgUnit orgUnit = new OrgUnit();
        try{
        	orgUnit.setId( din.readInt() );
        	orgUnit.setName( din.readUTF());
        	orgUnit.setProgramFormsLink(din.readUTF());
        	orgUnit.setActivitiesLink(din.readUTF());
        }catch(IOException ioe){}

        return orgUnit;
    }
	
	public static byte[] orgUnitToRecord( OrgUnit orgUnit)
    {
		ByteArrayOutputStream deOs = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(deOs);
        
        try {
            dout.writeInt(orgUnit.getId());
            dout.writeUTF(orgUnit.getName());
            dout.writeUTF(orgUnit.getProgramFormsLink());
            dout.writeUTF(orgUnit.getActivitiesLink());
            dout.flush();
        }
        catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return deOs.toByteArray();
    }
	
}

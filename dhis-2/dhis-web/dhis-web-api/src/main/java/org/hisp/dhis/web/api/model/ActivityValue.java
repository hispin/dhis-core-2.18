/**
 * 
 */
package org.hisp.dhis.web.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author abyotag_adm
 *
 */
public class ActivityValue implements ISerializable {
	
	private int programInstanceId;
	
	private List<DataValue> dataValues = new ArrayList<DataValue>();
	
	public ActivityValue() {
	}
	
	public void setProgramInstanceId(int programInstanceId) {
		this.programInstanceId = programInstanceId;
	}

	public int getProgramInstanceId() {
		return programInstanceId;
	}

	public void setDataValues(List<DataValue> dataValues) {
		this.dataValues = dataValues;
	}
	
	public List<DataValue> getDataValues() {
		return dataValues;
	}

	public byte[] serialize() throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       

        dout.writeInt(this.getProgramInstanceId());
        dout.writeInt(dataValues.size());

        for(int i=0; i<dataValues.size(); i++)
        {
            DataValue dv = (DataValue)dataValues.get(i);
            dout.writeInt( dv.getId() );
            dout.writeUTF( dv.getVal() );            
        }

        return bout.toByteArray();
    }

    public void deSerialize(byte[] data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(bin);

        this.setProgramInstanceId( din.readInt() ) ;        

        int size = din.readInt();

        for(int i=0; i<size; i++)
        {
            DataValue dv = new DataValue();
            dv.setId( din.readInt() );
            dv.setVal( din.readUTF() );            
            this.dataValues.add(dv);
        }
    }
    
    public ActivityValue deSerialize(InputStream stream) throws IOException
    {       
        DataInputStream din = new DataInputStream(stream);

        this.setProgramInstanceId( din.readInt() ) ;     

        int size = din.readInt();

        for(int i=0; i<size; i++)
        {
            DataValue dv = new DataValue();
            dv.setId( din.readInt() );
            dv.setVal( din.readUTF() );            
            this.dataValues.add(dv);
        }
        
        return this;
    }
}

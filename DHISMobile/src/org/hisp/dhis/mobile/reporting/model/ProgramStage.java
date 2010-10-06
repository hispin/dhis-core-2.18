package org.hisp.dhis.mobile.reporting.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ProgramStage extends AbstractModel {
	private int programId;
    
	private Vector dataElements = new Vector();
	
	
	
	public int getProgramId()
        {
            return programId;
        }
    
        public void setProgramId( int programId )
        {
            this.programId = programId;
        }

        public Vector getDataElements() {
		return dataElements;
	}

	public void setDataElements(Vector dataElements) {
		this.dataElements = dataElements;
	}

	public byte[] serialize() throws IOException
        {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       
        
        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());
        dout.writeInt( this.programId );
        dout.writeInt(dataElements.size());

        for(int i=0; i<dataElements.size(); i++)
        {
            DataElement de = (DataElement)dataElements.elementAt(i);
            dout.writeInt( de.getId() );
            dout.writeUTF( de.getName() );
            dout.writeUTF( de.getType() );
        }

        return bout.toByteArray();
    }

    public void deSerialize(byte[] data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(bin);
        
        this.setId( din.readInt() ) ;
        this.setName( din.readUTF() );        

        //ignore programId
        din.readInt();
        //end
        
        int size = din.readInt();

        for(int i=0; i<size; i++)
        {
            DataElement de = new DataElement();
            de.setId( din.readInt() );
            de.setName( din.readUTF() );
            de.setType( din.readUTF() );
            this.dataElements.addElement(de);
        }
    }    
    
    public void serialize( DataOutputStream dout ) throws IOException
    {
        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());        
        dout.writeInt(dataElements.size());

        for(int i=0; i<dataElements.size(); i++)
        {
            DataElement de = (DataElement)dataElements.elementAt(i);
            dout.writeInt( de.getId() );
            dout.writeUTF( de.getName() );
            dout.writeUTF( de.getType() );
        }       
        
        dout.flush();            	
    } 
    
    public void deSerialize(DataInputStream din) throws IOException {

		this.setId(din.readInt());
		this.setName(din.readUTF());	

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			DataElement de = new DataElement();
			de.setId(din.readInt());
			de.setName(din.readUTF());
			de.setType(din.readUTF());
			this.dataElements.addElement(de);
		}
	}
}
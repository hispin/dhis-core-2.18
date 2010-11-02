package org.hisp.dhis.web.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "DSV")
@XmlAccessorType(XmlAccessType.FIELD) 
public class DataSetValue extends AbstractModel {
   
    private String pName;
    
    private boolean completed;
    
    @XmlElement(name = "dv")
	private List<DataValue> dataValues = new ArrayList<DataValue>();

    public DataSetValue(){}	
    
    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
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

        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());
        dout.writeUTF(this.getpName());
        dout.writeBoolean( this.isCompleted() );
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

        this.setId( din.readInt() ) ;
        this.setName( din.readUTF() );
        this.setpName( din.readUTF() ) ;
        this.setCompleted( din.readBoolean() );
        int size = din.readInt();

        for(int i=0; i<size; i++)
        {
            DataValue dv = new DataValue();
            dv.setId( din.readInt() );
            dv.setVal( din.readUTF() );            
            this.dataValues.add(dv);
        }
    }
    
    public DataSetValue deSerialize(InputStream stream) throws IOException
    {       
        DataInputStream din = new DataInputStream(stream);

        this.setId( din.readInt() ) ;
        this.setName( din.readUTF() );
        this.setpName( din.readUTF() ) ;
        this.setCompleted( din.readBoolean() );
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

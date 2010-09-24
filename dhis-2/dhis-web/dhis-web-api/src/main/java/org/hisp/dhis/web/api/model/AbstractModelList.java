/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.web.api.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author abyotag_adm
 */
public class AbstractModelList implements ISerializable {

    private List<AbstractModel> abstractModels;    
    

    public List<AbstractModel> getAbstractModels() {
		return abstractModels;
	}

	public void setAbstractModels(List<AbstractModel> abstractModels) {
		this.abstractModels = abstractModels;
	}

	public byte[] serialize() throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        dout.writeInt(abstractModels.size());

        for(int i=0; i<abstractModels.size(); i++)
        {
        	AbstractModel abstractModel = (AbstractModel)abstractModels.get(i);
            dout.writeInt( abstractModel.getId() );
            dout.writeUTF( abstractModel.getName() );            
        }

        return bout.toByteArray();
    }

    public void deSerialize(byte[] data) throws IOException
    {
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        DataInputStream din = new DataInputStream(bin);       

        int size = din.readInt();

        for(int i=0; i<size; i++)
        {
        	AbstractModel abstractModel = new AbstractModel();
        	abstractModel.setId( din.readInt() );
        	abstractModel.setName( din.readUTF() );            
            this.abstractModels.add(abstractModel);
        }
    }
    
    public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       

        dout.writeInt(abstractModels.size());

        for(int i=0; i<abstractModels.size(); i++)
        {
        	AbstractModel abstractModel = (AbstractModel)abstractModels.get(i);
            dout.writeInt( abstractModel.getId() );
            dout.writeUTF( abstractModel.getName() );            
        }       
        
        //bout.flush();
        bout.writeTo(out);
    	
    }	
}

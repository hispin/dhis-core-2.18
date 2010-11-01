/**
 * 
 */
package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author abyotag_adm
 *
 */
public class Program extends AbstractModel {
	
	private List<ProgramStage> programStages;

	public List<ProgramStage> getProgramStages() {
		return programStages;
	}

	public void setProgramStages(List<ProgramStage> programStages) {
		this.programStages = programStages;
	}
	
	public byte[] serialize() throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       

        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());        
        dout.writeInt(programStages.size());

        for(int i=0; i<programStages.size(); i++)
        {
            ProgramStage prorgamStage = (ProgramStage)programStages.get(i);
            prorgamStage.serialize(dout);            
        }

        return bout.toByteArray();
    }  
	
	public void serialize( OutputStream out ) throws IOException
    {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);       
        dout.writeInt(this.getId());
        dout.writeUTF(this.getName());        
        dout.writeInt(programStages.size());
        for(int i=0; i<programStages.size(); i++)
        {
        	ProgramStage programStage = (ProgramStage)programStages.get(i);
        	programStage.serialize(dout);            
        }       
        
        bout.flush();
        bout.writeTo(out);    	
    }        
}

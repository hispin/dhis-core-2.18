package org.hisp.dhis.web.api.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Tran Ng Minh Luan
 *
 */
public class ActivityWrapper implements ISerializable
{
    private ActivityPlan activityPlan;
    private List<Program> programs;
    
    public ActivityPlan getActivityPlan()
    {
        return activityPlan;
    }

    public void setActivityPlan( ActivityPlan activityPlan )
    {
        this.activityPlan = activityPlan;
    }

    public List<Program> getPrograms()
    {
        return programs;
    }

    public void setPrograms( List<Program> programs )
    {
        this.programs = programs;
    }

    public ActivityWrapper()
    {
        
    }
    
    public void serialize(OutputStream out) throws IOException
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        dout.writeInt(programs.size());
        
        this.activityPlan.serialize( dout );
        
        for(Program each : programs){
            each.serialize( dout );
        }
        bout.flush();
        bout.writeTo(out);
    }

    @Override
    public byte[] serialize()
        throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deSerialize( byte[] data )
        throws IOException
    {
        // TODO Auto-generated method stub
        
    }
    
    
    
}

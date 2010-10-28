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
public class MobileWrapper implements ISerializable{
	private ActivityPlan activityPlan;
    private List<Program> programs;
    private List<DataSet> datasets;
    
	public MobileWrapper() {
	}

	public ActivityPlan getActivityPlan() {
		return activityPlan;
	}

	public void setActivityPlan(ActivityPlan activityPlan) {
		this.activityPlan = activityPlan;
	}

	public List<Program> getPrograms() {
		return programs;
	}

	public void setPrograms(List<Program> programs) {
		this.programs = programs;
	}

	public List<DataSet> getDatasets() {
		return datasets;
	}

	public void setDatasets(List<DataSet> datasets) {
		this.datasets = datasets;
	}

	public void serialize(OutputStream out) throws IOException
    {
		
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(bout);
        
        if(programs != null){
        	dout.writeInt(programs.size());
        }else{
        	dout.writeInt(0);
        }
        
        //Write ActivityPlans
        if(this.activityPlan == null){
        	dout.writeInt(0);
        }else{
        	this.activityPlan.serialize( dout );
        }
        System.out.println("finish serialize ActivityPlan");
        
        //Write Programs   
        if(programs != null || programs.size() > 0){
        	for(Program prog : programs){
	        	prog.serialize( dout );
	        }
        } 
        System.out.println("finish serialize Programs");
        
        //Write DataSets
        if(datasets == null){
        	dout.writeInt(0);
        }else{
        	dout.writeInt(datasets.size());
        	for(DataSet ds : datasets){
            	ds.serialize(dout);
            }        	
        }
        
        System.out.println("finish serialize Datasets");
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

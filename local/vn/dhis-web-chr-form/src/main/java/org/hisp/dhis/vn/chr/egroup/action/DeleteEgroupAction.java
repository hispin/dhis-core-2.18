package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.vn.chr.EgroupService;
import com.opensymphony.xwork.Action;

public class DeleteEgroupAction implements Action
{
	// -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

	private EgroupService egroupService;
    
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private Integer id;
	
	// -----------------------------------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------------------------------
	
	public void setEgroupService(EgroupService egroupService) {
		this.egroupService = egroupService;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
		egroupService.deleteEgroup(id.intValue());
		
		return SUCCESS;
	}
	
}

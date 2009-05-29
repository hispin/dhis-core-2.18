package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.FormService;

import com.opensymphony.xwork.Action;

public class AddEgroupAction implements Action
{
	// -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

	private FormService formService;
	
	private EgroupService egroupService;
    
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private Integer formID;
	
	private String name;

	private Integer sortOrder;
	
	// -----------------------------------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------------------------------

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setFormID(Integer formID) {
		this.formID = formID;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setEgroupService(EgroupService egroupService) {
		this.egroupService = egroupService;
	}

	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
//		System.out.print("\n\n\n name : " + name);
//		
//		System.out.print("\n\n\n sortOrder : " + sortOrder);
		
		Egroup egroup = new Egroup();
		
		egroup.setName(CodecUtils.unescape(name));
		
		egroup.setSortOrder(sortOrder.intValue());
		
		egroup.setForm(formService.getForm(formID.intValue()));
		
		egroupService.addEgroup(egroup);
		
		return SUCCESS;
	}

	
}

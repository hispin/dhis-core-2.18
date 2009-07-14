package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class AddEgroupAction extends ActionSupport
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
		
		Egroup egroup = new Egroup();
		
		egroup.setName(CodecUtils.unescape(name));
		
		egroup.setSortOrder(sortOrder.intValue());
		
		egroup.setForm(formService.getForm(formID.intValue()));
		
		egroupService.addEgroup(egroup);
		
		message = i18n.getString("success");
		
		return SUCCESS;
	}

	
}

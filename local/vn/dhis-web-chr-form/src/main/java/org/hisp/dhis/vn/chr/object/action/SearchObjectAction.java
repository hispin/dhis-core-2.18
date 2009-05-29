package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class SearchObjectAction extends ActionSupport{
	
	// -----------------------------------------------------------------------------------------------
    // Dependencies
    // -----------------------------------------------------------------------------------------------

	private FormManager formManager;
	
	private FormService formService;
	
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	// Form ID
	private Integer formId;
	
	// keyword to search
	private String keyword;
	
	private Form form;
	
	// Object data
	private ArrayList data;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public ArrayList getData() {
		return data;
	}

	public void setData(ArrayList data) {
		this.data = data;
	}
	
	public void setFormId(Integer formId) {
		this.formId = formId;
	}

	public Integer getFormId() {
		return this.formId;
	}
	
	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}
	
	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL 
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {
		
		try {

			form = formService.getForm(formId.intValue());
			
			data = formManager.searchObject(form, CodecUtils.unescape(keyword));
			
			return SUCCESS;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ERROR;
	}
}

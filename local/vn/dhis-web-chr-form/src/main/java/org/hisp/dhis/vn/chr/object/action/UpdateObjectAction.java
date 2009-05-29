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

public class UpdateObjectAction extends ActionSupport{
	
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
	
	// Object data
	private ArrayList data;
	
	// ID of Object
	private Integer id;
	
	// -----------------------------------------------------------------------------------------------
    // Getter && Setter
    // -----------------------------------------------------------------------------------------------
	
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
	
	// -----------------------------------------------------------------------------------------------
    // Implement : process Select SQL 
    // -----------------------------------------------------------------------------------------------


	public String execute() throws Exception {
		
		try {

			Form form = formService.getForm(formId.intValue());
			
//			// convert data to Unicode
//			for(int i=0 ;i<data.length ;i++){
//				data[i] = CodecUtils.unescape(data[i]);
//			}
			
			formManager.updateObject(form, data);
			
			return SUCCESS;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ERROR;
	}
}

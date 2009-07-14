package org.hisp.dhis.vn.chr.element.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;


public class UpdateElementAction extends ActionSupport
{
	// -----------------------------------------------------------------------------------------------
    // Dependency
    // -----------------------------------------------------------------------------------------------

	private FormService formService;
	
	private ElementService elementService;
    
	// -----------------------------------------------------------------------------------------------
    // Input && Output
    // -----------------------------------------------------------------------------------------------

	private Integer id;
	
	private int formID;

	private String name;

	private String label;

	private String type;

	private String controlType;

	private String initialValue;

	private int formLink;

	private boolean required;

	private int sortOrder;
	
	// -----------------------------------------------------------------------------------------------
    // Getters && Setters
    // -----------------------------------------------------------------------------------------------
	
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getFormID() {
		return formID;
	}

	public void setFormID(int formID) {
		this.formID = formID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getControlType() {
		return controlType;
	}

	public void setControlType(String controlType) {
		this.controlType = controlType;
	}

	public String getInitialValue() {
		return initialValue;
	}

	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
	}

	public int getFormLink() {
		return formLink;
	}

	public void setFormLink(int formLink) {
		this.formLink = formLink;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public FormService getFormService() {
		return formService;
	}

	public ElementService getElementService() {
		return elementService;
	}
	
	// -----------------------------------------------------------------------------------------------
    // Implement
    // -----------------------------------------------------------------------------------------------

	public String execute() throws Exception{
		
		Element element = elementService.getElement(id.intValue());
		
		element.setName(CodecUtils.unescape(name).toLowerCase());
		
		element.setLabel(CodecUtils.unescape(label));
		
		element.setType(type);
		
		element.setControlType(controlType);

		element.setInitialValue(initialValue);
		
		System.out.print("\n\n\n Formlink : " + formLink);
		
		if(formLink != 0){
			
			element.setFormLink(formService.getForm(formLink));
			
		}else{

			element.setFormLink(null);
			
		}
		
		element.setRequired(required);
		
		element.setSortOrder(sortOrder);
		
		elementService.updateElement(element);

		element.setForm(formService.getForm(formID));
		
		message = i18n.getString("success");
		
		return SUCCESS;
	}

}

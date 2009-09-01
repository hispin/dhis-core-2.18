package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.options.formconfiguration.FormConfigurationManager;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class SearchObjectAction extends ActionSupport {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormManager formManager;

	private FormService formService;

	private FormConfigurationManager formConfigurationManager;

	private ElementService elementService;

	public void setFormConfigurationManager(
			FormConfigurationManager formConfigurationManager) {
		this.formConfigurationManager = formConfigurationManager;
	}

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

	// formLinks
	private Collection<Element> formLinks;

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

	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	public Collection<Element> getFormLinks() {
		return formLinks;
	}

	public void setFormLinks(Collection<Element> formLinks) {
		this.formLinks = formLinks;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement : process Select SQL
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		try {

			form = formService.getForm(formId.intValue());

			formLinks = elementService.getElementsByFormLink(form);

			data = formManager.searchObject(form, CodecUtils.unescape(keyword),
					Integer.parseInt(formConfigurationManager
							.getNumberOfRecords()));

			return SUCCESS;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ERROR;
	}
}

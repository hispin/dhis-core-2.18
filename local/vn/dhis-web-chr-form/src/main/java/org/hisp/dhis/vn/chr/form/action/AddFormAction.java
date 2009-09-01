package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;

public class AddFormAction extends ActionSupport {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private String name;

	private String label;

	private int noColumn;

	private int noColumnLink;

	private String icon;

	private boolean visible;

	// -----------------------------------------------------------------------------------------------
	// Getter && Setter
	// -----------------------------------------------------------------------------------------------

	public void setFormService(FormService formService) {
		this.formService = formService;
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

	public int getNoColumn() {
		return noColumn;
	}

	public void setNoColumn(int noColumn) {
		this.noColumn = noColumn;
	}

	public int getNoColumnLink() {
		return noColumnLink;
	}

	public void setNoColumnLink(int noColumnLink) {
		this.noColumnLink = noColumnLink;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	// -----------------------------------------------------------------------------------------------
	// Implements
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		Form form = new Form();

		form.setName(CodecUtils.unescape(name));

		form.setLabel(CodecUtils.unescape(label));

		form.setNoColumn(noColumn);

		form.setNoColumnLink(noColumnLink);

		form.setIcon(icon);

		form.setVisible(visible);

		form.setCreated(false);

		formService.addForm(form);

		message = i18n.getString("success");

		return SUCCESS;
	}
}

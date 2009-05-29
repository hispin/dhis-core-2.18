
package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

public class ValidateFormAction extends ActionSupport {
	
	// -------------------------------------------
	// Input & Output
	// -------------------------------------------

	private Integer id;

	private String name;

	private String label;

	private int noRow;

	private int noColumn;

	private String message;

	// -------------------------------------------
	// Getter & Setter
	// -------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public int getNoRow() {
		return noRow;
	}

	public void setNoRow(int noRow) {
		this.noRow = noRow;
	}

	public int getNoColumn() {
		return noColumn;
	}

	public void setNoColumn(int noColumn) {
		this.noColumn = noColumn;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	// -------------------------------------------
	// Implement
	// -------------------------------------------

	public String execute() throws Exception {

		if (name == null || name.trim().length() == 0) {
			message = i18n.getString("name_is_null");
			return ERROR;
		}

		if (label == null || label.trim().length() == 0) {
			message = i18n.getString("label_is_null");
			return ERROR;
		}
		if (noRow == 0) {
			message = i18n.getString("noRow_is_null");
			return ERROR;
		}
		if (noColumn == 0) {
			message = i18n.getString("noColumn_is_null");
			return ERROR;
		}

		return SUCCESS;
	}
}

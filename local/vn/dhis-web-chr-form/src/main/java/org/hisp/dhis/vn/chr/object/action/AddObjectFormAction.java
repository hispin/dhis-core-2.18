package org.hisp.dhis.vn.chr.object.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;
import org.hisp.dhis.vn.chr.FormService;
import org.hisp.dhis.vn.chr.jdbc.FormManager;

import com.opensymphony.xwork.Action;

public class AddObjectFormAction implements Action {

	// -----------------------------------------------------------------------------------------------
	// Dependencies
	// -----------------------------------------------------------------------------------------------

	private FormService formService;

	private FormManager formManager;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private int formId;

	private Collection<Egroup> egroups;

	private Form form;

	private String objectId;

	private ArrayList parentObject;

	// -----------------------------------------------------------------------------------------------
	// Getter && Setter
	// -----------------------------------------------------------------------------------------------

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setFormManager(FormManager formManager) {
		this.formManager = formManager;
	}

	public Collection<Egroup> getEgroups() {
		return this.egroups;
	}

	public void setEgroups(Collection<Egroup> egroups) {
		this.egroups = egroups;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public ArrayList getParentObject() {
		return parentObject;
	}

	public void setParentObject(ArrayList parentObject) {
		this.parentObject = parentObject;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		form = formService.getForm(formId);

		egroups = form.getEgroups();

		if (objectId != null) {

			Iterator<Egroup> iter = egroups.iterator();
			if (iter.hasNext()) {
				for (Element element : iter.next().getElements()) {
					if (element.getFormLink() != null) {
						Form fparent = element.getFormLink();
						ArrayList<String> data = formManager.getObject(fparent,
								Integer.parseInt(objectId));
						parentObject = new ArrayList<String>();
						int k = 0;
						for (Egroup egroup : fparent.getEgroups()) {
							for (Element e : egroup.getElements()) {
								if (data.get(k) != null)
									parentObject.add(e.getLabel() + " : "
											+ data.get(k));
								k++;
								if (k == fparent.getNoColumnLink())
									break;
							}// end for element

							if (k == fparent.getNoColumnLink())
								break;
						}// end for egroup
					}
				}
			}
		}

		return SUCCESS;
	}
}

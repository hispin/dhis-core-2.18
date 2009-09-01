package org.hisp.dhis.vn.chr.form.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;
import java.util.Iterator;

import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

import com.opensymphony.xwork2.Action;

public class GetElementsForGroupAction implements Action {

	// ------------------------------------------------------------------------------------------
	// Input & Output
	// ------------------------------------------------------------------------------------------

	private Form form;

	// egroups of the form

	Collection<Egroup> egroups;

	Collection<Element> availableElements;

	// ------------------------------------------------------------------------------------------
	// Getters & Setters
	// ------------------------------------------------------------------------------------------

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public void setEgroups(Collection<Egroup> egroups) {
		this.egroups = egroups;
	}

	public Collection<Egroup> getEgroups() {
		return egroups;
	}

	public void setAvailableElements(Collection<Element> availableElements) {
		this.availableElements = availableElements;
	}

	public Collection<Element> getAvailableElements() {
		return availableElements;
	}

	// ------------------------------------------------------------------------------------------
	// Implement
	// ------------------------------------------------------------------------------------------
	public String execute() throws Exception {

		this.egroups = form.getEgroups();

		this.availableElements = form.getElements();

		if (availableElements != null) {
			Iterator<Element> iter = availableElements.iterator();
			while (iter.hasNext()) {
				if (iter.next().getEgroup() != null)
					iter.remove();
			}
		}

		return SUCCESS;

	}

}

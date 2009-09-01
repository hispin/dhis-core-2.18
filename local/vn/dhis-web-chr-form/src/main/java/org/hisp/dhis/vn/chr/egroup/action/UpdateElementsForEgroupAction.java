package org.hisp.dhis.vn.chr.egroup.action;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.HashSet;
import java.util.Set;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.EgroupService;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.ElementService;
import org.hisp.dhis.vn.chr.form.action.ActionSupport;

public class UpdateElementsForEgroupAction extends ActionSupport {
	// -----------------------------------------------------------------------------------------------
	// Dependency
	// -----------------------------------------------------------------------------------------------

	private EgroupService egroupService;

	private ElementService elementService;

	// -----------------------------------------------------------------------------------------------
	// Input && Output
	// -----------------------------------------------------------------------------------------------

	private Integer id;

	private Integer[] selectedElements;

	// -----------------------------------------------------------------------------------------------
	// Getters && Setters
	// -----------------------------------------------------------------------------------------------

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer[] getSelectedElements() {
		return selectedElements;
	}

	public void setSelectedElements(Integer[] selectedElements) {
		this.selectedElements = selectedElements;
	}

	public void setEgroupService(EgroupService egroupService) {
		this.egroupService = egroupService;
	}

	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	// -----------------------------------------------------------------------------------------------
	// Implement
	// -----------------------------------------------------------------------------------------------

	public String execute() throws Exception {

		Egroup egroup = egroupService.getEgroup(id.intValue());

		Set<Element> elements = new HashSet<Element>();

		for (int i = 0; i < selectedElements.length; i++) {

			Element e = elementService.getElement(selectedElements[i]
					.intValue());

			e.setEgroup(egroup);

			elements.add(e);
		}

		egroup.setElements(elements);

		egroupService.updateEgroup(egroup);

		message = i18n.getString("success");

		return SUCCESS;
	}

}

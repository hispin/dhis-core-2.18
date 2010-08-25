/**
 * 
 */
package org.hisp.dhis.patient.api.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;


/**
 * @author abyotag_adm
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD) 
@XmlType(propOrder= {"formId", "formName", "dataElements"})
public class Form {
	
	private int formId;
	
	private String formName;	
	
	@XmlElementWrapper( name = "des" )
	@XmlElement(name = "de")
	private List<IDataElement> dataElements;

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public List<IDataElement> getDataElements() {
		return dataElements;
	}

	public void setDataElements(List<IDataElement> dataElements) {
		this.dataElements = dataElements;
	}		
}



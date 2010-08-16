/**
 * 
 */
package org.hisp.dhis.cbhiswebservice.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author abyotag_adm
 * 
 */
@XmlRootElement
public class IDataElement {

	private int deId;

	private String deName;

	private String deType;

	public IDataElement() {

	}

	public int getDeId() {
		return deId;
	}

	public void setDeId(int deId) {
		this.deId = deId;
	}

	public String getDeName() {
		return deName;
	}

	public void setDeName(String deName) {
		this.deName = deName;
	}

	public String getDeType() {
		return deType;
	}

	public void setDeType(String deType) {
		this.deType = deType;
	}	
}

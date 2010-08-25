/**
 * 
 */
package org.hisp.dhis.patient.api.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author abyotag_adm
 * 
 */

@XmlRootElement
public class IProgramStage {

	private int id;

	private String name;

	public IProgramStage() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}

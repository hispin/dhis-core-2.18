package org.hisp.dhis.dxf2.events.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Note {

	private String value;
	
	private String storedBy;
	
	private String storedDate;
	
	public Note()
	{		
	}
	
	@JsonProperty
    @JacksonXmlProperty( isAttribute = true )
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@JsonProperty
    @JacksonXmlProperty( isAttribute = true )
	public String getStoredBy() {
		return storedBy;
	}

	public void setStoredBy(String storedBy) {
		this.storedBy = storedBy;
	}

	@JsonProperty
    @JacksonXmlProperty( isAttribute = true )
	public String getStoredDate() {
		return storedDate;
	}

	public void setStoredDate(String storedDate) {
		this.storedDate = storedDate;
	}	

}

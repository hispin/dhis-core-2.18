package org.hisp.dhis.integration.rims.api;

public class RIMS_Mapping_Orgunit 
{

	private String dhisid;
	private String rimsid;
	private String ouName;
	
	//-------------------------------------------------------------------------
	// Constructors
	//-------------------------------------------------------------------------

	public RIMS_Mapping_Orgunit()
	{
		
	}
	public RIMS_Mapping_Orgunit(String dhisid, String rimsid, String ouName)
	{
		this.dhisid = dhisid;
		this.rimsid = rimsid;
		this.ouName = ouName;
	}
	
	//-------------------------------------------------------------------------
	// Getters & Setters
	//-------------------------------------------------------------------------

	public String getDhisid() {
		return dhisid;
	}
	public void setDhisid(String dhisid) {
		this.dhisid = dhisid;
	}
	public String getRimsid() {
		return rimsid;
	}
	public void setRimsid(String rimsid) {
		this.rimsid = rimsid;
	}
	public String getOuName() {
		return ouName;
	}
	public void setOuName(String ouName) {
		this.ouName = ouName;
	}

	
}

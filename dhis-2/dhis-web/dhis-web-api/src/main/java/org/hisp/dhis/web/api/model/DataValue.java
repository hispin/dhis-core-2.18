package org.hisp.dhis.web.api.model;

import java.io.IOException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "dv")
@XmlAccessorType(XmlAccessType.FIELD) 
public class DataValue implements ISerializable {
	
	private int id;
	
	private String val;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	@Override
	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		
	}	
}

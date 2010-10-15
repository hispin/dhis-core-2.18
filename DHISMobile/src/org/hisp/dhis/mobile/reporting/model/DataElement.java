/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.mobile.reporting.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 * @author abyotag_adm
 */
public class DataElement extends AbstractModel implements ISerializable {

	private String type;

	public DataElement() {
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getName());
		dout.writeUTF(this.getType());

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		this.setId(din.readInt());
		this.setName(din.readUTF());
		this.setType(din.readUTF());
	}

	public void deSerialize(DataInputStream din) throws IOException {

		this.setId(din.readInt());
		this.setName(din.readUTF());
		this.setType(din.readUTF());
	}
}

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

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getVal());

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		this.setId(din.readInt());
		this.setVal(din.readUTF());
	}
}

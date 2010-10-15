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
public class AbstractModel implements ISerializable {

	private int id;

	private String name;

	public AbstractModel() {
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public static AbstractModel recordToAbstractModel(byte[] rec) {
		ByteArrayInputStream bin = new ByteArrayInputStream(rec);
		DataInputStream din = new DataInputStream(bin);

		AbstractModel model = new AbstractModel();

		try {
			model.setId(din.readInt());
			model.setName(din.readUTF());
		} catch (IOException ioe) {
		}

		return model;

	}

	public byte[] serialize() throws IOException {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getName());

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {

		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		this.setId(din.readInt());
		this.setName(din.readUTF());
	}
}

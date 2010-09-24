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
import java.util.Vector;

/**
 * 
 * @author abyotag_adm
 */
public class DataSetValue extends AbstractModel implements ISerializable {

	private String pName;

	private Vector dataValues = new Vector();

	public DataSetValue() {
	}

	/**
	 * @return the pName
	 */
	public String getpName() {
		return pName;
	}

	/**
	 * @param pName
	 *            the pName to set
	 */
	public void setpName(String pName) {
		this.pName = pName;
	}

	/**
	 * @return the dataValues
	 */
	public Vector getDataValues() {
		return dataValues;
	}

	/**
	 * @param dataValues
	 *            the dataValues to set
	 */
	public void setDataValues(Vector dataValues) {
		this.dataValues = dataValues;
	}

	public byte[] serialize() throws IOException {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getName());
		dout.writeUTF(this.getpName());
		dout.writeInt(dataValues.size());

		for (int i = 0; i < dataValues.size(); i++) {
			DataValue dv = (DataValue) dataValues.elementAt(i);
			dout.writeInt(dv.getId());
			dout.writeUTF(dv.getVal());
		}

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		this.setId(din.readInt());
		this.setName(din.readUTF());
		this.setpName(din.readUTF());

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			DataValue dv = new DataValue();
			dv.setId(din.readInt());
			dv.setVal(din.readUTF());
			this.dataValues.addElement(dv);
		}
	}
}

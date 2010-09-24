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
public class DataSet extends AbstractModel implements ISerializable {

	private Vector dataElements = new Vector();

	private String periodType;

	public DataSet() {
	}

	/**
	 * @return the periodType
	 */
	public String getPeriodType() {
		return periodType;
	}

	/**
	 * @param periodType
	 *            the periodType to set
	 */
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}

	/**
	 * @return the dataElements
	 */
	public Vector getDataElements() {
		return dataElements;
	}

	/**
	 * @param dataElements
	 *            the dataElements to set
	 */
	public void setDataElements(Vector dataElements) {
		this.dataElements = dataElements;
	}

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getName());
		dout.writeUTF(this.getPeriodType());
		dout.writeInt(dataElements.size());

		for (int i = 0; i < dataElements.size(); i++) {
			DataElement de = (DataElement) dataElements.elementAt(i);
			dout.writeInt(de.getId());
			dout.writeUTF(de.getName());
			dout.writeUTF(de.getType());
		}

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		this.setId(din.readInt());
		this.setName(din.readUTF());
		this.setPeriodType(din.readUTF());

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			DataElement de = new DataElement();
			de.setId(din.readInt());
			de.setName(din.readUTF());
			de.setType(din.readUTF());
			this.dataElements.addElement(de);
		}
	}

	public void deSerialize(DataInputStream din) throws IOException {

		this.setId(din.readInt());
		this.setName(din.readUTF());
		this.setPeriodType(din.readUTF());

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			DataElement de = new DataElement();
			de.setId(din.readInt());
			de.setName(din.readUTF());
			de.setType(din.readUTF());
			this.dataElements.addElement(de);
		}
	}
}

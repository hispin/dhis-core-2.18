/**
 * 
 */
package org.hisp.dhis.mobile.reporting.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author abyotag_adm
 * 
 */
public class ActivityValue implements ISerializable {

	private int programInstanceId;

	private Vector dataValues = new Vector();

	public ActivityValue() {
	}

	public void setProgramInstanceId(int programInstanceId) {
		this.programInstanceId = programInstanceId;
	}

	public int getProgramInstanceId() {
		return programInstanceId;
	}

	public Vector getDataValues() {
		return dataValues;
	}

	public void setDataValues(Vector dataValues) {
		this.dataValues = dataValues;
	}

	public byte[] serialize() throws IOException {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getProgramInstanceId());
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

		this.setProgramInstanceId(din.readInt());

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			DataValue dv = new DataValue();
			dv.setId(din.readInt());
			dv.setVal(din.readUTF());
			this.dataValues.addElement(dv);
		}
	}
}

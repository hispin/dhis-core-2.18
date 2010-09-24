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
public class AbstractModelList implements ISerializable {

	private Vector abstractModels = new Vector();

	/**
	 * @return the abstractModels
	 */
	public Vector getAbstractModels() {
		return abstractModels;
	}

	/**
	 * @param abstractModels
	 *            the abstractModels to set
	 */
	public void setAbstractModels(Vector abstractModels) {
		this.abstractModels = abstractModels;
	}

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(getAbstractModels().size());

		for (int i = 0; i < getAbstractModels().size(); i++) {
			AbstractModel abstractModel = (AbstractModel) getAbstractModels()
					.elementAt(i);
			dout.writeInt(abstractModel.getId());
			dout.writeUTF(abstractModel.getName());
		}

		return bout.toByteArray();
	}

	public void deSerialize(byte[] data) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		DataInputStream din = new DataInputStream(bin);

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			AbstractModel abstractModel = new AbstractModel();
			abstractModel.setId(din.readInt());
			abstractModel.setName(din.readUTF());

			this.getAbstractModels().addElement(abstractModel);
		}
	}

	public void deSerialize(DataInputStream din) throws IOException {
		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			AbstractModel abstractModel = new AbstractModel();
			abstractModel.setId(din.readInt());
			abstractModel.setName(din.readUTF());

			this.getAbstractModels().addElement(abstractModel);
		}
	}

}

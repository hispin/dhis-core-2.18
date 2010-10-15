/**
 * 
 */
package org.hisp.dhis.mobile.reporting.model;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author abyotag_adm
 * 
 */
public class Program extends AbstractModel {

	private Vector programStages = new Vector();

	public Vector getProgramStages() {
		return programStages;
	}

	public void setProgramStages(Vector programStages) {
		this.programStages = programStages;
	}

	public byte[] serialize() throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(bout);

		dout.writeInt(this.getId());
		dout.writeUTF(this.getName());
		dout.writeInt(programStages.size());

		for (int i = 0; i < programStages.size(); i++) {
			ProgramStage prorgamStage = (ProgramStage) programStages
					.elementAt(i);
			prorgamStage.serialize(dout);
		}

		return bout.toByteArray();
	}

	public void deSerialize(DataInputStream din) throws IOException {

		this.setId(din.readInt());
		this.setName(din.readUTF());

		int size = din.readInt();

		for (int i = 0; i < size; i++) {
			System.out.println("In loop... pr");
			ProgramStage prStg = new ProgramStage();
			prStg.deSerialize(din);
			this.programStages.addElement(prStg);
		}
	}
}

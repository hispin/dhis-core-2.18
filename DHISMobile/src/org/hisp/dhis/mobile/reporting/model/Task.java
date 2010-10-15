package org.hisp.dhis.mobile.reporting.model;

import java.io.DataInputStream;
import java.io.IOException;

public class Task implements ISerializable {

	private int progStageInstId;

	private int progStageId;

	private boolean complete;

	public int getProgStageInstId() {
		return progStageInstId;
	}

	public void setProgStageInstId(int progStageInstId) {
		this.progStageInstId = progStageInstId;
	}

	public int getProgStageId() {
		return progStageId;
	}

	public void setProgStageId(int progStageId) {
		this.progStageId = progStageId;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub

	}

	public void deSerialize(DataInputStream din) throws IOException {

		this.setProgStageInstId(din.readInt());
		this.setProgStageId(din.readInt());
		this.setComplete(din.readBoolean());
	}

}

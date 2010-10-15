package org.hisp.dhis.mobile.reporting.model;

import java.io.DataInputStream;
import java.io.IOException;

public class Beneficiary implements ISerializable {

	private int id;

	private String firstName, middleName, lastName;

	public Beneficiary() {
	}

	public String getFullName() {
		boolean space = false;
		String name = "";

		if (firstName != null && firstName.length() != 0) {
			name = firstName;
			space = true;
		}
		if (middleName != null && middleName.length() != 0) {
			if (space)
				name += " ";
			name += middleName;
			space = true;
		}
		if (lastName != null && lastName.length() != 0) {
			if (space)
				name += " ";
			name += lastName;
		}
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public byte[] serialize() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public void deSerialize(byte[] data) throws IOException {
		// TODO Auto-generated method stub

	}

	public void deSerialize(DataInputStream din) throws IOException {
		this.setId(din.readInt());
		this.setFirstName(din.readUTF());
		this.setMiddleName(din.readUTF());
		this.setLastName(din.readUTF());
	}
}

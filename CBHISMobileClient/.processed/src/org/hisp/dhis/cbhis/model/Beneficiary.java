package org.hisp.dhis.cbhis.model;



public class Beneficiary {

private int id;
private String firstName,middleName,lastName;


public Beneficiary() {
}



//Getter and Setter
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getFullName(){
	return getFirstName()+" "+getMiddleName()+" "+getLastName();
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


}

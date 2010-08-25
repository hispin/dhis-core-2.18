package org.hisp.dhis.mobile.model;

public class Task {
	
	private int progStageInstId,progStageId;
	private String progStageName;
	private boolean complete;
	
	
	public Task() {
	}
	
	//Getter and Setter
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
	public String getProgStageName() {
		return progStageName;
	}
	public void setProgStageName(String progStageName) {
		this.progStageName = progStageName;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	
}

package org.hisp.dhis.vn.chr.jdbc;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;

import org.hisp.dhis.vn.chr.Form;

public interface FormManager {
	
	/**
     * Create table from Form
     * 
     * @param form needs to create table
     */
	public void createTable(Form form );
	
	/**
     * Load list object
     * 
     * @param form needs to create the table
     * @param pageIndex Index of page
     * @return List Objects
     */
	public ArrayList listObject(Form form, int pageIndex );
	
	/**
     * Get data in a Object by id of Object
     * 
     * @param form needs to create the table
     * @param id Id of object
     * @return values of a Object
     */
	public ArrayList getObject(Form form, int id);
	
	/**
     * Add Object by ID
     * 
     * @param form needs to create the table
     * @param data Data of Object
     */
	public void addObject(Form form, String[] data);
	
	/**
     * Update Object by ID
     * 
     * @param form needs to create the table
     * @param data Data of Object
     */
	public void updateObject(Form form, ArrayList<String> data);
	
	/**
     * Delete Object by ID
     * 
     * @param form needs to create the table
     * @param id Id of object
     */
	public void deleteObject(Form form, int id);
	
	/**
     * Search Object by keyword
     * 
     * @param form needs to create the table
     * @param keyword Keyword
     */
	public ArrayList searchObject(Form form, String keyword);

	public ArrayList ListRelativeObject(Form form, String column ,String objectId);
}

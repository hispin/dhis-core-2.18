package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class ListDataStatement extends FormStatement {

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	
	public ListDataStatement(Form form, StatementDialect dialect, int pageIndex) {
		super(form, dialect, pageIndex);
	}

	// -------------------------------------------------------------------------
	// Override
	// -------------------------------------------------------------------------
	
	@Override
	protected void init(Form form) {
		
		StringBuffer buffer = new StringBuffer();

		// SELECT id,
		buffer.append("SELECT" + SPACE + "id" + SEPARATOR);
		
		// Number of columns selected
		int noColumn = form.getNoColumn();
		int index = 0;
		// Break loop for
		boolean flag = false;
		
		for(Egroup egroup : form.getEgroups()){
			
			if(flag) break;
			
//			int noOfElement = egroup.getElements().size();
			for(Element element : egroup.getElements()){
			
				if(index < noColumn-1){
//				if(index < noOfElement-1){
					// <column_name>,
					buffer.append(element.getName() + SEPARATOR);
				}else{ 
					//<column_name> 
					buffer.append(element.getName() + SPACE);
					flag = true;
					break;
				}
				index++;
			}// end for element
			
		}// end for egroup
		
		int pageSize = form.getNoRow();
		
		// FORM <table_name> 
//		 LIMIT (startIndex,endIndex>)
		buffer.append(SPACE + "FROM" + SPACE + form.getName() + SPACE );
		buffer.append(SPACE + "LIMIT" + SPACE + form.getNoRow());
//		buffer.append(SPACE + "FROM" + SPACE + form.getName() + SPACE + 
//				"LIMIT(" + ((pageIndex -1 )*pageSize + 1) + SEPARATOR + (pageIndex * pageSize) + ")" );  

		statement = buffer.toString();
	}

}

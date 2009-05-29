package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class SearchDataStatement extends FormStatement {

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	
	public SearchDataStatement(Form form, StatementDialect dialect, String keywork) {
		super(form, dialect, keywork);
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

		for (Egroup egroup : form.getEgroups()) {

			if (flag)
				break;

			for (Element element : egroup.getElements()) {

				if (index < noColumn - 1) {
						// <column_name>,
						buffer.append(element.getName() + SEPARATOR);
				} else {
					// <column_name>
					buffer.append(element.getName() + SPACE);
					flag = true;
					break;
				}
				index++;
			}// end for element

		}// end for egroup

		// FORM <table_name> WHERE 1=1
		buffer.append("FROM" + SPACE + form.getName() + SPACE + "WHERE" + SPACE);
		
		int i=0;
		String condition = "";
		// OR <column_name>=value OR <column_name>=value OR ...
		for(Egroup egroup : form.getEgroups()){
			for(Element element : egroup.getElements()){
				
				condition += element.getName() + SPACE + "like" + SPACE + "'%" + keyword + "%'" + SPACE + "OR" + SPACE;
				
				i++;
			}
		}		

		buffer.append(condition.substring(0, condition.length()-3) + SPACE );
		
		statement = buffer.toString();
	}

}

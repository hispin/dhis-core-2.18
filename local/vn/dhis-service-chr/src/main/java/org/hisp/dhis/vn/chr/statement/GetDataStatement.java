package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class GetDataStatement extends FormStatement {

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	
	public GetDataStatement(Form form, StatementDialect dialect, int id) {
		super(form, dialect, id);
	}

	// -------------------------------------------------------------------------
	// Override
	// -------------------------------------------------------------------------
	
	@Override
	protected void init(Form form) {
		
		StringBuffer buffer = new StringBuffer();

		// SELECT 
		buffer.append("SELECT" + SPACE );
		
		// Get Columns
		String columns = "";
		for(Egroup egroup : form.getEgroups()){
			for(Element element : egroup.getElements()){
				// <column_name>,
				columns+= element.getName() + SEPARATOR ;
			}
			
		}
		
		// delete SEPARATOR at the end of columns, add SPACE in SQL 
		buffer.append(columns.substring(0, columns.length()-2) + SPACE );
		
		// FROM <table_name> WHERE id = <id_column>
		buffer.append("FROM" + SPACE + form.getName() +
				SPACE + "WHERE" + SPACE + "id="+  value);
	
		statement = buffer.toString();
		
	}

}

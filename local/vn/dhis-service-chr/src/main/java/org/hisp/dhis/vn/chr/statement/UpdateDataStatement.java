package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;
import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class UpdateDataStatement extends FormStatement {

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------
	
	public UpdateDataStatement(Form form, StatementDialect dialect, ArrayList<String> data) {
		super(form, dialect, data);
	}

	// -------------------------------------------------------------------------
	// Override
	// -------------------------------------------------------------------------
	
	@Override
	protected void init(Form form) {
		
		StringBuffer buffer = new StringBuffer();

		// Update <table_name> SET 
		buffer.append("UPDATE" + SPACE + form.getName() + SPACE + "SET" + SPACE);
		
		// Get Columns and correlative values
		String columns = "";
		int i = 1;
		for(Egroup egroup : form.getEgroups()){
			for(Element element : egroup.getElements()){
				// <column_name>=<data>,
				columns+= element.getName() +"="+ data.get(i) + SEPARATOR + SPACE;
				i++;
			}
		}
		
		// delete SEPARATOR at the end of columns, add SPACE in SQL 
		buffer.append(columns.substring(0, columns.length()-1) + SPACE );
		
		// FROM <table_name> WHERE id = <id_column>
		buffer.append("WHERE" + SPACE + "id="+  data.get(i));
	
		statement = buffer.toString();
		
System.out.print("\n\n\n update data : " + statement);
	}

}

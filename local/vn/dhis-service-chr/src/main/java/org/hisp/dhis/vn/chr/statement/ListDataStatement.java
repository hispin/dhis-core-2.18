package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.Collection;

import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.user.User;
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
			
			if(flag){ 
				break;
			}// end if
			
			for(Element element : egroup.getElements()){
			
				if(index < noColumn){
					// <column_name>,
					buffer.append(element.getName().toLowerCase() + SEPARATOR + SPACE);
				}else{
					flag = true;
					break;
				}

				index++;
			}// end for element
			
		}// end for egroup
		
		buffer.append("addby" + SPACE + "FROM" + SPACE + form.getName() + SPACE );
		
		buffer.append("WHERE" + SPACE );
		
		buffer.append( "addby" + SPACE + "in"+ SPACE + "(" + SPACE);
		
		Collection<User> users = FormStatement.USERS;
		
		for(User user : users){
		
			buffer.append( user.getId() + "," + SPACE );
		}
		
		buffer.append( USERS.iterator().next().getId() + SPACE + ")" + SPACE);
		
		// order by editeddate
		buffer.append("order by" + SPACE + "createddate" + SPACE + "asc" + SPACE);
		
		buffer.append("LIMIT" + SPACE + value);
		
		statement = buffer.toString();

	}

}

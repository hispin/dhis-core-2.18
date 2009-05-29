package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.hisp.dhis.jdbc.StatementDialect;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class CreateTableStatement extends FormStatement{

	private static final String REQUIRED = "";

	// -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CreateTableStatement( Form form, StatementDialect dialect )
    {
        super( form, dialect );
    }

	@Override
	protected void init(Form form) {
		
		StringBuffer buffer = new StringBuffer();
       
		// CREATE TABLE <table_name> (
		buffer.append( "CREATE TABLE " + form.getName() + " ( " );

        // ---------------------------------------------------------------------
        // Create table
        // ---------------------------------------------------------------------
		
		// id INTEGER ;
        buffer.append("id" + SPACE + NUMBER + SPACE + REQUIRED + SEPARATOR);
        for ( Element column : form.getElements() )
        {
        	// Column name
        	// <column_name> 
            buffer.append( column.getName() + SPACE );
            
            // Data Type
            if(column.getType().equalsIgnoreCase("number")){
            	
            	// INTEGER
            	buffer.append( NUMBER  + SPACE);
            }else if(column.getType().equalsIgnoreCase("date")){
            	
            	// DATE
            	buffer.append( DATE  + SPACE);
            }else if(column.getType().equalsIgnoreCase("double")){
            	
            	// DOUBLE
            	buffer.append( DOUBLE  + SPACE);
            }else {// if(column.getType().equalsIgnoreCase("string")){
            	
            	// VARCHAR
            	buffer.append( STRING  + SPACE);
            }
            
            // ;
            buffer.append( SEPARATOR );
        }

        // ---------------------------------------------------------------------
        // Primary key
        // ---------------------------------------------------------------------

        // CONSTRAINT <form_name + random number>+ PRIMARY KEY (id)
        buffer.append( "CONSTRAINT" + SPACE + form.getName() + System.currentTimeMillis() + SPACE + "PRIMARY KEY (id)" );
        
        // end create table
        // ;
        buffer.append( ") " );
        
        statement = buffer.toString();
		
	}
	
	
}

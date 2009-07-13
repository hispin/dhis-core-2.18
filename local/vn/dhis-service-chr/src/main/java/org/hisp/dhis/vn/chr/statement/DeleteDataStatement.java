package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.amplecode.quick.StatementBuilder;
import org.hisp.dhis.vn.chr.Form;

public class DeleteDataStatement
    extends FormStatement
{

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public DeleteDataStatement( Form form, StatementBuilder statementBuilder, int id )
    {
        super( form, statementBuilder, id );
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void init( Form form )
    {
        StringBuffer buffer = new StringBuffer();

        // DELETE FROM <table_name> WHERE id = <id_column>
        buffer.append( "DELETE" + SPACE + "FROM" + SPACE + form.getName() + SPACE + "WHERE" + SPACE + "id=" + value );

        statement = buffer.toString();
    }
}

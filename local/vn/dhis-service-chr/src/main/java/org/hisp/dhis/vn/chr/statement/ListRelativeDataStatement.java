package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import org.amplecode.quick.StatementBuilder;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class ListRelativeDataStatement
    extends FormStatement
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public ListRelativeDataStatement( Form form, StatementBuilder statementBuilder, String status, String column )
    {
        super( form, statementBuilder, status, column );
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void init( Form form )
    {
        StringBuffer buffer = new StringBuffer();

        // SELECT id,
        buffer.append( "SELECT" + SPACE + "id" + SEPARATOR );

        // Number of columns selected
        int noColumn = form.getNoColumn();
        int index = 0;
        // Break loop for
        boolean flag = false;

        for ( Egroup egroup : form.getEgroups() )
        {

            if ( flag )
                break;

            for ( Element element : egroup.getElements() )
            {

                if ( index < noColumn - 1 )
                {
                    // <column_name>,
                    buffer.append( element.getName() + SEPARATOR );
                }
                else
                {
                    // <column_name>
                    buffer.append( element.getName() + SPACE );
                    flag = true;
                    break;
                }
                index++;
            }// end for element

        }// end for egroup

        // FORM <table_name> WHERE column=value
        buffer.append( "FROM" + SPACE + form.getName() + SPACE + "WHERE" + SPACE );

        buffer.append( column + "=" + status );

        statement = buffer.toString();
    }
}

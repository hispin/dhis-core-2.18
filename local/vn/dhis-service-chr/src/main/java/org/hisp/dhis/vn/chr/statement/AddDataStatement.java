package org.hisp.dhis.vn.chr.statement;

/**
 * @author Chau Thu Tran
 * 
 */

import java.util.ArrayList;

import org.amplecode.quick.StatementBuilder;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class AddDataStatement
    extends FormStatement
{

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AddDataStatement( Form form, StatementBuilder statementBuilder, ArrayList<String> data )
    {
        super( form, statementBuilder, data );
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void init( Form form )
    {

        StringBuffer buffer = new StringBuffer();

        // INSERT INTO <table_name> (
        buffer.append( "INSERT" + SPACE + "INTO" + SPACE + form.getName() + SPACE + "(" );

        // Get Columns
        String columns = "";
        // Get Values - VALUES (
        String values = "VALUES" + SPACE + "(";
        int i = 0;
        for ( Egroup egroup : form.getEgroups() )
        {
            for ( Element element : egroup.getElements() )
            {

                if ( data.get( i ).length() > 0 )
                {
                    // <column_name>=<data>,
                    columns += element.getName() + SEPARATOR;
                    values += "'" + data.get( i ) + "'" + SEPARATOR;

                }
                i++;
            }
        }

        buffer.append( columns + "id)" + SPACE );

        buffer.append( values + "'" + System.identityHashCode( values ) + "'" + ")" + SPACE );
        statement = buffer.toString();

        System.out.print( "\n\n\n statement : " + statement );
    }
}

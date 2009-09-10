package org.hisp.dhis.vn.chr.statement;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.user.User;
import org.hisp.dhis.vn.chr.Egroup;
import org.hisp.dhis.vn.chr.Element;
import org.hisp.dhis.vn.chr.Form;

public class CreateCodeStatement extends FormStatement
{

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public CreateCodeStatement( Form form, StatementBuilder statementBuilder)
    {
        super( form, statementBuilder );
    }

    // -------------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------------

    @Override
    protected void init( Form form )
    {

        StringBuffer buffer = new StringBuffer();

        // SELECT
        buffer.append( "SELECT" + SPACE  + "COUNT(*)" + SPACE);

        
        // FROM <table_name> WHERE id = <id_column>
        buffer.append( "FROM" + SPACE  + form.getName().toLowerCase() + SPACE);
        // WHERE
        Date date = new Date();
        Format formatter = new SimpleDateFormat( "yyyy-MM" );
        String period = formatter.format(date);
        
        buffer.append( "WHERE" +  SPACE + "createddate>=" + period + "-" + "01" + SPACE );
        buffer.append( "AND" +  SPACE + "createddate<=" + period + "-" + "31" );

        statement = buffer.toString();
        
    } 
}

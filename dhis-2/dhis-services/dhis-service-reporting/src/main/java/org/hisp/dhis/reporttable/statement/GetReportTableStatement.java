package org.hisp.dhis.reporttable.statement;

import java.util.Iterator;

import org.hisp.dhis.reporttable.ReportTable;

public class GetReportTableStatement
    extends ReportTableStatement
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public GetReportTableStatement( ReportTable reportTable )
    {
        super( reportTable );
    }

    // -------------------------------------------------------------------------
    // ReportTableStatement implementation
    // -------------------------------------------------------------------------

    @Override
    protected void init( ReportTable reportTable )
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append( "SELECT * FROM " + reportTable.getTableName() );
        
        Iterator<String> indexNameColumns = reportTable.getIndexNameColumns().iterator();
        
        if ( indexNameColumns.hasNext() )
        {
            buffer.append( " ORDER BY " );
            
            while ( indexNameColumns.hasNext() )
            {
                buffer.append( indexNameColumns.next() );
                
                if ( indexNameColumns.hasNext() )
                {
                    buffer.append( SEPARATOR );
                }
            }
        }
        
        System.out.println( buffer.toString() );
        statement = buffer.toString();
    }
}

package org.hisp.dhis.sqlview;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dang Duy Hieu
 */
public class SqlViewTable
{
    private List<String> headers = new ArrayList<String>();

    private List<List<Object>> records = new ArrayList<List<Object>>();

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SqlViewTable()
    {
    }

    public SqlViewTable( List<String> headers )
    {
        this.headers = headers;
    }

    public SqlViewTable( List<String> headers, List<List<Object>> records )
    {
        this.headers = headers;
        this.records = records;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public List<String> getHeaders()
    {
        return headers;
    }

    public void setHeaders( List<String> headers )
    {
        this.headers = headers;
    }

    public List<List<Object>> getRecords()
    {
        return records;
    }

    public void setRecords( List<List<Object>> records )
    {
        this.records = records;
    }

    // -------------------------------------------------------------------------
    // Other methods
    // -------------------------------------------------------------------------

    public void addRecord( List<Object> record )
    {
        records.add( record );
    }

    public void createViewerStructure( ResultSet rs )
    {
        try
        {
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnNo = rsmd.getColumnCount();

            for ( int i = 1; i <= columnNo; i++ )
            {
                headers.add( rsmd.getColumnLabel( i ) );
            }
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    public void addRecord( ResultSet rs )
    {
        try
        {
            int columnNo = rs.getMetaData().getColumnCount();

            while ( rs.next() )
            {
                List<Object> rows = new ArrayList<Object>();

                for ( int i = 1; i <= columnNo; i++ )
                {
                    rows.add( rs.getObject( i ) );
                }

                records.add( rows );
            }
        }
        catch ( SQLException ex )
        {
            throw new RuntimeException( ex );
        }
    }

    // -------------------------------------------------------------------------
    // hashCode() & equals()
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((headers == null) ? 0 : headers.hashCode());
        result = prime * result + ((records == null) ? 0 : records.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }

        final SqlViewTable other = (SqlViewTable) obj;

        if ( headers == null )
        {
            if ( other.headers != null )
            {
                return false;
            }
        }
        else if ( !headers.equals( other.headers ) )
        {
            return false;
        }
        if ( records == null )
        {
            if ( other.records != null )
            {
                return false;
            }
        }
        else if ( !records.equals( other.records ) )
        {
            return false;
        }
        return true;
    }
}

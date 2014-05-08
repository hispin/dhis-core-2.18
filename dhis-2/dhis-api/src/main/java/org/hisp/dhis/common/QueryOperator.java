package org.hisp.dhis.common;

public enum QueryOperator
{
    EQ, GT, GE, LT, LE, NE, LIKE, IN;
    
    public static final QueryOperator fromString( String string )
    {
        if ( string == null || string.isEmpty() )
        {
            return null;
        }
        
        return valueOf( string.toUpperCase() );
    }
}

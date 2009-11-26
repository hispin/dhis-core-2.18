package org.hisp.dhis.common;

public interface ProcessState
{
    String getMessage();
    
    void setMessage( String message );
    
    Object getOutput();
    
    void setOutput( Object output );
}

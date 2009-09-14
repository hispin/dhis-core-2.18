package org.hisp.dhis.util;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.util.StreamUtils;

import com.opensymphony.xwork2.ActionSupport;

public abstract class StreamActionSupport
    extends ActionSupport
{
    protected static final String CONTENT_TYPE_PDF = "application/pdf";

    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OutputStream out = null;
        
        HttpServletResponse response = ServletActionContext.getResponse();
        
        if ( getContentType() != null )
        {
            response.setContentType( getContentType() );
        }
        
        if ( getFilename() != null )
        {
            response.addHeader( "Content-disposition", "filename=\"" + getFilename() + "\"" );
        }
        
        try
        {
            out = response.getOutputStream();
            
            return execute( response, out );
        }
        finally
        {
            StreamUtils.closeOutputStream( out );
        }
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract String execute( HttpServletResponse response, OutputStream out )
        throws Exception;
    
    protected abstract String getContentType();
    
    protected abstract String getFilename();
}

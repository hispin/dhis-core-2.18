package org.hisp.dhis.dataanalyser.ds.action;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.opensymphony.xwork2.Action;

public class ExportToExcelAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    /*
    private String contentType;

    public String getContentType()
    {
        return contentType;
    }
    */



    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    /*
    private int bufferSize;

    public int getBufferSize()
    {
        return bufferSize;
    }
    */
    
    private String htmlCode;
    
    public void setHtmlCode( String htmlCode )
    {
        this.htmlCode = htmlCode;
    }
    
    private String htmlCode1;
    
    public void setHtmlCode1( String htmlCode1 )
    {
        this.htmlCode1 = htmlCode1;
    }

    /*
    private StringBuffer htmlCode;
    
    public void setHtmlCode( StringBuffer htmlCode )
    {
        this.htmlCode = htmlCode;
    }
*/
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------



    public String execute() throws Exception
    {                        
        System.out.println( "Inside Excel Import Action" );
        fileName = "dataStatusResult.xls";
        
        if( htmlCode != null )
        {
            inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode.getBytes("UTF-8") ) );
        }
        
        else 
        {
            inputStream = new BufferedInputStream( new ByteArrayInputStream( htmlCode1.getBytes("UTF-8") ) );
        }
        
       // System.out.println( "HTML CODE IS : "  + htmlCode1 );
        return SUCCESS;
    }


}

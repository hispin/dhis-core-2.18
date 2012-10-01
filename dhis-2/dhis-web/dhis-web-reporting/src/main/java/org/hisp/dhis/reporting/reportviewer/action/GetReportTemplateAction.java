package org.hisp.dhis.reporting.reportviewer.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.util.StreamUtils;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.core.io.ClassPathResource;

import com.opensymphony.xwork2.Action;

public class GetReportTemplateAction
    implements Action
{
    private static final String TEMPLATE = "template.jrxml";

    @Override
    public String execute()
        throws Exception
    {
        HttpServletResponse response = ServletActionContext.getResponse();

        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, false, TEMPLATE, true );
        
        StreamUtils.streamcopy( new BufferedInputStream( new ClassPathResource( TEMPLATE ).getInputStream() ), 
            new BufferedOutputStream( response.getOutputStream() ) );
        
        return SUCCESS;
    }
}

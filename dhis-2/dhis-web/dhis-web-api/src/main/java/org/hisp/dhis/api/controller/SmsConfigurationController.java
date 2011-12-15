package org.hisp.dhis.api.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.sms.SmsConfigurationManager;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping( value = SmsConfigurationController.RESOURCE_PATH )
public class SmsConfigurationController
{

    public static final String RESOURCE_PATH = "/config/sms";

    private static final Log log = LogFactory.getLog( SmsConfigurationController.class );

    @Autowired
    private SmsConfigurationManager smsConfigurationManager;
    
    @RequestMapping( method = RequestMethod.GET )
    public String getSmsConfiguration( Model model )
    {

        SmsConfiguration smsConfiguration = smsConfigurationManager.getSmsConfiguration();

        if (smsConfiguration == null) {
            smsConfiguration = new SmsConfiguration();
        }
        
        model.addAttribute( "model", smsConfiguration );

        return "smsConfiguration";
    }

    //-------------------------------------------------------------------------------------------------------
    // POST
    //-------------------------------------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.PUT )
    public String putSmsConfig( @RequestBody SmsConfiguration smsConfiguration, Model model  ) throws Exception
    {
        if ( smsConfiguration == null )
        {
            throw new  IllegalArgumentException();
        }

        smsConfigurationManager.updateSmsConfiguration( smsConfiguration );
        return getSmsConfiguration( model );
    }


    @ExceptionHandler
    public void mapException(IllegalArgumentException exception, HttpServletResponse response ) throws IOException
    {
        log.info( "Exception", exception );
        response.setStatus( HttpServletResponse.SC_CONFLICT );
        response.setContentType( "text/plain" );
        response.getWriter().write( exception.getMessage() );
    }
}

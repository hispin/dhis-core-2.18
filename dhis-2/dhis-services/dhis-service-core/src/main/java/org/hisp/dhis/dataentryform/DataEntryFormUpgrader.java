package org.hisp.dhis.dataentryform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * Upgrades the format of the input field identifiers from the legacy "value[12].value:value[34].value"
 * to the new "12-34-val"
 */
public class DataEntryFormUpgrader
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( DataEntryFormUpgrader.class );
    
    private final static String ID_EXPRESSION = "id=\"value\\[(\\d+)\\]\\.value:value\\[(\\d+)\\]\\.value\"";
    private final static Pattern ID_PATTERN = Pattern.compile( ID_EXPRESSION );
    
    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }
    
    @Transactional
    @Override
    public void execute()
    {
        int i = 0;
        
        for ( DataEntryForm form : dataEntryFormService.getAllDataEntryForms() )
        {
            Matcher matcher = ID_PATTERN.matcher( form.getHtmlCode() ); 

            StringBuffer out = new StringBuffer();
            
            while ( matcher.find() )
            {
                String upgradedId = "id=\"" + matcher.group(1) + "-" + matcher.group(2) + "-val\"";
                
                matcher.appendReplacement( out, upgradedId );
                
                i++;
            }

            matcher.appendTail( out );
                        
            form.setHtmlCode( out.toString() );
            form.setHtmlCode( form.getHtmlCode().replaceAll( "view=\"@@deshortname@@\"", "" ) );
            
            dataEntryFormService.updateDataEntryForm( form );
        }
        
        log.info( "Upgraded custom data entry form identifiers: " + i );
    }
}

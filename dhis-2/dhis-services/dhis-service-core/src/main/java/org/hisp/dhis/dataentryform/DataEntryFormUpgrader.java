package org.hisp.dhis.dataentryform;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.system.startup.AbstractStartupRoutine;
import org.springframework.transaction.annotation.Transactional;

/**
 * Upgrades the format of the input field identifiers from the legacy
 * "value[12].value:value[34].value" to the new "12-34-val"
 */
public class DataEntryFormUpgrader
    extends AbstractStartupRoutine
{
    private static final Log log = LogFactory.getLog( DataEntryFormUpgrader.class );

    private final static String ID_EXPRESSION = "id=\"value\\[(\\d+)\\]\\.value:value\\[(\\d+)\\]\\.value\"";

    private final static Pattern ID_PATTERN = Pattern.compile( ID_EXPRESSION );

    private final Pattern SELECT_PATTERN = Pattern.compile( "(<select.*?)[/]?</select>", Pattern.DOTALL );

    private final Pattern ID_PROGRAM_ENTRY_TEXTBOX = Pattern
        .compile( "id=\"value\\[(\\d+)\\].value:value\\[(\\d+)\\].value:value\\[(\\d+)\\].value\"" );

    private final Pattern ID_PROGRAM_ENTRY_OPTION = Pattern
        .compile( "id=\"value\\[(\\d+)\\].(combo|boolean){1}:value\\[(\\d+)\\].(combo|boolean){1}\"" );

    private final Pattern ID_PROGRAM_ENTRY_DATE = Pattern
        .compile( "id=\"value\\[(\\d+)\\].date:value\\[(\\d+)\\].date\"" );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }
    
    // -------------------------------------------------------------------------
    // Implementation method
    // -------------------------------------------------------------------------

    @Transactional
    @Override
    public void execute()
    {
        Collection<DataEntryForm> dataEntryForms = dataEntryFormService.getAllDataEntryForms();

        Collection<DataEntryForm> programDataEntryForms = dataEntryFormService.getProgramDataEntryForms();

        dataEntryForms.removeAll( programDataEntryForms );

        upgradeDataEntryForm( dataEntryForms );

        upgradeProgramDataEntryForm( programDataEntryForms );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void upgradeDataEntryForm( Collection<DataEntryForm> dataEntryForms )
    {
        int i = 0;

        for ( DataEntryForm form : dataEntryFormService.getAllDataEntryForms() )
        {
            Matcher matcher = ID_PATTERN.matcher( form.getHtmlCode() );

            StringBuffer out = new StringBuffer();

            while ( matcher.find() )
            {
                String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-val\"";

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

    private void upgradeProgramDataEntryForm( Collection<DataEntryForm> programDataEntryForms )
    {
        int i = 0;

        for ( DataEntryForm programDataEntryForm : programDataEntryForms )
        {
            String customForm = upgradeProgramDataEntryFormForTextBox( programDataEntryForm.getHtmlCode() );

            customForm = upgradeProgramDataEntryFormForDate( customForm );

            customForm = upgradeProgramDataEntryFormForOption( customForm );
            
            programDataEntryForm.setHtmlCode( customForm );

            dataEntryFormService.updateDataEntryForm( programDataEntryForm );

            i++;
        }

        log.info( "Upgraded custom case entry form identifiers: " + i );
    }

    private String upgradeProgramDataEntryFormForTextBox( String htmlCode )
    {
        int i = 0;

        Matcher matcher = ID_PROGRAM_ENTRY_TEXTBOX.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-" + matcher.group( 3 )
                + "-val\"";

            matcher.appendReplacement( out, upgradedId );

            i++;
        }

        matcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }

    private String upgradeProgramDataEntryFormForOption( String htmlCode )
    {
        StringBuffer out = new StringBuffer();
        Matcher inputMatcher = SELECT_PATTERN.matcher( htmlCode );
        
        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher matcher = ID_PROGRAM_ENTRY_OPTION.matcher( inputHtml );

            if ( matcher.find() )
            {
                String upgradedId = matcher.group( 1 ) + "-" + matcher.group( 3 ) + "-val";
                
                inputHtml = "<input name=\"entryselect\" id=\"" + upgradedId + "\" >";
            }

            inputMatcher.appendReplacement( out, inputHtml );
        }

        inputMatcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }

    private String upgradeProgramDataEntryFormForDate( String htmlCode )
    {
        Matcher matcher = ID_PROGRAM_ENTRY_DATE.matcher( htmlCode );

        StringBuffer out = new StringBuffer();

        while ( matcher.find() )
        {
            String upgradedId = "id=\"" + matcher.group( 1 ) + "-" + matcher.group( 2 ) + "-val\" ";

            matcher.appendReplacement( out, upgradedId );
        }

        matcher.appendTail( out );

        return out.toString().replaceAll( "view=\"@@deshortname@@\"", "" );
    }
}
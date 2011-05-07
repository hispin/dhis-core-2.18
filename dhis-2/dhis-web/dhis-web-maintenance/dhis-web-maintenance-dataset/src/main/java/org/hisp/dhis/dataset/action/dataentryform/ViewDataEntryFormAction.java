package org.hisp.dhis.dataset.action.dataentryform;

/*
 * Copyright (c) 2004-2010, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.comparator.DataElementOperandNameComparator;
import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.user.UserSettingService;

import com.opensymphony.xwork2.Action;

/**
 * @author Bharath Kumar
 * @version $Id$
 */
public class ViewDataEntryFormAction
    implements Action
{
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );
    private static final Pattern VIEWBY_PATTERN = Pattern.compile( "view=\"@@(.*)@@\"" );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    private int dataSetId;

    public void setDataSetId( int dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private DataEntryForm dataEntryForm;

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }

    private DataSet dataSet;

    public DataSet getDataSet()
    {
        return dataSet;
    }

    private boolean autoSave;

    public boolean getAutoSave()
    {
        return autoSave;
    }

    public List<DataElementOperand> operands;

    public List<DataElementOperand> getOperands()
    {
        return operands;
    }

    private String dataEntryValue;

    public String getDataEntryValue()
    {
        return dataEntryValue;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        dataSet = dataSetService.getDataSet( dataSetId );

        dataEntryForm = dataSet.getDataEntryForm();

        if ( dataEntryForm != null )
        {
            dataEntryValue = prepareDataEntryFormCode( dataEntryForm.getHtmlCode() );
        }
        else
        {
            dataEntryValue = "";
        }

        autoSave = (Boolean) userSettingService.getUserSetting( UserSettingService.AUTO_SAVE_DATA_ENTRY_FORM, false );

        operands = new ArrayList<DataElementOperand>( dataElementCategoryService.getFullOperands( dataSet
            .getDataElements() ) );

        Collections.sort( operands, new DataElementOperandNameComparator() );

        return SUCCESS;
    }

    /**
     * Prepares the data entry form code by injecting the dataElement name for
     * each entry field
     * 
     * @param dataEntryFormCode HTML code of the data entry form (as persisted
     *        in the database)
     * @return HTML code for the data entry form injected with dataelement name
     */
    private String prepareDataEntryFormCode( String dataEntryFormCode )
    {
        dataEntryFormCode = prepareDataEntryFormInputs( dataEntryFormCode );
        dataEntryFormCode = prepareDataEntryFormCombos( dataEntryFormCode );

        return dataEntryFormCode;
    }

    private String prepareDataEntryFormInputs( String preparedCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code.
        // ---------------------------------------------------------------------

        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( preparedCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------

        while ( matDataElement.find() )
        {
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------

            String dataElementCode = matDataElement.group( 1 );

            // -----------------------------------------------------------------
            // Pattern to extract data element ID from data element field
            // -----------------------------------------------------------------

            Matcher dataElementMatcher = IDENTIFIER_PATTERN.matcher( dataElementCode );

            Matcher viewByMatcher = VIEWBY_PATTERN.matcher( dataElementCode );

            if ( dataElementMatcher.find() && dataElementMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element id,name, optionCombo id,name of data element
                // -------------------------------------------------------------

                int dataElementId = Integer.parseInt( dataElementMatcher.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                int optionComboId = Integer.parseInt( dataElementMatcher.group( 2 ) );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( optionComboId );
                String optionComboName = optionCombo != null ? optionCombo.getName() : "";

                // -------------------------------------------------------------
                // Insert name of data element in output code
                // -------------------------------------------------------------

                String displayValue = "No Such DataElement Exists";

                if ( dataElement != null )
                {
                    displayValue = dataElement.getShortName();

                    if ( viewByMatcher.find() && viewByMatcher.groupCount() > 0 )
                    {
                        String viewByVal = viewByMatcher.group( 1 );
                        
                        if ( viewByVal.equalsIgnoreCase( "deid" ) )
                        {
                            displayValue = String.valueOf( dataElement.getId() );
                        }
                        else if ( viewByVal.equalsIgnoreCase( "dename" ) )
                        {
                            displayValue = dataElement.getName();
                        }
                    }

                    displayValue += " - " + optionComboName;

                    if ( dataElementCode.contains( "value=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"[ " + displayValue + " ]\"" );
                    }
                    else
                    {
                        dataElementCode += " value=\"[ " + displayValue + " ]\"";
                    }

                    StringBuilder title = new StringBuilder( "title=\"" ).append( dataElement.getId() ).append( " - " ).
                        append( dataElement.getName() ).append( " - " ).append( optionComboId ).append( " - " ).
                        append( optionComboName ).append( " - " ).append( dataElement.getType() ).append( "\"" );
                    
                    if ( dataElementCode.contains( "title=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "title=\"\"", title );
                    }
                    else
                    {
                        dataElementCode += " " + title;
                    }
                }
                else
                {
                    if ( dataElementCode.contains( "value=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"[ " + displayValue + " ]\"" );
                    }
                    else
                    {
                        dataElementCode += " value=\"[ " + displayValue + " ]\"";
                    }

                    if ( dataElementCode.contains( "title=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"" + displayValue + "\"" );
                    }
                    else
                    {
                        dataElementCode += " title=\"" + displayValue + "\"";
                    }
                }

                // -------------------------------------------------------------
                // Appends dataElementCode
                // -------------------------------------------------------------

                String appendCode = dataElementCode;
                appendCode += "/>";
                matDataElement.appendReplacement( sb, appendCode );
            }
        }

        // ---------------------------------------------------------------------
        // Add remaining code (after the last match), and return formatted code
        // ---------------------------------------------------------------------

        matDataElement.appendTail( sb );

        return sb.toString();
    }

    private String prepareDataEntryFormCombos( String preparedCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( preparedCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        while ( matDataElement.find() )
        {
            // -----------------------------------------------------------------
            // Get input HTML code
            // -----------------------------------------------------------------

            String dataElementCode = matDataElement.group( 1 );

            // -----------------------------------------------------------------
            // Pattern to extract data element ID from data element field
            // -----------------------------------------------------------------

            Matcher dataElementMatcher = IDENTIFIER_PATTERN.matcher( dataElementCode );

            Matcher viewByMatcher = VIEWBY_PATTERN.matcher( dataElementCode );

            if ( dataElementMatcher.find() && dataElementMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element id,name, optionCombo id,name of data element
                // -------------------------------------------------------------

                int dataElementId = Integer.parseInt( dataElementMatcher.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                int optionComboId = Integer.parseInt( dataElementMatcher.group( 2 ) );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( optionComboId );
                String optionComboName = optionCombo != null ? optionCombo.getName() : "";
                
                // -------------------------------------------------------------
                // Insert name of data element in output code.
                // -------------------------------------------------------------

                String dispVal = "No Such DataElement Exists";

                if ( dataElement != null )
                {
                    dispVal = dataElement.getShortName();

                    if ( viewByMatcher.find() && viewByMatcher.groupCount() > 0 )
                    {
                        String viewByVal = viewByMatcher.group( 1 );

                        if ( viewByVal.equalsIgnoreCase( "deid" ) )
                        {
                            dispVal = String.valueOf( dataElement.getId() );
                        }
                        else if ( viewByVal.equalsIgnoreCase( "dename" ) )
                        {
                            dispVal = dataElement.getName();
                        }
                    }

                    dispVal += " - " + optionComboName;

                    if ( dataElementCode.contains( "value=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"[ " + dispVal + " ]\"" );
                    }
                    else
                    {
                        dataElementCode += " value=\"[ " + dispVal + " ]\"";
                    }

                    StringBuilder title = new StringBuilder( "title=\"" ).append( dataElement.getId() ).append( " - " ).
                        append( dataElement.getName() ).append( " - " ).append( optionComboId ).append( " - " ).
                        append( optionComboName ).append( " - " ).append( dataElement.getType() ).append( "\"" );
                    
                    if ( dataElementCode.contains( "title=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "title=\"\"", title );
                    }
                    else
                    {
                        dataElementCode += " " + title;
                    }
                }
                else
                {
                    if ( dataElementCode.contains( "value=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"[ " + dispVal + " ]\"" );
                    }
                    else
                    {
                        dataElementCode += " value=\"[ " + dispVal + " ]\"";
                    }

                    if ( dataElementCode.contains( "title=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"" + dispVal + "\"" );
                    }
                    else
                    {
                        dataElementCode += " title=\"" + dispVal + "\"";
                    }
                }

                // -------------------------------------------------------------
                // Appends dataElementCode
                // -------------------------------------------------------------

                String appendCode = dataElementCode;
                appendCode += "/>";
                matDataElement.appendReplacement( sb, appendCode );
            }
        }

        // ---------------------------------------------------------------------
        // Add remaining code (after the last match), and return formatted code
        // ---------------------------------------------------------------------

        matDataElement.appendTail( sb );

        return sb.toString();
    }
}

package org.hisp.dhis.dataentryform;

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

import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;
import static org.hisp.dhis.datavalue.DataValue.FALSE;
import static org.hisp.dhis.datavalue.DataValue.TRUE;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Bharath Kumar
 * @version $Id$
 */
@Transactional
public class DefaultDataEntryFormService
    implements DataEntryFormService
{
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );
    private static final Pattern DATAELEMENT_PATTERN = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
    private static final String EMPTY = "";
    
    // ------------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------------

    private DataEntryFormStore dataEntryFormStore;

    public void setDataEntryFormStore( DataEntryFormStore dataEntryFormStore )
    {
        this.dataEntryFormStore = dataEntryFormStore;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // ------------------------------------------------------------------------
    // Implemented Methods
    // ------------------------------------------------------------------------

    public int addDataEntryForm( DataEntryForm dataEntryForm )
    {
        return dataEntryFormStore.addDataEntryForm( dataEntryForm );
    }

    public void updateDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.updateDataEntryForm( dataEntryForm );
    }

    public void deleteDataEntryForm( DataEntryForm dataEntryForm )
    {
        dataEntryFormStore.deleteDataEntryForm( dataEntryForm );
    }

    public DataEntryForm getDataEntryForm( int id )
    {
        return dataEntryFormStore.getDataEntryForm( id );
    }

    public DataEntryForm getDataEntryFormByName( String name )
    {
        return dataEntryFormStore.getDataEntryFormByName( name );
    }

    public Collection<DataEntryForm> getAllDataEntryForms()
    {
        return dataEntryFormStore.getAllDataEntryForms();
    }

    public String prepareDataEntryFormForSave( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code.
        // ---------------------------------------------------------------------

        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields.
        // ---------------------------------------------------------------------

        boolean result = matDataElement.find();

        while ( result )
        {
            // -----------------------------------------------------------------
            // Get input HTML code (HTML input field code).
            // -----------------------------------------------------------------

            String dataElementCode = matDataElement.group( 1 );

            // -----------------------------------------------------------------
            // Pattern to extract data element name from data element field
            // -----------------------------------------------------------------

            Pattern patDataElementName = Pattern.compile( "value=\"\\[ (.*) \\]\"" );
            Matcher matDataElementName = patDataElementName.matcher( dataElementCode );

            Pattern patTitle = Pattern.compile( "title=\"-- (.*) --\"" );
            Matcher matTitle = patTitle.matcher( dataElementCode );

            if ( matDataElementName.find() && matDataElementName.groupCount() > 0 )
            {
                String temp = "[ " + matDataElementName.group( 1 ) + " ]";
                dataElementCode = dataElementCode.replace( temp, "" );

                if ( matTitle.find() && matTitle.groupCount() > 0 )
                {
                    temp = "-- " + matTitle.group( 1 ) + " --";
                    dataElementCode = dataElementCode.replace( temp, "" );
                }

                // -------------------------------------------------------------
                // Appends dataElementCode
                // -------------------------------------------------------------

                String appendCode = dataElementCode;
                appendCode += "/>";
                matDataElement.appendReplacement( sb, appendCode );
            }

            // -----------------------------------------------------------------
            // Go to next data entry field
            // -----------------------------------------------------------------

            result = matDataElement.find();
        }

        // -----------------------------------------------------------------
        // Add remaining code (after the last match), and return formatted code.
        // -----------------------------------------------------------------

        matDataElement.appendTail( sb );

        return sb.toString();
    }

    public String prepareDataEntryFormForEdit( String htmlCode )
    {
        // ---------------------------------------------------------------------
        // Buffer to contain the final result.
        // ---------------------------------------------------------------------

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code.
        // ---------------------------------------------------------------------

        Pattern patDataElement = Pattern.compile( "(<input.*?)[/]?>" );
        Matcher matDataElement = patDataElement.matcher( htmlCode );

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

            if ( dataElementMatcher.find() && dataElementMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element id,name, optionCombo id,name of data element
                // -------------------------------------------------------------

                int dataElementId = Integer.parseInt( dataElementMatcher.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                int optionComboId = Integer.parseInt( dataElementMatcher.group( 2 ) );
                DataElementCategoryOptionCombo optionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );
                String optionComboName = optionCombo != null ? optionCombo.getName() : "";

                // -------------------------------------------------------------
                // Insert name of data element in output code
                // -------------------------------------------------------------

                String displayValue = "Data element does not exist";

                if ( dataElement != null )
                {
                    displayValue = dataElement.getShortName() + " " + optionComboName;

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

    public String prepareDataEntryFormForEntry( String htmlCode,
        Collection<DataValue> dataValues, Map<String, MinMaxDataElement> minMaxMap, String disabled, I18n i18n, DataSet dataSet )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------
        
        int i = 1;
        final String jsCodeForInputFields = " name=\"entryfield\" $DISABLED onchange=\"saveValue( $DATAELEMENTID, $OPTIONCOMBOID, '$DATAELEMENTNAME' )\" style=\"text-align:center\" onkeyup=\"return keyPress(event, this)\" ";
        final String jsCodeForSelectLists = " name=\"entryfield\" $DISABLED onchange=\"saveBoolean( $DATAELEMENTID, $OPTIONCOMBOID, this )\" onkeyup=\"return keyPress(event, this)\" >";
        final String historyCode = " ondblclick='javascript:viewHistory( $DATAELEMENTID, $OPTIONCOMBOID, true )' ";
        
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>"
            + "<div id=\"value[$DATAELEMENTID:$OPTIONCOMBOID].min\" style=\"display:none\">$MIN</div>"
            + "<div id=\"value[$DATAELEMENTID:$OPTIONCOMBOID].max\" style=\"display:none\">$MAX</div>";

        StringBuffer sb = new StringBuffer();

        Matcher dataElementMatcher = DATAELEMENT_PATTERN.matcher( htmlCode );

        // ---------------------------------------------------------------------
        // Iterate through all matching data element fields
        // ---------------------------------------------------------------------

        Map<Integer, DataElement> dataElementMap = getDataElementMap( dataSet );

        while ( dataElementMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String dataElementCode = dataElementMatcher.group( 1 );

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( dataElementCode );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                // -------------------------------------------------------------
                // Get data element ID of data element
                // -------------------------------------------------------------

                int dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                int optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );

                DataElement dataElement = dataElementMap.get( dataElementId ); 

                if ( dataElement == null )
                {
                    return "Data Element Id :" + dataElementId + " not found in this data set";
                }

                // -------------------------------------------------------------
                // Find value type of data element
                // -------------------------------------------------------------

                String dataElementValueType = dataElement.getType();

                // -------------------------------------------------------------
                // Find existing value of data element in data set
                // -------------------------------------------------------------

                String dataElementValue = getValue( dataValues, dataElementId, optionComboId );

                // -------------------------------------------------------------
                // Insert value of data element in output code
                // -------------------------------------------------------------

                if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
                {
                    dataElementCode = dataElementCode.replace( "input", "select" );
                    dataElementCode = dataElementCode.replaceAll( "value=\".*?\"", "" );
                }
                else
                {
                    if ( dataElementCode.contains( "value=\"\"" ) )
                    {
                        dataElementCode = dataElementCode.replace( "value=\"\"", "value=\"" + dataElementValue + "\"" );
                    }
                    else
                    {
                        dataElementCode += "value=\"" + dataElementValue + "\"";
                    }
                }

                // -------------------------------------------------------------
                // Min-max values
                // -------------------------------------------------------------

                MinMaxDataElement minMaxDataElement = minMaxMap.get( dataElement.getId() + ":" + optionComboId );
                String minValue = "No Min";
                String maxValue = "No Max";

                if ( minMaxDataElement != null )
                {
                    minValue = String.valueOf( minMaxDataElement.getMin() );
                    maxValue = String.valueOf( minMaxDataElement.getMax() );
                }

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" ); // For backwards compatibility

                // -------------------------------------------------------------
                // Insert title info
                // -------------------------------------------------------------

                StringBuilder title = new StringBuilder( "title=\"Name: " ).append( dataElement.getShortName() ).
                    append( " Type: " ).append( dataElement.getType() ).append( " Min: " ).append( minValue ).
                    append( " Max: " ).append( maxValue ).append( "\"" );
                
                if ( dataElementCode.contains( "title=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "title=\"\"", title );
                }
                else
                {
                    dataElementCode += " " + title;
                }

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                String appendCode = dataElementCode;

                if ( dataElement.getType().equals( VALUE_TYPE_BOOL ) )
                {
                    appendCode += jsCodeForSelectLists + "tabindex=\"" + i++ + "\"";

                    appendCode += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";

                    if ( dataElementValue.equals( TRUE ) )
                    {
                        appendCode += "<option value=\"true\" selected>" + i18n.getString( "yes" ) + "</option>";
                    }
                    else
                    {
                        appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                    }

                    if ( dataElementValue.equals( FALSE ) )
                    {
                        appendCode += "<option value=\"false\" selected>" + i18n.getString( "no" ) + "</option>";
                    }
                    else
                    {
                        appendCode += "<option value=\"false\">" + i18n.getString( "no" ) + "</option>";
                    }

                    appendCode += "</select>";
                }
                else
                {
                    appendCode += jsCodeForInputFields + "tabindex=\"" + i++ + "\"";

                    if ( dataElement.getType().equals( VALUE_TYPE_INT ) )
                    {
                        appendCode += historyCode;
                    }

                    appendCode += " />";
                }

                appendCode += metaDataCode;
                appendCode = appendCode.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                appendCode = appendCode.replace( "$DATAELEMENTNAME", dataElement.getName() );
                appendCode = appendCode.replace( "$DATAELEMENTTYPE", dataElementValueType );
                appendCode = appendCode.replace( "$OPTIONCOMBOID", String.valueOf( optionComboId ) );
                appendCode = appendCode.replace( "$DISABLED", disabled );

                if ( minMaxDataElement == null )
                {
                    appendCode = appendCode.replace( "$MIN", minValue );
                    appendCode = appendCode.replace( "$MAX", maxValue );
                }
                else
                {
                    appendCode = appendCode.replace( "$MIN", String.valueOf( minMaxDataElement.getMin() ) );
                    appendCode = appendCode.replace( "$MAX", String.valueOf( minMaxDataElement.getMax() ) );
                }

                dataElementMatcher.appendReplacement( sb, appendCode );
            }
        }

        dataElementMatcher.appendTail( sb );

        return sb.toString();
    }

    public Collection<DataEntryForm> listDisctinctDataEntryFormByProgramStageIds( List<Integer> programStageIds )
    {
        if ( programStageIds == null || programStageIds.isEmpty() )
        {
            return null;
        }

        return dataEntryFormStore.listDisctinctDataEntryFormByProgramStageIds( programStageIds );
    }

    public Collection<DataEntryForm> listDisctinctDataEntryFormByDataSetIds( List<Integer> dataSetIds )
    {
        if ( dataSetIds == null || dataSetIds.size() == 0 )
        {
            return null;
        }

        return dataEntryFormStore.listDisctinctDataEntryFormByDataSetIds( dataSetIds );
    }
    
    public Collection<DataEntryForm> getDataEntryForms( final Collection<Integer> identifiers ){
        
        Collection<DataEntryForm> dataEntryForms = getAllDataEntryForms();

        return identifiers == null ? dataEntryForms : FilterUtils.filter( dataEntryForms, new Filter<DataEntryForm>()
        {
            public boolean retain( DataEntryForm object )
            {
                return identifiers.contains( object.getId() );
            }
        } );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns the value of the DataValue in the Collection of DataValues with
     * the given data element identifier and category option combo id.
     */
    private String getValue( Collection<DataValue> dataValues, int dataElementId, int categoryOptionComboId )
    {
        for ( DataValue dataValue : dataValues )
        {
            if ( dataValue.getDataElement().getId() == dataElementId
                && dataValue.getOptionCombo().getId() == categoryOptionComboId )
            {
                return dataValue.getValue();
            }
        }

        return EMPTY;
    }

    /**
     * Returns a Map of all DataElements in the given DataSet where the key is
     * the DataElement identifier and the value is the DataElement.
     */
    private Map<Integer, DataElement> getDataElementMap( DataSet dataSet )
    {
        Map<Integer, DataElement> map = new HashMap<Integer, DataElement>();

        for ( DataElement element : dataSet.getDataElements() )
        {
            map.put( element.getId(), element );
        }

        return map;
    }
}

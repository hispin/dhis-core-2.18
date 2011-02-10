package org.hisp.dhis.de.screen;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.minmax.MinMaxDataElement;

/**
 * @author Abyot Asalefew
 * @version $Id$
 */
public class DefaultDataEntryScreenManager
    implements DataEntryScreenManager
{
    private static final Log log = LogFactory.getLog( DefaultDataEntryScreenManager.class );
    
    private static final String EMPTY = "";

    // -------------------------------------------------------------------------
    // DataEntryScreenManager implementation
    // -------------------------------------------------------------------------
    
    public String populateCustomDataEntryScreenForMultiDimensional( String dataEntryFormCode,
        Collection<DataValue> dataValues, Map<String, MinMaxDataElement> minMaxMap, String disabled, I18n i18n, DataSet dataSet )
    {
        // ---------------------------------------------------------------------
        // Inline Javascript to add to HTML before outputting
        // ---------------------------------------------------------------------
        int i = 1;
        final String jsCodeForInputs = " name=\"entryfield\" $DISABLED onchange=\"saveValue( $DATAELEMENTID, $OPTIONCOMBOID, '$DATAELEMENTNAME', $SAVEMODE )\" style=\"text-align:center\" onkeyup=\"return keyPress(event, this)\" ";
        final String jsCodeForCombos = " name=\"entryfield\" $DISABLED onchange=\"saveBoolean( $DATAELEMENTID, $OPTIONCOMBOID, this )\" onkeyup=\"return keyPress(event, this)\" >";
        final String historyCode = " ondblclick='javascript:viewHistory( $DATAELEMENTID, $OPTIONCOMBOID, true )' ";
        
        // ---------------------------------------------------------------------
        // Metadata code to add to HTML before outputting
        // ---------------------------------------------------------------------

        final String metaDataCode = "<span id=\"value[$DATAELEMENTID].name\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"value[$DATAELEMENTID].type\" style=\"display:none\">$DATAELEMENTTYPE</span>"
            + "<div id=\"value[$DATAELEMENTID:$OPTIONCOMBOID].min\" style=\"display:none\">$MIN</div>"
            + "<div id=\"value[$DATAELEMENTID:$OPTIONCOMBOID].max\" style=\"display:none\">$MAX</div>";

        StringBuffer sb = new StringBuffer();

        // ---------------------------------------------------------------------
        // Pattern to match data elements in the HTML code
        // ---------------------------------------------------------------------

        Pattern dataElementPattern = Pattern.compile( "(<input.*?)[/]?>", Pattern.DOTALL );
        Matcher dataElementMatcher = dataElementPattern.matcher( dataEntryFormCode );

        // ---------------------------------------------------------------------
        // Pattern to extract data element ID from data element field
        // ---------------------------------------------------------------------

        Pattern identifierPattern = Pattern.compile( "value\\[(.*)\\].value:value\\[(.*)\\].value" );

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

            Matcher identifierMatcher = identifierPattern.matcher( dataElementCode );

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
                    //throw new RuntimeException( "Data Element Id: " + dataElementId + " not found" );
                	
                	log.error( "Data Element Id: " + dataElementId + " not found in this data set" );
                    
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
                    dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );
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

                // -------------------------------------------------------------
                // Remove placeholder view attribute from input field
                // -------------------------------------------------------------

                dataElementCode = dataElementCode.replaceAll( "view=\".*?\"", "" );

                // -------------------------------------------------------------
                // Insert title information - Data element id, name, type, min,
                // max
                // -------------------------------------------------------------

                if ( dataElementCode.contains( "title=\"\"" ) )
                {
                    dataElementCode = dataElementCode.replace( "title=\"\"", "title=\"-- ID:" + dataElement.getId()
                        + " Name:" + dataElement.getShortName() + " Type:" + dataElement.getType() + " Min:" + minValue
                        + " Max:" + maxValue + " --\"" );
                }
                else
                {
                    dataElementCode += "title=\"-- ID:" + dataElement.getId() + " Name:" + dataElement.getShortName()
                        + " Type:" + dataElement.getType() + " Min:" + minValue + " Max:" + maxValue + " --\"";
                }

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------

                String appendCode = dataElementCode;

                if ( dataElement.getType().equals( VALUE_TYPE_BOOL ) )
                {
                    appendCode += jsCodeForCombos + "tabindex=\"" + i++ + "\"";

                    appendCode += "<option value=\"\">" + i18n.getString( "no_value" ) + "</option>";

                    if ( dataElementValue.equalsIgnoreCase( "true" ) )
                    {
                        appendCode += "<option value=\"true\" selected>" + i18n.getString( "yes" ) + "</option>";
                    }
                    else
                    {
                        appendCode += "<option value=\"true\">" + i18n.getString( "yes" ) + "</option>";
                    }

                    if ( dataElementValue.equalsIgnoreCase( "false" ) )
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
                    appendCode += jsCodeForInputs + "tabindex=\"" + i++ + "\"";

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
                appendCode = appendCode.replace( "$SAVEMODE", "false" ); // TODO backwards compatibility, save mode removed
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

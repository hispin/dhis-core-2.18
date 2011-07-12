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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
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
    private static final String EMPTY_VALUE_TAG = "value=\"\"";
    private static final String EMPTY_TITLE_TAG = "title=\"\"";
    private static final String STYLE_TAG = "style=\"";
    private static final String TAG_CLOSE = "/>";
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
    
    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
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
        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Remove value and title tags from the HTML code
            // -----------------------------------------------------------------

            String dataElementCode = inputMatcher.group();
            
            Matcher valueTagMatcher = VALUE_TAG_PATTERN.matcher( dataElementCode );
            Matcher titleTagMatcher = TITLE_TAG_PATTERN.matcher( dataElementCode );

            if ( valueTagMatcher.find() && valueTagMatcher.groupCount() > 0 )
            {
                dataElementCode = dataElementCode.replace( valueTagMatcher.group( 1 ), EMPTY );
            }
            
            if ( titleTagMatcher.find() && valueTagMatcher.groupCount() > 0 )
            {
                dataElementCode = dataElementCode.replace( titleTagMatcher.group( 1 ), EMPTY );
            }

            inputMatcher.appendReplacement( sb, dataElementCode );
        }

        inputMatcher.appendTail( sb );

        return sb.toString();
    }

    public String prepareDataEntryFormForEdit( String htmlCode )
    {
        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        while ( inputMatcher.find() )
        {
            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );
            Matcher indicatorMatcher = INDICATOR_PATTERN.matcher( inputHtml );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                int dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                DataElement dataElement = dataElementService.getDataElement( dataElementId );

                int optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );
                DataElementCategoryOptionCombo categegoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );
                String optionComboName = categegoryOptionCombo != null ? categegoryOptionCombo.getName() : "[ Category option combo does not exist ]";

                // -------------------------------------------------------------
                // Insert name of data element operand as value and title
                // -------------------------------------------------------------

                StringBuilder title = dataElement != null ? 
                    new StringBuilder( "title=\"" ).append( dataElement.getId() ).append( " - " ).
                    append( dataElement.getName() ).append( " - " ).append( optionComboId ).append( " - " ).
                    append( optionComboName ).append( " - " ).append( dataElement.getType() ).append( "\"" ) : new StringBuilder();
                
                String displayValue = dataElement != null ? "value=\"[ " + dataElement.getName() + " " + optionComboName + " ]\"" : "[ Data element does not exist ]";
                String displayTitle = dataElement != null ? title.toString() : "[ Data element does not exist ]";
                
                inputHtml = inputHtml.contains( EMPTY_VALUE_TAG ) ? inputHtml.replace( EMPTY_VALUE_TAG, displayValue ) : inputHtml + " " + displayValue;                    
                inputHtml = inputHtml.contains( EMPTY_TITLE_TAG ) ? inputHtml.replace( EMPTY_TITLE_TAG, displayTitle ) : " " + displayTitle;

                inputMatcher.appendReplacement( sb, inputHtml );
            }
            else if ( indicatorMatcher.find() && indicatorMatcher.groupCount() > 0 )
            {
                int indicatorId = Integer.parseInt( indicatorMatcher.group( 1 ) );
                Indicator indicator = indicatorService.getIndicator( indicatorId );

                // -------------------------------------------------------------
                // Insert name of indicator as value and title
                // -------------------------------------------------------------

                String displayValue = indicator != null ? "value=\"[ " + indicator.getName() + " ]\"" : "[ Indicator does not exist ]";
                String displayTitle = indicator != null ? "title=\"" + indicator.getName() + "\"" : "[ Indicator does not exist ]";

                inputHtml = inputHtml.contains( EMPTY_VALUE_TAG ) ? inputHtml.replace( EMPTY_VALUE_TAG, displayValue ) : inputHtml + " " + displayValue;                    
                inputHtml = inputHtml.contains( EMPTY_TITLE_TAG ) ? inputHtml.replace( EMPTY_TITLE_TAG, displayTitle ) : " " + displayTitle;

                inputMatcher.appendReplacement( sb, inputHtml );                
            }
        }

        inputMatcher.appendTail( sb );

        return sb.toString();
    }

    public String prepareDataEntryFormForEntry( String htmlCode,
        Collection<DataValue> dataValues, Map<String, MinMaxDataElement> minMaxMap, String disabled, I18n i18n, DataSet dataSet )
    {
        // ---------------------------------------------------------------------
        // Inline javascript/html to add to HTML before output
        // ---------------------------------------------------------------------
        
        int i = 1;
        
        final String jsCodeForInputFields = " name=\"entryfield\" $DISABLED onchange=\"saveVal( $DATAELEMENTID, $OPTIONCOMBOID )\" style=\"text-align:center\" onkeyup=\"return keyPress(event, this)\" ";
        final String jsCodeForSelectLists = " name=\"entryfield\" $DISABLED onchange=\"saveBoolean( $DATAELEMENTID, $OPTIONCOMBOID )\" onkeyup=\"return keyPress(event, this)\" ";
        
        final String historyCode = " ondblclick='javascript:viewHist( $DATAELEMENTID, $OPTIONCOMBOID )' ";
        
        final String metaDataCode = "<span id=\"$DATAELEMENTID-dataelement\" style=\"display:none\">$DATAELEMENTNAME</span>"
            + "<span id=\"$DATAELEMENTID-type\" style=\"display:none\">$DATAELEMENTTYPE</span>"
            + "<div id=\"$DATAELEMENTID-$OPTIONCOMBOID-min\" style=\"display:none\">$MIN</div>"
            + "<div id=\"$DATAELEMENTID-$OPTIONCOMBOID-max\" style=\"display:none\">$MAX</div>";

        StringBuffer sb = new StringBuffer();

        Matcher inputMatcher = INPUT_PATTERN.matcher( htmlCode );

        Map<Integer, DataElement> dataElementMap = getDataElementMap( dataSet );

        while ( inputMatcher.find() )
        {
            // -----------------------------------------------------------------
            // Get HTML input field code
            // -----------------------------------------------------------------

            String inputHtml = inputMatcher.group();

            Matcher identifierMatcher = IDENTIFIER_PATTERN.matcher( inputHtml );

            if ( identifierMatcher.find() && identifierMatcher.groupCount() > 0 )
            {
                int dataElementId = Integer.parseInt( identifierMatcher.group( 1 ) );
                int optionComboId = Integer.parseInt( identifierMatcher.group( 2 ) );

                DataElement dataElement = dataElementMap.get( dataElementId ); 

                if ( dataElement == null )
                {
                    return "Data element with id : " + dataElementId + " does not exist";
                }
                
                DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDataElementCategoryOptionCombo( optionComboId );
                
                if ( categoryOptionCombo == null )
                {
                    return "Category option combo with id: " + optionComboId + " does not exist";
                }

                String dataElementValueType = dataElement.getDetailedNumberType();

                String dataElementValue = getValue( dataValues, dataElementId, optionComboId );

                // -------------------------------------------------------------
                // Insert data value for data element in output code
                // -------------------------------------------------------------

                if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
                {
                    inputHtml = inputHtml.replace( "input", "select" );
                    inputHtml = inputHtml.replaceAll( "value=\".*?\"", "" );
                }
                else
                {
                    if ( inputHtml.contains( EMPTY_VALUE_TAG ) )
                    {
                        inputHtml = inputHtml.replace( EMPTY_VALUE_TAG, "value=\"" + dataElementValue + "\"" );
                    }
                    else
                    {
                        inputHtml += "value=\"" + dataElementValue + "\"";
                    }
                }

                // -------------------------------------------------------------
                // Insert title info
                // -------------------------------------------------------------

                MinMaxDataElement minMaxDataElement = minMaxMap.get( dataElement.getId() + ":" + optionComboId );
                String minValue = minMaxDataElement != null ? String.valueOf( minMaxDataElement.getMin() ) : "-";
                String maxValue = minMaxDataElement != null ? String.valueOf( minMaxDataElement.getMax() ) : "-";

                StringBuilder title = new StringBuilder( "title=\"Name: " ).append( dataElement.getName() ).append( " " ).
                    append( categoryOptionCombo.getName() ).append( " Type: " ).append( dataElement.getType() ).
                    append( " Min: " ).append( minValue ).append( " Max: " ).append( maxValue ).append( "\"" );
                
                inputHtml = inputHtml.contains( EMPTY_TITLE_TAG ) ? inputHtml.replace( EMPTY_TITLE_TAG, title ) : inputHtml + " " + title;

                // -------------------------------------------------------------
                // Append Javascript code and meta data (type/min/max) for
                // persisting to output code, and insert value and type for
                // fields
                // -------------------------------------------------------------
                
                String backgroundColor = "style=\"";
                
                String appendCode = "";

                if ( dataElement.getType().equals( VALUE_TYPE_BOOL ) )
                {
                    appendCode += jsCodeForSelectLists + "tabindex=\"" + i++ + "\">";

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
                        
                        if ( minMaxDataElement != null && !dataElementValue.equals( EMPTY ) )
                        {
                            double value = Double.parseDouble( dataElementValue );
                            
                            if ( value < minMaxDataElement.getMin() || value > minMaxDataElement.getMax() )
                            {
                                backgroundColor = "style=\"background-color:#ff6600;";
                            }
                        }
                    }

                    appendCode += TAG_CLOSE;
                }

                inputHtml = inputHtml.replace( TAG_CLOSE, appendCode );
                
                inputHtml += metaDataCode;
                inputHtml = inputHtml.replace( "$DATAELEMENTID", String.valueOf( dataElementId ) );
                inputHtml = inputHtml.replace( "$DATAELEMENTNAME", dataElement.getName() );
                inputHtml = inputHtml.replace( "$DATAELEMENTTYPE", dataElementValueType );
                inputHtml = inputHtml.replace( "$OPTIONCOMBOID", String.valueOf( optionComboId ) );
                inputHtml = inputHtml.replace( "$DISABLED", disabled );
                inputHtml = inputHtml.replace( STYLE_TAG, backgroundColor );

                if ( minMaxDataElement == null )
                {
                    inputHtml = inputHtml.replace( "$MIN", minValue );
                    inputHtml = inputHtml.replace( "$MAX", maxValue );
                }
                else
                {
                    inputHtml = inputHtml.replace( "$MIN", String.valueOf( minMaxDataElement.getMin() ) );
                    inputHtml = inputHtml.replace( "$MAX", String.valueOf( minMaxDataElement.getMax() ) );
                }

                inputMatcher.appendReplacement( sb, inputHtml );
            }
        }

        inputMatcher.appendTail( sb );

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
    
    public Collection<DataEntryForm> getDataEntryForms( final Collection<Integer> identifiers )
    {        
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

package org.hisp.dhis.api.utils;

/*
* Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.api.webdomain.form.Field;
import org.hisp.dhis.api.webdomain.form.Form;
import org.hisp.dhis.api.webdomain.form.InputType;
import org.hisp.dhis.api.webdomain.form.Section;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FormUtils
{
    public static Form fromDataSet( DataSet dataSet )
    {
        Form form = new Form();
        form.setName( dataSet.getName() );
        form.setPeriod( dataSet.getPeriodType().getIsoFormat() );

        if ( dataSet.getSections().size() > 0 )
        {
            for ( org.hisp.dhis.dataset.Section section : dataSet.getSections() )
            {
                Section s = new Section();
                s.setName( section.getName() );
                s.setFields( inputsFromDataElements( section.getDataElements() ) );
                form.getSections().add( s );
            }
        }
        else
        {
            Section s = new Section();
            s.setName( "default" );
            s.setFields( inputsFromDataElements( dataSet.getDataElements() ) );

            form.getSections().add( s );
        }

        return form;
    }

    private static List<Field> inputsFromDataElements( Collection<DataElement> dataElements )
    {
        List<Field> fields = new ArrayList<Field>();

        for ( DataElement dataElement : dataElements )
        {
            if ( dataElement.getCategoryCombo().isDefault() )
            {
                Field field = new Field();

                field.setName( dataElement.getName() );
                field.setDataElement( dataElement.getUid() );
                field.setCategoryOptionCombo( dataElement.getCategoryCombo().getSortedOptionCombos().get( 0 ).getUid() );
                field.setType( inputTypeFromDataElement( dataElement ) );
                field.setValue( "" );

                fields.add( field );
            }
            else
            {
                for ( DataElementCategoryOptionCombo categoryOptionCombo : dataElement.getCategoryCombo().getSortedOptionCombos() )
                {
                    Field field = new Field();

                    field.setName( dataElement.getName() + " " + categoryOptionCombo.getName() );
                    field.setDataElement( dataElement.getUid() );
                    field.setCategoryOptionCombo( categoryOptionCombo.getUid() );
                    field.setType( inputTypeFromDataElement( dataElement ) );
                    field.setValue( "" );

                    fields.add( field );
                }
            }
        }

        return fields;
    }

    private static InputType inputTypeFromDataElement( DataElement dataElement )
    {
        if ( DataElement.VALUE_TYPE_STRING.equals( dataElement.getType() ) )
        {
            if ( DataElement.VALUE_TYPE_TEXT.equals( dataElement.getTextType() ) )
            {
                return InputType.TEXT;
            }
            /*
            else if ( dataElement.getTextType().equalsIgnoreCase( "longtext" ) )
            {
                return InputType.TEXT_LONG;
            }
            */
        }
        else if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getType() ) )
        {
            if ( DataElement.VALUE_TYPE_NUMBER.equals( dataElement.getNumberType() ) )
            {
                return InputType.NUMBER;
            }
            else if ( DataElement.VALUE_TYPE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER;
            }
            else if ( DataElement.VALUE_TYPE_POSITIVE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER_POSITIVE;
            }
            else if ( DataElement.VALUE_TYPE_NEGATIVE_INT.equals( dataElement.getNumberType() ) )
            {
                return InputType.INTEGER_NEGATIVE;
            }
        }
        else if ( DataElement.VALUE_TYPE_BOOL.equals( dataElement.getType() ) )
        {
            return InputType.BOOLEAN;
        }
        /*
        else if ( dataElement.getType().equalsIgnoreCase( "trueOnly" ) )
        {
            return InputType.TRUE_ONLY;
        }
        */
        else if ( DataElement.VALUE_TYPE_DATE.equals( dataElement.getType() ) )
        {
            return InputType.DATE;
        }

        return null;
    }
}

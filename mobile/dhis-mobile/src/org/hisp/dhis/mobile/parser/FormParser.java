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
package org.hisp.dhis.mobile.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.hisp.dhis.mobile.model.DataElement;
import org.hisp.dhis.mobile.model.ProgramStageForm;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FormParser
extends AbstractXmlParser

{
    public static final String FORM_TAG = "form";
    public static final String DATAELEMENTS_TAG = "des";

    Vector programStageDataElementsVctr = new Vector();

    public Object readInternal(KXmlParser parser) throws XmlPullParserException, IOException {
        ProgramStageForm form = new ProgramStageForm();
        // picking ID
        parser.nextTag();
        form.setId( Integer.parseInt( parser.nextText() ) );

        // picking name
        parser.nextTag();
        form.setName( parser.nextText() );

        // picking dataElements
        parser.nextTag();
        parser.require( XmlPullParser.START_TAG, null, DATAELEMENTS_TAG );
        while ( parser.nextTag() != XmlPullParser.END_TAG )
            parseDataElements( parser );

        form.setDataElements( programStageDataElementsVctr );
        return form;
    }


    private void parseDataElements( KXmlParser parser )
    throws IOException, XmlPullParserException
{

    DataElement de = new DataElement();

    parser.require( XmlPullParser.START_TAG, null, "de" );

    while ( parser.nextTag() != XmlPullParser.END_TAG )
    {
        parser.require( XmlPullParser.START_TAG, null, null );
        String name = parser.getName();
        String text = parser.nextText();

        if ( name.equals( "id" ) )
        {
            de.setId( Integer.valueOf( text ).intValue() );
        }
        else if ( name.equals( "name" ) )
        {
            de.setName( text );
        }
        else if ( name.equals( "type" ) )
        {
            if ( text.equals( "int" ) )
                de.setType( DataElement.TYPE_INT );
            else if ( text.equals( "date" ) )
                de.setType( DataElement.TYPE_DATE );
            else if ( text.equals( "bool" ) )
                de.setType( DataElement.TYPE_BOOL );
            else
                de.setType( DataElement.TYPE_STRING );
        }
    }

    programStageDataElementsVctr.addElement( de );
    parser.require( XmlPullParser.END_TAG, null, "de" );
}


    public String getTag()
    {
        return FORM_TAG;
    }

}

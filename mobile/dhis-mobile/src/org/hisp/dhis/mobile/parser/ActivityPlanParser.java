/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: * Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. * Neither the name of the HISP project nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.mobile.parser;

import java.io.IOException;
import java.util.Vector;

import org.hisp.dhis.mobile.model.Activity;
import org.hisp.dhis.mobile.model.Beneficiary;
import org.hisp.dhis.mobile.model.Task;
import org.hisp.dhis.mobile.util.StringUtil;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ActivityPlanParser
    extends AbstractXmlParser
{

    public static final String ACTIVITYPLAN_TAG = "activityPlan";

    private static final String ACTIVITY_TAG = "activity";
    private static final String BENEFICIARY_TAG = "beneficiary";

    public String getTag()
    {
        return ACTIVITYPLAN_TAG;
    }

    public Object readInternal( KXmlParser parser )
        throws Exception
    {
        Vector activities = new Vector();
        while ( parser.nextTag() != XmlPullParser.END_TAG )
        {
            activities.addElement( parseActivity( parser ) );

        }
        return activities;
    }

    private Activity parseActivity( KXmlParser parser )
        throws IOException, XmlPullParserException
    {
        Activity activity = new Activity();
        Task task = new Task();
        Beneficiary beneficiary = new Beneficiary();
        parser.require( XmlPullParser.START_TAG, null, ACTIVITY_TAG );
        while ( parser.nextTag() != XmlPullParser.END_TAG )
        {
            if ( parser.getName().equals( BENEFICIARY_TAG ) )
            {
                activity.setBeneficiary( beneficiary );

                beneficiary.setMiddleName( parser.getAttributeValue( 0 ) );
                beneficiary.setLastName( parser.getAttributeValue( 1 ) );
                beneficiary.setId( Integer.parseInt( parser.getAttributeValue( 2 ) ) );
                beneficiary.setFirstName( parser.getAttributeValue( 3 ) );
                parser.nextTag();

            }
            else if ( parser.getName().equals( "dueDate" ) )
            {

                String dateStr = parser.nextText();
                activity.setDueDate( StringUtil.getDateFromString( dateStr ) );

            }
            else if ( parser.getName().equals( "task" ) )
            {

                task.setProgStageId( Integer.parseInt( parser.getAttributeValue( 0 ) ) );
                task.setProgStageInstId( Integer.parseInt( parser.getAttributeValue( 1 ) ) );
                task.setComplete( parser.getAttributeValue( 2 ).equals( "true" ) ? true : false );
                activity.setTask( task );
                parser.nextTag();

            }
        }
        return activity;
    }

}

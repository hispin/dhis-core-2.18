/*
 * Copyright (c) 2004-2007, University of Oslo
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
package org.hisp.dhis.mobile.api;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.hisp.dhis.dataentryform.DataEntryFormService;
import org.hisp.dhis.dataset.DataSetService;

public class XmlCreatorService extends Thread
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    DataEntryFormService dataEntryFormService;

    public void setDataEntryFormService( DataEntryFormService dataEntryFormService )
    {
        this.dataEntryFormService = dataEntryFormService;
    }

    DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    String phoneNumber;

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    String sendTime;

    public void setSendTime( String sendTime )
    {
        this.sendTime = sendTime;
    }

    String info;

    public void setInfo( String info )
    {
        this.info = info;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    @Override
    public void run()
    {
        //System.out.println( "Info to convert to XML: " + info );
        String dhis2Home = System.getenv( "DHIS2_HOME" );
        String[] text = info.split( "#" );
        String msgVersion = text[0];
        text = text[1].split( "\\*" );
        String formID = text[0];
        text = text[1].split( "\\?" );
        String periodType = text[0];
        text = text[1].split( "\\$" );
        String period = text[0];
        String[] dataValues = text[1].split( "\\|" );

        try
        {
            Properties props = new Properties();
            props.load( new FileReader( dhis2Home + File.separator + "mi" + File.separator + "formIDLayout.csv" ) );
            String IdString = props.getProperty( formID );

            String[] elementIds = IdString.split( "\\," );

            File file = new File( dhis2Home + File.separator + "mi" + File.separator + "pending" + File.separator + phoneNumber + sendTime.replace( ":", "-" ) + ".xml" );
            FileWriter writer = new FileWriter( file );
            writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
            writer.write( "<mxf version=\"" + msgVersion + "\">\n" );
            writer.write( "<source>" + phoneNumber + "</source>\n" );
            writer.write( "<periodType>" + periodType + "</periodType>\n" );
            writer.write( "<period>" + period + "</period>\n" );
            writer.write( "<timeStamp>" + sendTime + "</timeStamp>\n" );
            for ( int i = 0; i < elementIds.length; i++ )
            {
                if ( ( dataValues.length - 1 ) < i || dataValues[i].isEmpty() )
                {
                } else
                {
                    writer.write( "<dataValue>\n" );
                    writer.write( "<dataElement>" + elementIds[i] + "</dataElement>\n" );
                    writer.write( "<value>" + dataValues[i] + "</value>\n" );
                    writer.write( "</dataValue>\n" );
                }

            }
            writer.write( "<info>" + info + "</info>\n" );
            writer.write( "</mxf>\n" );
            writer.close();
        } catch (Exception e)
        {
            System.out.println("Exception while creating XML File"+ e.getMessage());
            return;
        }
    }
}

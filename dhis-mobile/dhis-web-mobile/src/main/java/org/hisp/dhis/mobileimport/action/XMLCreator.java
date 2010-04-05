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

package org.hisp.dhis.mobileimport.action;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XMLCreator
{

    public void writeXML( String phoneNumber, String sendTime, String info )
    {
        String dhis2Home = System.getenv( "DHIS2_HOME" );
        String[] elementIds =
        {
            "3.1", "4.1", "5.1", "6.1", "7.1", "8.1", "9.1", "10.1", "12.1", "14.1", "15.1", "17.1", "16.1", "1251.1", "19.1", "20.1", "21.1", "25.1", "24.1", "23.1", "32.1", "33.1", "515.1", "517.1", "521.1", "40.1", "41.1", "1279.1", "59.1", "60.1", "61.1", "62.1", "63.1", "64.1", "65.1", "66.1", "67.1", "1139.7", "1139.8", "68.1", "71.1", "74.1", "77.1", "69.1", "70.1", "73.1", "76.1", "72.1", "75.1", "78.1", "79.1", "80.1", "81.1", "83.1", "82.1", "84.1", "85.1", "86.1", "87.1", "88.1", "89.1", "90.1", "1247.7", "91.1", "92.1", "93.1", "94.1", "96.1", "97.1", "98.1", "104.1", "105.1", "106.1", "162.1", "130.1", "134.1", "135.1"
        };

        String[] formData = info.split( "\\$" );
        String period = formData[0];
        String actualData = formData[1];
        String[] dataValues = actualData.split( "\\|", 78 );
        System.out.println( "Total datavalues = " + dataValues.length );

        for ( int i = 0; i < dataValues.length; i++ )
        {
            System.out.println( "Values = " + dataValues[i] );
        }
        if ( dataValues.length == 78 )
        {
            File file = new File( dhis2Home + "\\mi\\pending\\" + phoneNumber + sendTime.replace( ":", "-" ) + ".xml" );
            try
            {
                System.out.println( file.getAbsolutePath() );
                FileWriter writer = new FileWriter( file );
                writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
                writer.write( "<dxf>\n" );
                writer.write( "<source>" + phoneNumber + "</source>\n" );
                if ( period.length() == 1 )
                {
                    writer.write( "<period>2009-0" + period + "-01</period>\n" );
                } else
                {
                    if ( period.length() == 2 )
                    {
                        if ( Integer.parseInt( period ) > 12 && Integer.parseInt( period ) < 22 )
                        {
                            writer.write( "<period>2008-0" + ( Integer.parseInt( period ) - 12 ) + "-01</period>\n" );
                        } else
                        {
                            if ( Integer.parseInt( period ) >= 22 )
                            {
                                writer.write( "<period>2008-" + ( Integer.parseInt( period ) - 12 ) + "-01</period>\n" );
                            } else
                            {
                                writer.write( "<period>2009-" + period + "-01</period>\n" );
                            }
                        }
                    } else
                    {
                        writer.write( "<period>" + period + "</period>\n" );
                    }
                }
                writer.write( "<timeStamp>" + sendTime + "</timeStamp>\n" );
                for ( int i = 0; i < elementIds.length; i++ )
                {
                    if ( dataValues[i].isEmpty() )
                    {
                        continue;
                    }
                    writer.write( "<dataValue>\n" );
                    writer.write( "<dataElement>" + elementIds[i] + "</dataElement>\n" );
                    writer.write( "<value>" + dataValues[i] + "</value>\n" );
                    writer.write( "</dataValue>\n" );
                }
                writer.write( "<info>" + info + "</info>\n" );
                writer.write( "</dxf>\n" );
                writer.close();
            } catch ( IOException ex )
            {
                return;
            }
        }
    }
}

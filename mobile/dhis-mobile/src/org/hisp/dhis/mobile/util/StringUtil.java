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
package org.hisp.dhis.mobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

public class StringUtil
{

    public static String streamToString( InputStream is )
        throws IOException
    {
        InputStreamReader r = new InputStreamReader( is );
        char[] buffer = new char[32];
        StringBuffer sb = new StringBuffer();
        int count;

        while ( (count = r.read( buffer, 0, buffer.length )) > -1 )
        {
            sb.append( buffer, 0, count );
        }

        return sb.toString();
    }

    public static Date getDateFromString( String strDate )
    {
        Calendar cal = Calendar.getInstance();
        int day = Integer.parseInt( strDate.substring( 8, 10 ) );
        int month = Integer.parseInt( strDate.substring( 5, 7 ) );
        int year = Integer.parseInt( strDate.substring( 0, 4 ) );

        cal.set( Calendar.DATE, day );
        cal.set( Calendar.MONTH, month-1 );
        cal.set( Calendar.YEAR, year );
        return cal.getTime();
    }
    
    public static String getStringFromDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int d = c.get(Calendar.DATE);
        
        int m = c.get(Calendar.MONTH)+1;
        
        int y = c.get(Calendar.YEAR);
        
        return y+"-"+(m<10? "0": "")+m+"-"+(d<10? "0": "")+d; 
    }

}

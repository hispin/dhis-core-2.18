package org.hisp.dhis.calendar;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DateUnitPeriodTypeParserTest
{
    private PeriodTypeParser format;

    @Before
    public void init()
    {
        format = new DateUnitPeriodTypeParser();
    }

    @Test
    public void testDateUnitFormatParser()
    {
        // daily
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 2, 4 ), new DateUnit( 2014, 2, 4 ) ), format.parse( "20140204" ) );

        // weekly
        Assert.assertEquals( new DateInterval( new DateUnit( 2013, 12, 30 ), new DateUnit( 2014, 1, 5 ) ), format.parse( "2014W1" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 1, 6 ), new DateUnit( 2014, 1, 12 ) ), format.parse( "2014W2" ) );

        // monthly
        Assert.assertNull( format.parse( "2014W0" ) );
        Assert.assertNull( format.parse( "2014W53" ) );
        Assert.assertNotNull( format.parse( "2009W53" ) ); // 2009 has 53 weeks
        Assert.assertNull( format.parse( "2009W54" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 2, 1 ), new DateUnit( 2014, 2, 28 ) ), format.parse( "201402" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 4, 1 ), new DateUnit( 2014, 4, 30 ) ), format.parse( "201404" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 3, 1 ), new DateUnit( 2014, 3, 31 ) ), format.parse( "2014-03" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 5, 1 ), new DateUnit( 2014, 5, 31 ) ), format.parse( "2014-05" ) );

        // bi-monthly
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 2, 1 ), new DateUnit( 2014, 3, 31 ) ), format.parse( "201402B" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 7, 1 ), new DateUnit( 2014, 8, 31 ) ), format.parse( "201407B" ) );

        // quarter
        Assert.assertNull( format.parse( "2014Q0" ) );
        Assert.assertNull( format.parse( "2014Q5" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 1, 1 ), new DateUnit( 2014, 3, 31 ) ), format.parse( "2014Q1" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 4, 1 ), new DateUnit( 2014, 6, 30 ) ), format.parse( "2014Q2" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 7, 1 ), new DateUnit( 2014, 9, 30 ) ), format.parse( "2014Q3" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 10, 1 ), new DateUnit( 2014, 12, 31 ) ), format.parse( "2014Q4" ) );

        // six-monthly
        Assert.assertNull( format.parse( "2014S0" ) );
        Assert.assertNull( format.parse( "2014S3" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 1, 1 ), new DateUnit( 2014, 6, 30 ) ), format.parse( "2014S1" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 7, 1 ), new DateUnit( 2014, 12, 31 ) ), format.parse( "2014S2" ) );

        // six-monthly april
        Assert.assertNull( format.parse( "2014AprilS0" ) );
        Assert.assertNull( format.parse( "2014AprilS3" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 4, 1 ), new DateUnit( 2014, 9, 30 ) ), format.parse( "2014AprilS1" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 10, 1 ), new DateUnit( 2015, 3, 31 ) ), format.parse( "2014AprilS2" ) );

        // yearly
        Assert.assertEquals( new DateInterval( new DateUnit( 2013, 1, 1 ), new DateUnit( 2013, 12, 31 ) ), format.parse( "2013" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 1, 1 ), new DateUnit( 2014, 12, 31 ) ), format.parse( "2014" ) );

        // financial april
        Assert.assertEquals( new DateInterval( new DateUnit( 2013, 4, 1 ), new DateUnit( 2014, 3, 31 ) ), format.parse( "2013April" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 4, 1 ), new DateUnit( 2015, 3, 31 ) ), format.parse( "2014April" ) );

        // financial july
        Assert.assertEquals( new DateInterval( new DateUnit( 2013, 7, 1 ), new DateUnit( 2014, 6, 30 ) ), format.parse( "2013July" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 7, 1 ), new DateUnit( 2015, 6, 30 ) ), format.parse( "2014July" ) );

        // financial october
        Assert.assertEquals( new DateInterval( new DateUnit( 2013, 10, 1 ), new DateUnit( 2014, 9, 30 ) ), format.parse( "2013Oct" ) );
        Assert.assertEquals( new DateInterval( new DateUnit( 2014, 10, 1 ), new DateUnit( 2015, 9, 30 ) ), format.parse( "2014Oct" ) );
    }
}

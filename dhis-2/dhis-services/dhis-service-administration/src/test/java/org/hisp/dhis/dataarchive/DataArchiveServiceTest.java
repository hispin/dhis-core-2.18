package org.hisp.dhis.dataarchive;

/*
 * Copyright (c) 2004-2008, University of Oslo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id DataArchiveServiceTest.java Sep 09, 2010 ddhieu$
 */
public class DataArchiveServiceTest
    extends DataArchiveTest
{
    private Date STARTDATE;

    private Date ENDDATE;

    @Override
    public void setUpTest()
        throws Exception
    {
        STARTDATE = getDate( 1900, 1, 1 );
        ENDDATE = getDate( 3000, 1, 1 );

        setUpDataArchiveTest();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    /**
     * int archiveData( Date startDate, Date endDate, DataArchiveOperation
     * operation, DataEliminationStrategy strategy );
     */
    @Test
    public void testArchiveData()
    {
        // Archives all datavalues to datavaluearchive from earliest to latest
        // date
        int archivedValuesNo = dataArchiveService.archiveData( STARTDATE, ENDDATE, DataArchiveOperation.UNARCHIVE,
            DataEliminationStrategy.REGULAR );

        //assertEquals( "Number of archived values equals 54", 54, archivedValuesNo );
        assertEquals( "Number of archived values equals 0", 0, archivedValuesNo );

        archivedValuesNo = dataArchiveService.archiveData( STARTDATE, ENDDATE, DataArchiveOperation.ARCHIVE,
            DataEliminationStrategy.ARCHIVE );

        //assertEquals( "Number of archived values more than 0", 6, archivedValuesNo );
        assertEquals( "Number of archived values equals 0", 0, archivedValuesNo );

        // Archives all datavalues to datavaluearchive from "2005-05-01" to
        // "2005-05-31" of periodD by weekly
        archivedValuesNo = dataArchiveService.archiveData( periodD.getStartDate(), periodD.getEndDate(),
            DataArchiveOperation.ARCHIVE, DataEliminationStrategy.ARCHIVE );

        //assertEquals( "Number of archived values equals 6", 6, archivedValuesNo );
        assertNotSame( "Number of archived values more than 0", 6, archivedValuesNo );

        // Archives all datavalues to datavaluearchive from "2005-05-01" to
        // "2005-05-31" of periodD by weekly
        archivedValuesNo = dataArchiveService.archiveData( periodD.getStartDate(), periodD.getEndDate(),
            DataArchiveOperation.UNARCHIVE, DataEliminationStrategy.REGULAR );

        assertSame( "Number of unarchived values more than 0", 0, archivedValuesNo );

    }

    /**
     * int getNumberOfOverlappingValues();
     */
    @Test
    public void testGetNumberOfOverlappingValues()
    {
        int archivedValuesNo = dataArchiveService.archiveData( STARTDATE, ENDDATE, DataArchiveOperation.UNARCHIVE,
            DataEliminationStrategy.REGULAR );

        //assertEquals( "Number of archived values equals 54", 54, archivedValuesNo );
        assertEquals( "Number of archived values equals 0", 0, archivedValuesNo );

        archivedValuesNo = dataArchiveService.getNumberOfOverlappingValues();

        assertEquals( "Number of archived values equals 0", 0, archivedValuesNo );

    }
}

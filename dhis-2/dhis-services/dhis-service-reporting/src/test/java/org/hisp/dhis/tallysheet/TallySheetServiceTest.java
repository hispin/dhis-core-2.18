package org.hisp.dhis.tallysheet;

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

import static junit.framework.Assert.assertNotNull;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.junit.Test;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben
 *         Wangberg
 * @version $Id$
 */
public class TallySheetServiceTest
    extends DhisSpringTest
{
    private TallySheetService tallySheetService;

    private TallySheetPdfService tallySheetPdfService;

    @Override
    public void setUpTest()
    {
        tallySheetService = (TallySheetService) getBean( TallySheetService.ID );

        tallySheetPdfService = (TallySheetPdfService) getBean( TallySheetPdfService.ID );
    }

    @Test
    public void testCreateTallySheet()
    {
        List<DataElement> dataElements = new ArrayList<DataElement>();

        TallySheet tallySheet = tallySheetService.createTallySheet( createOrganisationUnit( 'A' ), dataElements, false,
            true, createDataSet( 'A', new MonthlyPeriodType() ), "DHIS 2" );

        assertNotNull( tallySheet );
    }

    @Test
    public void testCreateTallySheetPdf()
    {
        List<DataElement> dataElements = new ArrayList<DataElement>();

        TallySheet tallySheet = tallySheetService.createTallySheet( createOrganisationUnit( 'A' ), dataElements, false,
            true, createDataSet( 'A', new MonthlyPeriodType() ), "DHIS 2" );

        assertNotNull( tallySheet );

        InputStream in = tallySheetPdfService.createTallySheetPdf( tallySheet, null );

        assertNotNull( in );
    }
}

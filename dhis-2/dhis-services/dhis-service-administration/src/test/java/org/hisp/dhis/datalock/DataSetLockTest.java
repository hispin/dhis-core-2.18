package org.hisp.dhis.datalock;

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

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public abstract class DataSetLockTest
    extends DhisSpringTest
{
    protected DataSetLockService dataSetLockService;

    protected DataSet dataSetA;

    protected DataSet dataSetB;

    protected DataSet dataSetC;

    protected Period periodA;

    protected Period periodB;

    protected Period periodC;

    protected Period periodD;

    protected OrganisationUnit unitA;

    protected OrganisationUnit unitB;

    protected OrganisationUnit unitC;

    protected OrganisationUnit unitD;

    protected OrganisationUnit unitE;

    protected OrganisationUnit unitF;

    protected OrganisationUnit unitG;

    protected OrganisationUnit unitH;

    protected OrganisationUnit unitI;

    protected String user1;

    protected String user2;

    protected String user3;

    protected String user4;

    protected String user5;

    public void setUpDataSetLockTest()
        throws Exception
    {
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        dataSetLockService = (DataSetLockService) getBean( DataSetLockService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        Iterator<PeriodType> periodTypeIt = periodService.getAllPeriodTypes().iterator();
        PeriodType periodTypeA = periodTypeIt.next(); // Daily
        PeriodType periodTypeB = periodTypeIt.next(); // Weekly

        Date mar01 = super.getDate( 2005, 3, 1 );
        Date mar31 = super.getDate( 2005, 3, 31 );
        Date apr01 = super.getDate( 2005, 4, 1 );
        Date apr30 = super.getDate( 2005, 4, 30 );
        Date may01 = super.getDate( 2005, 5, 1 );
        Date may31 = super.getDate( 2005, 5, 31 );

        periodA = super.createPeriod( periodTypeA, mar01, mar31 );
        periodB = super.createPeriod( periodTypeA, apr01, apr30 );
        periodC = super.createPeriod( periodTypeB, mar01, may31 );
        periodD = super.createPeriod( periodTypeB, may01, may31 );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );

        // ---------------------------------------------------------------------
        // Setup DataSets
        // ---------------------------------------------------------------------

        dataSetA = super.createDataSet( 'A', periodTypeA );
        dataSetB = super.createDataSet( 'B', periodTypeB );
        dataSetC = super.createDataSet( 'C', periodTypeB );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = super.createOrganisationUnit( 'A' );
        unitB = super.createOrganisationUnit( 'B', unitA );
        unitC = super.createOrganisationUnit( 'C', unitA );
        unitD = super.createOrganisationUnit( 'D', unitB );
        unitE = super.createOrganisationUnit( 'E', unitB );
        unitF = super.createOrganisationUnit( 'F', unitB );
        unitG = super.createOrganisationUnit( 'G', unitF );
        unitH = super.createOrganisationUnit( 'H', unitF );
        unitI = super.createOrganisationUnit( 'I' );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );

        // ---------------------------------------------------------------------
        // Setup Users
        // ---------------------------------------------------------------------

        user1 = "admin";
        user2 = "User2";
        user3 = "User3";
        user4 = "User4";
        user5 = "User5";
    }

    public void initOrgunitSet( Set<Source> units, Source... sources )
    {
        for ( Source s : sources )
        {
            units.add( s );
        }
    }
}

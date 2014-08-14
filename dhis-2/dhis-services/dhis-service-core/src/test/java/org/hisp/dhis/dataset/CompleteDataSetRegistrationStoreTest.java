package org.hisp.dhis.dataset;

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

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CompleteDataSetRegistrationStoreTest
    extends DhisSpringTest
{
    private CompleteDataSetRegistrationStore registrationStore;
    
    private CompleteDataSetRegistration registrationA;
    private CompleteDataSetRegistration registrationB;
    private CompleteDataSetRegistration registrationC;
    private CompleteDataSetRegistration registrationD;
    private CompleteDataSetRegistration registrationE;
    private CompleteDataSetRegistration registrationF;
    private CompleteDataSetRegistration registrationG;
    private CompleteDataSetRegistration registrationH;
    
    private DataSet dataSetA;
    private DataSet dataSetB;
    
    private Period periodA;
    private Period periodB;
    
    private OrganisationUnit sourceA;
    private OrganisationUnit sourceB;

    private Date onTimeA;
    private Date onTimeB;
    private Date deadlineA;
    private Date tooLateA;
    private Date tooLateB;
    
    private DataElementCategoryOptionCombo optionCombo;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        registrationStore = (CompleteDataSetRegistrationStore) getBean( CompleteDataSetRegistrationStore.ID );
        
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataSetA = createDataSet( 'A', new MonthlyPeriodType() );
        dataSetB = createDataSet( 'B', new MonthlyPeriodType() );
        
        periodA = createPeriod( new MonthlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 1, 31 ) );
        periodB = createPeriod( new MonthlyPeriodType(), getDate( 2000, 2, 1 ), getDate( 2000, 2, 28 ) );
        
        sourceA = createOrganisationUnit( 'A' );
        sourceB = createOrganisationUnit( 'B' );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        
        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        
        organisationUnitService.addOrganisationUnit( sourceA );
        organisationUnitService.addOrganisationUnit( sourceB );

        optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        onTimeA = getDate( 2000, 1, 10 );
        onTimeB = getDate( 2000, 2, 10 );
        deadlineA = getDate( 2000, 1, 15 );
        tooLateA = getDate( 2000, 1, 25 );
        tooLateB = getDate( 2000, 2, 25 );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testSaveGet()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, new Date(), "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, new Date(), "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        
        assertEquals( registrationA, registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertEquals( registrationB, registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );
    }

    @Test
    public void testDelete()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, new Date(), "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, new Date(), "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );

        registrationStore.deleteCompleteDataSetRegistration( registrationA );
        
        assertNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );

        registrationStore.deleteCompleteDataSetRegistration( registrationB );
        
        assertNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );        
    }

    @Test
    public void testGetAll()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, new Date(), "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, new Date(), "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        
        Collection<CompleteDataSetRegistration> registrations = registrationStore.getAllCompleteDataSetRegistrations();
        
        assertEquals( 2, registrations.size() );
        assertTrue( registrations.contains( registrationA ) );
        assertTrue( registrations.contains( registrationB ) );
    }

    @Test
    public void testGetDataSetsSourcesPeriods()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, new Date(), "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo, new Date(), "");
        registrationC = new CompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo, new Date(), "");
        registrationD = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, new Date(), "");
        registrationE = new CompleteDataSetRegistration( dataSetA, periodA, sourceB, optionCombo, new Date(), "");
        registrationF = new CompleteDataSetRegistration( dataSetB, periodA, sourceB, optionCombo, new Date(), "");
        registrationG = new CompleteDataSetRegistration( dataSetA, periodB, sourceB, optionCombo, new Date(), "");
        registrationH = new CompleteDataSetRegistration( dataSetB, periodB, sourceB, optionCombo, new Date(), "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        registrationStore.saveCompleteDataSetRegistration( registrationC );
        registrationStore.saveCompleteDataSetRegistration( registrationD );
        registrationStore.saveCompleteDataSetRegistration( registrationE );
        registrationStore.saveCompleteDataSetRegistration( registrationF );
        registrationStore.saveCompleteDataSetRegistration( registrationG );
        registrationStore.saveCompleteDataSetRegistration( registrationH );
        
        Collection<DataSet> dataSets = new ArrayList<DataSet>();
        
        dataSets.add( dataSetB );
        
        Collection<OrganisationUnit> sources = new ArrayList<OrganisationUnit>();

        sources.add( sourceA );
        sources.add( sourceB );
        
        Collection<Period> periods = new ArrayList<Period>();
        
        periods.add( periodA );
        
        Collection<CompleteDataSetRegistration> registrations = registrationStore.
            getCompleteDataSetRegistrations( dataSets, sources, periods );
        
        assertNotNull( registrations );
        assertEquals( 2, registrations.size() );
        assertTrue( registrations.contains( registrationB ) );
        assertTrue( registrations.contains( registrationF ) );        
    }

    @Test
    public void testGetDataSetSourcesPeriod()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, new Date(), "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo, new Date(), "");
        registrationC = new CompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo, new Date(), "");
        registrationD = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, new Date(), "");
        registrationE = new CompleteDataSetRegistration( dataSetA, periodA, sourceB, optionCombo, new Date(), "");
        registrationF = new CompleteDataSetRegistration( dataSetB, periodA, sourceB, optionCombo, new Date(), "");
        registrationG = new CompleteDataSetRegistration( dataSetA, periodB, sourceB, optionCombo, new Date(), "");
        registrationH = new CompleteDataSetRegistration( dataSetB, periodB, sourceB, optionCombo, new Date(), "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        registrationStore.saveCompleteDataSetRegistration( registrationC );
        registrationStore.saveCompleteDataSetRegistration( registrationD );
        registrationStore.saveCompleteDataSetRegistration( registrationE );
        registrationStore.saveCompleteDataSetRegistration( registrationF );
        registrationStore.saveCompleteDataSetRegistration( registrationG );
        registrationStore.saveCompleteDataSetRegistration( registrationH );
        
        Collection<OrganisationUnit> sources = new ArrayList<OrganisationUnit>();
        
        sources.add( sourceA );
        sources.add( sourceB );
        
        Collection<CompleteDataSetRegistration> registrations = registrationStore.
            getCompleteDataSetRegistrations( dataSetA, sources, periodA );
        
        assertNotNull( registrations );
        assertEquals( 2, registrations.size() );
        assertTrue( registrations.contains( registrationA ) );
        assertTrue( registrations.contains( registrationE ) );        
    }

    @Test
    public void testGetDataSetSourcesPeriodDate()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, onTimeA, "");
        registrationB = new CompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo, tooLateA, "");
        registrationC = new CompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo, onTimeB, "");
        registrationD = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, tooLateB, "");
        registrationE = new CompleteDataSetRegistration( dataSetA, periodA, sourceB, optionCombo, tooLateA, "");
        registrationF = new CompleteDataSetRegistration( dataSetB, periodA, sourceB, optionCombo, onTimeA, "");
        registrationG = new CompleteDataSetRegistration( dataSetA, periodB, sourceB, optionCombo, tooLateB, "");
        registrationH = new CompleteDataSetRegistration( dataSetB, periodB, sourceB, optionCombo, onTimeB, "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        registrationStore.saveCompleteDataSetRegistration( registrationC );
        registrationStore.saveCompleteDataSetRegistration( registrationD );
        registrationStore.saveCompleteDataSetRegistration( registrationE );
        registrationStore.saveCompleteDataSetRegistration( registrationF );
        registrationStore.saveCompleteDataSetRegistration( registrationG );
        registrationStore.saveCompleteDataSetRegistration( registrationH );
        
        Collection<OrganisationUnit> sources = new ArrayList<OrganisationUnit>();
        
        sources.add( sourceA );
        sources.add( sourceB );
        
        Collection<CompleteDataSetRegistration> registrations = registrationStore.
            getCompleteDataSetRegistrations( dataSetA, sources, periodA, deadlineA );
        
        assertNotNull( registrations );
        assertEquals( 1, registrations.size() );
        assertTrue( registrations.contains( registrationA ) );      
    }

    @Test
    public void testDeleteByDataSet()
    {
        registrationA = new CompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo, onTimeA, "");
        registrationB = new CompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo, onTimeA, "");
        registrationC = new CompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo, onTimeA, "");
        registrationD = new CompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo, onTimeA, "");

        registrationStore.saveCompleteDataSetRegistration( registrationA );
        registrationStore.saveCompleteDataSetRegistration( registrationB );
        registrationStore.saveCompleteDataSetRegistration( registrationC );
        registrationStore.saveCompleteDataSetRegistration( registrationD );
        
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );
        
        registrationStore.deleteCompleteDataSetRegistrations( dataSetA );

        assertNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodA, sourceA, optionCombo ) );
        assertNull( registrationStore.getCompleteDataSetRegistration( dataSetA, periodB, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodA, sourceA, optionCombo ) );
        assertNotNull( registrationStore.getCompleteDataSetRegistration( dataSetB, periodB, sourceA, optionCombo ) );   
    }
}

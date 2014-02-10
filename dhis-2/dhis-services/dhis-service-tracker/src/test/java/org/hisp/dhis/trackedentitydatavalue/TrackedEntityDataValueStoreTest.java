/*
 * Copyright (c) 2004-2013, University of Oslo
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

package org.hisp.dhis.trackedentitydatavalue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValue;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValueStore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chau Thu Tran
 * 
 * @version $ TrackedEntityDataValueStoreTest.java Nov 5, 2013 3:11:48 PM $
 */
public class TrackedEntityDataValueStoreTest
    extends DhisSpringTest
{
    @Autowired
    private TrackedEntityDataValueStore dataValueStore;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    private ProgramInstance programInstance;

    private ProgramStageInstance stageInstanceA;

    private ProgramStageInstance stageInstanceB;

    private DataElement dataElementA;

    private DataElement dataElementB;

    private TrackedEntityDataValue dataValueA;

    private TrackedEntityDataValue dataValueB;

    private TrackedEntityDataValue dataValueC;

    private TrackedEntityDataValue dataValueD;

    private Date yesterday;

    private Date tomorrow;

    private TrackedEntityInstance entityInstance;

    @Override
    public void setUpTest()
    {
        OrganisationUnit organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitService.addOrganisationUnit( organisationUnit );

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );

        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );

        entityInstance = createTrackedEntityInstance( 'A', organisationUnit );
        entityInstanceService.saveTrackedEntityInstance( entityInstance );

        Program program = createProgram( 'A', new HashSet<ProgramStage>(), organisationUnit );
        programService.addProgram( program );

        ProgramStage stageA = new ProgramStage( "A", program );
        programStageService.saveProgramStage( stageA );

        ProgramStage stageB = new ProgramStage( "B", program );
        programStageService.saveProgramStage( stageB );

        Set<ProgramStage> programStages = new HashSet<ProgramStage>();
        programStages.add( stageA );
        programStages.add( stageB );
        program.setProgramStages( programStages );
        programService.updateProgram( program );

        Calendar calYesterday = Calendar.getInstance();
        calYesterday.add( Calendar.DATE, -1 );
        yesterday = calYesterday.getTime();
        Calendar calTomorrow = Calendar.getInstance();
        calTomorrow.add( Calendar.DATE, 1 );
        tomorrow = calTomorrow.getTime();

        programInstance = programInstanceService.enrollTrackedEntityInstance( entityInstance, program, yesterday, yesterday,
            organisationUnit, null );

        stageInstanceA = programStageInstanceService.getProgramStageInstance( programInstance, stageA );
        stageInstanceB = programStageInstanceService.getProgramStageInstance( programInstance, stageB );

        dataValueA = new TrackedEntityDataValue( stageInstanceA, dataElementA, "A" );
        dataValueB = new TrackedEntityDataValue( stageInstanceA, dataElementB, "B" );
        dataValueC = new TrackedEntityDataValue( stageInstanceB, dataElementA, "C" );
        dataValueD = new TrackedEntityDataValue( stageInstanceB, dataElementB, "D" );
    }

    @Test
    public void testSaveTrackedEntityDataValue()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );

        assertNotNull( dataValueStore.get( stageInstanceA, dataElementA ) );
        assertNotNull( dataValueStore.get( stageInstanceA, dataElementB ) );
    }

    @Test
    public void testDeleteTrackedEntityDataValueByStageInstance()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        assertNotNull( dataValueStore.get( stageInstanceA, dataElementA ) );
        assertNotNull( dataValueStore.get( stageInstanceA, dataElementB ) );
        assertNotNull( dataValueStore.get( stageInstanceB, dataElementA ) );
        assertNotNull( dataValueStore.get( stageInstanceB, dataElementB ) );

        dataValueStore.delete( stageInstanceA );
        assertNull( dataValueStore.get( stageInstanceA, dataElementA ) );
        assertNull( dataValueStore.get( stageInstanceA, dataElementB ) );
        assertNotNull( dataValueStore.get( stageInstanceB, dataElementA ) );
        assertNotNull( dataValueStore.get( stageInstanceB, dataElementB ) );
    }

    @Test
    public void testGetTrackedEntityDataValuesByStageInstance()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        Collection<TrackedEntityDataValue> dataValues = dataValueStore.get( stageInstanceA );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );

        dataValues = dataValueStore.get( stageInstanceB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueC ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetTrackedEntityDataValuesByStageElement()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        Collection<TrackedEntityDataValue> dataValues = dataValueStore.get( stageInstanceA, dataElements );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
    }

    @Test
    public void testGetTrackedEntityDataValues()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        Collection<ProgramStageInstance> programStageInstances = new HashSet<ProgramStageInstance>();
        programStageInstances.add( stageInstanceA );
        programStageInstances.add( stageInstanceB );

        Collection<TrackedEntityDataValue> dataValues = dataValueStore.get( programStageInstances );
        assertEquals( 4, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueC ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetTrackedEntityDataValuesByDataElement()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        Collection<TrackedEntityDataValue> dataValues = dataValueStore.get( dataElementA );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueA ) );
        assertTrue( dataValues.contains( dataValueC ) );

        dataValues = dataValueStore.get( dataElementB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetTrackedEntityDataValuesByEntityDataElement()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );
        dataValueStore.saveVoid( dataValueC );
        dataValueStore.saveVoid( dataValueD );

        Collection<DataElement> dataElements = new HashSet<DataElement>();
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        Collection<TrackedEntityDataValue> dataValues = dataValueStore.get( entityInstance, dataElements, yesterday,
            tomorrow );

        dataValues = dataValueStore.get( dataElementB );
        assertEquals( 2, dataValues.size() );
        assertTrue( dataValues.contains( dataValueB ) );
        assertTrue( dataValues.contains( dataValueD ) );
    }

    @Test
    public void testGetTrackedEntityDataValue()
    {
        dataValueStore.saveVoid( dataValueA );
        dataValueStore.saveVoid( dataValueB );

        TrackedEntityDataValue dataValue = dataValueStore.get( stageInstanceA, dataElementA );
        assertEquals( dataValueA, dataValue );

        dataValue = dataValueStore.get( stageInstanceA, dataElementB );
        assertEquals( dataValueB, dataValue );
    }

}

package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 */
public class DataApprovalLevelServiceTest
    extends DhisSpringTest
{

    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private CategoryOptionGroupSet setA;
    private CategoryOptionGroupSet setB;
    private CategoryOptionGroupSet setC;
    private CategoryOptionGroupSet setD;

    private DataApprovalLevel level1;
    private DataApprovalLevel level1A;
    private DataApprovalLevel level1B;
    private DataApprovalLevel level1C;
    private DataApprovalLevel level1D;

    private DataApprovalLevel level2;
    private DataApprovalLevel level2A;
    private DataApprovalLevel level2B;
    private DataApprovalLevel level2C;
    private DataApprovalLevel level2D;

    private DataApprovalLevel level3;
    private DataApprovalLevel level3A;
    private DataApprovalLevel level3B;
    private DataApprovalLevel level3C;
    private DataApprovalLevel level3D;

    private DataApprovalLevel level4;
    private DataApprovalLevel level4A;
    private DataApprovalLevel level4B;
    private DataApprovalLevel level4C;
    private DataApprovalLevel level4D;

    private OrganisationUnit organisationUnitA;
    private OrganisationUnit organisationUnitB;
    private OrganisationUnit organisationUnitC;
    private OrganisationUnit organisationUnitD;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );

        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        setA = new CategoryOptionGroupSet( "Set A" );
        setB = new CategoryOptionGroupSet( "Set B" );
        setC = new CategoryOptionGroupSet( "Set C" );
        setD = new CategoryOptionGroupSet( "Set D" );

        categoryService.saveCategoryOptionGroupSet( setA );
        categoryService.saveCategoryOptionGroupSet( setB );
        categoryService.saveCategoryOptionGroupSet( setC );
        categoryService.saveCategoryOptionGroupSet( setD );

        level1 = new DataApprovalLevel( "1", 1, null );
        level1A = new DataApprovalLevel( "1A", 1, setA );
        level1B = new DataApprovalLevel( "1B", 1, setB );
        level1C = new DataApprovalLevel( "1C", 1, setC );
        level1D = new DataApprovalLevel( "1D", 1, setD );

        level2 = new DataApprovalLevel( "2", 2, null );
        level2A = new DataApprovalLevel( "2A", 2, setA );
        level2B = new DataApprovalLevel( "2B", 2, setB );
        level2C = new DataApprovalLevel( "2C", 2, setC );
        level2D = new DataApprovalLevel( "2D", 2, setD );

        level3 = new DataApprovalLevel( "3", 3, null );
        level3A = new DataApprovalLevel( "3A", 3, setA );
        level3B = new DataApprovalLevel( "3B", 3, setB );
        level3C = new DataApprovalLevel( "3C", 3, setC );
        level3D = new DataApprovalLevel( "3D", 3, setD );

        level4 = new DataApprovalLevel( "4", 4, null );
        level4A = new DataApprovalLevel( "4A", 4, setA );
        level4B = new DataApprovalLevel( "4B", 4, setB );
        level4C = new DataApprovalLevel( "4C", 4, setC );
        level4D = new DataApprovalLevel( "4D", 4, setD );

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B', organisationUnitA );
        organisationUnitC = createOrganisationUnit( 'C', organisationUnitB );
        organisationUnitD = createOrganisationUnit( 'D', organisationUnitC );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        organisationUnitService.addOrganisationUnit( organisationUnitC );
        organisationUnitService.addOrganisationUnit( organisationUnitD );
    }

    // -------------------------------------------------------------------------
    // Basic DataApprovalLevel
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataApprovalLevel() throws Exception
    {
        List<DataApprovalLevel> levels;

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 0, levels.size() );

        dataApprovalLevelService.addDataApprovalLevel( level3B );
        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 1, levels.size() );

        assertEquals( 3, levels.get( 0 ).getOrgUnitLevel() );
        assertEquals( "Set B", levels.get( 0 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "3B", levels.get( 0 ).getName() );

        dataApprovalLevelService.addDataApprovalLevel( level2C );
        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 2, levels.size() );

        assertEquals( 2, levels.get( 0 ).getOrgUnitLevel() );
        assertEquals( "Set C", levels.get( 0 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "2C", levels.get( 0 ).getName() );

        assertEquals( 3, levels.get( 1 ).getOrgUnitLevel() );
        assertEquals( "Set B", levels.get( 1 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "3B", levels.get( 1 ).getName() );

        dataApprovalLevelService.addDataApprovalLevel( level3 );
        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 3, levels.size() );

        assertEquals( 2, levels.get( 0 ).getOrgUnitLevel() );
        assertEquals( "Set C", levels.get( 0 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "2C", levels.get( 0 ).getName() );

        assertEquals( 3, levels.get( 1 ).getOrgUnitLevel() );
        assertNull( levels.get( 1 ).getCategoryOptionGroupSet() );
        assertEquals( "3", levels.get( 1 ).getName() );

        assertEquals( 3, levels.get( 2 ).getOrgUnitLevel() );
        assertEquals( "Set B", levels.get( 2 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "3B", levels.get( 2 ).getName() );

        dataApprovalLevelService.addDataApprovalLevel( level4A );
        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 4, levels.size() );

        assertEquals( 2, levels.get( 0 ).getOrgUnitLevel() );
        assertEquals( "Set C", levels.get( 0 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "2C", levels.get( 0 ).getName() );

        assertEquals( 3, levels.get( 1 ).getOrgUnitLevel() );
        assertNull( levels.get( 1 ).getCategoryOptionGroupSet() );
        assertEquals( "3", levels.get( 1 ).getName() );

        assertEquals( 3, levels.get( 2 ).getOrgUnitLevel() );
        assertEquals( "Set B", levels.get( 2 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "3B", levels.get( 2 ).getName() );

        assertEquals( 4, levels.get( 3 ).getOrgUnitLevel() );
        assertEquals( "Set A", levels.get( 3 ).getCategoryOptionGroupSet().getName() );
        assertEquals( "4A", levels.get( 3 ).getName() );
    }

    @Test
    public void testDeleteDataApprovalLevel() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level3C );
        dataApprovalLevelService.addDataApprovalLevel( level4D );

        List<DataApprovalLevel> levels;

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 4, levels.size() );
        assertEquals( "1A", levels.get( 0 ).getName() );
        assertEquals( "2B", levels.get( 1 ).getName() );
        assertEquals( "3C", levels.get( 2 ).getName() );
        assertEquals( "4D", levels.get( 3 ).getName() );

        dataApprovalLevelService.deleteDataApprovalLevel( 2 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 3, levels.size() );
        assertEquals( "1A", levels.get( 0 ).getName() );
        assertEquals( "3C", levels.get( 1 ).getName() );
        assertEquals( "4D", levels.get( 2 ).getName() );

        dataApprovalLevelService.deleteDataApprovalLevel( 3 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 2, levels.size() );
        assertEquals( "1A", levels.get( 0 ).getName() );
        assertEquals( "3C", levels.get( 1 ).getName() );

        dataApprovalLevelService.deleteDataApprovalLevel( 1 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 1, levels.size() );
        assertEquals( "3C", levels.get( 0 ).getName() );

        dataApprovalLevelService.deleteDataApprovalLevel( 1 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 0, levels.size() );
    }

    @Test
    public void testExists() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2B );

        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level1A ) );
        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level1A ) );
        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level2A ) );
        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level2B ) );
        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level2 ) );
        assertTrue( dataApprovalLevelService.dataApprovalLevelExists( level1 ) );

        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level3 ) );
        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level4 ) );
        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level1C ) );
        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level1D ) );
        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level2C ) );
        assertFalse( dataApprovalLevelService.dataApprovalLevelExists( level2D ) );
    }

    @Test
    public void testCanMoveDown() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3B );

        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( -1 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 0 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 1 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveDown( 2 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 3 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 4 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveDown( 5 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 6 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 7 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveDown( 8 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 9 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 10 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveDown( 11 ) );
    }

    @Test
    public void testCanMoveUp() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3B );

        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( -1 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 0 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 1 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 2 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveUp( 3 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 4 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 5 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveUp( 6 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 7 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 8 ) );
        assertTrue( dataApprovalLevelService.canDataApprovalLevelMoveUp( 9 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 10 ) );
        assertFalse( dataApprovalLevelService.canDataApprovalLevelMoveUp( 11 ) );
    }

    @Test
    public void testMoveDown() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1D );
        dataApprovalLevelService.addDataApprovalLevel( level1C );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        List<DataApprovalLevel> levels;

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1A", levels.get( 1 ).getName() );
        assertEquals( "1B", levels.get( 2 ).getName() );
        assertEquals( "1C", levels.get( 3 ).getName() );
        assertEquals( "1D", levels.get( 4 ).getName() );

        dataApprovalLevelService.moveDataApprovalLevelDown( 2 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1B", levels.get( 1 ).getName() );
        assertEquals( "1A", levels.get( 2 ).getName() );
        assertEquals( "1C", levels.get( 3 ).getName() );
        assertEquals( "1D", levels.get( 4 ).getName() );

        dataApprovalLevelService.moveDataApprovalLevelDown( 3 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1B", levels.get( 1 ).getName() );
        assertEquals( "1C", levels.get( 2 ).getName() );
        assertEquals( "1A", levels.get( 3 ).getName() );
        assertEquals( "1D", levels.get( 4 ).getName() );
    }

    @Test
    public void testMoveUp() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1D );
        dataApprovalLevelService.addDataApprovalLevel( level1C );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        List<DataApprovalLevel> levels;

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1A", levels.get( 1 ).getName() );
        assertEquals( "1B", levels.get( 2 ).getName() );
        assertEquals( "1C", levels.get( 3 ).getName() );
        assertEquals( "1D", levels.get( 4 ).getName() );

        dataApprovalLevelService.moveDataApprovalLevelUp( 5 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1A", levels.get( 1 ).getName() );
        assertEquals( "1B", levels.get( 2 ).getName() );
        assertEquals( "1D", levels.get( 3 ).getName() );
        assertEquals( "1C", levels.get( 4 ).getName() );

        dataApprovalLevelService.moveDataApprovalLevelUp( 4 );

        levels = dataApprovalLevelService.getAllDataApprovalLevels();
        assertEquals( 5, levels.size() );
        assertEquals( "1", levels.get( 0 ).getName() );
        assertEquals( "1A", levels.get( 1 ).getName() );
        assertEquals( "1D", levels.get( 2 ).getName() );
        assertEquals( "1B", levels.get( 3 ).getName() );
        assertEquals( "1C", levels.get( 4 ).getName() );
    }

    @Test
    public void testGetUserDataApprovalLevelsNoAuthorities() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level4B );
        dataApprovalLevelService.addDataApprovalLevel( level4A );
        dataApprovalLevelService.addDataApprovalLevel( level4 );
        dataApprovalLevelService.addDataApprovalLevel( level3B );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false );

        List<DataApprovalLevel> levels = dataApprovalLevelService.getUserDataApprovalLevels();
        assertEquals( 0, levels.size() );
    }

    @Test
    public void testGetUserDataApprovalLevelsApproveHere() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level4B );
        dataApprovalLevelService.addDataApprovalLevel( level4A );
        dataApprovalLevelService.addDataApprovalLevel( level4 );
        dataApprovalLevelService.addDataApprovalLevel( level3B );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE );

        List<DataApprovalLevel> levels = dataApprovalLevelService.getUserDataApprovalLevels();

        assertEquals( 4, levels.size() );
        assertEquals( "2", levels.get( 0 ).getName() );
        assertEquals( "2A", levels.get( 1 ).getName() );
        assertEquals( "2B", levels.get( 2 ).getName() );
        assertEquals( "3", levels.get( 3 ).getName() );
    }

    @Test
    public void testGetUserDataApprovalLevelsApproveLower() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level4B );
        dataApprovalLevelService.addDataApprovalLevel( level4A );
        dataApprovalLevelService.addDataApprovalLevel( level4 );
        dataApprovalLevelService.addDataApprovalLevel( level3B );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        List<DataApprovalLevel> levels = dataApprovalLevelService.getUserDataApprovalLevels();
        
        assertEquals( 8, levels.size() );
        assertEquals( "2A", levels.get( 0 ).getName() );
        assertEquals( "2B", levels.get( 1 ).getName() );
        assertEquals( "3", levels.get( 2 ).getName() );
        assertEquals( "3A", levels.get( 3 ).getName() );
        assertEquals( "3B", levels.get( 4 ).getName() );
        assertEquals( "4", levels.get( 5 ).getName() );
        assertEquals( "4A", levels.get( 6 ).getName() );
        assertEquals( "4B", levels.get( 7 ).getName() );
    }

    @Test
    public void testGetUserDataApprovalLevelsApproveHereAndLower() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level4B );
        dataApprovalLevelService.addDataApprovalLevel( level4A );
        dataApprovalLevelService.addDataApprovalLevel( level4 );
        dataApprovalLevelService.addDataApprovalLevel( level3B );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );

        List<DataApprovalLevel> levels = dataApprovalLevelService.getUserDataApprovalLevels();
        
        assertEquals( 9, levels.size() );
        assertEquals( "2", levels.get( 0 ).getName() );
        assertEquals( "2A", levels.get( 1 ).getName() );
        assertEquals( "2B", levels.get( 2 ).getName() );
        assertEquals( "3", levels.get( 3 ).getName() );
        assertEquals( "3A", levels.get( 4 ).getName() );
        assertEquals( "3B", levels.get( 5 ).getName() );
        assertEquals( "4", levels.get( 6 ).getName() );
        assertEquals( "4A", levels.get( 7 ).getName() );
        assertEquals( "4B", levels.get( 8 ).getName() );
    }

    @Test
    public void testGetUserDataApprovalLevelsAcceptLower() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level4B );
        dataApprovalLevelService.addDataApprovalLevel( level4A );
        dataApprovalLevelService.addDataApprovalLevel( level4 );
        dataApprovalLevelService.addDataApprovalLevel( level3B );
        dataApprovalLevelService.addDataApprovalLevel( level3A );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level2B );
        dataApprovalLevelService.addDataApprovalLevel( level2A );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level1B );
        dataApprovalLevelService.addDataApprovalLevel( level1A );
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        createUserAndInjectSecurityContext( units, false, DataApproval.AUTH_ACCEPT_LOWER_LEVELS );

        List<DataApprovalLevel> levels = dataApprovalLevelService.getUserDataApprovalLevels();
        
        assertEquals( 3, levels.size() );
        assertEquals( "2A", levels.get( 0 ).getName() );
        assertEquals( "2B", levels.get( 1 ).getName() );
        assertEquals( "3", levels.get( 2 ).getName() );
    }
}

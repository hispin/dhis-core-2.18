package org.hisp.dhis.dataapproval;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.mock.MockCurrentUserService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 */
public class DataApprovalServiceTest
    extends DhisSpringTest
{
    private static final String AUTH_APPR_LEVEL = "F_SYSTEM_SETTING";

    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private DataApprovalStore dataApprovalStore;

    @Autowired
    private DataApprovalLevelService dataApprovalLevelService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private final static boolean NOT_ACCEPTED = false;

    private final static CategoryOptionGroup NO_GROUP = null;

    private final static Set<CategoryOptionGroup> NO_GROUPS = null;

    private final static Set<DataElementCategoryOption> NO_OPTIONS = null;

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private Period periodC;

    private Period periodD;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private OrganisationUnit organisationUnitC;

    private OrganisationUnit organisationUnitD;

    private OrganisationUnit organisationUnitE;

    private OrganisationUnit organisationUnitF;

    private DataApprovalLevel level1;

    private DataApprovalLevel level2;

    private DataApprovalLevel level3;

    private DataApprovalLevel level4;

    private DataApprovalLevel level1ABCD;

    private DataApprovalLevel level1EFGH;

    private DataApprovalLevel level2ABCD;

    private User userA;

    private User userB;

    private DataElementCategoryOption optionA;

    private DataElementCategoryOption optionB;

    private DataElementCategoryOption optionC;

    private DataElementCategoryOption optionD;

    private DataElementCategoryOption optionE;

    private DataElementCategoryOption optionF;

    private DataElementCategoryOption optionG;

    private DataElementCategoryOption optionH;

    private DataElementCategoryOptionCombo optionComboA;

    private DataElementCategoryOptionCombo optionComboB;

    private DataElementCategoryOptionCombo optionComboC;

    private DataElementCategoryOptionCombo optionComboD;

    private DataElementCategoryOptionCombo optionComboE;

    private DataElementCategoryOptionCombo optionComboF;

    private DataElementCategoryOptionCombo optionComboG;

    private DataElementCategoryOptionCombo optionComboH;

    private DataElementCategoryOptionCombo optionComboI;

    private DataElementCategoryOptionCombo optionComboJ;

    private DataElementCategoryOptionCombo optionComboK;

    private DataElementCategoryOptionCombo optionComboL;

    private DataElementCategoryOptionCombo optionComboM;

    private DataElementCategoryOptionCombo optionComboN;

    private DataElementCategoryOptionCombo optionComboO;

    private DataElementCategoryOptionCombo optionComboP;

    private DataElementCategory categoryA;

    private DataElementCategory categoryB;

    private DataElementCategoryCombo categoryComboA;

    private CategoryOptionGroup groupAB;

    private CategoryOptionGroup groupCD;

    private CategoryOptionGroup groupEF;

    private CategoryOptionGroup groupGH;

    private CategoryOptionGroupSet groupSetABCD;

    private CategoryOptionGroupSet groupSetEFGH;

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

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        periodA = createPeriod( getDay( 5 ), getDay( 6 ) );
        periodB = createPeriod( getDay( 6 ), getDay( 7 ) );
        periodC = createPeriod( PeriodType.getPeriodTypeByName( "Yearly" ), getDay( 1 ), getDay( 365 ) );
        periodD = createPeriod( PeriodType.getPeriodTypeByName( "Weekly" ), getDay( 1 ), getDay( 365 ) );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );

        //
        // Organisation unit hierarchy:
        //
        //      A
        //      |
        //      B
        //     / \
        //    C   E
        //    |   |
        //    D   F
        //

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B', organisationUnitA );
        organisationUnitC = createOrganisationUnit( 'C', organisationUnitB );
        organisationUnitD = createOrganisationUnit( 'D', organisationUnitC );
        organisationUnitE = createOrganisationUnit( 'E', organisationUnitB );
        organisationUnitF = createOrganisationUnit( 'F', organisationUnitE );

        organisationUnitA.setLevel( 1 );
        organisationUnitB.setLevel( 2 );
        organisationUnitC.setLevel( 3 );
        organisationUnitD.setLevel( 4 );
        organisationUnitE.setLevel( 3 );
        organisationUnitF.setLevel( 4 );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        organisationUnitService.addOrganisationUnit( organisationUnitC );
        organisationUnitService.addOrganisationUnit( organisationUnitD );
        organisationUnitService.addOrganisationUnit( organisationUnitE );
        organisationUnitService.addOrganisationUnit( organisationUnitF );

        level1 = new DataApprovalLevel( "level1", 1, null );
        level2 = new DataApprovalLevel( "level2", 2, null );
        level3 = new DataApprovalLevel( "level3", 3, null );
        level4 = new DataApprovalLevel( "level4", 4, null );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );
    }

    // ---------------------------------------------------------------------
    // Set Up Categories
    // ---------------------------------------------------------------------

    private void setUpCategories() throws Exception
    {
        optionA = new DataElementCategoryOption( "CategoryOptionA" );
        optionB = new DataElementCategoryOption( "CategoryOptionB" );
        optionC = new DataElementCategoryOption( "CategoryOptionC" );
        optionD = new DataElementCategoryOption( "CategoryOptionD" );
        optionE = new DataElementCategoryOption( "CategoryOptionE" );
        optionF = new DataElementCategoryOption( "CategoryOptionF" );
        optionG = new DataElementCategoryOption( "CategoryOptionG" );
        optionH = new DataElementCategoryOption( "CategoryOptionH" );

        categoryService.addDataElementCategoryOption( optionA );
        categoryService.addDataElementCategoryOption( optionB );
        categoryService.addDataElementCategoryOption( optionC );
        categoryService.addDataElementCategoryOption( optionD );
        categoryService.addDataElementCategoryOption( optionE );
        categoryService.addDataElementCategoryOption( optionF );
        categoryService.addDataElementCategoryOption( optionG );
        categoryService.addDataElementCategoryOption( optionH );

        categoryA = createDataElementCategory( 'A', optionA, optionB, optionC, optionD );
        categoryB = createDataElementCategory( 'B', optionE, optionF, optionG, optionH );

        categoryService.addDataElementCategory( categoryA );
        categoryService.addDataElementCategory( categoryB );

        categoryComboA = createCategoryCombo( 'A', categoryA, categoryB );

        categoryService.addDataElementCategoryCombo( categoryComboA );

        optionComboA = createCategoryOptionCombo( 'A', categoryComboA, optionA, optionE );
        optionComboB = createCategoryOptionCombo( 'B', categoryComboA, optionA, optionF );
        optionComboC = createCategoryOptionCombo( 'C', categoryComboA, optionA, optionG );
        optionComboD = createCategoryOptionCombo( 'D', categoryComboA, optionA, optionH );
        optionComboE = createCategoryOptionCombo( 'E', categoryComboA, optionB, optionE );
        optionComboF = createCategoryOptionCombo( 'F', categoryComboA, optionB, optionF );
        optionComboG = createCategoryOptionCombo( 'G', categoryComboA, optionB, optionG );
        optionComboH = createCategoryOptionCombo( 'H', categoryComboA, optionB, optionH );
        optionComboI = createCategoryOptionCombo( 'I', categoryComboA, optionC, optionE );
        optionComboJ = createCategoryOptionCombo( 'J', categoryComboA, optionC, optionF );
        optionComboK = createCategoryOptionCombo( 'K', categoryComboA, optionC, optionG );
        optionComboL = createCategoryOptionCombo( 'L', categoryComboA, optionC, optionH );
        optionComboM = createCategoryOptionCombo( 'M', categoryComboA, optionD, optionE );
        optionComboN = createCategoryOptionCombo( 'N', categoryComboA, optionD, optionF );
        optionComboO = createCategoryOptionCombo( 'O', categoryComboA, optionD, optionG );
        optionComboP = createCategoryOptionCombo( 'P', categoryComboA, optionD, optionH );

        categoryService.addDataElementCategoryOptionCombo( optionComboA );
        categoryService.addDataElementCategoryOptionCombo( optionComboB );
        categoryService.addDataElementCategoryOptionCombo( optionComboC );
        categoryService.addDataElementCategoryOptionCombo( optionComboD );
        categoryService.addDataElementCategoryOptionCombo( optionComboE );
        categoryService.addDataElementCategoryOptionCombo( optionComboF );
        categoryService.addDataElementCategoryOptionCombo( optionComboG );
        categoryService.addDataElementCategoryOptionCombo( optionComboH );
        categoryService.addDataElementCategoryOptionCombo( optionComboI );
        categoryService.addDataElementCategoryOptionCombo( optionComboJ );
        categoryService.addDataElementCategoryOptionCombo( optionComboK );
        categoryService.addDataElementCategoryOptionCombo( optionComboL );
        categoryService.addDataElementCategoryOptionCombo( optionComboM );
        categoryService.addDataElementCategoryOptionCombo( optionComboN );
        categoryService.addDataElementCategoryOptionCombo( optionComboO );
        categoryService.addDataElementCategoryOptionCombo( optionComboP );

        groupAB = createCategoryOptionGroup( 'A', optionA, optionB );
        groupCD = createCategoryOptionGroup( 'C', optionC, optionD );
        groupEF = createCategoryOptionGroup( 'E', optionE, optionF );
        groupGH = createCategoryOptionGroup( 'G', optionG, optionH );

        categoryService.saveCategoryOptionGroup( groupAB );
        categoryService.saveCategoryOptionGroup( groupCD );
        categoryService.saveCategoryOptionGroup( groupEF );
        categoryService.saveCategoryOptionGroup( groupGH );

        groupSetABCD = new CategoryOptionGroupSet( "GroupSetABCD" );
        groupSetEFGH = new CategoryOptionGroupSet( "GroupSetEFGH" );

        categoryService.saveCategoryOptionGroupSet( groupSetABCD );
        categoryService.saveCategoryOptionGroupSet( groupSetEFGH );

        groupSetABCD.addCategoryOptionGroup( groupAB );
        groupSetABCD.addCategoryOptionGroup( groupCD );
        groupSetEFGH.addCategoryOptionGroup( groupEF );
        groupSetEFGH.addCategoryOptionGroup( groupGH );

        level1ABCD = new DataApprovalLevel( "level1ABCD", 1, groupSetABCD );
        level1EFGH = new DataApprovalLevel( "level1EFGH", 1, groupSetEFGH );
        level2ABCD = new DataApprovalLevel( "level2ABCD", 2, groupSetABCD );
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1, 1 );
        dataApprovalLevelService.addDataApprovalLevel( level2, 2 );
        dataApprovalLevelService.addDataApprovalLevel( level3, 3 );
        dataApprovalLevelService.addDataApprovalLevel( level4, 4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level1, dataSetA, periodB, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level1, dataSetB, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        dataSetA.setApproveData( true );
        dataSetB.setApproveData( true );

        DataApprovalStatus status;
        DataApproval da;
        DataApprovalLevel level;

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, NO_GROUPS, NO_OPTIONS );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodB, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getDataApprovalState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetB.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level1, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodB, organisationUnitB, NO_GROUPS, NO_OPTIONS );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, status.getDataApprovalState() );
        assertNull ( status.getDataApproval() );
        assertNull ( status.getDataApprovalLevel() );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalService.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.addDataApproval( dataApprovalB );
            fail("Should give unique constraint violation");
        }
        catch ( Exception e )
        {
            // Expected
        }
    }

    @Test
    public void testDeleteDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );

        dataSetA.setApproveData( true );

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNotNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.deleteDataApproval( dataApprovalA ); // Only A should be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.deleteDataApproval( dataApprovalB ); // A and B should both be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApproval();
        assertNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        // Not enabled.
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Enabled for data set, but data set not associated with organisation unit.

        dataSetA.setApproveData( true );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Enabled for data set, and associated with organisation unit C.
        organisationUnitC.addDataSet( dataSetA );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        Date date = new Date();

        // Approved for sourceD
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA );
        dataApprovalService.addDataApproval( dataApprovalD );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Also approved for sourceC
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA );
        dataApprovalService.addDataApproval( dataApprovalC );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Also approved for sourceF
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, NO_GROUP, NOT_ACCEPTED, date, userA );
        dataApprovalService.addDataApproval( dataApprovalF );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Also approved also for sourceE
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA );
        dataApprovalService.addDataApproval( dataApprovalE );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        // Disable approval for dataset.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateWithMultipleChildren() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        dataSetA.setApproveData( true );

        organisationUnitD.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Date date = new Date();

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitF, NO_GROUP, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
    }

    @Test
    public void testGetDataApprovalStateOtherPeriodTypes() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodD, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
    }

    @Test
    public void testMayApprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayApprove());
    }

    @Test
    public void testMayApproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayApprove());
    }

    @Test
    public void testMayApproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayApprove());
    }

    @Test
    public void testMayApproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        dataApprovalService.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayApprove());

        dataApprovalService.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayApprove());
    }

    @Test
    public void testMayUnapprove() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalC );
        dataApprovalStore.addDataApproval( dataApprovalE );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
    }

    @Test
    public void testMayUnapproveLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalC );
        dataApprovalStore.addDataApproval( dataApprovalE );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
    }

    @Test
    public void testMayUnapproveSameAndLowerLevels() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalC );
        dataApprovalStore.addDataApproval( dataApprovalE );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
    }

    @Test
    public void testMayUnapproveNoAuthority() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitB );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, NO_GROUP, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, NO_GROUP, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalC );
        dataApprovalStore.addDataApproval( dataApprovalE );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).isMayUnapprove());
    }

    // ---------------------------------------------------------------------
    // Test with Categories
    // ---------------------------------------------------------------------

    @Test
    public void testApprovalStateWithCategories() throws Exception
    {
        setUpCategories();

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Set<CategoryOptionGroup> groupABSet = new HashSet<CategoryOptionGroup>();
        groupABSet.add( groupAB );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitC.addDataSet( dataSetA );

        Date date = new Date();

        //
        // Group set ABCD -> Groups AB,CD
        // Group set EFGH -> Groups EF,GH
        //
        // Group AB -> Options A,B
        // Group CD -> Options C,D
        // Group EF -> Options E,F
        // Group GH -> Options G,H
        //
        // Category A -> Options A,B,C,D
        // Category B -> Options E,F,G,H
        //
        // Option combo A -> Options A,E -> Groups A,C
        // Option combo B -> Options A,F -> Groups A,C
        // Option combo C -> Options A,G -> Groups A,D
        // Option combo D -> Options A,H -> Groups A,D
        // Option combo E -> Options B,E -> Groups B,C
        // Option combo F -> Options B,F -> Groups B,C
        // Option combo G -> Options B,G -> Groups B,D
        // Option combo H -> Options B,H -> Groups B,D
        // Option combo I -> Options C,E -> Groups B,D
        // Option combo J -> Options C,F -> Groups B,D
        // Option combo K -> Options C,G -> Groups B,D

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboA ).getDataApprovalState() );

        dataApprovalLevelService.addDataApprovalLevel( level2ABCD );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboB ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboC ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboI ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboJ ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboK ).getDataApprovalState() );

        dataApprovalService.addDataApproval( new DataApproval( level2ABCD, dataSetA, periodA, organisationUnitB, groupAB, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboB ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboC ).getDataApprovalState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboI ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboJ ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboK ).getDataApprovalState() );
    }

    @Test
    public void testApprovalLevelWithCategories() throws Exception
    {
        setUpCategories();

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );

        Set<CategoryOptionGroup> groupASet = new HashSet<CategoryOptionGroup>();
        groupASet.add( groupAB ); // GroupA is a member of DataSetA

        Set<CategoryOptionGroup> groupBSet = new HashSet<CategoryOptionGroup>();
        groupBSet.add ( groupCD );

        Set<CategoryOptionGroup> groupCSet = new HashSet<CategoryOptionGroup>();
        groupCSet.add ( groupEF );

        Set<CategoryOptionGroup> groupXSet = new HashSet<CategoryOptionGroup>();
        groupXSet.add ( groupAB );
        groupXSet.add( groupEF );

        Set<OrganisationUnit> units = new HashSet<OrganisationUnit>();
        units.add( organisationUnitA );
        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dab = new DataApproval( level1EFGH, dataSetA, periodA, organisationUnitA, groupCD, NOT_ACCEPTED, date, userA );

        dataApprovalLevelService.addDataApprovalLevel( level1EFGH );
        dataApprovalService.addDataApproval( dab );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );

        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );

        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalLevel() );

        dataApprovalLevelService.addDataApprovalLevel( level1ABCD );
        dataApprovalService.addDataApproval( new DataApproval( level1ABCD, dataSetA, periodA, organisationUnitA, groupAB, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalLevel() );

        dataApprovalService.deleteDataApproval( dab );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalLevel() );

        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalService.addDataApproval( new DataApproval( level1, dataSetA, periodA, organisationUnitA, NO_GROUP, NOT_ACCEPTED, date, userA ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalState() );

        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboA ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalState() );

        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboA ).getDataApprovalLevel() );
    }
}

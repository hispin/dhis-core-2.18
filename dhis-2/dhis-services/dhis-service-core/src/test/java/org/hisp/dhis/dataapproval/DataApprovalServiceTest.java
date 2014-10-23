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

import static com.google.common.collect.Lists.newArrayList;
import static org.hisp.dhis.system.util.CollectionUtils.asList;
import static org.hisp.dhis.system.util.CollectionUtils.asSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dataapproval.exceptions.UserCannotAccessApprovalLevelException;
import org.hisp.dhis.dataapproval.exceptions.UserMayNotApproveDataException;
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
   
    @Autowired 
    protected IdentifiableObjectManager _identifiableObjectManager;
    
    @Autowired
    protected UserService _userService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private final static boolean NOT_ACCEPTED = false;

    private final static Set<DataElementCategoryOption> NO_OPTIONS = null;

    private final static Set<CategoryOptionGroup> NO_GROUPS = null;

    private DataElementCategoryOptionCombo defaultCombo;

    private DataSet dataSetA;
    private DataSet dataSetB;

    private Period periodA; // Monthly: Jan
    private Period periodB; // Monthly: Feb
    private Period periodC; // Monthly: Mar

    private Period periodD; // Daily
    private Period periodQ; // Quarterly
    private Period periodW; // Weekly
    private Period periodY; // Yearly

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
    private DataApprovalLevel level3ABCD;

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

    private DataElementCategoryOptionCombo optionComboAE;
    private DataElementCategoryOptionCombo optionComboAF;
    private DataElementCategoryOptionCombo optionComboAG;
    private DataElementCategoryOptionCombo optionComboAH;
    private DataElementCategoryOptionCombo optionComboBE;
    private DataElementCategoryOptionCombo optionComboBF;
    private DataElementCategoryOptionCombo optionComboBG;
    private DataElementCategoryOptionCombo optionComboBH;
    private DataElementCategoryOptionCombo optionComboCE;
    private DataElementCategoryOptionCombo optionComboCF;
    private DataElementCategoryOptionCombo optionComboCG;
    private DataElementCategoryOptionCombo optionComboCH;
    private DataElementCategoryOptionCombo optionComboDE;
    private DataElementCategoryOptionCombo optionComboDF;
    private DataElementCategoryOptionCombo optionComboDG;
    private DataElementCategoryOptionCombo optionComboDH;

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
        identifiableObjectManager = _identifiableObjectManager;
        userService = _userService;
        
        // ---------------------------------------------------------------------
        // Add supporting data
        // ---------------------------------------------------------------------

        PeriodType periodType = PeriodType.getPeriodTypeByName( "Monthly" );

        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );

        periodA = createPeriod( "201401" ); // Monthly: Jan
        periodB = createPeriod( "201402" ); // Monthly: Feb
        periodC = createPeriod( "201403" ); // Monthly: Mar

        periodD = createPeriod( "20140105" ); // Daily

        periodQ = createPeriod( "2014Q1" ); // Quarterly

        periodW = createPeriod( "2014W1" ); // Weekly

        periodY = createPeriod( "2014" ); // Yearly

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodY );
        periodService.addPeriod( periodW );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodQ );

        //
        // Organisation unit hierarchy:
        //
        // Level 1       A
        //               |
        // Level 2       B
        //              / \
        // Level 3     C   E
        //             |   |
        // Level 4     D   F
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

        defaultCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
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

        optionComboAE = createCategoryOptionCombo( 'A', categoryComboA, optionA, optionE );
        optionComboAF = createCategoryOptionCombo( 'B', categoryComboA, optionA, optionF );
        optionComboAG = createCategoryOptionCombo( 'C', categoryComboA, optionA, optionG );
        optionComboAH = createCategoryOptionCombo( 'D', categoryComboA, optionA, optionH );
        optionComboBE = createCategoryOptionCombo( 'E', categoryComboA, optionB, optionE );
        optionComboBF = createCategoryOptionCombo( 'F', categoryComboA, optionB, optionF );
        optionComboBG = createCategoryOptionCombo( 'G', categoryComboA, optionB, optionG );
        optionComboBH = createCategoryOptionCombo( 'H', categoryComboA, optionB, optionH );
        optionComboCE = createCategoryOptionCombo( 'I', categoryComboA, optionC, optionE );
        optionComboCF = createCategoryOptionCombo( 'J', categoryComboA, optionC, optionF );
        optionComboCG = createCategoryOptionCombo( 'K', categoryComboA, optionC, optionG );
        optionComboCH = createCategoryOptionCombo( 'L', categoryComboA, optionC, optionH );
        optionComboDE = createCategoryOptionCombo( 'M', categoryComboA, optionD, optionE );
        optionComboDF = createCategoryOptionCombo( 'N', categoryComboA, optionD, optionF );
        optionComboDG = createCategoryOptionCombo( 'O', categoryComboA, optionD, optionG );
        optionComboDH = createCategoryOptionCombo( 'P', categoryComboA, optionD, optionH );

        categoryService.addDataElementCategoryOptionCombo( optionComboAE );
        categoryService.addDataElementCategoryOptionCombo( optionComboAF );
        categoryService.addDataElementCategoryOptionCombo( optionComboAG );
        categoryService.addDataElementCategoryOptionCombo( optionComboAH );
        categoryService.addDataElementCategoryOptionCombo( optionComboBE );
        categoryService.addDataElementCategoryOptionCombo( optionComboBF );
        categoryService.addDataElementCategoryOptionCombo( optionComboBG );
        categoryService.addDataElementCategoryOptionCombo( optionComboBH );
        categoryService.addDataElementCategoryOptionCombo( optionComboCE );
        categoryService.addDataElementCategoryOptionCombo( optionComboCF );
        categoryService.addDataElementCategoryOptionCombo( optionComboCG );
        categoryService.addDataElementCategoryOptionCombo( optionComboCH );
        categoryService.addDataElementCategoryOptionCombo( optionComboDE );
        categoryService.addDataElementCategoryOptionCombo( optionComboDF );
        categoryService.addDataElementCategoryOptionCombo( optionComboDG );
        categoryService.addDataElementCategoryOptionCombo( optionComboDH );

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

        groupSetEFGH.addCategoryOptionGroup( groupAB );
        groupSetEFGH.addCategoryOptionGroup( groupEF );

        groupAB.setGroupSet( groupSetABCD );
        groupCD.setGroupSet( groupSetABCD );
        groupEF.setGroupSet( groupSetEFGH );
        groupGH.setGroupSet( groupSetEFGH );

        level1ABCD = new DataApprovalLevel( "level1ABCD", 1, groupSetABCD );
        level1EFGH = new DataApprovalLevel( "level1EFGH", 1, groupSetEFGH );
        level2ABCD = new DataApprovalLevel( "level2ABCD", 2, groupSetABCD );
        level3ABCD = new DataApprovalLevel( "level3ABCD", 3, groupSetABCD );
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAllAndGetDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1, 1 );
        dataApprovalLevelService.addDataApprovalLevel( level2, 2 );

        dataSetA.setApproveData( true );
        dataSetB.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetB );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level2, dataSetA, periodB, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level2, dataSetB, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalService.approveData( newArrayList(dataApprovalB, dataApprovalC, dataApprovalD) ); // Must be approved before A.
        dataApprovalService.approveData( newArrayList(dataApprovalA) );

        DataApprovalStatus status;
        DataApproval da;
        DataApprovalLevel level;

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getState() );
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

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, status.getState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitA.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level2, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetA.getId(), da.getDataSet().getId() );
        assertEquals( periodB, da.getPeriod() );
        assertEquals( organisationUnitB.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level2, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodA, organisationUnitB, defaultCombo );
        assertEquals( DataApprovalState.APPROVED_HERE, status.getState() );
        da = status.getDataApproval();
        assertNotNull( da );
        assertEquals( dataSetB.getId(), da.getDataSet().getId() );
        assertEquals( periodA, da.getPeriod() );
        assertEquals( organisationUnitB.getId(), da.getOrganisationUnit().getId() );
        assertEquals( date, da.getCreated() );
        assertEquals( userA.getId(), da.getCreator().getId() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level2, level );

        status = dataApprovalService.getDataApprovalStatus( dataSetB, periodB, organisationUnitB, defaultCombo );
        assertEquals( DataApprovalState.UNAPPROVED_READY, status.getState() );
        assertNotNull( status.getDataApproval() );
        level = status.getDataApprovalLevel();
        assertNotNull( level );
        assertEquals( level2, level );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA ); // Same

        dataApprovalService.approveData( asList( dataApprovalA ) );
        dataApprovalService.approveData( asList( dataApprovalB ) ); // Redundant, call is so ignored.
    }

    @Test
    public void testDeleteDataApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.approveData( asList( dataApprovalB ) );
        dataApprovalService.approveData( asList( dataApprovalA ) );

        dataSetA.setApproveData( true );

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getDataApproval();
        assertNotNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.unapproveData( asList( dataApprovalA ) ); // Only A should be deleted.

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getDataApproval();
        assertNotNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getDataApproval();
        assertNotNull( testB );

        dataApprovalService.unapproveData( asList( dataApprovalB ) );

        testA = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getDataApproval();
        assertNull( testA );

        testB = dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getDataApproval();
        assertNotNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        // Not enabled.
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Enabled for data set, but data set not associated with organisation unit.

        dataSetA.setApproveData( true );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Enabled for data set, and associated with organisation unit C.
        organisationUnitC.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Associated with all the other organisation units.
        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        Date date = new Date();

        // Approved for organisation unit F
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalF ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Also approved also for organisation unit E
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalE ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Also approved for organisation unit D
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalD ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Also approved for organisation unit C
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalC ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Also approved for organisation unit B
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalB ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Also approved for organisation unit A
        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        dataApprovalService.approveData( asList( dataApprovalA ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        // Disable approval for data set.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );
    }

    @Test
    public void testGetDataApprovalStateWithMultipleChildren() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        dataSetA.setApproveData( true );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        organisationUnitD.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Date date = new Date();

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitF, defaultCombo ).getState() );
    }

    @Test
    public void testGetDataApprovalStateOtherPeriodTypes() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level4 );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitD.addDataSet( dataSetA );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodY, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodY, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodD, organisationUnitD, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodW, organisationUnitD, defaultCombo ).getState() );
    }

    @Test
    public void testMayApproveSameLevel() throws Exception
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        // Level 4 (organisationUnitD and organisationUnitF ready)
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 3 (organisationUnitC) and Level 4 (organisationUnitF) ready
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit C." );
        }
        catch ( UserMayNotApproveDataException ex )
        {
            // Expected
        }

        dataApprovalStore.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 2 (organisationUnitB) ready
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit F." );
        }
        catch ( UserMayNotApproveDataException ex )
        {
            // Expected
        }
        dataApprovalStore.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA ) );

        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit E." );
        }
        catch ( UserMayNotApproveDataException ex )
        {
            // Expected
        }
        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) );

        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit C." );
        }
        catch ( UserMayNotApproveDataException ex )
        {
            // Expected
        }
        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) );

        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) ready
        dataApprovalService.approveData( asList( new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) try to approve
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit A." );
        }
        catch ( UserCannotAccessApprovalLevelException ex )
        {
            // Expected
        }
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        // Level 4 (organisationUnitD and organisationUnitF ready)
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 3 (organisationUnitC) and Level 4 (organisationUnitF) ready
        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 2 (organisationUnitB) ready
        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) ready
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit B." );
        }
        catch ( UserMayNotApproveDataException ex )
        {
            // Expected
        }

        dataApprovalStore.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA ) );

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) try to approve
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit A." );
        }
        catch ( UserCannotAccessApprovalLevelException ex )
        {
            // Expected
        }
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        // Level 4 (organisationUnitD and organisationUnitF ready)
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 3 (organisationUnitC) and Level 4 (organisationUnitF) ready
        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 2 (organisationUnitB) ready
        dataApprovalService.approveData( asList( new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) ready
        dataApprovalService.approveData( asList( new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        // Level 1 (organisationUnitA) try to approve
        try
        {
            dataApprovalService.approveData( asList( new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
            fail( "User should not have permission to approve org unit A." );
        }
        catch ( UserCannotAccessApprovalLevelException ex )
        {
            // Expected
        }
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

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) );
        dataApprovalStore.addDataApproval( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) );
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayApprove());

        dataApprovalStore.addDataApproval( new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA ) );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS  );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }
    }

    @Test
    public void testMayUnapproveSameLevel() throws Exception
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalF );
        dataApprovalStore.addDataApproval( dataApprovalE );
        dataApprovalStore.addDataApproval( dataApprovalC );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
    }

    @Test
    public void testMayUnapproveLowerLevels() throws Exception
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalF );
        dataApprovalStore.addDataApproval( dataApprovalE );
        dataApprovalStore.addDataApproval( dataApprovalC );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
    }

    @Test
    public void testMayUnapproveWithUnacceptAuthority() throws Exception
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
        organisationUnitE.addDataSet( dataSetA );
        organisationUnitF.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_ACCEPT_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalF );
        dataApprovalStore.addDataApproval( dataApprovalE );
        dataApprovalStore.addDataApproval( dataApprovalC );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( true, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
    }

    @Test
    public void testMayUnapproveNoAuthority() throws Exception
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
        organisationUnitE.addDataSet( dataSetA );

        Set<OrganisationUnit> units = asSet( organisationUnitB );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dataApprovalA = new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalB = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalC = new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalD = new DataApproval( level4, dataSetA, periodA, organisationUnitD, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalE = new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA );
        DataApproval dataApprovalF = new DataApproval( level4, dataSetA, periodA, organisationUnitF, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalStore.addDataApproval( dataApprovalD );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalF );
        dataApprovalStore.addDataApproval( dataApprovalE );
        dataApprovalStore.addDataApproval( dataApprovalC );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalB );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());

        dataApprovalStore.addDataApproval( dataApprovalA );

        try
        {
            dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS );
            fail("User should not have permission to see org unit A.");
        }
        catch ( Exception e )
        {
            // Expected
        }

        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitD, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitE, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
        assertEquals( false, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitF, NO_GROUPS, NO_OPTIONS ).getPermissions().isMayUnapprove());
    }

    // ---------------------------------------------------------------------
    // Test multi-period approval
    // ---------------------------------------------------------------------

    @Test
    public void testMultiPeriodApproval() throws Exception
    {
        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalLevelService.addDataApprovalLevel( level2 );

        dataSetA.setApproveData( true );

        organisationUnitB.addDataSet( dataSetA );

        Date date = new Date();

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, DataApproval.AUTH_ACCEPT_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        DataApproval dataApprovalJan = new DataApproval( level2, dataSetA, periodA, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        new DataApproval( level2, dataSetA, periodB, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );
        new DataApproval( level2, dataSetA, periodC, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalService.approveData( asList( dataApprovalJan ) );
        dataApprovalService.acceptData( asList( dataApprovalJan ) );

        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );

        DataApproval dataApprovalQ1 = new DataApproval( level2, dataSetA, periodQ, organisationUnitB, defaultCombo, NOT_ACCEPTED, date, userA );

        dataApprovalService.approveData( asList( dataApprovalQ1 ) );

        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );

        // Repeat to make sure we get the same answer (this was a bug.)
        assertEquals( DataApprovalState.PARTIALLY_ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.acceptData( asList( dataApprovalQ1 ) );

        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.ACCEPTED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.unacceptData( asList( dataApprovalQ1 ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.unapproveData( asList( dataApprovalQ1 ) );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodB, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodC, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodQ, organisationUnitB, defaultCombo ).getState() );
    }

    // ---------------------------------------------------------------------
    // Test with Categories
    // ---------------------------------------------------------------------

    @Test
    public void testApprovalStateWithCategories() throws Exception
    {
        setUpCategories();

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Set<CategoryOptionGroup> groupABSet = asSet( groupAB );

        Set<DataElementCategoryOption> optionsAE = asSet( optionA, optionE );
        Set<DataElementCategoryOption> optionsAF = asSet( optionA, optionF );
        Set<DataElementCategoryOption> optionsAG = asSet( optionA, optionG );

        Set<DataElementCategoryOption> optionsCE = asSet( optionC, optionE );
        Set<DataElementCategoryOption> optionsCF = asSet( optionC, optionF );
        Set<DataElementCategoryOption> optionsCG = asSet( optionC, optionG );

        Set<DataElementCategoryOption> optionsEF = asSet( optionE, optionF );

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

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsAE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, optionsAE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsAE ).getState() );

        dataApprovalLevelService.addDataApprovalLevel( level2ABCD ); // Groups AB, CD. Options A,B,C,D.

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getState() );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsAE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, optionsAF ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsAG ).getState() );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsCE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, optionsCF ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsCG ).getState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsEF ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level2ABCD, dataSetA, periodA, organisationUnitB, optionComboAE, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_HERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, groupABSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, groupABSet, NO_OPTIONS ).getState() );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsAE ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_HERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, optionsAF ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsAG ).getState() );

        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsCE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitB, NO_GROUPS, optionsCF ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsCG ).getState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitC, NO_GROUPS, optionsEF ).getState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getState() );
        assertEquals( DataApprovalState.PARTIALLY_APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAF ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboAG ).getState() );

        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboCE ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboCF ).getState() );
        assertEquals( DataApprovalState.UNAPPROVABLE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, optionComboCG ).getState() );
    }

/*
    //TODO: convert tests below for changes in service.
    @Test
    public void testApprovalLevelWithCategories() throws Exception
    {
        setUpCategories();

        dataSetA.setApproveData( true );

        organisationUnitA.addDataSet( dataSetA );
        organisationUnitB.addDataSet( dataSetA );

        Set<CategoryOptionGroup> groupASet = asSet( groupAB ); // GroupA is a member of DataSetA
        Set<CategoryOptionGroup> groupBSet = asSet( groupCD );
        Set<CategoryOptionGroup> groupCSet = asSet( groupEF );
        Set<CategoryOptionGroup> groupXSet = asSet( groupAB, groupEF );

        Set<DataElementCategoryOption> optionsAE = asSet( optionA, optionE );

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, AUTH_APPR_LEVEL );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        Date date = new Date();

        DataApproval dab = new DataApproval( level1EFGH, dataSetA, periodA, organisationUnitA, groupCD, NOT_ACCEPTED, date, userA );

        dataApprovalLevelService.addDataApprovalLevel( level1EFGH );
        dataApprovalService.approveData( asList( dab ) );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalStatus().getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalStatus().getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalStatus().getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalStatus().getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalStatus().getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionsAE ).getDataApprovalStatus().getState() );

        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalStatus().getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, NO_OPTIONS ).getDataApprovalStatus().getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalStatus().getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalStatus().getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalStatus().getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatusAndPermissions( dataSetA, periodA, organisationUnitA, NO_GROUPS, optionComboAE ).getDataApprovalStatus().getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getState() );

        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertNull( dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getDataApprovalLevel() );

        dataApprovalLevelService.addDataApprovalLevel( level1ABCD );
        dataApprovalService.approveData( asList( new DataApproval( level1ABCD, dataSetA, periodA, organisationUnitA, groupAB, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getDataApprovalLevel() );

        dataApprovalService.unapproveData( asList( dab ) );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1EFGH, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getState() );

        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( null, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1ABCD, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getDataApprovalLevel() );

        dataApprovalLevelService.addDataApprovalLevel( level1 );
        dataApprovalService.approveData( asList( new DataApproval( level1, dataSetA, periodA, organisationUnitA, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getState() );

        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitA, optionComboAE ).getDataApprovalLevel() );

        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getState() );

        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, NO_COMBOS, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupASet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupBSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupCSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, groupXSet, NO_OPTIONS ).getDataApprovalLevel() );
        assertEquals( level1, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, optionComboAE ).getDataApprovalLevel() );
    }

    @Test
    public void testCategoriesWithOrgUnits_2Levels() throws Exception
    {
        setUpCategories();

        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3ABCD );

        dataSetA.setApproveData( true );

        organisationUnitC.addDataSet( dataSetA );
        organisationUnitE.addDataSet( dataSetA );

        Date date = new Date();

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, DataApproval.AUTH_ACCEPT_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        optionA.setOrganisationUnits( asSet( organisationUnitC ) );
        optionB.setOrganisationUnits( asSet( organisationUnitE ) );
        optionC.setOrganisationUnits( asSet( organisationUnitE ) );
        optionD.setOrganisationUnits( asSet( organisationUnitE ) );

        categoryService.updateDataElementCategoryOption( optionA );
        categoryService.updateDataElementCategoryOption( optionB );
        categoryService.updateDataElementCategoryOption( optionC );
        categoryService.updateDataElementCategoryOption( optionD );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitC, groupAB, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitE, groupAB, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitE, groupCD, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
    }

    @Test
    public void testCategoriesWithOrgUnits_3Levels() throws Exception
    {
        setUpCategories();

        dataApprovalLevelService.addDataApprovalLevel( level2 );
        dataApprovalLevelService.addDataApprovalLevel( level3 );
        dataApprovalLevelService.addDataApprovalLevel( level3ABCD );

        dataSetA.setApproveData( true );

        organisationUnitC.addDataSet( dataSetA );
        organisationUnitE.addDataSet( dataSetA );

        Date date = new Date();

        Set<OrganisationUnit> units = asSet( organisationUnitA );

        CurrentUserService currentUserService = new MockCurrentUserService( units, null, AUTH_APPR_LEVEL, DataApproval.AUTH_APPROVE, DataApproval.AUTH_APPROVE_LOWER_LEVELS, DataApproval.AUTH_ACCEPT_LOWER_LEVELS );
        setDependency( dataApprovalService, "currentUserService", currentUserService, CurrentUserService.class );
        setDependency( dataApprovalLevelService, "currentUserService", currentUserService, CurrentUserService.class );

        optionA.setOrganisationUnits( asSet( organisationUnitC ) );
        optionB.setOrganisationUnits( asSet( organisationUnitE ) );
        optionC.setOrganisationUnits( asSet( organisationUnitE ) );
        optionD.setOrganisationUnits( asSet( organisationUnitE ) );

        categoryService.updateDataElementCategoryOption( optionA );
        categoryService.updateDataElementCategoryOption( optionB );
        categoryService.updateDataElementCategoryOption( optionC );
        categoryService.updateDataElementCategoryOption( optionD );

        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitC, groupAB, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitE, groupAB, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3ABCD, dataSetA, periodA, organisationUnitE, groupCD, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_WAITING, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );

        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitC, defaultCombo, NOT_ACCEPTED, date, userA ) ) );
        dataApprovalService.approveData( asList( new DataApproval( level3, dataSetA, periodA, organisationUnitE, defaultCombo, NOT_ACCEPTED, date, userA ) ) );

        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupAB ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_ELSEWHERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, asSet( groupCD ), NO_OPTIONS ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitC, defaultCombo ).getState() );
        assertEquals( DataApprovalState.APPROVED_HERE, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitE, defaultCombo ).getState() );
        assertEquals( DataApprovalState.UNAPPROVED_READY, dataApprovalService.getDataApprovalStatus( dataSetA, periodA, organisationUnitB, defaultCombo ).getState() );
    }
    */
}

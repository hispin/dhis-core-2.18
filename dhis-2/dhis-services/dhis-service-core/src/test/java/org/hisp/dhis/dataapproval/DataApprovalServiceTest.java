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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Date;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 * @version $Id$
 */
public class DataApprovalServiceTest
        extends DhisSpringTest
{
    @Autowired
    private DataApprovalService dataApprovalService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    // -------------------------------------------------------------------------
    // Supporting data
    // -------------------------------------------------------------------------

    private DataSet dataSetA;

    private DataSet dataSetB;

    private Period periodA;

    private Period periodB;

    private OrganisationUnit organisationUnitA;

    private OrganisationUnit organisationUnitB;

    private OrganisationUnit organisationUnitC;

    private OrganisationUnit organisationUnitD;

    private User userA;

    private User userB;

    private DataElementCategoryOptionCombo attributeOptionCombo;
    
    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest() throws Exception
    {
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

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );

        organisationUnitA = createOrganisationUnit( 'A' );
        organisationUnitB = createOrganisationUnit( 'B', organisationUnitA );
        organisationUnitC = createOrganisationUnit( 'C', organisationUnitB );
        organisationUnitD = createOrganisationUnit( 'D', organisationUnitC );

        organisationUnitService.addOrganisationUnit( organisationUnitA );
        organisationUnitService.addOrganisationUnit( organisationUnitB );
        organisationUnitService.addOrganisationUnit( organisationUnitC );
        organisationUnitService.addOrganisationUnit( organisationUnitD );

        userA = createUser( 'A' );
        userB = createUser( 'B' );

        userService.addUser( userA );
        userService.addUser( userB );
        
        attributeOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
    }

    // -------------------------------------------------------------------------
    // Basic DataApproval
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGetDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodB, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetB, periodA, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalE;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        dataApprovalA = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo );
        assertNotNull( dataApprovalA );
        assertEquals( dataSetA.getId(), dataApprovalA.getDataSet().getId() );
        assertEquals( periodA, dataApprovalA.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalA.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalA.getCreated() );
        assertEquals( userA.getId(), dataApprovalA.getCreator().getId() );

        dataApprovalB = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo );
        assertNotNull( dataApprovalB );
        assertEquals( dataSetA.getId(), dataApprovalB.getDataSet().getId() );
        assertEquals( periodA, dataApprovalB.getPeriod() );
        assertEquals( organisationUnitB.getId(), dataApprovalB.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalB.getCreated() );
        assertEquals( userA.getId(), dataApprovalB.getCreator().getId() );

        dataApprovalC = dataApprovalService.getDataApproval( dataSetA, periodB, organisationUnitA, attributeOptionCombo );
        assertNotNull( dataApprovalC );
        assertEquals( dataSetA.getId(), dataApprovalC.getDataSet().getId() );
        assertEquals( periodB, dataApprovalC.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalC.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalC.getCreated() );
        assertEquals( userA.getId(), dataApprovalC.getCreator().getId() );

        dataApprovalD = dataApprovalService.getDataApproval( dataSetB, periodA, organisationUnitA, attributeOptionCombo );
        assertNotNull( dataApprovalD );
        assertEquals( dataSetB.getId(), dataApprovalD.getDataSet().getId() );
        assertEquals( periodA, dataApprovalD.getPeriod() );
        assertEquals( organisationUnitA.getId(), dataApprovalD.getOrganisationUnit().getId() );
        assertEquals( date, dataApprovalD.getCreated() );
        assertEquals( userA.getId(), dataApprovalD.getCreator().getId() );

        dataApprovalE = dataApprovalService.getDataApproval( dataSetB, periodB, organisationUnitB, attributeOptionCombo );
        assertNull( dataApprovalE );
    }

    @Test
    public void testAddDuplicateDataApproval() throws Exception
    {
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo, date, userA );

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
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo, date, userB );
        DataApproval testA;
        DataApproval testB;

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.addDataApproval( dataApprovalB );

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo );
        assertNotNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo );
        assertNotNull( testB );

        dataApprovalService.deleteDataApproval( dataApprovalA ); // Only A should be deleted.

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo );
        assertNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo );
        assertNotNull( testB );

        dataApprovalService.addDataApproval( dataApprovalA );
        dataApprovalService.deleteDataApproval( dataApprovalB ); // A and B should both be deleted.

        testA = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo );
        assertNull( testA );

        testB = dataApprovalService.getDataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo );
        assertNull( testB );
    }

    @Test
    public void testGetDataApprovalState() throws Exception
    {
        // Not enabled.
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitA, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitB, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitC, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitD, attributeOptionCombo ) );

        // Enabled for data set, but data set not associated with organisation unit.
        dataSetA.setApproveData( true );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitA, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitB, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitC, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitD, attributeOptionCombo ) );

        // Enabled for data set, and associated with organisation unit C.
        organisationUnitC.addDataSet( dataSetA );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitA, attributeOptionCombo ) );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitB, attributeOptionCombo ) );
        assertEquals( DataApprovalState.READY_FOR_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitC, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitD, attributeOptionCombo ) );

        // Approved for sourceC
        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitC, attributeOptionCombo, date, userA );
        dataApprovalService.addDataApproval( dataApprovalA );
        assertEquals( DataApprovalState.WAITING_FOR_LOWER_LEVEL_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitA, attributeOptionCombo ) );
        assertEquals( DataApprovalState.READY_FOR_APPROVAL, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitB, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitC, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitD, attributeOptionCombo ) );

        // Disable approval for dataset.
        dataSetA.setApproveData( false );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitA, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitB, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitC, attributeOptionCombo ) );
        assertEquals( DataApprovalState.APPROVAL_NOT_NEEDED, dataApprovalService.getDataApprovalState( dataSetA, periodA, organisationUnitD, attributeOptionCombo ) );
    }

    @Test
    public void testMayApprove() throws Exception
    {
        userB.addOrganisationUnit( organisationUnitB );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitC, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD, userB, false, false ) );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA, userB, false, true ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitB, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD, userB, false, true ) );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitB, userB, true, false ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitC, userB, true, false ) );
        assertEquals( false, dataApprovalService.mayApprove( organisationUnitD, userB, true, false ) );

        assertEquals( false, dataApprovalService.mayApprove( organisationUnitA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayApprove( organisationUnitD, userB, true, true ) );
    }

    @Test
    public void testMayUnapprove() throws Exception
    {
        userA.addOrganisationUnit( organisationUnitA );
        userB.addOrganisationUnit( organisationUnitB );

        Date date = new Date();
        DataApproval dataApprovalA = new DataApproval( dataSetA, periodA, organisationUnitA, attributeOptionCombo, date, userA );
        DataApproval dataApprovalB = new DataApproval( dataSetA, periodA, organisationUnitB, attributeOptionCombo, date, userA );
        DataApproval dataApprovalC = new DataApproval( dataSetA, periodA, organisationUnitC, attributeOptionCombo, date, userA );
        DataApproval dataApprovalD = new DataApproval( dataSetA, periodA, organisationUnitD, attributeOptionCombo, date, userA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC, userB, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD, userB, false, false ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, false, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, false, true ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, false ) );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );

        // If the organisation unit has no parent:
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userA, false, false ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userA, false, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalA, userA, true, false ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalA, userA, true, true ) );

        dataApprovalService.addDataApproval( dataApprovalB );
        dataApprovalService.addDataApproval( dataApprovalC );
        dataApprovalService.addDataApproval( dataApprovalD );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( true, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );

        dataApprovalService.addDataApproval( dataApprovalA );

        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalA, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalB, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalC, userB, true, true ) );
        assertEquals( false, dataApprovalService.mayUnapprove( dataApprovalD, userB, true, true ) );
    }
}

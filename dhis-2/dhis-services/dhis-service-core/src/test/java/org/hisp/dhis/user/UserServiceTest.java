package org.hisp.dhis.user;

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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

/**
 * @author Lars Helge Overland
 */
public class UserServiceTest
    extends DhisSpringTest
{
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    private OrganisationUnit unit1;
    private OrganisationUnit unit2;

    @Override
    public void setUpTest()
        throws Exception
    { 
        unit1 = createOrganisationUnit( 'A' );
        unit2 = createOrganisationUnit( 'B' );

        organisationUnitService.addOrganisationUnit( unit1 );
        organisationUnitService.addOrganisationUnit( unit2 );        
    }

    @Test
    public void testAddGetUser()
    {        
        Set<OrganisationUnit> units = new HashSet<>();
        
        units.add( unit1 );
        units.add( unit2 );

        User userA = createUser( 'A' );
        User userB = createUser( 'B' );
        
        userA.setOrganisationUnits( units );
        userB.setOrganisationUnits( units );

        int idA = userService.addUser( userA );
        int idB = userService.addUser( userB );
        
        assertEquals( userA, userService.getUser( idA ) );
        assertEquals( userB, userService.getUser( idB ) );
        
        assertEquals( units, userService.getUser( idA ).getOrganisationUnits() );
        assertEquals( units, userService.getUser( idB ).getOrganisationUnits() );
    }

    @Test
    public void testUpdateUser()
    {
        User userA = createUser( 'A' );
        User userB = createUser( 'B' );

        int idA = userService.addUser( userA );
        int idB = userService.addUser( userB );

        assertEquals( userA, userService.getUser( idA ) );
        assertEquals( userB, userService.getUser( idB ) );
        
        userA.setSurname( "UpdatedSurnameA" );
        
        userService.updateUser( userA );
        
        assertEquals( userService.getUser( idA ).getSurname(), "UpdatedSurnameA" );
    }
    
    @Test
    public void testDeleteUser()
    {
        User userA = createUser( 'A' );
        User userB = createUser( 'B' );

        int idA = userService.addUser( userA );
        int idB = userService.addUser( userB );

        assertEquals( userA, userService.getUser( idA ) );
        assertEquals( userB, userService.getUser( idB ) );
        
        userService.deleteUser( userA );
        
        assertNull( userService.getUser( idA ) );
        assertNotNull( userService.getUser( idB ) );
    }
    
    @Test
    public void testManagedGroups()
    {
        User userA = createUser( 'A' );
        User userB = createUser( 'B' );
        User userC = createUser( 'C' );
        User userD = createUser( 'D' );
        
        userService.addUser( userA );
        userService.addUser( userB );
        userService.addUser( userC );
        userService.addUser( userD );
        
        UserGroup userGroup1 = createUserGroup( 'A', Sets.newHashSet( userA, userB ) );
        UserGroup userGroup2 = createUserGroup( 'B', Sets.newHashSet( userC, userD ) );
        userA.getGroups().add( userGroup1 );
        userB.getGroups().add( userGroup1 );
        userC.getGroups().add( userGroup2 );
        userD.getGroups().add( userGroup2 );
        
        userGroup1.setManagedGroups( Sets.newHashSet( userGroup2 ) );
        userGroup2.setManagedByGroups( Sets.newHashSet( userGroup1 ) );
        
        int group1 = userGroupService.addUserGroup( userGroup1 );
        int group2 = userGroupService.addUserGroup( userGroup2 );

        assertEquals( 1, userGroupService.getUserGroup( group1 ).getManagedGroups().size() );
        assertTrue( userGroupService.getUserGroup( group1 ).getManagedGroups().contains( userGroup2 ) );
        assertEquals( 1, userGroupService.getUserGroup( group2 ).getManagedByGroups().size() );
        assertTrue( userGroupService.getUserGroup( group2 ).getManagedByGroups().contains( userGroup1 ) );
        
        assertTrue( userA.canManage( userGroup2 ) );
        assertTrue( userB.canManage( userGroup2 ) );
        assertFalse( userC.canManage( userGroup1 ) );
        assertFalse( userD.canManage( userGroup1 ) );

        assertTrue( userA.canManage( userC ) );
        assertTrue( userA.canManage( userD ) );
        assertTrue( userB.canManage( userC ) );
        assertTrue( userA.canManage( userD ) );
        assertFalse( userC.canManage( userA ) );
        assertFalse( userC.canManage( userB ) );
        
        assertTrue( userC.isManagedBy( userGroup1 ) );
        assertTrue( userD.isManagedBy( userGroup1 ) );
        assertFalse( userA.isManagedBy( userGroup2 ) );
        assertFalse( userB.isManagedBy( userGroup2 ) );

        assertTrue( userC.isManagedBy( userA ) );
        assertTrue( userC.isManagedBy( userB ) );
        assertTrue( userD.isManagedBy( userA ) );
        assertTrue( userD.isManagedBy( userB ) );
        assertFalse( userA.isManagedBy( userC ) );
        assertFalse( userA.isManagedBy( userD ) );
    }

    @Test
    public void testGetManagedGroups()
    {
        User userA = createUser( 'A' );
        User userB = createUser( 'B' );
        User userC = createUser( 'C' );
        User userD = createUser( 'D' );
        
        userService.addUser( userA );
        userService.addUser( userB );
        userService.addUser( userC );
        userService.addUser( userD );
        
        UserGroup userGroup1 = createUserGroup( 'A', Sets.newHashSet( userA, userB ) );
        UserGroup userGroup2 = createUserGroup( 'B', Sets.newHashSet( userC, userD ) );
        userA.getGroups().add( userGroup1 );
        userB.getGroups().add( userGroup1 );
        userC.getGroups().add( userGroup2 );
        userD.getGroups().add( userGroup2 );
        
        userGroup1.setManagedGroups( Sets.newHashSet( userGroup2 ) );
        userGroup2.setManagedByGroups( Sets.newHashSet( userGroup1 ) );
        
        userGroupService.addUserGroup( userGroup1 );
        userGroupService.addUserGroup( userGroup2 );
        
        List<User> users = userService.getManagedUsers( userA );
        
        assertEquals( 2, users.size() );
        assertTrue( users.contains( userC ) );
        assertTrue( users.contains( userD ) );

        users = userService.getManagedUsersBetween( userA, 0, 1 );
        
        assertEquals( 1, users.size() );

        users = userService.getManagedUsers( userB );
        
        assertEquals( 2, users.size() );
        assertTrue( users.contains( userC ) );
        assertTrue( users.contains( userD ) );

        users = userService.getManagedUsersBetween( userB, 0, 1 );
        
        assertEquals( 1, users.size() );

        users = userService.getManagedUsers( userC );
        
        assertEquals( 0, users.size() );
    }
}

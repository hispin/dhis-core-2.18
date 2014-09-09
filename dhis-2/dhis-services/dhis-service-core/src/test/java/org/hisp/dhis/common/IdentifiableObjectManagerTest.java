package org.hisp.dhis.common;

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

import com.google.common.collect.Sets;
import org.hibernate.SessionFactory;
import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.acl.AccessStringHelper;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupAccess;
import org.hisp.dhis.user.UserService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class IdentifiableObjectManagerTest
    extends DhisSpringTest
{
    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DataElementService dataElementService;

    @Override
    protected void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );
    }

    @Test
    public void testGetObject()
    {
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );

        int dataElementIdA = dataElementService.addDataElement( dataElementA );
        int dataElementIdB = dataElementService.addDataElement( dataElementB );

        DataElementGroup dataElementGroupA = createDataElementGroup( 'A' );
        DataElementGroup dataElementGroupB = createDataElementGroup( 'B' );

        int dataElementGroupIdA = dataElementService.addDataElementGroup( dataElementGroupA );
        int dataElementGroupIdB = dataElementService.addDataElementGroup( dataElementGroupB );

        assertEquals( dataElementA, identifiableObjectManager.getObject( dataElementIdA, DataElement.class.getSimpleName() ) );
        assertEquals( dataElementB, identifiableObjectManager.getObject( dataElementIdB, DataElement.class.getSimpleName() ) );

        assertEquals( dataElementGroupA, identifiableObjectManager.getObject( dataElementGroupIdA, DataElementGroup.class.getSimpleName() ) );
        assertEquals( dataElementGroupB, identifiableObjectManager.getObject( dataElementGroupIdB, DataElementGroup.class.getSimpleName() ) );
    }

    @Test
    public void publicAccessSetIfNoUser()
    {
        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        assertNotNull( dataElement.getPublicAccess() );
        assertFalse( AccessStringHelper.canRead( dataElement.getPublicAccess() ) );
        assertFalse( AccessStringHelper.canWrite( dataElement.getPublicAccess() ) );
    }

    @Test
    public void getCount()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataElement.class ) );
    }

    @Test
    public void getCountByName()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 1, identifiableObjectManager.getCountLikeName( DataElement.class, "DataElementA" ) );
        assertEquals( 1, identifiableObjectManager.getCountLikeName( DataElement.class, "DataElementB" ) );
        assertEquals( 1, identifiableObjectManager.getCountLikeName( DataElement.class, "DataElementC" ) );
        assertEquals( 1, identifiableObjectManager.getCountLikeName( DataElement.class, "DataElementD" ) );
    }

    @Test
    public void getEqualToName()
    {
        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        assertNotNull( identifiableObjectManager.getByName( DataElement.class, "DataElementA" ) );
        assertNull( identifiableObjectManager.getByName( DataElement.class, "DataElementB" ) );
        assertEquals( dataElement, identifiableObjectManager.getByName( DataElement.class, "DataElementA" ) );
    }

    @Test
    public void getAllEqualToName()
    {
        OrganisationUnit organisationUnitA1 = createOrganisationUnit( 'A' );
        organisationUnitA1.setCode( null );
        identifiableObjectManager.save( organisationUnitA1 );

        OrganisationUnit organisationUnitA2 = createOrganisationUnit( 'B' );
        organisationUnitA2.setName( "OrganisationUnitA" );
        organisationUnitA2.setCode( null );
        identifiableObjectManager.save( organisationUnitA2 );

        assertEquals( 2, identifiableObjectManager.getAllByName( OrganisationUnit.class, "OrganisationUnitA" ).size() );
        assertEquals( 0, identifiableObjectManager.getAllByName( OrganisationUnit.class, "organisationunita" ).size() );
    }

    @Test
    public void getAllEqualToNameIgnoreCase()
    {
        OrganisationUnit organisationUnitA1 = createOrganisationUnit( 'A' );
        organisationUnitA1.setCode( null );
        identifiableObjectManager.save( organisationUnitA1 );

        OrganisationUnit organisationUnitA2 = createOrganisationUnit( 'B' );
        organisationUnitA2.setName( "OrganisationUnitA" );
        organisationUnitA2.setCode( null );
        identifiableObjectManager.save( organisationUnitA2 );

        assertEquals( 2, identifiableObjectManager.getAllByNameIgnoreCase( OrganisationUnit.class, "OrganisationUnitA" ).size() );
        assertEquals( 2, identifiableObjectManager.getAllByNameIgnoreCase( OrganisationUnit.class, "organisationunita" ).size() );
    }

    @Test
    public void getAllLikeName()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCountLikeName( DataElement.class, "DataElement" ) );
        assertEquals( 4, identifiableObjectManager.getCountLikeName( DataElement.class, "dataElement" ) );

        assertEquals( 4, identifiableObjectManager.getLikeName( DataElement.class, "DataElement" ).size() );
        assertEquals( 4, identifiableObjectManager.getLikeName( DataElement.class, "dataElement" ).size() );
    }

    @Test
    public void getAllLikeShortName()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCountLikeShortName( DataElement.class, "DataElementShort" ) );
        assertEquals( 4, identifiableObjectManager.getCountLikeShortName( DataElement.class, "dataElementSHORT" ) );

        assertEquals( 4, identifiableObjectManager.getLikeShortName( DataElement.class, "DataElementShort" ).size() );
        assertEquals( 4, identifiableObjectManager.getLikeShortName( DataElement.class, "dataElementSHORT" ).size() );
    }

    @Test
    public void getAllOrderedName()
    {
        identifiableObjectManager.save( createDataElement( 'D' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'A' ) );

        List<DataElement> dataElements = new ArrayList<>( identifiableObjectManager.getAllSorted( DataElement.class ) );

        assertEquals( 4, dataElements.size() );
        assertEquals( "DataElementA", dataElements.get( 0 ).getName() );
        assertEquals( "DataElementB", dataElements.get( 1 ).getName() );
        assertEquals( "DataElementC", dataElements.get( 2 ).getName() );
        assertEquals( "DataElementD", dataElements.get( 3 ).getName() );
    }

    @Test
    public void getAllOrderedLastUpdated()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        List<DataElement> dataElements = new ArrayList<>( identifiableObjectManager.getAllSortedByLastUpdated( DataElement.class ) );

        assertEquals( 4, dataElements.size() );
        assertEquals( "DataElementD", dataElements.get( 0 ).getName() );
        assertEquals( "DataElementC", dataElements.get( 1 ).getName() );
        assertEquals( "DataElementB", dataElements.get( 2 ).getName() );
        assertEquals( "DataElementA", dataElements.get( 3 ).getName() );
    }

    @Test
    public void userIsCurrentIfNoUserSet()
    {
        User user = createUserAndInjectSecurityContext( true );

        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        assertNotNull( dataElement.getUser() );
        assertEquals( user, dataElement.getUser() );
    }

    @Test
    public void userCanCreatePublic()
    {
        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD" );

        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        assertNotNull( dataElement.getPublicAccess() );
        assertTrue( AccessStringHelper.canRead( dataElement.getPublicAccess() ) );
        assertTrue( AccessStringHelper.canWrite( dataElement.getPublicAccess() ) );
    }

    @Test
    public void userCanCreatePrivate()
    {
        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PRIVATE_ADD" );

        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        assertNotNull( dataElement.getPublicAccess() );
        assertFalse( AccessStringHelper.canRead( dataElement.getPublicAccess() ) );
        assertFalse( AccessStringHelper.canWrite( dataElement.getPublicAccess() ) );
    }

    @Test( expected = CreateAccessDeniedException.class )
    public void userDeniedCreateObject()
    {
        createUserAndInjectSecurityContext( false );
        identifiableObjectManager.save( createDataElement( 'A' ) );
    }

    @Test( expected = DeleteAccessDeniedException.class )
    public void userDeniedDeleteObject()
    {
        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        dataElement.setUser( user );
        dataElement.setPublicAccess( AccessStringHelper.DEFAULT );
        sessionFactory.getCurrentSession().update( dataElement );

        identifiableObjectManager.delete( dataElement );
    }

    @Test
    public void objectsWithNoUser()
    {
        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataElement.class ) );
        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );
    }

    @Test
    public void readPrivateObjects()
    {
        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );

        List<DataElement> dataElements = new ArrayList<>( identifiableObjectManager.getAll( DataElement.class ) );

        for ( DataElement dataElement : dataElements )
        {
            dataElement.setUser( user );
            dataElement.setPublicAccess( AccessStringHelper.DEFAULT );

            sessionFactory.getCurrentSession().update( dataElement );
        }

        assertEquals( 0, identifiableObjectManager.getCount( DataElement.class ) );
        assertEquals( 0, identifiableObjectManager.getAll( DataElement.class ).size() );
    }

    @Test
    public void readUserGroupSharedObjects()
    {
        User loginUser = createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD", "F_USERGROUP_PUBLIC_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        UserGroup userGroup = createUserGroup( 'A', Sets.newHashSet( loginUser ) );
        identifiableObjectManager.save( userGroup );

        identifiableObjectManager.save( createDataElement( 'A' ) );
        identifiableObjectManager.save( createDataElement( 'B' ) );
        identifiableObjectManager.save( createDataElement( 'C' ) );
        identifiableObjectManager.save( createDataElement( 'D' ) );

        assertEquals( 4, identifiableObjectManager.getCount( DataElement.class ) );
        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );

        List<DataElement> dataElements = new ArrayList<>( identifiableObjectManager.getAll( DataElement.class ) );

        for ( DataElement dataElement : dataElements )
        {
            dataElement.setUser( user );
            dataElement.setPublicAccess( AccessStringHelper.newInstance().build() );

            UserGroupAccess userGroupAccess = new UserGroupAccess();
            userGroupAccess.setAccess( AccessStringHelper.READ );
            userGroupAccess.setUserGroup( userGroup );

            sessionFactory.getCurrentSession().save( userGroupAccess );

            dataElement.getUserGroupAccesses().add( userGroupAccess );
            sessionFactory.getCurrentSession().update( dataElement );
        }

        assertEquals( 4, identifiableObjectManager.getCount( DataElement.class ) );
        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );
    }
}

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
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.dataelement.DataElement;
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
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class SharingTest
    extends DhisSpringTest
{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    protected void setUpTest() throws Exception
    {
        identifiableObjectManager = (IdentifiableObjectManager) getBean( IdentifiableObjectManager.ID );
        userService = (UserService) getBean( UserService.ID );
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

    @Test(expected = CreateAccessDeniedException.class)
    public void userDeniedCreateObject()
    {
        createUserAndInjectSecurityContext( false );
        identifiableObjectManager.save( createDataElement( 'A' ) );
    }

    @Test(expected = DeleteAccessDeniedException.class)
    public void userDeniedDeleteObject()
    {
        createUserAndInjectSecurityContext( false, "F_DATAELEMENT_PUBLIC_ADD", "F_USER_ADD" );

        User user = createUser( 'B' );
        identifiableObjectManager.save( user );

        DataElement dataElement = createDataElement( 'A' );
        identifiableObjectManager.save( dataElement );

        dataElement.setUser( user );
        dataElement.setPublicAccess( AccessStringHelper.newInstance().build() );
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

        Collection<DataElement> all = identifiableObjectManager.getAll( DataElement.class );

        assertEquals( 4, all.size() );
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
            dataElement.setPublicAccess( AccessStringHelper.newInstance().build() );

            sessionFactory.getCurrentSession().update( dataElement );
        }

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

        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );

        List<DataElement> dataElements = new ArrayList<>( identifiableObjectManager.getAll( DataElement.class ) );

        for ( DataElement dataElement : dataElements )
        {
            dataElement.setUser( user );
            dataElement.setPublicAccess( AccessStringHelper.newInstance().build() );

            UserGroupAccess userGroupAccess = new UserGroupAccess();
            userGroupAccess.setAccess( AccessStringHelper.newInstance().enable( AccessStringHelper.Permission.READ ).build() );
            userGroupAccess.setUserGroup( userGroup );

            sessionFactory.getCurrentSession().save( userGroupAccess );

            dataElement.getUserGroupAccesses().add( userGroupAccess );
            sessionFactory.getCurrentSession().update( dataElement );
        }

        assertEquals( 4, identifiableObjectManager.getAll( DataElement.class ).size() );
    }
}

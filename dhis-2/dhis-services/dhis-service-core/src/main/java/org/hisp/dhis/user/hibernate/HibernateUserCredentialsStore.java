package org.hisp.dhis.user.hibernate;

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

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserCredentialsStore;

/**
 * TODO extend BaseIdentifiableObjectStore
 * 
 * @author Lars Helge Overland
 */
public class HibernateUserCredentialsStore
    implements UserCredentialsStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // UserCredentials
    // -------------------------------------------------------------------------

    @Override
    public int addUserCredentials( UserCredentials userCredentials )
    {
        return (Integer) sessionFactory.getCurrentSession().save( userCredentials );
    }

    @Override
    public void updateUserCredentials( UserCredentials userCredentials )
    {
        sessionFactory.getCurrentSession().update( userCredentials );
    }

    @Override
    public UserCredentials getUserCredentials( User user )
    {
        if ( user == null )
        {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();

        return (UserCredentials) session.get( UserCredentials.class, user.getId() );
    }
    
    @Override
    public UserCredentials getUserCredentials( int id )
    {
        return (UserCredentials) sessionFactory.getCurrentSession().get( UserCredentials.class, id );
    }

    @Override
    public UserCredentials getUserCredentialsByUsername( String username )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserCredentials uc where uc.username = :username" );

        query.setString( "username", username );
        query.setCacheable( true );

        return (UserCredentials) query.uniqueResult();
    }

    @Override
    public UserCredentials getUserCredentialsByOpenID( String openId )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "from UserCredentials uc where uc.openId = :openId" );

        query.setString( "openId", openId );
        query.setCacheable( true );

        return (UserCredentials) query.uniqueResult();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Collection<UserCredentials> getAllUserCredentials()
    {
        Session session = sessionFactory.getCurrentSession();

        return session.createCriteria( UserCredentials.class ).list();
    }

    @Override
    public void deleteUserCredentials( UserCredentials userCredentials )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( userCredentials );
    }
}

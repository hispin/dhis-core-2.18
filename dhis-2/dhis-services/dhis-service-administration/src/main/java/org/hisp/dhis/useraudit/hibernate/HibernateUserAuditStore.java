package org.hisp.dhis.useraudit.hibernate;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.useraudit.LoginFailure;
import org.hisp.dhis.useraudit.UserAuditStore;

/**
 * @author Lars Helge Overland
 */
public class HibernateUserAuditStore
    implements UserAuditStore
{
    private SessionFactory sessionFactory;
    
    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    public void saveLoginFailure( LoginFailure login )
    {
        sessionFactory.getCurrentSession().save( login );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<LoginFailure> getAllLoginFailures()
    {
        return sessionFactory.getCurrentSession().createCriteria( LoginFailure.class ).list();
    }
    
    public void deleteLoginFailures( String username )
    {
        String hql = "delete from LoginFailure where username = :username";
        
        sessionFactory.getCurrentSession().createQuery( hql ).setString( "username", username ).executeUpdate();
    }
        
    public int getLoginFailures( String username, Date date )
    {
        Session session = sessionFactory.getCurrentSession();
        
        String hql = "delete from LoginFailure where date < :date";
        
        session.createQuery( hql ).setDate( "date", date ).executeUpdate();
        
        hql = "select count(*) from LoginFailure where username = :username";
        
        Long no = (Long) session.createQuery( hql ).setString( "username", username ).uniqueResult();
        
        return no.intValue();
    }
}

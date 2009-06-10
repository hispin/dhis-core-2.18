package org.hisp.dhis.options.setting;

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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hisp.dhis.options.SystemSetting;
import org.hisp.dhis.options.SystemSettingStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Stian Strandli
 * @author Lars Helge Overland
 * @version $Id: HibernateSystemSettingStore.java 6530 2008-11-28 15:02:47Z eivindwa $
 */
@Transactional
public class HibernateSystemSettingStore
    implements SystemSettingStore
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
    // Implementations
    // -------------------------------------------------------------------------

    public void addSystemSetting( SystemSetting setting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( setting );
    }

    public void updateSystemSetting( SystemSetting setting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( setting );
    }

    public void deleteSystemSetting( SystemSetting setting )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( setting );
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SystemSetting> getAllSystemSettings()
    {
        Session session = sessionFactory.getCurrentSession();
        
        return session.createQuery( "from SystemSetting " ).setCacheable( true ).list();
    }

    public SystemSetting getSystemSetting( String name )
    {
        Session session = sessionFactory.getCurrentSession();
        
        Query query = session.createQuery( "from SystemSetting where name = :name" );
        
        query.setString( "name", name );
        query.setCacheable( true );

        return (SystemSetting) query.uniqueResult();
    }
}

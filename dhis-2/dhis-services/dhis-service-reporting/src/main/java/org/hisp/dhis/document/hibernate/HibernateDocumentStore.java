package org.hisp.dhis.document.hibernate;

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

import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentStore;
import org.hisp.dhis.hibernate.HibernateSessionManager;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class HibernateDocumentStore
    implements DocumentStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private HibernateSessionManager sessionManager;

    public void setSessionManager( HibernateSessionManager sessionManager )
    {
        this.sessionManager = sessionManager;
    }

    // -------------------------------------------------------------------------
    // DocumentStore implementation
    // -------------------------------------------------------------------------

    public int saveDocument( Document document )
    {
        return (Integer) sessionManager.getCurrentSession().save( document );
    }
    
    public Document getDocument( int id )
    {
        return (Document) sessionManager.getCurrentSession().get( Document.class, id );
    }
    
    public void deleteDocument( Document document )
    {
        sessionManager.getCurrentSession().delete( document );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Document> getAllDocuments()
    {
        return sessionManager.getCurrentSession().createCriteria( Document.class ).list();
    }
    
    public Document getDocumentByName( String name )
    {
        return (Document) sessionManager.getCurrentSession().
            createCriteria( Document.class ).add( Restrictions.eq( "name", name ) ).uniqueResult();
    } 
}

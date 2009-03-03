package org.hisp.dhis.expression.hibernate;

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

import org.hibernate.Session;
import org.hisp.dhis.expression.Expression;
import org.hisp.dhis.expression.ExpressionStore;
import org.hisp.dhis.hibernate.HibernateSessionManager;

/**
 * @author Margrethe Store
 * @version $Id: HibernateExpressionStore.java 6246 2008-11-10 11:33:17Z larshelg $
 */
public class HibernateExpressionStore 
    implements ExpressionStore
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
    // Expression
    // -------------------------------------------------------------------------

    public int addExpression( Expression expression )
    {
        Session session = sessionManager.getCurrentSession();

        return (Integer) session.save( expression );
    }
    
    public void deleteExpression( Expression expression )
    {
        Session session = sessionManager.getCurrentSession();

        session.delete( expression );
    }

    public Expression getExpression( int id )
    {
        Session session = sessionManager.getCurrentSession();

        return (Expression) session.get( Expression.class, id );
    }

    public void updateExpression( Expression expression )
    {
        Session session = sessionManager.getCurrentSession();

        session.update( expression );
    }
    
    @SuppressWarnings( "unchecked" )
    public Collection<Expression> getAllExpressions()
    {
        Session session = sessionManager.getCurrentSession();

        return session.createCriteria( Expression.class ).list();
    }
}

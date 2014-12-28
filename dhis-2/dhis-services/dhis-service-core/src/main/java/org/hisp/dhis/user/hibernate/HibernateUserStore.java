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
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserQueryParams;
import org.hisp.dhis.user.UserStore;

/**
 * @author Nguyen Hong Duc
 */
public class HibernateUserStore
    extends HibernateIdentifiableObjectStore<User>
    implements UserStore
{
    // -------------------------------------------------------------------------
    // UserStore implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllOrderedName( int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllLikeName( String name, int first, int max )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.ilike( "surname", "%" + name + "%" ),
            Restrictions.ilike( "firstName", "%" + name + "%" ) ) );
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );
        criteria.setFirstResult( first );
        criteria.setMaxResults( max );
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getUsersByPhoneNumber( String phoneNumber )
    {
        String hql = "from User u where u.phoneNumber = :phoneNumber";

        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        query.setString( "phoneNumber", phoneNumber );

        return query.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<User> getUsersByName( String name )
    {
        Criteria criteria = getCriteria();
        criteria.add( Restrictions.or( Restrictions.ilike( "surname", "%" + name + "%" ),
            Restrictions.ilike( "firstName", "%" + name + "%" ) ) );
        criteria.addOrder( Order.asc( "surname" ) ).addOrder( Order.asc( "firstName" ) );

        return criteria.list();
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<User> getUsers( UserQueryParams params )
    {
        return getUserQuery( params, false ).list();
    }

    @Override
    public long getUserCount( UserQueryParams params )
    {
        return (Long) getUserQuery( params, true ).uniqueResult();
    }

    private Query getUserQuery( UserQueryParams params, boolean count )
    {
        SqlHelper hlp = new SqlHelper();
        
        String hql = count ? "select count(distinct u) " : "select distinct u ";
        
        hql +=
            "from User u " +
            "inner join u.userCredentials uc " +
            "left join u.groups g ";

        if ( params.getSearchKey() != null )
        {
            hql += hlp.whereAnd() + " (" +
                "lower(u.firstName) like :key " +
                "or lower(u.surname) like :key " +
                "or lower(uc.username) like :key) ";
        }
        
        if ( params.isCanManage() )
        {
            hql += hlp.whereAnd() + " g.id in (:ids) ";
        }
        
        if ( params.isAuthSubset() )
        {
            hql += hlp.whereAnd() + " not exists (" +
                "select uc2 from UserCredentials uc2 " +
                "inner join uc2.userAuthorityGroups ag2 " +
                "inner join ag2.authorities a " +
                "where uc2.id = uc.id " +
                "and a not in (:auths) ) ";
        }
        
        if ( params.isDisjointRoles() )
        {
            hql += hlp.whereAnd() + " not exists (" +
                "select uc3 from UserCredentials uc3 " +
                "inner join uc3.userAuthorityGroups ag3 " +
                "where uc3.id = uc.id " +
                "and ag3.id in (:roles) ) ";
        }
        
        if ( params.getInactiveSince() != null )
        {
            hql += hlp.whereAnd() + " uc.lastLogin < :inactiveSince ";
        }
        
        if ( params.isSelfRegistered() )
        {
            hql += hlp.whereAnd() + " uc.selfRegistered = true ";
        }
        
        if ( params.getOrganisationUnit() != null )
        {
            hql += hlp.whereAnd() + " :organisationUnit in elements(u.organisationUnits) ";
        }
        
        if ( !count )
        {
            hql += "order by u.surname, u.firstName";
        }
        
        Query query = sessionFactory.getCurrentSession().createQuery( hql );
        
        if ( params.getSearchKey() != null )
        {
            query.setString( "key", "%" + params.getSearchKey().toLowerCase() + "%" );
        }
        
        if ( params.isCanManage() && params.getUser() != null )
        {
            Collection<Integer> managedGroups = IdentifiableObjectUtils.getIdentifiers( params.getUser().getManagedGroups() );

            query.setParameterList( "ids", managedGroups );
        }
        
        if ( params.isAuthSubset() && params.getUser() != null )
        {
            Set<String> auths = params.getUser().getUserCredentials().getAllAuthorities();
            
            query.setParameterList( "auths", auths );
        }
        
        if ( params.isDisjointRoles() && params.getUser() != null )
        {
            Collection<Integer> roles = IdentifiableObjectUtils.getIdentifiers( params.getUser().getUserCredentials().getUserAuthorityGroups() );
            
            query.setParameterList( "roles", roles );
        }
        
        if ( params.getInactiveSince() != null )
        {
            query.setDate( "inactiveSince", params.getInactiveSince() );
        }
        
        if ( params.getOrganisationUnit() != null )
        {
            query.setEntity( "organisationUnit", params.getOrganisationUnit() );
        }
        
        if ( params.getFirst() != null )
        {
            query.setFirstResult( params.getFirst() );
        }
        
        if ( params.getMax() != null )
        {
            query.setMaxResults( params.getMax() ).list();
        }
        
        return query;
    }
}

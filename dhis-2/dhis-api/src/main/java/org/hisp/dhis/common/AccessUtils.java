package org.hisp.dhis.common;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroupAccess;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AccessUtils
{
    public static boolean canWrite( User user, IdentifiableObject object )
    {
        if ( defaultAccessIsNull( object ) ) return true;

        if ( user.equals( object.getUser() ) || AccessStringHelper.canWrite( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canWrite( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean canRead( User user, IdentifiableObject object )
    {
        if ( defaultAccessIsNull( object ) ) return true;

        if ( user.equals( object.getUser() ) || AccessStringHelper.canRead( object.getPublicAccess() ) )
        {
            return true;
        }

        for ( UserGroupAccess userGroupAccess : object.getUserGroupAccesses() )
        {
            if ( AccessStringHelper.canRead( userGroupAccess.getAccess() )
                && userGroupAccess.getUserGroup().getMembers().contains( user ) )
            {
                return true;
            }
        }

        return false;
    }

    public static boolean canUpdate( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    public static boolean canDelete( User user, IdentifiableObject object )
    {
        return canWrite( user, object );
    }

    public static boolean canManage( User user, IdentifiableObject object )
    {
        return user.getUserCredentials().getAllAuthorities().contains( "ALL" ) || !defaultAccessIsNull( object ) && canWrite( user, object );
    }

    private static boolean defaultAccessIsNull( IdentifiableObject identifiableObject )
    {
        return identifiableObject.getUser() == null;
    }
}

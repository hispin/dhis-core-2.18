package org.hisp.dhis.sharing;

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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultSharingService implements SharingService
{
    @Autowired
    private SchemaService schemaService;

    public static final List<String> SHARING_OVERRIDE_AUTHORITIES = Arrays.asList( "ALL", "F_METADATA_IMPORT" );

    @Override
    public boolean isSupported( String type )
    {
        return false;
    }

    @Override
    public boolean isSupported( Class<?> klass )
    {
        return false;
    }

    @Override
    public boolean canWrite( User user, IdentifiableObject object )
    {
        return false;
    }

    @Override
    public boolean canRead( User user, IdentifiableObject object )
    {
        return false;
    }

    @Override
    public boolean canUpdate( User user, IdentifiableObject object )
    {
        return false;
    }

    @Override
    public boolean canDelete( User user, IdentifiableObject object )
    {
        return false;
    }

    @Override
    public boolean canManage( User user, IdentifiableObject object )
    {
        return false;
    }

    @Override
    public <T extends IdentifiableObject> boolean canCreatePublic( User user, Class<T> klass )
    {
        return false;
    }

    @Override
    public <T extends IdentifiableObject> boolean canCreatePrivate( User user, Class<T> klass )
    {
        return false;
    }

    @Override
    public <T extends IdentifiableObject> boolean canExternalize( User user, Class<T> klass )
    {
        return false;
    }

    @Override
    public <T extends IdentifiableObject> boolean defaultPublic( Class<T> klass )
    {
        return false;
    }

    @Override
    public Class<? extends IdentifiableObject> classForType( String type )
    {
        return null;
    }

    private boolean haveOverrideAuthority( User user )
    {
        return user == null || CollectionUtils.containsAny( user.getUserCredentials().getAllAuthorities(), SHARING_OVERRIDE_AUTHORITIES );
    }
}

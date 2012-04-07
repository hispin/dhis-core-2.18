package org.hisp.dhis.dxf2.metadata;

/*
 * Copyright (c) 2012, University of Oslo
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


/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class IdScheme
{
    public static final String UID_SCHEME = "uid";
    public static final String CODE_SCHEME = "code";
    public static final String NAME_SCHEME = "name";

    private String scheme;

    private boolean uidScheme;

    private boolean codeScheme;

    private boolean nameScheme;

    public static IdScheme getDefaultIdScheme()
    {
        return new IdScheme( IdScheme.UID_SCHEME );
    }

    public IdScheme( String scheme )
    {
        setScheme( scheme );
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme( String scheme )
    {
        this.scheme = scheme;

        if ( scheme != null )
        {
            uidScheme = scheme.equals( IdScheme.UID_SCHEME );
            nameScheme = scheme.equals( IdScheme.NAME_SCHEME );
            codeScheme = scheme.equals( IdScheme.CODE_SCHEME );
        }
    }

    public boolean isUidScheme()
    {
        return uidScheme;
    }

    public boolean isNameScheme()
    {
        return nameScheme;
    }

    public boolean isCodeScheme()
    {
        return codeScheme;
    }
}

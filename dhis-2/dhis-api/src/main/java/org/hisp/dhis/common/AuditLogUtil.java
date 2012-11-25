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

import org.apache.commons.logging.Log;

public class AuditLogUtil
{
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_READ = "read";
    public static final String ACTION_UPDATE = "updated";
    public static final String ACTION_DELETE = "deleted";

    public static void infoWrapper( Log log, String username, Object object, String action )
    {
        if ( log.isInfoEnabled() )
        {
            if ( username != null && object != null && IdentifiableObject.class.isInstance( object ) )
            {
                IdentifiableObject idObject = (IdentifiableObject) object;
                StringBuilder builder = new StringBuilder();

                builder.append( "'" );
                builder.append( username );
                builder.append( "' " );
                builder.append( action );
                builder.append( " " );
                builder.append( object.getClass().getName() );

                if ( idObject.getName() != null && !idObject.getName().isEmpty() )
                {
                    builder.append( ", name: " );
                    builder.append( idObject.getName() );
                }

                if ( idObject.getUid() != null && !idObject.getUid().isEmpty() )
                {
                    builder.append( ", uid: " );
                    builder.append( idObject.getUid() );
                }

                // String msg = logMessage( username, action, object.getClass().getName(), builder.toString() );
                log.info( builder.toString() );
            }
        }
    }

    /**
     * Generate audit trail logging message
     *
     * @param userName   : Current user name
     * @param action     : user's action ( add, edit, delete )
     * @param objectType : The name of the object that user is working on
     * @param objectName : The value of the name attribute of the object that
     *                   user is working on
     * @return : the audit trail logging message
     */
    public static String logMessage( String userName, String action, String objectType, String objectName )
    {
        return "'" + userName + "' " + action + " " + objectType + " '" + objectName + "'";
    }
}

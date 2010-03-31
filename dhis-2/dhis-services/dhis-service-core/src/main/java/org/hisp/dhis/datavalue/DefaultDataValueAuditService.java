package org.hisp.dhis.datavalue;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Quang Nguyen
 * @version Mar 30, 2010 9:45:56 PM
 */

public class DefaultDataValueAuditService
    implements DataValueAuditService
{
    private static final Log log = LogFactory.getLog( DefaultDataValueAuditService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueAuditStore dataValueAuditStore;

    public void setDataValueAuditStore( DataValueAuditStore dataValueAuditStore )
    {
        this.dataValueAuditStore = dataValueAuditStore;
    }

    public void addDataValueAudit( DataValueAudit dataValueAudit )
    {
        if ( dataValueAudit != null )
        {
            dataValueAuditStore.addDataValueAudit( dataValueAudit );
        }
    }

    public void deleteDataValueAudit( DataValueAudit dataValueAudit )
    {
        dataValueAuditStore.deleteDataValueAudit( dataValueAudit );
    }

    public int deleteDataValuesByDataValue( DataValue dataValue )
    {
        return dataValueAuditStore.deleteDataValuesByDataValue( dataValue );
    }

    public Collection<DataValueAudit> getDataValueAuditByDataValue( DataValue dataValue )
    {
        return dataValueAuditStore.getDataValueAuditByDataValue( dataValue );
    }

    public Collection<DataValueAudit> getAll()
    {
        return dataValueAuditStore.getAll();
    }
}

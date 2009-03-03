/*
 * Copyright (c) 2004-2008, University of Oslo
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
package org.hisp.dhis.integration.rims.action;

import org.hisp.dhis.integration.rims.api.RIMSTable;
import org.hisp.dhis.integration.rims.util.Configuration;

import com.opensymphony.xwork.ActionSupport;

/**
 * @author Leif Arne Storset
 * @version $Id$
 */
public class TruncateAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    
    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------
    private String confirm;
    
    private boolean districtsOnly;
    private String connection;
    
    // -------------------------------------------------------------------------
    // Out parameters
    // -------------------------------------------------------------------------
    private int rowsDeleted;

    private int tablesTruncated;
    public String execute() throws Exception
    {
        if ( confirm != null && confirm.equals( "Yes" ) )
        {
            for( RIMSTable table: new Configuration( connection ).getTables())
            {
                rowsDeleted += 
                ( 
                        districtsOnly 
                        ? table.deleteDistrictData() 
                        : table.truncate() 
                );
                tablesTruncated++;
            }
        }

        return SUCCESS;
    }
    public void setConfirm( String confirm )
    {
        this.confirm = confirm;
    }
    public int getRowsDeleted()
    {
        return rowsDeleted;
    }
    public int getTablesTruncated()
    {
        return tablesTruncated;
    }
    public void setDistrictsOnly( boolean districtsOnly )
    {
        this.districtsOnly = districtsOnly;
    }
    public void setConnection( String connection )
    {
        this.connection = connection;
    }
}

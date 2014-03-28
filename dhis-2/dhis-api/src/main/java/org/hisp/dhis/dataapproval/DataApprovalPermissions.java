package org.hisp.dhis.dataapproval;

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

/**
 * Current status of data approval for a given selection of data from a
 * data set. Also shows what approval-type actions (if any) the current user
 * is permitted to perform on this data selection.
 *
 * @author Jim Grace
 * @version $Id$
 */

public class DataApprovalPermissions
{
    private DataApprovalStatus dataApprovalStatus;

    private boolean mayApprove;

    private boolean mayUnapprove;

    private boolean mayAccept;

    private boolean mayUnaccept;

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataApprovalStatus getDataApprovalStatus()
    {
        return dataApprovalStatus;
    }

    public void setDataApprovalStatus( DataApprovalStatus dataApprovalStatus )
    {
        this.dataApprovalStatus = dataApprovalStatus;
    }

    public boolean isMayApprove()
    {
        return mayApprove;
    }

    public void setMayApprove( boolean mayApprove )
    {
        this.mayApprove = mayApprove;
    }

    public boolean isMayUnapprove()
    {
        return mayUnapprove;
    }

    public void setMayUnapprove( boolean mayUnapprove )
    {
        this.mayUnapprove = mayUnapprove;
    }

    public boolean isMayAccept()
    {
        return mayAccept;
    }

    public void setMayAccept( boolean mayAccept )
    {
        this.mayAccept = mayAccept;
    }

    public boolean isMayUnaccept()
    {
        return mayUnaccept;
    }

    public void setMayUnaccept( boolean mayUnaccept )
    {
        this.mayUnaccept = mayUnaccept;
    }
}

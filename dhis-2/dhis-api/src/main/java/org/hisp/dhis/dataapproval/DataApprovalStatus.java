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
 * data set. Returns the approval state and, if approved for this particular
 * selection, the approval object.
 *
 * @author Jim Grace
 * @version $Id$
 */

public class DataApprovalStatus
{
    /**
     * State of data approval for a given selection of data from a data set.
     */
    private DataApprovalState dataApprovalState;

    /**
     * If the selection of data is approved, the data approval object.
     * If the selection is approved at more than one level, this is the
     * approval object for the highest level of approval.
     */
    private DataApproval dataApproval;

    /**
     * If the selection of data is approved, the data approval level object
     * at which it is approved. If the selection is approved at more than
     * one level, this is for the highest level of approval.
     */
    private DataApprovalLevel dataApprovalLevel;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataApprovalStatus()
    {
    }

    public DataApprovalStatus( DataApprovalState dataApprovalState,
                               DataApproval dataApproval,
                               DataApprovalLevel dataApprovalLevel )
    {
        this.dataApprovalState = dataApprovalState;
        this.dataApproval = dataApproval;
        this.dataApprovalLevel = dataApprovalLevel;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataApprovalState getDataApprovalState()
    {
        return dataApprovalState;
    }

    public void setDataApprovalState( DataApprovalState dataApprovalState )
    {
        this.dataApprovalState = dataApprovalState;
    }

    public DataApproval getDataApproval()
    {
        return dataApproval;
    }

    public void setDataApproval( DataApproval dataApproval )
    {
        this.dataApproval = dataApproval;
    }

    public DataApprovalLevel getDataApprovalLevel()
    {
        return dataApprovalLevel;
    }

    public void setDataApprovalLevel( DataApprovalLevel dataApprovalLevel )
    {
        this.dataApprovalLevel = dataApprovalLevel;
    }
}

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
 * Current state of data approval for a selected combination of data set, period,
 * organisation unit, and category options or category group options.
 *
 * @author Jim Grace
 */
public enum DataApprovalState
{
    /**
     * Data approval does not apply to this selection. (Data is neither
     * "approved" nor "unapproved".)
     */
    UNAPPROVABLE ( DataApprovalBaseState.UNAPPROVABLE, false, false, false, false, false ),

    /**
     * Data is unapproved, and is waiting for some lower-level approval.
     */
    UNAPPROVED_WAITING ( DataApprovalBaseState.UNAPPROVED_NOT_READY, false, true, true, false, false ),

    /**
     * Data is unapproved, and is waiting for approval somewhere else
     * (not approvable here.)
     */
    UNAPPROVED_ELSEWHERE ( DataApprovalBaseState.UNAPPROVED_NOT_READY, false, true, false, false, false ),

    /**
     * Data is unapproved, and is ready to be approved for this selection.
     */
    UNAPPROVED_READY ( DataApprovalBaseState.UNAPPROVED_READY, false, true, true, false, true ),

    /**
     * Data is approved, and was approved here (so could be unapproved here.)
     */
    APPROVED_HERE ( DataApprovalBaseState.APPROVED, true, false, true, false, false ),

    /**
     * Data is approved, but was not approved here (so cannot be unapproved here.)
     * This covers the following cases:
     * <ul>
     * <li>Data is approved at a higher level.</li>
     * <li>Data is approved for wider scope of category options.</li>
     * <li>Data is approved for all sub-periods in selected period.</li>
     * </ul>
     * In the first two cases, there is a single data approval object
     * that covers the selection. In the third case there is not.
     */
    APPROVED_ELSEWHERE( DataApprovalBaseState.APPROVED, true, false, false, false, false ),

    /**
     * Data is approved and accepted here (so could be unapproved here.)
     */
    ACCEPTED_HERE ( DataApprovalBaseState.ACCEPTED, true, false, true, true, false ),

    /**
     * Data is approved and accepted, but elsewhere.
     */
    ACCEPTED_ELSEWHERE ( DataApprovalBaseState.ACCEPTED, true, false, false, true, false );

    /**
     * "Base", or simplified, state of data approval.
     */
    private DataApprovalBaseState baseState;

    /**
     * Is this data approved (and therefore locked)?
     */
    private boolean approved;

    /**
     * Is this data unapproved (could be approved but is not)?
     */
    private boolean unapproved;

    /**
     * Is this data approvable for this selection?
     */
    private boolean approvable;

    /**
     * Is this data (approved and) accepted?
     */
    private boolean accepted;

    /**
     * Is this data ready to be approved in this combination of data set, etc.?
     */
    private boolean ready;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    DataApprovalState( DataApprovalBaseState baseState, boolean approved, boolean unapproved,
                       boolean approvable, boolean accepted, boolean ready )
    {
        this.baseState = baseState;
        this.approved = approved;
        this.unapproved = unapproved;
        this.approvable = approvable;
        this.accepted = accepted;
        this.ready = ready;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public DataApprovalBaseState getBaseState()
    {
        return baseState;
    }

    public boolean isApproved()
    {
        return approved;
    }

    public boolean isUnapproved()
    {
        return unapproved;
    }

    public boolean isApprovable()
    {
        return approvable;
    }

    public boolean isAccepted()
    {
        return accepted;
    }

    public boolean isReady()
    {
        return ready;
    }
}

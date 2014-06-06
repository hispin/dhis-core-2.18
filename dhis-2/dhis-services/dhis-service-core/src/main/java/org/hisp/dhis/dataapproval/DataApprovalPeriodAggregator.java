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

import org.hisp.dhis.common.MapMap;

import static org.hisp.dhis.common.MapMap.*;
import static org.hisp.dhis.dataapproval.DataApprovalState.*;
import static org.hisp.dhis.system.util.CollectionUtils.*;

/**
 * This package-private class is used by the data approval service to
 * form a composite data approval state for a period spanning more than
 * one data approval period.
 *
 * @author Jim Grace
 * @version $Id$
 */
class DataApprovalPeriodAggregator
{
    /**
     * Represents the data approval state transitions from a current state
     * representing the combined state of all periods tested so far, combined
     * with the state of a new period, resulting in the next current state.
     * <p>
     * When checking states for a number of periods where all other selection
     * criteria are the same, the states will always be in one of three
     * mutually exclusive categories:
     * <ul>
     * <li>UNAPPROVABLE</li>
     * <li>Approvable elsewhere states:
     *     UNAPPROVED_ELSEWHERE,
     *     PARTIALLY_APPROVED_ELSEWHERE,
     *     APPROVED_ELSEWHERE,
     *     ACCEPTED_ELSEWHERE,
     *     PARTIALLY_ACCEPTED_ELSEWHERE
     * </li>
     * <li>Approvable here states:
     *     UNAPPROVED_WAITING,
     *     UNAPPROVED_READY,
     *     PARTIALLY_APPROVED_HERE,
     *     APPROVED_HERE,
     *     PARTIALLY_ACCEPTED_HERE,
     *     ACCEPTED_HERE
     * </li>
     * </ul>
     * We don't have to worry about state transitions between these
     * categories; they will not exist. We need only consider state
     * transitions within each of the categories. (And we don't have to
     * consider state transitions within the first category since there
     * is only one state.)
     * <p>
     * The state transitions are coded in a MapMap. Conceptually, they
     * form a triangular matrix (minus the diagonal) like the following,
     * where "*" shows the entries:
     * <pre>
     *
     * current A  B  C  D  E
     *  new A  .  *  *  *  *
     *      B  .  .  *  *  *
     *      C  .  .  .  *  *
     *      D  .  .  .  .  *
     *
     * </pre>
     * The diagonal is not required because when the current and new states
     * are the same, the next current state will be the same as both.
     * The lower triangle of the matrix is not required because the
     * matrix is tested both ways: current (columns) - new (rows), and also
     * current (rows) - new (columns) (The matrix dimensions are commutative.)
     */
    static private final MapMap<DataApprovalState, DataApprovalState, DataApprovalState> transitionMap = asMapMap(

        // ---------------------------------------------------------------------
        // States where data can be approved, but not here
        // ---------------------------------------------------------------------

        //
        // Data is unapproved, and is waiting for approval somewhere else.
        //
        asEntry( UNAPPROVED_ELSEWHERE, asMap(
                asEntry( PARTIALLY_APPROVED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ),
                asEntry( APPROVED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ),
                asEntry( PARTIALLY_ACCEPTED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ),
                asEntry( ACCEPTED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ) ) ),

        //
        // Some periods within this selection are approved elsewhere and
        // some are unapproved elsewhere.
        //
        asEntry( PARTIALLY_APPROVED_ELSEWHERE, asMap(
                asEntry( APPROVED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ),
                asEntry( PARTIALLY_ACCEPTED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ),
                asEntry( ACCEPTED_ELSEWHERE, PARTIALLY_APPROVED_ELSEWHERE ) ) ),

        //
        // Data is unapproved, and is waiting for approval somewhere else.
        //
        asEntry( APPROVED_ELSEWHERE, asMap(
                asEntry( PARTIALLY_ACCEPTED_ELSEWHERE, PARTIALLY_ACCEPTED_ELSEWHERE ),
                asEntry( ACCEPTED_ELSEWHERE, PARTIALLY_ACCEPTED_ELSEWHERE ) ) ),

        //
        // Data is approved somewhere else.
        //
        asEntry( PARTIALLY_ACCEPTED_ELSEWHERE, asMap(
                asEntry( ACCEPTED_ELSEWHERE, PARTIALLY_ACCEPTED_ELSEWHERE ) ) ),

        // ---------------------------------------------------------------------
        // States where data can be approved here
        // ---------------------------------------------------------------------

        //
        // Data is unapproved, and is waiting for some lower-level approval.
        //
        asEntry( UNAPPROVED_WAITING, asMap(
                asEntry( UNAPPROVED_READY, UNAPPROVED_WAITING ),
                asEntry( PARTIALLY_APPROVED_HERE, UNAPPROVED_WAITING ),
                asEntry( APPROVED_HERE, UNAPPROVED_WAITING ),
                asEntry( PARTIALLY_ACCEPTED_HERE, UNAPPROVED_WAITING ),
                asEntry( ACCEPTED_HERE, UNAPPROVED_WAITING ) ) ),

        //
        // Data is unapproved, and is ready to be approved here.
        //
        asEntry( UNAPPROVED_READY, asMap(
                asEntry( PARTIALLY_APPROVED_HERE, PARTIALLY_APPROVED_HERE ),
                asEntry( APPROVED_HERE, PARTIALLY_APPROVED_HERE ),
                asEntry( PARTIALLY_ACCEPTED_HERE, PARTIALLY_APPROVED_HERE ),
                asEntry( ACCEPTED_HERE, PARTIALLY_APPROVED_HERE ) ) ),

        //
        // Data is approved for some but not all periods inside this longer period
        // and is ready for approval in all periods inside this containing period.
        //
        asEntry( PARTIALLY_APPROVED_HERE, asMap(
                asEntry( APPROVED_HERE, PARTIALLY_APPROVED_HERE ),
                asEntry( PARTIALLY_ACCEPTED_HERE, PARTIALLY_APPROVED_HERE ),
                asEntry( ACCEPTED_HERE, PARTIALLY_APPROVED_HERE ) ) ),

        //
        // Data is approved, and was approved here.
        //
        asEntry( APPROVED_HERE, asMap(
                asEntry( PARTIALLY_ACCEPTED_HERE, PARTIALLY_ACCEPTED_HERE ),
                asEntry( ACCEPTED_HERE, PARTIALLY_ACCEPTED_HERE ) ) ),

        //
        // Data is accepted for some but not all periods inside this longer period
        // and is ready to be accepted in all periods inside this containing period.
        //
        asEntry( PARTIALLY_ACCEPTED_HERE, asMap(
                asEntry( ACCEPTED_HERE, PARTIALLY_ACCEPTED_HERE ) ) )
    );

    /**
     * Finds the next data approval state for the multi-period selection by
     * considering the current aggregate state of all periods so far, and the
     * state of a new, additional period.
     * <p>
     * Note that that arguments to this function have the commutative property.
     * It is unimportant as to which is the current composite state and
     * which is the new state for a period within the data selection.
     *
     * @param s1 current aggregate state (or new state)
     * @param s2 new period state (or current state)
     * @return the next current state
     */
    static DataApprovalState nextState( DataApprovalState s1, DataApprovalState s2 )
    {
        return firstNonNull(
            transitionMap.getValue( s1, s2 ),
            transitionMap.getValue( s2, s1 ),
            s1,
            s2 );
    }

    /**
     * Returns the first non-null argument. This simulates a method found in
     * org.apache.commons.lang3.ObjectUtils, and can be replaced some day
     * by that or a comparable method.
     *
     * @param values values to check
     * @param <T> type of items
     * @return the first non-null item
     */
    private static <T> T firstNonNull(T ...values)
    {
        for ( T value : values )
        {
            if ( value != null )
            {
                return value;
            }
        }
        return null;
    }
}

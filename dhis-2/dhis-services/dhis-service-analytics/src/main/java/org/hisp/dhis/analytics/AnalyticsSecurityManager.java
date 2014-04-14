package org.hisp.dhis.analytics;

import org.hisp.dhis.common.IllegalQueryException;

public interface AnalyticsSecurityManager
{    
    /**
     * Decides whether the current user has privileges to execute the given query.
     * 
     * @param params the data query params.
     * @throws IllegalQueryException if the current user does not have privileges
     *         to execute the given query.
     */
    void decideAccess( DataQueryParams params );
    
    /**
     * Applies dimension constraints to the given params. Dimension constraints
     * with all accessible dimension items will be added as filters to this query.
     * If current user has no dimension constraints, no action is taken. If the 
     * constraint dimensions are already specified with accessible items in the 
     * query, no action is taken. If the current user does not have accessible 
     * items in any dimension constraint, an IllegalQueryException is thrown.
     */
    void applyDimensionConstraints( DataQueryParams params );
}

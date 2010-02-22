/*
 * Copyright (c) 2004-2009, University of Oslo
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
package org.hisp.dhis.caseaggregation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Viet Nguyen
 *
 * @version $Id$
 */
public class CaseAggregationQuery
{
    /**
     * List all conditions of the query
     */
    private List<CaseAggregationCondition> conditions = new ArrayList<CaseAggregationCondition>();

    /**
     * Value: SUM | COUNT
     */
    private String function;

    public List<CaseAggregationCondition> getConditions()
    {
        return conditions;
    }

    public void setConditions( List<CaseAggregationCondition> conditions )
    {
        this.conditions = conditions;
    }

    public String getFunction()
    {
        return function;
    }

    public void setFunction( String function )
    {
        this.function = function;
    }
    
    public void addCondition( CaseAggregationCondition c )
    {
        if( conditions == null )
            conditions = new ArrayList<CaseAggregationCondition>();
        conditions.add( c );
    }
    
    
}

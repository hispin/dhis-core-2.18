package org.hisp.dhis.caseentry.state;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.List;

import org.hisp.dhis.period.Period;

/**
 * @author Chau Thu Tran
 * @version $Id$
 */
public interface PeriodGenericManager
{
    public static final String SESSION_KEY_BASE_PERIOD_START = "SESSION_KEY_BASE_PERIOD_START";

    public static final String SESSION_KEY_BASE_PERIOD_END = "SESSION_KEY_BASE_PERIOD_END";

    public static final String SESSION_KEY_SELECTED_PERIOD_INDEX_START = "SESSION_KEY_SELECTED_PERIOD_INDEX_START";

    public static final String SESSION_KEY_SELECTED_PERIOD_INDEX_END = "SESSION_KEY_SELECTED_PERIOD_INDEX_END";

   
    public void setSelectedPeriodIndex( String key, Integer index );

    public Integer getSelectedPeriodIndex( String key );

    public Period getSelectedPeriod( String key, String baseKey );

    public void setPeriodType( String periodTypeName );

    public void clearSelectedPeriod();

    public void clearBasePeriod();

    public List<Period> getPeriodList( String key, String baseKey );

    public void nextPeriodSpan( String key, String baseKey );

    public void previousPeriodSpan( String key, String baseKey );

}

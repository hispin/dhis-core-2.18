package org.hisp.dhis.trackedentityreport;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hisp.dhis.trackedentityreport.TrackedEntityAggregateReport;
import org.hisp.dhis.trackedentityreport.TrackedEntityAggregateReportService;
import org.hisp.dhis.trackedentityreport.TrackedEntityAggregateReportStore;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author Chau Thu Tran
 * @version DefaultTrackedEntityAggregateReportService.java 12:22:45 PM Jan 10, 2013 $
 */
@Transactional
public class DefaultTrackedEntityAggregateReportService
    implements TrackedEntityAggregateReportService
{

    private TrackedEntityAggregateReportStore aggregateReportStore;

    public void setAggregateReportStore( TrackedEntityAggregateReportStore aggregateReportStore )
    {
        this.aggregateReportStore = aggregateReportStore;
    }

    @Override
    public int addTrackedEntityAggregateReport( TrackedEntityAggregateReport aggregateReport )
    {
        return aggregateReportStore.save( aggregateReport );
    }

    @Override
    public void updateTrackedEntityAggregateReport( TrackedEntityAggregateReport aggregateReport )
    {
        aggregateReportStore.update( aggregateReport );
    }

    @Override
    public TrackedEntityAggregateReport getTrackedEntityAggregateReport( int id )
    {
        return aggregateReportStore.get( id );
    }
    
    @Override
    public TrackedEntityAggregateReport getTrackedEntityAggregateReportByUid( String uid )
    {
        return aggregateReportStore.getByUid( uid );
    }

    @Override
    public void deleteTrackedEntityAggregateReport( TrackedEntityAggregateReport aggregateReport )
    {
        aggregateReportStore.delete( aggregateReport );
    }

    @Override
    public Collection<TrackedEntityAggregateReport> getAllTrackedEntityAggregateReports()
    {
        return aggregateReportStore.getAll();
    }

    @Override
    public Collection<TrackedEntityAggregateReport> getTrackedEntityAggregateReports( User user, String query, Integer min,
        Integer max )
    {
        return aggregateReportStore.get( user, query, min, max );
    }

    @Override
    public int countTrackedEntityAggregateReportList( User user, String query )
    {
        return aggregateReportStore.countList( user, query );
    }

    @Override
    public TrackedEntityAggregateReport getTrackedEntityAggregateReport( String name )
    {
        return aggregateReportStore.getByName( name );
    }
}

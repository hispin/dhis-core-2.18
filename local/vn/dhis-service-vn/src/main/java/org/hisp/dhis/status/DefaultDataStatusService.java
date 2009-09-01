package org.hisp.dhis.status;

import java.util.Collection;

import org.hisp.dhis.dataset.DataSet;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
@Transactional
public class DefaultDataStatusService
    implements DataStatusService
{
    // -------------------------------------------------
    // Dependency
    // -------------------------------------------------

    private DataStatusStore dataStatusStore;

    public void setDataStatusStore( DataStatusStore dataStatusStore )
    {
        this.dataStatusStore = dataStatusStore;
    }

    // -------------------------------------------------
    // Implement
    // -------------------------------------------------

    public void delete( int id )
    {
        dataStatusStore.delete( id );
    }

    public DataStatus get( int id )
    {      
        return dataStatusStore.get( id );
    }

    public void save( DataStatus dataStatus )
    {
        dataStatusStore.save( dataStatus );
    }

    public void update( DataStatus dataStatus )
    {
        dataStatusStore.update( dataStatus );
    }

    public Collection<DataStatus> getALL()
    {       
        return dataStatusStore.getALL();
    }

    public Collection<DataStatus> getDataStatusDefault()
    {      
        return dataStatusStore.getDataStatusDefault();
    }

    public Collection<DataStatus> getDataStatusByDataSets( Collection<DataSet> dataSets )
    {        
        return dataStatusStore.getDataStatusByDataSets( dataSets );
    }

    public Collection<DataStatus> getDataStatusDefaultByDataSets( Collection<DataSet> dataSets )
    {
        // TODO Auto-generated method stub
        return dataStatusStore.getDataStatusDefaultByDataSets( dataSets );
    }

}

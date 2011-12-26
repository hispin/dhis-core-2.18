package org.hisp.dhis.datamart;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.deletion.DeletionHandler;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataMartExportDeletionHandler
    extends DeletionHandler
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartService dataMartService;

    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    // -------------------------------------------------------------------------
    // DeletionHandler implementation
    // -------------------------------------------------------------------------

    @Override
    public String getClassName()
    {
        return DataMartExport.class.getSimpleName();
    }
    
    @Override
    public void deleteDataElement( DataElement dataElement )
    {
        for ( DataMartExport export : dataMartService.getAllDataMartExports() )
        {
            if ( export.getDataElements().remove( dataElement ) )
            {
                dataMartService.saveDataMartExport( export );
            }
        }
    }
    
    @Override
    public void deleteIndicator( Indicator indicator )
    {
        for ( DataMartExport export : dataMartService.getAllDataMartExports() )
        {
            if ( export.getIndicators().remove( indicator ) )
            {
                dataMartService.saveDataMartExport( export );
            }
        }
    }
    
    @Override
    public void deletePeriod( Period period )
    {
        for ( DataMartExport export : dataMartService.getAllDataMartExports() )
        {
            if ( export.getPeriods().remove( period ) )
            {
                dataMartService.saveDataMartExport( export );
            }
        }
    }
    
    @Override
    public void deleteOrganisationUnit( OrganisationUnit unit )
    {
        for ( DataMartExport export : dataMartService.getAllDataMartExports() )
        {
            if ( export.getOrganisationUnits().remove( unit ) )
            {
                dataMartService.saveDataMartExport( export );
            }
        }
    }    
}

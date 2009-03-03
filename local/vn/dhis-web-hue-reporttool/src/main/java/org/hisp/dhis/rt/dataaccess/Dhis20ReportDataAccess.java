package org.hisp.dhis.rt.dataaccess;

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

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Lars Helge Overland
 * @version $Id: Dhis20ReportDataAccess.java 2871 2007-02-20 16:04:11Z andegje $
 */
public class Dhis20ReportDataAccess
    implements ReportDataAccess
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    OrganisationUnitService organisationUnitService;

    IndicatorStore indicatorStore;

    DataElementService dataElementService;

    AggregationService aggregationService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setIndicatorStore( IndicatorStore indicatorStore )
    {
        this.indicatorStore = indicatorStore;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    // -------------------------------------------------------------------------
    // Dhis 2.0 implementation
    // -------------------------------------------------------------------------

    public Collection<DataElement> getAllDataElements()
        throws ReportDataAccessException
    {
        return dataElementService.getAllDataElements();
    }

    public Collection<DataElementGroup> getAllDataElementGroups()
        throws ReportDataAccessException
    {
        return dataElementService.getAllDataElementGroups();
    }

    public Collection<DataElement> getMembersOfDataElementGroup( int dataElementGroupId )
        throws ReportDataAccessException
    {
        DataElementGroup group = dataElementService.getDataElementGroup( dataElementGroupId );

        return group.getMembers();
    }

    public Collection<Indicator> getAllIndicators()
        throws ReportDataAccessException
    {
        return indicatorStore.getAllIndicators();
    }

    public Collection<IndicatorGroup> getAllIndicatorGroups()
        throws ReportDataAccessException
    {
        return indicatorStore.getAllIndicatorGroups();
    }

    public Collection<Indicator> getMembersOfIndicatorGroup( int indicatorGroupId )
        throws ReportDataAccessException
    {
        IndicatorGroup group = indicatorStore.getIndicatorGroup( indicatorGroupId );

        return group.getMembers();
    }

    public double getAggregatedDataValue( int dataElementId, Date startDate, Date endDate, String source )
        throws ReportDataAccessException
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( source ) );

        return aggregationService.getAggregatedDataValue( dataElement, startDate, endDate, organisationUnit );
    }

    public double getAggregatedIndicatorValue( int indicatorId, Date startDate, Date endDate, String source )
        throws ReportDataAccessException
    {
        Indicator indicator = indicatorStore.getIndicator( indicatorId );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( source ) );

        return aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, organisationUnit );
    }

    public String getOrganisationUnitName( int organisationUnitId )
        throws ReportDataAccessException
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        return organisationUnit.getName();
    }

    public String getOrganisationUnitShortName( int organisationUnitId )
        throws ReportDataAccessException
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        return organisationUnit.getShortName();
    }

    public String getDataElementName( int dataElementId )
        throws ReportDataAccessException
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        return dataElement.getName();
    }

    public String getDataElementShortName( int dataElementId )
        throws ReportDataAccessException
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        return dataElement.getShortName();
    }

    public String getIndicatorName( int indicatorId )
        throws ReportDataAccessException
    {
        Indicator indicator = indicatorStore.getIndicator( indicatorId );
        return indicator.getName();
    }

    public String getIndicatorShortName( int indicatorId )
        throws ReportDataAccessException
    {
        Indicator indicator = indicatorStore.getIndicator( indicatorId );
        return indicator.getShortName();
    }

    public Set<OrganisationUnit> getOrganisationUnitChildren( int organisationUnitId )
        throws ReportDataAccessException
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        return organisationUnit.getChildren();
    }

    public int getOrganisationUnitByName( String name )
        throws ReportDataAccessException
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnitByName( name );

        if ( orgUnit != null )
        {
            return orgUnit.getId();
        }

        throw new ReportDataAccessException( "Failed to get OrganisationUnit id" );
    }
}

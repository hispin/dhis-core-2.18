package org.hisp.dhis.caseentry.action.report;

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

import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentityreport.TrackedEntityTabularReport;
import org.hisp.dhis.trackedentityreport.TrackedEntityTabularReportService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $DeleteTabularReportAction.java May 7, 2012 4:11:43 PM$
 */
public class GetTabularReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityTabularReportService tabularReportService;

    public void setTabularReportService( TrackedEntityTabularReportService tabularReportService )
    {
        this.tabularReportService = tabularReportService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private TrackedEntityTabularReport tabularReport;

    public TrackedEntityTabularReport getTabularReport()
    {
        return tabularReport;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private List<TrackedEntityAttribute> dimensionAttributes = new ArrayList<TrackedEntityAttribute>();

    public List<TrackedEntityAttribute> getDimensionAttributes()
    {
        return dimensionAttributes;
    }

    private List<TrackedEntityAttribute> filterAttributes = new ArrayList<TrackedEntityAttribute>();

    public List<TrackedEntityAttribute> getFilterAttributes()
    {
        return filterAttributes;
    }

    private List<DataElement> dimensionDataElements = new ArrayList<DataElement>();

    public List<DataElement> getDimensionDataElements()
    {
        return dimensionDataElements;
    }

    private List<DataElement> filterDataElements = new ArrayList<DataElement>();

    public List<DataElement> getFilterDataElements()
    {
        return filterDataElements;
    }

    private Map<String, String> mapFilters = new HashMap<String, String>();

    public Map<String, String> getMapFilters()
    {
        return mapFilters;
    }

    private Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();

    public Collection<OrganisationUnit> getOrgunits()
    {
        return orgunits;
    }

    private Boolean userOrgunits;

    public Boolean getUserOrgunits()
    {
        return userOrgunits;
    }

    private Boolean userOrgunitChildren;

    public Boolean getUserOrgunitChildren()
    {
        return userOrgunitChildren;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        tabularReport = tabularReportService.getTrackedEntityTabularReportByUid( id );

        Program program = tabularReport.getProgram();

        programStage = tabularReport.getProgramStage();

        for ( String dimension : tabularReport.getDimension() )
        {
            String dimensionId = DataQueryParams.getDimensionFromParam( dimension );

            String[] filters = dimension.split( DataQueryParams.DIMENSION_NAME_SEP );
            if ( filters.length > 1 )
            {
                mapFilters.put( dimensionId, dimension.substring( dimensionId.length() + 1, dimension.length() ) );
            }

            if ( ORGUNIT_DIM_ID.equals( dimensionId ) )
            {
                List<String> items = DataQueryParams.getDimensionItemsFromParam( dimension );
                for ( String item : items )
                {
                    if ( item.equals( "USER_ORGUNIT" ) )
                    {
                        userOrgunits = true;
                    }
                    else if ( item.equals( "USER_ORGUNIT_CHILDREN" ) )
                    {
                        userOrgunitChildren = true;
                    }
                    orgunits.add( organisationUnitService.getOrganisationUnit( item ) );
                }
            }
            else
            {
                TrackedEntityAttribute at = attributeService.getTrackedEntityAttribute( dimensionId );

                if ( at != null && program.getAttributes().contains( at ) )
                {
                    dimensionAttributes.add( at );
                }

                DataElement de = dataElementService.getDataElement( dimensionId );

                if ( de != null && program.getAllDataElements().contains( de ) )
                {
                    dimensionDataElements.add( de );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Get filters
        // ---------------------------------------------------------------------

        for ( String filter : tabularReport.getFilter() )
        {
            String filterId = DataQueryParams.getDimensionFromParam( filter );

            String[] filters = filter.split( DataQueryParams.DIMENSION_NAME_SEP );
            if ( filters.length > 1 )
            {
                mapFilters.put( filterId, filter.substring( filterId.length() + 1, filter.length() ) );
            }

            TrackedEntityAttribute at = attributeService.getTrackedEntityAttribute( filterId );

            if ( at != null && program.getAttributes().contains( at ) )
            {
                filterAttributes.add( at );
            }

            DataElement de = dataElementService.getDataElement( filterId );

            if ( de != null && program.getAllDataElements().contains( de ) )
            {
                filterDataElements.add( de );
            }
        }

        return SUCCESS;
    }
}

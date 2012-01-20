package org.hisp.dhis.datamart.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datamart.DataMartExport;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id: GetOptionsAction.java 6256 2008-11-10 17:10:30Z larshelg $
 */
public class GetOptionsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataMartService dataMartService;

    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }
    
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private final static int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

    private final static int DATAVALUE = 1;

    public int getDATAVALUE()
    {
        return DATAVALUE;
    }

    private final static int INDICATORVALUE = 2;

    public int getINDICATORVALUE()
    {
        return INDICATORVALUE;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public Integer getId()
    {
        return id;
    }
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitLevel> levels;

    public List<OrganisationUnitLevel> getLevels()
    {
        return levels;
    }
    
    private List<Period> periods = new ArrayList<Period>();

    public List<Period> getPeriods()
    {
        return periods;
    }

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    private List<DataElement> selectedDataElements;

    public List<DataElement> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private List<Indicator> selectedIndicators;

    public List<Indicator> getSelectedIndicators()
    {
        return selectedIndicators;
    }

    private List<OrganisationUnit> selectedOrganisationUnits;

    public List<OrganisationUnit> getSelectedOrganisationUnits()
    {
        return selectedOrganisationUnits;
    }

    private List<Period> selectedPeriods;

    public List<Period> getSelectedPeriods()
    {
        return selectedPeriods;
    }
    
    private DataMartExport export;

    public DataMartExport getExport()
    {
        return export;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<DataElement> dataElementComparator;

    public void setDataElementComparator( Comparator<DataElement> dataElementComparator )
    {
        this.dataElementComparator = dataElementComparator;
    }

    private Comparator<Indicator> indicatorComparator;

    public void setIndicatorComparator( Comparator<Indicator> indicatorComparator )
    {
        this.indicatorComparator = indicatorComparator;
    }
    
    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        // ---------------------------------------------------------------------
        // Level
        // ---------------------------------------------------------------------

        levels = organisationUnitService.getOrganisationUnitLevels();
        
        // ---------------------------------------------------------------------
        // Period type
        // ---------------------------------------------------------------------

        periodTypes = periodService.getAllPeriodTypes();

        // ---------------------------------------------------------------------
        // Period
        // ---------------------------------------------------------------------

        periods = new MonthlyPeriodType().generatePeriods( new Date() );

        Collections.reverse( periods );
        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );
        
        if ( id != null )
        {
            export = dataMartService.getDataMartExport( id );
            
            // -----------------------------------------------------------------
            // Data element
            // -----------------------------------------------------------------

            selectedDataElements = new ArrayList<DataElement>( export.getDataElements() );
            Collections.sort( selectedDataElements, dataElementComparator );
            
            // ---------------------------------------------------------------------
            // Indicator
            // ---------------------------------------------------------------------

            selectedIndicators = new ArrayList<Indicator>( export.getIndicators() );
            Collections.sort( selectedIndicators, indicatorComparator );
            
            // ---------------------------------------------------------------------
            // Organisation unit
            // ---------------------------------------------------------------------

            selectedOrganisationUnits = new ArrayList<OrganisationUnit>( export.getOrganisationUnits() );
            Collections.sort( selectedOrganisationUnits, organisationUnitComparator );
            
            // ---------------------------------------------------------------------
            // Period
            // ---------------------------------------------------------------------

            selectedPeriods = new ArrayList<Period>( export.getPeriods() );
            Collections.sort( selectedPeriods, new PeriodComparator() );
            periods.removeAll( selectedPeriods );
        }
        
        return SUCCESS;
    }
}

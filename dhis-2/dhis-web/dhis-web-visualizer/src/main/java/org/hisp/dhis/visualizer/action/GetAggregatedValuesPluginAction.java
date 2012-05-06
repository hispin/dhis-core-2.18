package org.hisp.dhis.visualizer.action;

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

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_JSON;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 */
public class GetAggregatedValuesPluginAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    @Autowired
    private ContextUtils contextUtils;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Collection<String> indicatorIds;

    public void setIndicatorIds( Collection<String> indicatorIds )
    {
        this.indicatorIds = indicatorIds;
    }

    private Collection<String> dataElementIds;

    public void setDataElementIds( Collection<String> dataElementIds )
    {
        this.dataElementIds = dataElementIds;
    }

    private Collection<String> organisationUnitIds;

    public void setOrganisationUnitIds( Collection<String> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }

    private boolean lastMonth;

    public void setLastMonth( boolean lastMonth )
    {
        this.lastMonth = lastMonth;
    }

    private boolean last12Months;

    public void setLast12Months( boolean last12Months )
    {
        this.last12Months = last12Months;
    }

    private boolean lastQuarter;

    public void setLastQuarter( boolean lastQuarter )
    {
        this.lastQuarter = lastQuarter;
    }

    private boolean last4Quarters;

    public void setLast4Quarters( boolean last4Quarters )
    {
        this.last4Quarters = last4Quarters;
    }

    private boolean lastSixMonth;

    public void setLastSixMonth( boolean lastSixMonth )
    {
        this.lastSixMonth = lastSixMonth;
    }

    private boolean last2SixMonths;

    public void setLast2SixMonths( boolean last2SixMonths )
    {
        this.last2SixMonths = last2SixMonths;
    }

    private boolean thisYear;

    public void setThisYear( boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    private boolean last5Years;

    public void setLast5Years( boolean last5Years )
    {
        this.last5Years = last5Years;
    }

    public String callback;

    public String getCallback()
    {
        return callback;
    }

    public void setCallback( String callback )
    {
        this.callback = callback;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<AggregatedIndicatorValue> indicatorValues = new HashSet<AggregatedIndicatorValue>();

    public Collection<AggregatedIndicatorValue> getIndicatorValues()
    {
        return indicatorValues;
    }

    private Collection<AggregatedDataValue> dataValues = new HashSet<AggregatedDataValue>();

    public Collection<AggregatedDataValue> getDataValues()
    {
        return dataValues;
    }

    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private Collection<String> dataNames = new ArrayList<String>();

    public Collection<String> getDataNames()
    {
        return dataNames;
    }
    
    private Collection<String> organisationUnitNames = new ArrayList<String>();

    public Collection<String> getOrganisationUnitNames()
    {
        return organisationUnitNames;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    private List<Period> setNames( List<Period> periods )
    {
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }

        return periods;
    }

    public String execute()
        throws Exception
    {
        if ( organisationUnitIds != null
            && (lastMonth || last12Months || lastQuarter || last4Quarters || lastSixMonth || last2SixMonths || thisYear || last5Years) )
        {
            RelativePeriods rp = new RelativePeriods();
            rp.setReportingMonth( lastMonth );
            rp.setLast12Months( last12Months );
            rp.setReportingQuarter( lastQuarter );
            rp.setLast4Quarters( last4Quarters );
            rp.setLastSixMonth( lastSixMonth );
            rp.setLast2SixMonths( last2SixMonths );
            rp.setThisYear( thisYear );
            rp.setLast5Years( last5Years );

            periods = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

            Collection<Integer> periodIds = getIdentifiers( Period.class, periods );

            Set<OrganisationUnit> organisationUnits = organisationUnitService
                .getOrganisationUnitsByUid( organisationUnitIds );
            
            for ( String id : organisationUnitIds )
            {
                organisationUnitNames.add( organisationUnitService.getOrganisationUnit( id ).getName() );
            }

            if ( indicatorIds != null )
            {
                Set<Indicator> indicators = indicatorService.getIndicatorsByUid( indicatorIds );

                indicatorValues = aggregatedDataValueService.getAggregatedIndicatorValues(
                    getIdentifiers( Indicator.class, indicators ), periodIds,
                    getIdentifiers( OrganisationUnit.class, organisationUnits ) );

                for ( AggregatedIndicatorValue value : indicatorValues )
                {
                    value.setIndicatorName( indicatorService.getIndicator( value.getIndicatorId() ).getShortName() );
                    value.setPeriodName( format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) ) );
                    value.setOrganisationUnitName( organisationUnitService.getOrganisationUnit(
                        value.getOrganisationUnitId() ).getName() );
                }
                
                for ( String id : indicatorIds )
                {
                    dataNames.add( indicatorService.getIndicator( id ).getDisplayShortName() );
                }
            }

            if ( dataElementIds != null )
            {
                Set<DataElement> dataElements = dataElementService.getDataElementsByUid( dataElementIds );
                
                dataValues = aggregatedDataValueService.getAggregatedDataValueTotals( getIdentifiers( DataElement.class, dataElements ), periodIds,
                    getIdentifiers( OrganisationUnit.class, organisationUnits ) );

                for ( AggregatedDataValue value : dataValues )
                {
                    value.setDataElementName( dataElementService.getDataElement( value.getDataElementId() )
                        .getShortName() );
                    value.setPeriodName( format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) ) );
                    value.setOrganisationUnitName( organisationUnitService.getOrganisationUnit(
                        value.getOrganisationUnitId() ).getName() );
                }
                
                for ( String id : dataElementIds )
                {
                    dataNames.add( dataElementService.getDataElement( id ).getDisplayShortName() );
                }
            }
        }

        contextUtils.configureResponse( ServletActionContext.getResponse(), CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING, null, false );
        
        return SUCCESS;
    }
}

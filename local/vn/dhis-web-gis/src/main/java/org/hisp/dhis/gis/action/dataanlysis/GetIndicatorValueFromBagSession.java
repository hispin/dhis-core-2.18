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

package org.hisp.dhis.gis.action.dataanlysis;

/**
 * @author Tran Thanh Tri
 * @version $Id: GetIndicatorValueFromBagSession.java 28-04-2008 16:06:00 $
 */
import java.util.Date;

import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.datamart.DataMartStore;
import org.hisp.dhis.gis.Feature;
import org.hisp.dhis.gis.FeatureService;
import org.hisp.dhis.gis.GISConfiguration;
import org.hisp.dhis.gis.GISConfigurationService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

import com.opensymphony.xwork2.Action;

public class GetIndicatorValueFromBagSession
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------   

    private IndicatorService indicatorService;

    private DataMartStore dataMartStore;

    private PeriodService periodService;

    private AggregationService aggregationService;

    private GISConfigurationService gisConfigurationService;
    
    private FeatureService featureService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String orgCode;

    private Integer indicatorId;

    private Integer periodId;

    private String startdate;

    private String enddate;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Indicator indicator;

    private OrganisationUnit organisationUnit;

    private double indicatorValue;

    private String message;

    private I18n i18n;
    
    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------
    

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    public void setFeatureService( FeatureService featureService )
    {
        this.featureService = featureService;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setDataMartStore( DataMartStore dataMartStore )
    {
        this.dataMartStore = dataMartStore;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    public void setGisConfigurationService( GISConfigurationService gisConfigurationService )
    {
        this.gisConfigurationService = gisConfigurationService;
    }

    public void setIndicatorId( Integer indicatorId )
    {
        this.indicatorId = indicatorId;
    }

    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }

    public void setStartdate( String startdate )
    {
        this.startdate = startdate;
    }

    public void setEnddate( String enddate )
    {
        this.enddate = enddate;
    }

    public String getStartdate()
    {
        return startdate;
    }

    public String getEnddate()
    {
        return enddate;
    }

    public Indicator getIndicator()
    {
        return indicator;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrgCode( String orgCode )
    {
        this.orgCode = orgCode;
    }

    public double getIndicatorValue()
    {
        return indicatorValue;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }


    public String execute()
        throws Exception
    {
        indicator = indicatorService.getIndicator( indicatorId.intValue() );
        
        Feature feature = featureService.get( orgCode );
        
        if(feature==null){
            message = i18n.getString( "no_data" );
            return ERROR;
        }
        
        organisationUnit = feature.getOrganisationUnit();
        
        if (gisConfigurationService.getValue(GISConfiguration.KEY_GETINDICATOR)
            .equalsIgnoreCase(GISConfiguration.AggregationService)) {
            
            Date startDate = format.parseDate( startdate );
            
            Date endDate = format.parseDate( enddate ); 
            
            indicatorValue = aggregationService.getAggregatedIndicatorValue( indicator, startDate, endDate, organisationUnit );
            
            
        }else{
            
            Period period = periodService.getPeriod( periodId.intValue() );
            
            indicatorValue = dataMartStore.getAggregatedValue( indicator, period, organisationUnit );
            
            startdate = format.formatDate( period.getStartDate() );
            
            enddate = format.formatDate( period.getEndDate() );
            
        }    
        
        if(indicatorValue < 0.0){
            message = i18n.getString( "no_data" );
            return ERROR;            
        }
        

        return SUCCESS;
    }

}

package org.hisp.dhis.api.controller;

import static org.hisp.dhis.api.utils.ContextUtils.CONTENT_TYPE_JSON;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.DateUtils.setNames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.api.utils.ContextUtils;
import org.hisp.dhis.api.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.api.webdomain.ChartPluginValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping( value = ChartPluginController.RESOURCE_PATH )
public class ChartPluginController
{
    public static final String RESOURCE_PATH = "/chartValues";
    
    @Autowired
    private AggregatedDataValueService aggregatedDataValueService;

    @Autowired
    private IndicatorService indicatorService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private I18nManager i18nManager;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( method = RequestMethod.GET )
    public String getChartValues( @RequestParam(required=false) Set<String> indicatorIds,
                                @RequestParam(required=false) Set<String> dataElementIds,
                                @RequestParam Set<String> organisationUnitIds,
                                RelativePeriods relativePeriods,
                                Model model, 
                                HttpServletResponse response )
        throws Exception
    {
        ChartPluginValue chartValue = new ChartPluginValue();
        
        I18nFormat format = i18nManager.getI18nFormat();
                
        List<Period> periods = periodService.reloadPeriods( setNames( relativePeriods.getRelativePeriods(), format ) );
        
        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periods ) );
        
        chartValue.setP( periodIds );

        Set<OrganisationUnit> organisationUnits = organisationUnitService
            .getOrganisationUnitsByUid( organisationUnitIds );

        if ( indicatorIds != null )
        {
            Set<Indicator> indicators = indicatorService.getIndicatorsByUid( indicatorIds );

            Collection<AggregatedIndicatorValue> indicatorValues = aggregatedDataValueService.getAggregatedIndicatorValues(
                getIdentifiers( Indicator.class, indicators ), periodIds,
                getIdentifiers( OrganisationUnit.class, organisationUnits ) );

            for ( AggregatedIndicatorValue value : indicatorValues )
            {
                String[] record = new String[4];
                
                record[0] = String.valueOf( value.getValue() );
                record[1] = indicatorService.getIndicator( value.getIndicatorId() ).getShortName();
                record[2] = format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) );
                record[3] = organisationUnitService.getOrganisationUnit( value.getOrganisationUnitId() ).getName();
                
                chartValue.getV().add( record );
            }
        }

        if ( dataElementIds != null )
        {
            Set<DataElement> dataElements = dataElementService.getDataElementsByUid( dataElementIds );
            
            Collection<AggregatedDataValue> dataValues = aggregatedDataValueService.getAggregatedDataValueTotals( 
                getIdentifiers( DataElement.class, dataElements ), periodIds,
                getIdentifiers( OrganisationUnit.class, organisationUnits ) );

            for ( AggregatedDataValue value : dataValues )
            {
                String[] record = new String[4];
                
                record[0] = String.valueOf( value.getValue() );
                record[1] = dataElementService.getDataElement( value.getDataElementId() ).getShortName();
                record[2] = format.formatPeriod( periodService.getPeriod( value.getPeriodId() ) );
                record[3] = organisationUnitService.getOrganisationUnit( value.getOrganisationUnitId() ).getName();
                
                chartValue.getV().add( record );              
            }
        }
        
        contextUtils.configureResponse( response, CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING, null, false );

        model.addAttribute( "model", chartValue );
        //JacksonUtils.toJson( response.getOutputStream(), chartValue );
        
        return "chartValues";
    }
}

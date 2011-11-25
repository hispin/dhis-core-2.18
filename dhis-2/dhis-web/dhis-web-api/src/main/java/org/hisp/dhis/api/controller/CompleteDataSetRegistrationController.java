package org.hisp.dhis.api.controller;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.Charts;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.CompleteDataSetRegistrations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/completeDataSetRegistrations" )
public class CompleteDataSetRegistrationController
{
    @Autowired
    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    public CompleteDataSetRegistrationController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public CompleteDataSetRegistrations getCompleteDataSetRegistrations()
    {
        CompleteDataSetRegistrations completeDataSetRegistrations = new CompleteDataSetRegistrations();
        completeDataSetRegistrations.setCompleteDataSetRegistrations( new ArrayList<CompleteDataSetRegistration>( completeDataSetRegistrationService.getAllCompleteDataSetRegistrations() ) );

        return completeDataSetRegistrations;
    }

    /*
    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public Chart getChart( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        CompleteDataSetRegistration completeDataSetRegistration = completeDataSetRegistrationService.getCompleteDataSetRegistration(  )
        Chart chart = chartService.getChart( uid );

        return chart;
    }*/
}

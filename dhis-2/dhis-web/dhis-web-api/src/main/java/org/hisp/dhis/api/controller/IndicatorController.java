package org.hisp.dhis.api.controller;

import org.hisp.dhis.codelist.CodeList;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping( value = "/indicators" )
public class IndicatorController
{
    @Autowired
    private IndicatorService indicatorService;

    @RequestMapping( method = RequestMethod.GET )
    public CodeList<Indicator> getIndicators()
    {
        List<Indicator> indicators = new ArrayList<Indicator>( indicatorService.getAllIndicators() );
        CodeList<Indicator> codeList = new CodeList<Indicator>( indicators );

        return codeList;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public Indicator getIndicator( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        Indicator indicator = indicatorService.getIndicator( uid );

        return indicator;
    }
}

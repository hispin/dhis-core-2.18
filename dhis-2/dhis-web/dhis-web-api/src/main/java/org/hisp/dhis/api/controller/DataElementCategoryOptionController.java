package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptions;
import org.hisp.dhis.dataelement.DataElementCategoryService;
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
@RequestMapping( value = "/dataElementCategoryOptions" )
public class DataElementCategoryOptionController
{
    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    public DataElementCategoryOptionController()
    {
    }

    @RequestMapping( method = RequestMethod.GET )
    public DataElementCategoryOptions getDataElementCategoryOptions()
    {
        DataElementCategoryOptions dataElementCategoryOptions = new DataElementCategoryOptions();
        dataElementCategoryOptions.setDataElementCategoryOptions( new ArrayList<DataElementCategoryOption>( dataElementCategoryService.getAllDataElementCategoryOptions() ) );

        return dataElementCategoryOptions;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataElementCategoryOption getDataElementCategoryOption( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataElementCategoryOption dataElementCategoryOption = dataElementCategoryService.getDataElementCategoryOption( uid );

        return dataElementCategoryOption;
    }
}

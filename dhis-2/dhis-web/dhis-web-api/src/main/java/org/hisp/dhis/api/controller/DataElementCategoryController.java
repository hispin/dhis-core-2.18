package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataelement.DataElementCategories;
import org.hisp.dhis.dataelement.DataElementCategory;
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
@RequestMapping( value = "/dataElementCategories" )
public class DataElementCategoryController
{
    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    public DataElementCategoryController()
    {
    }

    @RequestMapping( method = RequestMethod.GET )
    public DataElementCategories getDataElementCategories()
    {
        DataElementCategories dataElementCategories = new DataElementCategories();
        dataElementCategories.setDataElementCategories( new ArrayList<DataElementCategory>( dataElementCategoryService.getAllDataElementCategories() ) );

        return dataElementCategories;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataElementCategory getDataElementCategory( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataElementCategory dataElementCategory = dataElementCategoryService.getDataElementCategory( uid );

        return dataElementCategory;
    }
}

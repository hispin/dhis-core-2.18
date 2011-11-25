package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombos;
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
@RequestMapping( value = "/dataElementCategoryOptionCombos" )
public class DataElementCategoryOptionComboController
{
    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    public DataElementCategoryOptionComboController()
    {
    }

    @RequestMapping( method = RequestMethod.GET )
    public DataElementCategoryOptionCombos getDataElementCategoryOptionCombos()
    {
        DataElementCategoryOptionCombos dataElementCategoryOptionCombos = new DataElementCategoryOptionCombos();
        dataElementCategoryOptionCombos.setDataElementCategoryOptionCombos( new ArrayList<DataElementCategoryOptionCombo>( dataElementCategoryService.getAllDataElementCategoryOptionCombos() ) );

        return dataElementCategoryOptionCombos;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataElementCategoryOptionCombo getDataElementCategoryCombo( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataElementCategoryOptionCombo dataElementCategoryOptionCombo = dataElementCategoryService.getDataElementCategoryOptionCombo( uid );

        return dataElementCategoryOptionCombo;
    }
}

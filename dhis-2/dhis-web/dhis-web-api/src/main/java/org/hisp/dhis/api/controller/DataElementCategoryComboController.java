package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryCombos;
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
@RequestMapping( value = "/dataElementCategoryCombos" )
public class DataElementCategoryComboController
{
    @Autowired
    private DataElementCategoryService dataElementCategoryService;

    public DataElementCategoryComboController()
    {
    }

    @RequestMapping( method = RequestMethod.GET )
    public DataElementCategoryCombos getDataElementCategoryCombos()
    {
        DataElementCategoryCombos dataElementCategoryCombos = new DataElementCategoryCombos();
        dataElementCategoryCombos.setDataElementCategoryCombos( new ArrayList<DataElementCategoryCombo>( dataElementCategoryService.getAllDataElementCategoryCombos() ) );

        return dataElementCategoryCombos;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataElementCategoryCombo getDataElementCategoryCombo( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataElementCategoryCombo dataElementCategoryCombo = dataElementCategoryService.getDataElementCategoryCombo( uid );

        return dataElementCategoryCombo;
    }
}

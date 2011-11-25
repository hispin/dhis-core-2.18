package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.DataSets;
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
@RequestMapping( value = "/dataSets" )
public class DataSetController
{
    @Autowired
    private DataSetService dataSetService;

    public DataSetController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public DataSets getDataSets()
    {
        DataSets dataSets = new DataSets();
        dataSets.setDataSets( new ArrayList<DataSet>( dataSetService.getAllDataSets() ) );

        return dataSets;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataSet getDataSet( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataSet dataSet = dataSetService.getDataSet( uid );

        return dataSet;
    }
}

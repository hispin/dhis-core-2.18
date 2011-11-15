package org.hisp.dhis.api.controller;

import org.hisp.dhis.codelist.CodeList;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping( value = "/dataElements" )
public class DataElementController
{
    @Autowired
    private DataElementService dataElementService;

    @RequestMapping( method = RequestMethod.GET )
    public CodeList<DataElement> getDataElements()
    {
        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getAllActiveDataElements() );
        CodeList<DataElement> codeList = new CodeList<DataElement>( dataElements );

        return codeList;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public DataElement getDataElement( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        DataElement dataElement = dataElementService.getDataElement( uid );

        return dataElement;
    }
}

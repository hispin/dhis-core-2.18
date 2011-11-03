package org.hisp.dhis.api.controller;

import org.hisp.dhis.api.resources.Element;
import org.hisp.dhis.api.resources.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = "/elements" )
public class ElementController
{
    private Elements elements = new Elements();

    public ElementController()
    {
        elements.getElements().add( new Element( 1, "Element #1" ) );
        elements.getElements().add( new Element( 2, "Element #2" ) );
        elements.getElements().add( new Element( 3, "Element #3" ) );
        elements.getElements().add( new Element( 4, "Element #4" ) );
        elements.getElements().add( new Element( 5, "Element #5" ) );
    }

    @RequestMapping
    public String getElements( Model model )
    {
        model.addAttribute( "elements", elements );

        return "elements";
    }

    @RequestMapping( value = "/{uid}" )
    public String getElement( @PathVariable( "uid" ) Integer uid, Model model )
    {
        model.addAttribute( "element", elements.getElement( uid ) );

        return "element";
    }
}

package org.hisp.dhis.api.controller;

import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.SectionService;
import org.hisp.dhis.dataset.Sections;
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
@RequestMapping( value = "/sections" )
public class SectionController
{
    @Autowired
    private SectionService sectionService;

    public SectionController()
    {

    }

    @RequestMapping( method = RequestMethod.GET )
    public Sections getDataSets()
    {
        Sections sections = new Sections();
        sections.setSections( new ArrayList<Section>( sectionService.getAllSections() ) );

        return sections;
    }

    @RequestMapping( value = "/{uid}", method = RequestMethod.GET )
    public Section getDataSet( @PathVariable( "uid" ) Integer uid, HttpServletRequest request )
    {
        Section section = sectionService.getSection( uid );

        return section;
    }
}

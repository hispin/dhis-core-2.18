package org.hisp.dhis.webapi.controller.equipment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttribute;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeOption;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeAttributeService;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping(value = EquipmentTypeAttributeController.RESOURCE_PATH)
public class EquipmentTypeAttributeController extends AbstractCrudController<EquipmentTypeAttribute> 
{
    public static final String RESOURCE_PATH = "/equipmentTypeAttributes";
    
    @Autowired
    private EquipmentTypeAttributeService equipmentTypeAttributeService;

    
    @RequestMapping( value = "/{uid}/attributeOptions", method = RequestMethod.GET )
    public String getAttributeOptions( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttributeByUid( options.getOptions().get( uid ) );

        if ( equipmentTypeAttribute == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type Attribute not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<EquipmentTypeAttributeOption> equipmentTypeAttributeOptions = Lists.newArrayList( equipmentTypeAttribute.getAttributeOptions() );

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentTypeAttributeOptions.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentTypeAttributeOptions = PagerUtils.pageCollection( equipmentTypeAttributeOptions, pager );
        }
        
        /*
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }
        */
        
        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
    
    @RequestMapping(value = "/{uid}/attributeOptions/query/{q}", method = RequestMethod.GET)
    public String getAttributeOptionsByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentTypeAttribute equipmentTypeAttribute = equipmentTypeAttributeService.getEquipmentTypeAttributeByUid( options.getOptions().get( uid ) );

        if ( equipmentTypeAttribute == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type Attribute not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        
        
        /*
        List<EquipmentTypeAttributeOption> equipmentTypeAttributeOptions = Lists.newArrayList( equipmentTypeAttribute.getAttributeOptions() );

        for ( EquipmentTypeAttributeOption equipmentTypeAttributeOption : equipmentTypeAttribute.getAttributeOptions()) 
        {
            if ( equipmentTypeAttributeOption.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                equipmentTypeAttributeOptions.add( equipmentTypeAttributeOption );
            }
        }
        

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentTypeAttributeOptions.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentTypeAttributeOptions = PagerUtils.pageCollection( equipmentTypeAttributeOptions, pager );
        }

        metaData.setEquipmentTypeAttributeOptions(equipmentTypeAttributeOptions);
        */
        
        /*
        if ( options.hasLinks() )
        {
            WebUtils.generateLinks( metaData );
        }
        */
        
        model.addAttribute( "model", metaData );
        model.addAttribute( "viewClass", options.getViewClass( "basic" ) );

        return StringUtils.uncapitalize( getEntitySimpleName() );
    }
}

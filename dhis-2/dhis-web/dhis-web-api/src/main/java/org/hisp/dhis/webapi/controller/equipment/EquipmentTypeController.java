package org.hisp.dhis.webapi.controller.equipment;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.equipment.EquipmentType;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.coldchain.equipment.EquipmentType_Attribute;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dataset.DataSet;
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
@RequestMapping(value = EquipmentTypeController.RESOURCE_PATH)
public class EquipmentTypeController extends AbstractCrudController<EquipmentType>
{
    public static final String RESOURCE_PATH = "/equipmentTypes";
    
    @Autowired
    private EquipmentTypeService equipmentTypeService;


    @RequestMapping( value = "/{uid}/equipmentType_Attributes", method = RequestMethod.GET )
    public String getEquipmentType_Attributes( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( options.getOptions().get( uid ) );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<EquipmentType_Attribute> equipmentType_Attributes = Lists.newArrayList( equipmentType.getEquipmentType_Attributes()) ;

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentType_Attributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentType_Attributes = PagerUtils.pageCollection( equipmentType_Attributes, pager );
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
    

    @RequestMapping(value = "/{uid}/equipmentType_Attributes/query/{q}", method = RequestMethod.GET)
    public String getEquipmentType_AttributesByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( options.getOptions().get( uid ) );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        /*
        List<EquipmentType_Attribute> equipmentType_Attributes = Lists.newArrayList( equipmentType.getEquipmentType_Attributes()) ;

        
        for ( EquipmentType_Attribute equipmentType_Attribute : equipmentType.getEquipmentType_Attributes() )
        {
            if ( equipmentType_Attribute.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                equipmentType_Attributes.add( equipmentType_Attribute );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), equipmentType_Attributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            equipmentType_Attributes = PagerUtils.pageCollection( equipmentType_Attributes, pager );
        }

        metaData.setEquipmentType_Attributes(equipmentType_Attributes );
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
    
    @RequestMapping( value = "/{uid}/dataSets", method = RequestMethod.GET )
    public String getDataSets( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( options.getOptions().get( uid ) );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<org.hisp.dhis.dataset.DataSet> dataSets = Lists.newArrayList( equipmentType.getDataSets()) ;

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataSets.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataSets = PagerUtils.pageCollection( dataSets, pager );
        }

        metaData.setDataSets(dataSets);
        
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

    @RequestMapping(value = "/{uid}/dataSets/query/{q}", method = RequestMethod.GET)
    public String getDataSetsByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        EquipmentType equipmentType = equipmentTypeService.getEquipmentType( options.getOptions().get( uid ) );

        if ( equipmentType == null )
        {
            ContextUtils.notFoundResponse( response, "Equipment Type not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<org.hisp.dhis.dataset.DataSet> dataSets = Lists.newArrayList( equipmentType.getDataSets()) ;

        for ( DataSet dataSet : equipmentType.getDataSets() )
        {
            if ( dataSet.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                dataSets.add( dataSet );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), dataSets.size(), options.getPageSize() );
            metaData.setPager( pager );
            dataSets = PagerUtils.pageCollection( dataSets, pager );
        }

        metaData.setDataSets(dataSets );
        
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

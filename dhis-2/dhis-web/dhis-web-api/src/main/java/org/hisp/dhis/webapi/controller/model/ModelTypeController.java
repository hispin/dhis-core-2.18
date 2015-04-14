package org.hisp.dhis.webapi.controller.model;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.model.ModelType;
import org.hisp.dhis.coldchain.model.ModelTypeAttribute;
import org.hisp.dhis.coldchain.model.ModelTypeService;
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
@RequestMapping(value = ModelTypeController.RESOURCE_PATH)
public class ModelTypeController extends AbstractCrudController<ModelType>
{
    public static final String RESOURCE_PATH = "/modelTypes";
    
    @Autowired
    private ModelTypeService modelTypeService;
    
    
    @RequestMapping( value = "/{uid}/modelTypeAttributes", method = RequestMethod.GET )
    public String getModelTypeAttributes( @PathVariable( "uid" ) String uid, @RequestParam Map<String, String> parameters,
        Model model, HttpServletRequest request, HttpServletResponse response ) throws Exception
   {
        
        WebOptions options = new WebOptions( parameters );
        ModelType modelType = modelTypeService.getModelTypeByUid( options.getOptions().get( uid ) );

        if ( modelType == null )
        {
            ContextUtils.notFoundResponse( response, "Model Type Attribute not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<ModelTypeAttribute> modelTypeAttributes = Lists.newArrayList( modelType.getModelTypeAttributes()) ;

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), modelTypeAttributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            modelTypeAttributes = PagerUtils.pageCollection( modelTypeAttributes, pager );
        }

        metaData.setModelTypeAttributes(modelTypeAttributes);
        
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
    
    @RequestMapping(value = "/{uid}/modelTypeAttributes/query/{q}", method = RequestMethod.GET)
    public String getModelTypeAttributesByQuery( @PathVariable("uid") String uid, @PathVariable("q") String q,
        @RequestParam Map<String, String> parameters, Model model, HttpServletRequest request,
        HttpServletResponse response ) throws Exception
    {
        WebOptions options = new WebOptions( parameters );
        ModelType modelType = modelTypeService.getModelTypeByUid( options.getOptions().get( uid ) );

        if ( modelType == null )
        {
            ContextUtils.notFoundResponse( response, "Model Type Attribute not found for uid: " + uid );
            return null;
        }

        WebMetaData metaData = new WebMetaData();
        List<ModelTypeAttribute> modelTypeAttributes = Lists.newArrayList( modelType.getModelTypeAttributes()) ;

        for ( ModelTypeAttribute modelTypeAttribute : modelTypeAttributes )
        {
            if ( modelTypeAttribute.getDisplayName().toLowerCase().contains( q.toLowerCase() ) )
            {
                modelTypeAttributes.add( modelTypeAttribute );
            }
        }

        if ( options.hasPaging() )
        {
            Pager pager = new Pager( options.getPage(), modelTypeAttributes.size(), options.getPageSize() );
            metaData.setPager( pager );
            modelTypeAttributes = PagerUtils.pageCollection( modelTypeAttributes, pager );
        }

        metaData.setModelTypeAttributes(modelTypeAttributes );
        
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

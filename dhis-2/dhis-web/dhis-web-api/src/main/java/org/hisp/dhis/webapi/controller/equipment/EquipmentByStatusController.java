package org.hisp.dhis.webapi.controller.equipment;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_XML;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.dxf2.metadata.MetaData;
import org.hisp.dhis.dxf2.utils.JacksonUtils;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.utils.ContextUtils.CacheStrategy;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Mithilesh Kumar Thakur
 */
@Controller
@RequestMapping( method = RequestMethod.GET )
public class EquipmentByStatusController
{
    public static final String RESOURCE_PATH = "/eqStatus";
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private EquipmentStatusService equipmentStatusService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    @Autowired
    private ContextUtils contextUtils;
    
    @RequestMapping( value = EquipmentByStatusController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL')" )
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        List<Equipment> equipmentList = new ArrayList<Equipment>();
        
        //System.out.println("Inside EquipmentStatusController 1 " + equipmentStatusList.size() );
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
        String EqTypeId = null;
        
        String eqStatus = null;
        
        response.setContentType( CONTENT_TYPE_XML );
        
        if ( options.getOptions().containsKey( "status" ) )
        {
            eqStatus = options.getOptions().get( "status" ) ;
            equipmentList= new ArrayList<Equipment>( equipmentService.getEquipmentsByStatus( eqStatus ) );
        }        
 
        else if ( options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "status" ) )
        {
            EqTypeId =  options.getOptions().get( "eqType" ) ; 
            eqStatus = options.getOptions().get( "status" ) ;
            equipmentList = new ArrayList<Equipment>( equipmentService.getEquipmentsByStatus( equipmentTypeService.getEquipmentType( EqTypeId ), eqStatus ) );
        }         
        
        else
        {               
            equipmentList = new ArrayList<Equipment>( equipmentService.getAllEquipment() );
        }
        
        metaData.setEquipments( equipmentList );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );
        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
        
    }
}

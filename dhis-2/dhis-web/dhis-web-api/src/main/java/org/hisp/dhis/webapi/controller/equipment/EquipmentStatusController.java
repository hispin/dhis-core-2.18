package org.hisp.dhis.webapi.controller.equipment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
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
public class EquipmentStatusController
{
    // eqstatus.xml?ou=VEUHx5MQdKd&eqType=hnxagrozu8C&eqTrackId=D
    public static final String RESOURCE_PATH = "/equipmentStatus";
    
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
    
    @RequestMapping( value = EquipmentStatusController.RESOURCE_PATH + ".xml", produces = "*/*" )
    @PreAuthorize( "hasRole('ALL')" )
    
    public void exportXml( @RequestParam Map<String, String> parameters, HttpServletResponse response ) throws IOException
    {
        List<EquipmentStatus> equipmentStatusList = new ArrayList<EquipmentStatus>();
        
        //System.out.println("Inside EquipmentStatusController 1 " + equipmentStatusList.size() );
        
        WebOptions options = new WebOptions( parameters );
        MetaData metaData = new MetaData();
        
        String OUId = null;

        String EqTypeId = null;
        
        String eqTrackingId = null;
        
        response.setContentType( ContextUtils.CONTENT_TYPE_XML );
        
        if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "eqTrackId" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            EqTypeId =  options.getOptions().get( "eqType" );
            eqTrackingId = options.getOptions().get( "eqTrackId" );
                        
            if( eqTrackingId == null || eqTrackingId.trim().equals("") )
            {                   
                List<Equipment> equipmentList =  new ArrayList<Equipment>();
                
                equipmentList =  new ArrayList<Equipment>( equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) ) );
            
                for( Equipment equipment : equipmentList )
                {
                    if( equipment != null )
                    {
                        equipmentStatusList.addAll( new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) ) );
                    }
                }
            
            }
            else
            {
                List<Equipment> equipmentList =  new ArrayList<Equipment>();
                equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ), eqTrackingId ) );
                
                for( Equipment equipment : equipmentList )
                {
                    if( equipment != null )
                    {
                        equipmentStatusList.addAll( new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) ) );
                    }
                }
            }
        }
        else if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) )
        {
            OUId = options.getOptions().get( "ou" );
            EqTypeId =  options.getOptions().get( "eqType" );
            
            List<Equipment> equipmentList =  new ArrayList<Equipment>();
            equipmentList = new ArrayList<Equipment>( equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) ) );
            
            for( Equipment equipment : equipmentList )
            {
                if( equipment != null )
                {
                    equipmentStatusList.addAll( new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) ) );
                }
            }
        }
        else if ( options.getOptions().containsKey( "ou" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            
            List<Equipment> equipmentList =  new ArrayList<Equipment>();
            equipmentList = new ArrayList<Equipment>( equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ) ) );
            
            for( Equipment equipment : equipmentList )
            {
                if( equipment != null )
                {
                    equipmentStatusList.addAll( new ArrayList<EquipmentStatus>( equipmentStatusService.getEquipmentStatusHistory( equipment ) ) );
                }
            }
        }
        else
        {               
            equipmentStatusList = new ArrayList<EquipmentStatus>( equipmentStatusService.getAllEquipmentStatus() );
            //System.out.println("Inside EquipmentStatusController 2 " + equipmentStatusList.size() );
        }
        
        /*
        for( EquipmentStatus euipStatus : equipmentStatusList )
        {
            System.out.println( " euipStatus --  " + euipStatus.getStatus() + " Note --  " + euipStatus.getDescription() );
        }
        */
        
       
        metaData.setEquipmentStatusSet( equipmentStatusList );
        
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, CacheStrategy.NO_CACHE, "metaData.xml", true );
        Class<?> viewClass = JacksonUtils.getViewClass( options.getViewClass( "export" ) );
        JacksonUtils.toXmlWithView( response.getOutputStream(), metaData, viewClass );
        
        
    }
}

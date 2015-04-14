package org.hisp.dhis.webapi.controller.equipment;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentStatus;
import org.hisp.dhis.coldchain.equipment.EquipmentStatusService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.utils.ContextUtils;
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
@RequestMapping( value = EquipmentStatusPostController.RESOURCE_PATH )
public class EquipmentStatusPostController
{
    
    public static final String RESOURCE_PATH = "/importEqpStatus";
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private EquipmentStatusService equipmentStatusService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    //consumes = "application/json"
    //produces = "text/plain";
    //@RequestMapping( value = EquipmentStatusPostController.RESOURCE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL')" )
    @RequestMapping( method = RequestMethod.POST, produces = "text/plain"  )
    public void postEquipmentStatus( 
        //@RequestParam String equipmentId,
        //@RequestParam String reportingDate,
        //@RequestParam String dateOfUpdation,
        //@RequestParam String status,
        //@RequestParam( required = false ) String description, HttpServletResponse response
        
        @RequestParam Map<String, String> parameters , HttpServletResponse response )
    {
        //System.out.println(" Inside EquipmentStatus Post Controller 1 " + equipmentId + "--" + reportingDate + "--" + dateOfUpdation + "--" + status + "--" + description );
        
        try
        {
            //EquipmentStatus equipmentStatus = JacksonUtils.fromJson( in, EquipmentStatus.class );
            
            //JSONObject jsonObject = (JSONObject);
            
            WebOptions options = new WebOptions( parameters );
            
            System.out.println( "Size of parameter " + parameters.values().iterator().next().toString() );
            
            //options.getOptions().containsKey( "eqType" );
            
            String equipmentId =  options.getOptions().get( "equipmentId" );
            String reportingDate =  options.getOptions().get( "reportingDate" ) ;
            String dateOfUpdation =  options.getOptions().get( "dateOfUpdation" ) ;
            String status =  options.getOptions().get( "status" ) ;
            String description =  options.getOptions().get( "description" ) ;
            
            System.out.println(" Inside EquipmentStatus Post Controller 1 " + equipmentId + "--" + reportingDate + "--" + dateOfUpdation + "--" + status + "--" + description );
            
            // update in equipment
            Equipment equipment = equipmentService.getEquipment( equipmentId );

            if ( equipment == null )
            {
                ContextUtils.conflictResponse( response, "Illegal equipment : " + equipment );
                return;
            }
            
            if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ) )
            {
                equipment.setWorking( true );
                equipment.setCws( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            else if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE ) )
            {
                equipment.setWorking( false );
                equipment.setCws( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            else
            {
                equipment.setWorking( false );
                equipment.setCws( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            // add in equipmentStatus
            EquipmentStatus equipmentStatus = new EquipmentStatus();
            
            String storedBy = currentUserService.getCurrentUsername();
            
            equipmentStatus.setDescription( description );
            equipmentStatus.setEquipment( equipment );
            equipmentStatus.setStatus( status );
            equipmentStatus.setReportingDate( format.parseDate( reportingDate.trim() ) );
            equipmentStatus.setUpdationDate( format.parseDate( dateOfUpdation.trim() ) );
            equipmentStatus.setStoredBy( storedBy );
            
            equipmentStatusService.addEquipmentStatus( equipmentStatus );
            
        }
        catch( Exception e )
        {
            System.out.println( "***********************Exception in Importing: " + e.getMessage() );
            e.printStackTrace();
        }
    }
    
    /*
    @RequestMapping( value = EquipmentStatusPostController.RESOURCE_PATH, method = RequestMethod.POST, consumes = "application/json" )
    @PreAuthorize( "hasRole('ALL')" )
    public void importJson( ImportOptions importOptions, HttpServletResponse response, HttpServletRequest request )
    {
        try
        {
            MetaData metaData = null;
            
            metaData = JacksonUtils.fromJson( request.getInputStream(), MetaData.class );
        
            System.out.println( metaData );
            
            String equipmentId = "";
            
            String status = "";
            String description = "";
            
            String reportingDate = "";
            
            String dateOfUpdation = "";
            
            Equipment equipment = equipmentService.getEquipment( equipmentId );
            
            EquipmentStatus equipmentStatus = new EquipmentStatus();
            
            if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_WELL ) )
            {
                equipment.setWorking( true );
                equipment.setCws( EquipmentStatus.STATUS_WORKING_WELL.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            else if( status.equalsIgnoreCase( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE ) )
            {
                equipment.setWorking( false );
                equipment.setCws( EquipmentStatus.STATUS_WORKING_NEEDS_MAINTENANCE.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            else
            {
                equipment.setWorking( false );
                equipment.setCws( EquipmentStatus.STATUS_NOT_WORKING.trim() );
                
                equipmentService.updateEquipment( equipment );
            }
            
            String storedBy = currentUserService.getCurrentUsername();
                        
            equipmentStatus.setDescription( description );
            equipmentStatus.setEquipment( equipment );
            equipmentStatus.setStatus( status );
            equipmentStatus.setReportingDate( format.parseDate( reportingDate.trim() ) );
            equipmentStatus.setUpdationDate( format.parseDate( dateOfUpdation.trim() ) );
            equipmentStatus.setStoredBy( storedBy );
            
            equipmentStatusService.addEquipmentStatus( equipmentStatus );
            
        }
        
        catch( Exception e )
        {
            System.out.println( "***********************Exception in Importing: " + e.getMessage() );
            e.printStackTrace();
        }
    }
    
    private ImportSummary saveValue( ImportOptions importOptions, EquipmentStatus equipmentStatus )
    {
        ImportSummary summary = new ImportSummary();
        
        importOptions = importOptions != null ? importOptions : ImportOptions.getDefaultImportOptions();

        IdentifiableProperty equipmentId = equipmentStatus.getEquipment().getUid() != null ? IdentifiableProperty.valueOf( dataValueSet.getDataElementIdScheme().toUpperCase() ) : importOptions..getDataElementIdScheme();
        
        
        
        return summary;
    }
    
    */
    
    
}

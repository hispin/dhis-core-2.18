package org.hisp.dhis.webapi.controller.equipment;

import java.util.List;

import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.coldchain.equipment.EquipmentTypeService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.WebMetaData;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Mithilesh Kumar Thakur
 */

@Controller
@RequestMapping(value = EquipmentController.RESOURCE_PATH)
public class EquipmentController 
        extends AbstractCrudController<Equipment>
{
    public static final String RESOURCE_PATH = "/equipments";

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private EquipmentTypeService equipmentTypeService;

    
    protected List<Equipment> getEntityList( WebMetaData metaData, WebOptions options )
    {
        List<Equipment> entityList=null;
     
        String OUId = null;

        String EqTypeId = null;
        
        String eqTrackingId = null;
        
        String eqStatus = null;

        if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "eqTrackId" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            EqTypeId =  options.getOptions().get( "eqType" );
            eqTrackingId = options.getOptions().get( "eqTrackId" );
                        
            if( eqTrackingId == null || eqTrackingId.trim().equals("") )
            {                   
                entityList= (List<Equipment>) equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) );
            }
            else
            {
                entityList= (List<Equipment>) equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ), eqTrackingId );
            }
        }
        
        else if ( options.getOptions().containsKey( "ou" ) && options.getOptions().containsKey( "eqType" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            EqTypeId =  options.getOptions().get( "eqType" ) ;             
            entityList= (List<Equipment>) equipmentService.getEquipmentList( organisationUnitService.getOrganisationUnit( OUId ), equipmentTypeService.getEquipmentType( EqTypeId ) );
        }
        
        else if ( options.getOptions().containsKey( "status" ) )
        {
            eqStatus = options.getOptions().get( "status" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipmentsByStatus( eqStatus );
        }        
 
        else if ( options.getOptions().containsKey( "eqType" ) && options.getOptions().containsKey( "status" ) )
        {
            EqTypeId =  options.getOptions().get( "eqType" ) ; 
            eqStatus = options.getOptions().get( "status" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipmentsByStatus( equipmentTypeService.getEquipmentType( EqTypeId ), eqStatus );
        }         
        
        else if ( options.getOptions().containsKey( "ou" ) )
        {
            OUId = options.getOptions().get( "ou" ) ;
            entityList= (List<Equipment>) equipmentService.getEquipments( organisationUnitService.getOrganisationUnit( OUId ) );                      
        }
        
        else
        {               
            entityList=(List<Equipment>) equipmentService.getAllEquipment();
        }
        
        return entityList;
    }

}

package org.hisp.dhis.webapi.controller.equipment;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.coldchain.equipment.Equipment;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValue;
import org.hisp.dhis.coldchain.equipment.EquipmentDataValueService;
import org.hisp.dhis.coldchain.equipment.EquipmentService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.utils.ContextUtils;
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
@RequestMapping(value = EquipmentDataValueController.RESOURCE_PATH)
public class EquipmentDataValueController
{
    public static final String RESOURCE_PATH = "/equipmentDataValues";
   
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private EquipmentService equipmentService;
    
    @Autowired
    private EquipmentDataValueService equipmentDataValueService;
    
    @Autowired
    private DataSetService dataSetService;

    @PreAuthorize( "hasRole('ALL') or hasRole('F_DATAVALUE_ADD')" )
    @RequestMapping( method = RequestMethod.POST, produces = "text/plain" )
    public void saveEquipmentDataValue( 
        @RequestParam String eq, 
        @RequestParam String de, 
        @RequestParam String pe, 
        @RequestParam( required = false ) String value, HttpServletResponse response )
    {
        // ---------------------------------------------------------------------
        // Input validation
        // ---------------------------------------------------------------------
        
        Equipment equipment = equipmentService.getEquipment( eq.toString() );

        if ( equipment == null )
        {
            ContextUtils.conflictResponse( response, "Illegal equipment identifier: " + eq );
            return;
        }
        
        DataElement dataElement = dataElementService.getDataElement(de);

        if ( dataElement == null )
        {
            ContextUtils.conflictResponse( response, "Illegal data element identifier: " + de );
            return;
        }
        
        Period period = PeriodType.getPeriodFromIsoString( pe );

        if ( period == null )
        {
            ContextUtils.conflictResponse( response, "Illegal period identifier: " + pe );
            return;
        }
        
        // ---------------------------------------------------------------------
        // Locking validation
        // ---------------------------------------------------------------------

        if ( dataSetService.isLocked( dataElement, period, equipment.getOrganisationUnit(), null ) )
        {
            ContextUtils.conflictResponse( response, "Data set is locked" );
            return;
        }

        // ---------------------------------------------------------------------
        // Assemble and save equipment data value
        // ---------------------------------------------------------------------
        
        String storedBy = currentUserService.getCurrentUsername();
        System.out.println("equipment :" + equipment);        
        System.out.println("dataElement :" + dataElement);
        System.out.println("period :" + period);
        System.out.println("value :" + value);
        
        EquipmentDataValue equipmentDataValue = equipmentDataValueService.getEquipmentDataValue( equipment, period, dataElement );

        if ( equipmentDataValue == null )
        {
                equipmentDataValue = new EquipmentDataValue( equipment, dataElement, period );

            if ( value != null )
            {
                equipmentDataValue.setValue( StringUtils.trimToNull( value ) );
                equipmentDataValue.setValue( value );
            }

            equipmentDataValue.setStoredBy( storedBy );
            equipmentDataValue.setTimestamp( new Date() );
            equipmentDataValueService.addEquipmentDataValue( equipmentDataValue );
        }
        else
        {
            if ( value != null )
            {
                equipmentDataValue.setValue( StringUtils.trimToNull( value ) );
                equipmentDataValue.setValue( value );
            }
            
            equipmentDataValue.setStoredBy( storedBy );
            equipmentDataValue.setTimestamp( new Date() );
            equipmentDataValueService.updateEquipmentDataValue( equipmentDataValue );
        }
    }
}

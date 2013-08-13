package org.hisp.dhis.coldchain.equipment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.coldchain.catalog.Catalog;
import org.hisp.dhis.coldchain.catalog.CatalogService;
import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.EquipmentService;
import org.hisp.dhis.coldchain.inventory.EquipmentStatus;
import org.hisp.dhis.coldchain.inventory.EquipmentStatusService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOption;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeOptionService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version AddUpdateEquipmentIcePacksAction.javaDec 21, 2012 2:49:06 PM	
 */

public class AddUpdateEquipmentIcePacksAction implements Action
{

    public static final String PREFIX_ATTRIBUTE = "attr";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private InventoryTypeService inventoryTypeService;
    
    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
    private InventoryTypeAttributeOptionService inventoryTypeAttributeOptionService;
    
    public void setInventoryTypeAttributeOptionService( InventoryTypeAttributeOptionService inventoryTypeAttributeOptionService )
    {
        this.inventoryTypeAttributeOptionService = inventoryTypeAttributeOptionService;
    }
    
    private EquipmentInstanceService equipmentInstanceService;
    
    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }

    private CatalogService catalogService;
    
    public void setCatalogService( CatalogService catalogService )
    {
        this.catalogService = catalogService;
    }
    
    private EquipmentService equipmentService;
    
    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }

    private CurrentUserService currentUserService;
    
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    private EquipmentStatusService equipmentStatusService;
    
    public void setEquipmentStatusService( EquipmentStatusService equipmentStatusService )
    {
        this.equipmentStatusService = equipmentStatusService;
    }
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    
    private String equipmentInstanceID;
    
    public void setEquipmentInstanceID( String equipmentInstanceID )
    {
        this.equipmentInstanceID = equipmentInstanceID;
    }

    /*
    private Integer equipmentInstanceID;
    
    public void setEquipmentInstanceID( Integer equipmentInstanceID )
    {
        this.equipmentInstanceID = equipmentInstanceID;
    }
    */
    private Integer inventoryTypeIcePacksId;
    
    public void setInventoryTypeIcePacksId( Integer inventoryTypeIcePacksId )
    {
        this.inventoryTypeIcePacksId = inventoryTypeIcePacksId;
    }
    
    private Integer healthFacility;
    
    public void setHealthFacility( Integer healthFacility )
    {
        this.healthFacility = healthFacility;
    }
    
    private Integer catalog;
    
    public void setCatalog( Integer catalog )
    {
        this.catalog = catalog;
    }
    
    private String message;
    
    public String getMessage()
    {
        return message;
    }
    
    private InventoryType inventoryType;
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( healthFacility );
        
        EquipmentInstance equipmentInstance = null;
        
        if( equipmentInstanceID != null && !equipmentInstanceID.equalsIgnoreCase( "" ) )
        {
            equipmentInstance = equipmentInstanceService.getEquipmentInstance( Integer.parseInt( equipmentInstanceID ) );
        }
        
        
        //EquipmentInstance equipmentInstance = equipmentInstanceService.getEquipmentInstance( equipmentInstanceID );
        
        Catalog selCatalog = null;
        
        if( catalog != null )
        {    
            selCatalog = catalogService.getCatalog( catalog );
        }
        
        HttpServletRequest request = ServletActionContext.getRequest();
        String value = null;
        
        
        if( equipmentInstance == null )
        {
            inventoryType = inventoryTypeService.getInventoryType( inventoryTypeIcePacksId );
            
            // -----------------------------------------------------------------------------
            // Preparing EquipmentInstance
            // -----------------------------------------------------------------------------
            
            EquipmentInstance equipmentInstanceIcePacks = new EquipmentInstance();
            
            equipmentInstanceIcePacks.setInventoryType( inventoryType );
            equipmentInstanceIcePacks.setOrganisationUnit( orgUnit );
            
            if( selCatalog != null )
            {
                equipmentInstanceIcePacks.setCatalog( selCatalog );
            }
            
            // -----------------------------------------------------------------------------
            // Preparing Equipment Details
            // -----------------------------------------------------------------------------
            
            List<InventoryTypeAttribute> inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( );
            for( InventoryType_Attribute inventoryType_Attribute : inventoryType.getInventoryType_Attributes() )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
            
            List<Equipment> equipmentDeatilsList = new ArrayList<Equipment>();
            
            Equipment equipmentDetails = null;
            for ( InventoryTypeAttribute attribute : inventoryTypeAttributes )
            {
                
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    equipmentDetails = new Equipment();
                    equipmentDetails.setEquipmentInstance( equipmentInstanceIcePacks );
                    equipmentDetails.setInventoryTypeAttribute( attribute );

                    if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        
                        InventoryTypeAttributeOption option = inventoryTypeAttributeOptionService.getInventoryTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( option.getName() );
                            
                            if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                            {
                                System.out.println( "Option ID is  : " + option.getId() + "Option Name is : "+option.getName() );
                                
                                if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                                {
                                    equipmentInstanceIcePacks.setWorking( false );
                                }
                                else
                                {
                                    equipmentInstanceIcePacks.setWorking( true );
                                }
                            }
                        }
                        else
                        {
                            // Someone deleted this option ...
                        }
                    }
                    else if ( InventoryTypeAttribute.TYPE_CATALOG.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        Catalog catalog = catalogService.getCatalog( NumberUtils.toInt( value, 0 ) );
                        if ( catalog != null )
                        {
                            //equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( catalog.getName() );
                        }
                        else
                        {
                            // Someone deleted this catalog ...
                        }
                    }
                    else
                    {
                        equipmentDetails.setValue( value.trim() );
                    }
                    equipmentDeatilsList.add( equipmentDetails );
                }
            }
            
            // -----------------------------------------------------------------------------
            // Creating Equipment Instance and saving equipment data
            // -----------------------------------------------------------------------------
            Integer id = equipmentInstanceService.createEquipment( equipmentInstanceIcePacks, equipmentDeatilsList );

            message = id + "";
        }
        
        else
        {
            inventoryType = equipmentInstance.getInventoryType();
            
            if( selCatalog != null )
            {
                equipmentInstance.setCatalog( selCatalog );
                
                equipmentInstanceService.updateEquipmentInstance( equipmentInstance );
            }
            
            List<InventoryTypeAttribute> inventoryTypeAttributes = new ArrayList<InventoryTypeAttribute>( );
            
            for( InventoryType_Attribute inventoryType_Attribute : inventoryType.getInventoryType_Attributes() )
            {
                inventoryTypeAttributes.add( inventoryType_Attribute.getInventoryTypeAttribute() );
            }
            
            Equipment equipmentDetails = null;
            for ( InventoryTypeAttribute attribute : inventoryTypeAttributes )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE + attribute.getId() );
                
                equipmentDetails = equipmentService.getEquipment( equipmentInstance, attribute );
                
                if( equipmentDetails == null && value != null )
                {
                    equipmentDetails = new Equipment();
                    equipmentDetails.setEquipmentInstance( equipmentInstance );
                    equipmentDetails.setInventoryTypeAttribute( attribute );

                    if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        InventoryTypeAttributeOption option = inventoryTypeAttributeOptionService.getInventoryTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                        if ( option != null )
                        {
                            equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( option.getName() );
                            
                            if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                            {
                                //System.out.println( "Option ID is  : " + option.getId() + "---Option Name is : "+option.getName() );
                                
                                if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                                {
                                    equipmentInstance.setWorking( false );
                                    equipmentInstanceService.updateEquipmentInstance( equipmentInstance );
                                }
                                else
                                {
                                    equipmentInstance.setWorking( true );
                                    equipmentInstanceService.updateEquipmentInstance( equipmentInstance );
                                }
                                
                                
                                String storedBy = currentUserService.getCurrentUsername();
                                
                                EquipmentStatus equipmentStatus = new EquipmentStatus();
                                
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
                                String currentDate = sdf.format(new Date());

                                equipmentStatus.setDescription( "Updated from edit equipment screen" );
                                equipmentStatus.setEquipmentInstance( equipmentInstance );
                                equipmentStatus.setStatus( option.getName() );
                                
                                equipmentStatus.setReportingDate( format.parseDate( currentDate.trim() ) );
                                equipmentStatus.setUpdationDate( format.parseDate( currentDate.trim() ) );
                                equipmentStatus.setStoredBy( storedBy );
                                
                                equipmentStatusService.addEquipmentStatus( equipmentStatus );
                                
                            }
                            
                        }
                        
                        else
                        {
                            // Someone deleted this option ...
                        }
                    }
                    else if ( InventoryTypeAttribute.TYPE_CATALOG.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        Catalog catalog = catalogService.getCatalog( NumberUtils.toInt( value, 0 ) );
                        if ( catalog != null )
                        {
                            //equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( catalog.getName() );
                        }
                        else
                        {
                            // Someone deleted this catalog ...
                        }
                    }
                    else
                    {
                        equipmentDetails.setValue( value.trim() );
                    }
                    
                    equipmentService.addEquipment( equipmentDetails );
                }
                else
                {
                    if ( InventoryTypeAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        InventoryTypeAttributeOption option = inventoryTypeAttributeOptionService.getInventoryTypeAttributeOption( NumberUtils.toInt( value, 0 ) );
                        
                        //System.out.println( " Option is  : " + option + "-- and value is --" + value.trim());
                        
                        if ( option != null )
                        {
                            equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( option.getName() );
                            
                            
                            if ( EquipmentStatus.WORKING_STATUS.equalsIgnoreCase( attribute.getDescription() ) )
                            {
                                //System.out.println( " Option ID is  : " + option.getId() + "---Option Name is : " + option.getName() );
                                
                                if ( EquipmentStatus.STATUS_NOT_WORKING.equalsIgnoreCase( option.getName() ) )
                                {
                                    equipmentInstance.setWorking( false );
                                    equipmentInstanceService.updateEquipmentInstance( equipmentInstance );
                                }
                                else
                                {
                                    equipmentInstance.setWorking( true );
                                    equipmentInstanceService.updateEquipmentInstance( equipmentInstance );
                                }
                                
                                String storedBy = currentUserService.getCurrentUsername();
                                
                                EquipmentStatus equipmentStatus = new EquipmentStatus();
                                
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");      
                                String currentDate = sdf.format(new Date());

                                equipmentStatus.setDescription( "Updated from edit equipment screen" );
                                equipmentStatus.setEquipmentInstance( equipmentInstance );
                                equipmentStatus.setStatus( option.getName() );
                                
                                equipmentStatus.setReportingDate( format.parseDate( currentDate.trim() ) );
                                equipmentStatus.setUpdationDate( format.parseDate( currentDate.trim() ) );
                                equipmentStatus.setStoredBy( storedBy );
                                
                                equipmentStatusService.addEquipmentStatus( equipmentStatus );
                                
                            }
                            
                        }
                        else
                        {
                            // Someone deleted this option ...
                            equipmentDetails.setValue( value.trim() );
                        }
                    }
                    else if ( InventoryTypeAttribute.TYPE_CATALOG.equalsIgnoreCase( attribute.getValueType() ) )
                    {
                        Catalog catalog = catalogService.getCatalog( NumberUtils.toInt( value, 0 ) );
                        if ( catalog != null )
                        {
                            //equipmentDetails.setInventoryTypeAttributeOption( option );
                            equipmentDetails.setValue( catalog.getName() );
                        }
                        else
                        {
                            // Someone deleted this catalog ...
                            equipmentDetails.setValue( value.trim() );
                        }
                    }
                    else
                    {
                        equipmentDetails.setValue( value.trim() );
                    }

                    equipmentService.updateEquipment( equipmentDetails );
                }
                    
            }
             
            message = ""+ equipmentInstanceID;
            
        }
        
        return SUCCESS;
    }

}
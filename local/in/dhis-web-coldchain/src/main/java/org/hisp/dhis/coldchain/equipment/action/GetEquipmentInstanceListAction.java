package org.hisp.dhis.coldchain.equipment.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetEquipmentInstanceListAction  extends ActionPagingSupport<EquipmentInstance>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    private EquipmentInstanceService equipmentInstanceService;

    public void setEquipmentInstanceService( EquipmentInstanceService equipmentInstanceService )
    {
        this.equipmentInstanceService = equipmentInstanceService;
    }
    
    private InventoryTypeService inventoryTypeService;
    
    public void setInventoryTypeService( InventoryTypeService inventoryTypeService )
    {
        this.inventoryTypeService = inventoryTypeService;
    }
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private InventoryTypeAttributeService inventoryTypeAttributeService;
    
    public void setInventoryTypeAttributeService( InventoryTypeAttributeService inventoryTypeAttributeService )
    {
        this.inventoryTypeAttributeService = inventoryTypeAttributeService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<EquipmentInstance> equipmentInstanceList;

    public List<EquipmentInstance> getEquipmentInstanceList()
    {
        return equipmentInstanceList;
    }
    
    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String inventoryTypeId;

    public void setInventoryTypeId( String inventoryTypeId )
    {
        this.inventoryTypeId = inventoryTypeId;
    }

    private String inventoryTypeAttributeId;
    
    public void setInventoryTypeAttributeId( String inventoryTypeAttributeId )
    {
        this.inventoryTypeAttributeId = inventoryTypeAttributeId;
    }

    private Boolean listAll;
    
    public void setListAll( Boolean listAll )
    {
        this.listAll = listAll;
    }

    private Integer total;
    
    public Integer getTotal()
    {
        return total;
    }
    
    private String searchText;
    
    public String getSearchText()
    {
        return searchText;
    }

    public void setSearchText( String searchText )
    {
        this.searchText = searchText;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        System.out.println("insde GetEquipmentInstanceListAction");
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        InventoryType inventoryType = inventoryTypeService.getInventoryType( Integer.parseInt( inventoryTypeId ) );
        
        
        if ( listAll != null && listAll )
        {
            listAllEquipmentInstance( orgUnit, inventoryType );

            return SUCCESS;
        }

        InventoryTypeAttribute inventoryTypeAttribute = inventoryTypeAttributeService.getInventoryTypeAttribute( Integer.parseInt( inventoryTypeAttributeId ) );

        listEquipmentInstancesByFilter( orgUnit, inventoryType, inventoryTypeAttribute, searchText);
        //equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType ) );
        
        return SUCCESS;
    }
    
    private void listAllEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType );
        
        this.paging = createPaging( total );

        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, paging.getStartPos(), paging
            .getPageSize() ) );
    }
    
    private void listEquipmentInstancesByFilter( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey )
    {
        //total = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType, inventoryTypeAttribute, searchText );
        
        this.paging = createPaging( total );
        
        //equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, inventoryTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ) );
    }
}

package org.hisp.dhis.ccem.equipment.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.coldchain.inventory.Equipment;
import org.hisp.dhis.coldchain.inventory.EquipmentInstance;
import org.hisp.dhis.coldchain.inventory.EquipmentInstanceService;
import org.hisp.dhis.coldchain.inventory.EquipmentService;
import org.hisp.dhis.coldchain.inventory.InventoryType;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttribute;
import org.hisp.dhis.coldchain.inventory.InventoryTypeAttributeService;
import org.hisp.dhis.coldchain.inventory.InventoryTypeService;
import org.hisp.dhis.coldchain.inventory.InventoryType_Attribute;
import org.hisp.dhis.coldchain.inventory.InventoryType_AttributeService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetEquipmentInstanceListAction  extends ActionPagingSupport<EquipmentInstance>
{
    private final String CLINIC = "Medical Clinic  Private";
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
    
    private EquipmentService equipmentService;

    public void setEquipmentService( EquipmentService equipmentService )
    {
        this.equipmentService = equipmentService;
    }
    
    private InventoryType_AttributeService inventoryType_AttributeService;
    
    public void setInventoryType_AttributeService( InventoryType_AttributeService inventoryType_AttributeService )
    {
        this.inventoryType_AttributeService = inventoryType_AttributeService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<EquipmentInstance> equipmentInstanceList;

    public List<EquipmentInstance> getEquipmentInstanceList()
    {
        return equipmentInstanceList;
    }
    
    private InventoryType inventoryType;
    
    public InventoryType getInventoryType()
    {
        return inventoryType;
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

    public Map<String, String> equipmentDetailsMap;
    
    public Map<String, String> getEquipmentDetailsMap()
    {
        return equipmentDetailsMap;
    }
    
    /*
    public List<InventoryTypeAttribute> inventoryTypeAttributeList;
    
    public List<InventoryTypeAttribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }
    */
    
    public List<InventoryType_Attribute> inventoryTypeAttributeList;
    
    public List<InventoryType_Attribute> getInventoryTypeAttributeList()
    {
        return inventoryTypeAttributeList;
    }
    
    private Map<Integer, String> equipmentOrgUnitHierarchyMap = new HashMap<Integer, String>();
    
    public Map<Integer, String> getEquipmentOrgUnitHierarchyMap()
    {
        return equipmentOrgUnitHierarchyMap;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    String orgUnitIdsByComma;
    String searchBy = "";
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("insde GetEquipmentInstanceListAction");
        
        equipmentDetailsMap = new HashMap<String, String>();
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        inventoryType = inventoryTypeService.getInventoryType( Integer.parseInt( inventoryTypeId ) );
        
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
        
		List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( CLINIC ) ); 
        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );
       
        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }
        
        orgUnitIdsByComma = "-1";
        for( OrganisationUnit orgnisationUnit : orgUnitList )
        {
            orgUnitIdsByComma += "," + orgnisationUnit.getId();
        }
        
        
        if ( listAll != null && listAll )
        {
            //listAllEquipmentInstance( orgUnit, inventoryType );
            
            listAllEquipmentInstance( orgUnitList, inventoryType );
            
            getInventoryTypeAttributeData();
            
            return SUCCESS;
        }
        
        if( inventoryTypeAttributeId.equalsIgnoreCase(  Equipment.PREFIX_CATALOG_NAME ))
        {
            //System.out.println( inventoryTypeAttributeId + " -- inside search by -- " + Equipment.PREFIX_CATALOG_NAME );
            
            searchBy = inventoryTypeAttributeId;
            
            listEquipmentInstancesByFilter( orgUnitIdsByComma, inventoryType, null, searchText, searchBy );
            
            getInventoryTypeAttributeData();
            
            return SUCCESS;
        }
        
        if ( inventoryTypeAttributeId.equalsIgnoreCase( Equipment.PREFIX_ORGANISATIONUNIT_NAME ))
        {
            //System.out.println( inventoryTypeAttributeId + " -- inside search by -- " + Equipment.PREFIX_ORGANISATIONUNIT_NAME );
            
            searchBy = inventoryTypeAttributeId;
            
            listEquipmentInstancesByFilter( orgUnitIdsByComma, inventoryType, null, searchText, searchBy );
            
            getInventoryTypeAttributeData();
            
            return SUCCESS;
        }
        
        
        InventoryTypeAttribute inventoryTypeAttribute = inventoryTypeAttributeService.getInventoryTypeAttribute( Integer.parseInt( inventoryTypeAttributeId ) );
        
        //listEquipmentInstancesByFilter( orgUnit, inventoryType, inventoryTypeAttribute, searchText);
        
        listEquipmentInstancesByFilter( orgUnitIdsByComma, inventoryType, inventoryTypeAttribute, searchText , "" );
        
        getInventoryTypeAttributeData();
        
        return SUCCESS;
    }
    
    // supportive methods
    
    private void getInventoryTypeAttributeData()
    {
        //InventoryTypeAttribute tempInventoryTypeAttribute = inventoryTypeAttributeService.getInventoryTypeAttribute( Integer.parseInt( inventoryTypeAttributeId ) );
        
        //inventoryTypeAttributeList = new ArrayList<InventoryTypeAttribute>( inventoryTypeService.getAllInventoryTypeAttributesForDisplay( inventoryType ));
        
        inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributeForDisplay( inventoryType, true ) );
       
        /*
        List<InventoryTypeAttribute> tempinventoryTypeAttributeList = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() ) ;
        
        if( tempinventoryTypeAttributeList != null )
        {
            
            for( InventoryTypeAttribute tempInventoryTypeAttribute : tempinventoryTypeAttributeList )
            {
                if ( tempInventoryTypeAttribute.isDisplay() )
                {
                    inventoryTypeAttributeList.add( tempInventoryTypeAttribute );
                }
            }
        }
        */
        //inventoryTypeAttributeList = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() );
       
        //System.out.println("size of inventoryTypeAttributeList " + inventoryTypeAttributeList.size() );
        
        if( inventoryTypeAttributeList == null || inventoryTypeAttributeList.size() == 0  )
        {
            //inventoryTypeAttributeList = new ArrayList<InventoryTypeAttribute>( inventoryType.getInventoryTypeAttributes() );
            
            inventoryTypeAttributeList = new ArrayList<InventoryType_Attribute>( inventoryType_AttributeService.getAllInventoryTypeAttributesByInventoryType( inventoryType ) );
            
            //Collections.sort( inventoryTypeAttributeList, new InventoryTypeAttributeMandatoryComparator() );
            if( inventoryTypeAttributeList != null && inventoryTypeAttributeList.size() > 3 )
            {
                int count = 1;
                //Iterator<InventoryTypeAttribute> iterator = inventoryTypeAttributeList.iterator();
                Iterator<InventoryType_Attribute> iterator = inventoryTypeAttributeList.iterator();
                while( iterator.hasNext() )
                {
                    iterator.next();
                    
                    if( count > 3 )
                        iterator.remove();
                    
                    count++;
                }            
            }
            
        }

        for( EquipmentInstance equipmentInstance : equipmentInstanceList )
        {
            for( InventoryType_Attribute inventoryTypeAttribute1 : inventoryTypeAttributeList )
            {
                Equipment equipmentDetails = equipmentService.getEquipment( equipmentInstance, inventoryTypeAttribute1.getInventoryTypeAttribute() );
                if( equipmentDetails != null && equipmentDetails.getValue() != null )
                {
                    equipmentDetailsMap.put( equipmentInstance.getId()+":"+inventoryTypeAttribute1.getInventoryTypeAttribute().getId(), equipmentDetails.getValue() );
                }
            }
            equipmentOrgUnitHierarchyMap.put( equipmentInstance.getOrganisationUnit().getId(), getHierarchyOrgunit( equipmentInstance.getOrganisationUnit() ) );
        }
    }
    
    /*
    private void listAllEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType )
    {
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType );
        
        this.paging = createPaging( total );

        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, paging.getStartPos(), paging
            .getPageSize() ) );
    }
    
    private void listEquipmentInstancesByFilter( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey )
    {
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType, inventoryTypeAttribute, searchText );
        
        this.paging = createPaging( total );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, inventoryTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ) );
    }
    */
    
    private void listAllEquipmentInstance( List<OrganisationUnit> orgUnitList, InventoryType inventoryType )
    {
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnitList, inventoryType );
        
        this.paging = createPaging( total );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnitList, inventoryType, paging.getStartPos(), paging.getPageSize() ) );
    }    
    
    
    private void listEquipmentInstancesByFilter( String orgUnitIdsByComma, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey ,String searchBy )
    {
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnitIdsByComma, inventoryType, inventoryTypeAttribute, searchText, searchBy );
        
        this.paging = createPaging( total );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnitIdsByComma, inventoryType, inventoryTypeAttribute, searchText, searchBy, paging.getStartPos(), paging.getPageSize() ) );
        
    }
        
    
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}

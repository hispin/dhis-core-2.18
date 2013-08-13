package org.hisp.dhis.coldchain.equipment.action;

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
   // private final String HEALTHFACILITY = "Health Facality";
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
    
    private List<EquipmentInstance> finalEquipmentInstanceList =  new ArrayList<EquipmentInstance>();;
    
    public List<EquipmentInstance> getFinalEquipmentInstanceList()
    {
        return finalEquipmentInstanceList;
    }
    
    String orgUnitIdsByComma;
    String searchBy = "";
    
    
    
    private List<String> filteredOrgUnitList = new ArrayList<String>();
    
    public void setFilteredOrgUnitList( List<String> filteredOrgUnitList )
    {
        this.filteredOrgUnitList = filteredOrgUnitList;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute() throws Exception
    {
        //System.out.println("insde GetEquipmentInstanceListAction");
        
        //System.out.println(" Initial Size of filter OrgUnit List is : " + filteredOrgUnitList.size());
        
        // How to find duplicate words in a string in java
        /*
        String text = "a r b k c d se f g a d f s s f d s ft gh f ws w f v x s g h d h j j k f sd j e wed a d f";
        
        List<String> list = Arrays.asList(text.split(" "));
        
        Set<String> uniqueWords = new HashSet<String>(list);
        
        for (String word : uniqueWords) 
        {
            System.out.println(word + ": " + Collections.frequency( list, word ));
        }
        */
        
        equipmentDetailsMap = new HashMap<String, String>();
        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitId ) );
        
        //System.out.println(" orgUnit is : " + orgUnit.getId() + " -- " + orgUnit.getName() );
        
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );
       
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() );
        
       
        List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( Equipment.HEALTHFACILITY ) ); 
        //OrganisationUnitGroup ouGroup = organisationUnitGroupService.getOrganisationUnitGroupByName( Equipment.HEALTHFACILITY );
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
        
        if ( orgUnitList.size() == 0 ) 
        {
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( orgUnit.getId() ) );  
        }
        
        //System.out.println(" Size of orgUnitList is : " + orgUnitList.size() + " -- " + orgUnitIdsByComma );
        
        if ( filteredOrgUnitList != null && filteredOrgUnitList.size() > 0 )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            
            Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
            while ( orGUnitIterator.hasNext() )
            {
                int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                
                OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                
                orgUnitList.add( filterOrgUnit );
            }
            
            orgUnitIdsByComma = new String();
            
            orgUnitIdsByComma = "-1";
            
            for( OrganisationUnit orgnisationUnit : orgUnitList )
            {
                orgUnitIdsByComma += "," + orgnisationUnit.getId();
            }
            //System.out.println(" Size of filteredOrgUnitList is : " + filteredOrgUnitList.size() + " -- " + orgUnitIdsByComma );
        }
        
        //orgUnitList.add( orgUnit );
        
        //System.out.println(" Size of filteredOrgUnitList is : " + filteredOrgUnitList.size() + " -- " + orgUnitIdsByComma );
        
        inventoryType = inventoryTypeService.getInventoryType( Integer.parseInt( inventoryTypeId ) );
        
        //inventoryType.getCatalogType().getName();
        
        //inventoryType.getDataSets().size();
        
        //System.out.println( " Size of initial OrgUnit List is : " + orgUnitList.size() + " -- initial filter orgUnit List " + filteredOrgUnitList.size() );
        
        if ( listAll != null && listAll )
        {
            if ( filteredOrgUnitList != null && filteredOrgUnitList.size() > 0 )
            {
                //System.out.println( " Size of filter OrgUnit List inside filter condition is : " + filteredOrgUnitList.size());
                
                orgUnitList = new ArrayList<OrganisationUnit>();
                
                Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
                while ( orGUnitIterator.hasNext() )
                {
                    int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                    
                    OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                    
                    orgUnitList.add( filterOrgUnit );
                }
                
                listAllEquipmentInstance( orgUnitList, inventoryType );

                getInventoryTypeAttributeData();
            }
            
            //listAllEquipmentInstance( orgUnit, inventoryType );
            listAllEquipmentInstance( orgUnitList, inventoryType );

            getInventoryTypeAttributeData();
            
            //System.out.println(" Size of final Equipment Instance List is : " + finalEquipmentInstanceList.size());
            
            return SUCCESS;
        }
        
        /*
        if (  ( filteredOrgUnitList != null ) && ( listAll != null && listAll ) )
        {
            System.out.println( " Size of filter OrgUnit List is : " + filteredOrgUnitList.size());
            
            orgUnitList = new ArrayList<OrganisationUnit>();
            
            Iterator<String> orGUnitIterator = filteredOrgUnitList.iterator();
            while ( orGUnitIterator.hasNext() )
            {
                int filterOrgUnitID = Integer.parseInt( (String) orGUnitIterator.next() );
                
                OrganisationUnit filterOrgUnit = organisationUnitService.getOrganisationUnit(  filterOrgUnitID  );
                
                orgUnitList.add( filterOrgUnit );
            }
            
            listAllEquipmentInstance( orgUnitList, inventoryType );

            getInventoryTypeAttributeData();
            
            return SUCCESS;
        }
        
        */
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
        
        //listEquipmentInstancesByFilter( orgUnitList, inventoryType, inventoryTypeAttribute, searchText);
        
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
            //equipmentInstance.getOrganisationUnit().getId();
            //equipmentInstance.getCatalog().getName();
            
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
    //private void listAllEquipmentInstance( OrganisationUnit orgUnit, InventoryType inventoryType ) 
    private void listAllEquipmentInstance( List<OrganisationUnit> orgUnitList, InventoryType inventoryType )
    {
       
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnitList, inventoryType );
        
        this.paging = createPaging( total );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnitList, inventoryType, paging.getStartPos(), paging.getPageSize() ) );
        
        //System.out.println(" Size of Equipment Instance List is : " + equipmentInstanceList.size());
        
        
        /*
        int tempTotal;
        
        List <EquipmentInstance> tempequipmentInstanceList = new ArrayList<EquipmentInstance>();
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            tempTotal = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType );
            
            this.paging = createPaging( tempTotal );

            //equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, paging.getStartPos(), paging.getPageSize() ) );
            
            tempequipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, paging.getStartPos(), paging.getPageSize() ) );
            
           // System.out.println(" Size of temp Equipment Instance List is : " + tempequipmentInstanceList.size());
            
            finalEquipmentInstanceList.addAll( tempequipmentInstanceList );
        }
        */
    }
    
    //private void listEquipmentInstancesByFilter( OrganisationUnit orgUnit, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey )
    //private void listEquipmentInstancesByFilter( List<OrganisationUnit> orgUnitList, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey )
    private void listEquipmentInstancesByFilter( String orgUnitIdsByComma, InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, String searchKey ,String searchBy )
    {
        /*
        int tempTotal;
        
        List <EquipmentInstance> tempequipmentInstanceList = new ArrayList<EquipmentInstance>();
        
        
        for ( OrganisationUnit orgUnit : orgUnitList )
        {
            tempTotal =  total = equipmentInstanceService.getCountEquipmentInstance( orgUnit, inventoryType, inventoryTypeAttribute, searchText );
            
            this.paging = createPaging( tempTotal );

            //equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, paging.getStartPos(), paging.getPageSize() ) );
            
            tempequipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnit, inventoryType, inventoryTypeAttribute, searchText, paging.getStartPos(), paging.getPageSize() ) );
            
           // System.out.println(" Size of temp Equipment Instance List is : " + tempequipmentInstanceList.size());
            
            finalEquipmentInstanceList.addAll( tempequipmentInstanceList );
        }
        */
        
        
        total = equipmentInstanceService.getCountEquipmentInstance( orgUnitIdsByComma, inventoryType, inventoryTypeAttribute, searchText, searchBy );
        
        this.paging = createPaging( total );
        
        equipmentInstanceList = new ArrayList<EquipmentInstance>( equipmentInstanceService.getEquipmentInstances( orgUnitIdsByComma, inventoryType, inventoryTypeAttribute, searchText, searchBy, paging.getStartPos(), paging.getPageSize() ) );
        
        
    }
    
    
    
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
      //String hierarchyOrgunit = orgunit.getName();
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }
    
    
}

package org.hisp.dhis.ll.action.lldataentry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dbmanager.DataBaseManagerInterface;
import org.hisp.dhis.linelisting.LineListDataValue;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.organisationunit.OrganisationUnit;

import com.opensymphony.xwork2.Action;

public class ShowUpdateEmployeePostFormAction
implements Action
{
    //--------------------------------------------------------------------------
    // Dependencies
    //--------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }
    
    private DataBaseManagerInterface dataBaseManagerInterface;

    public void setDataBaseManagerInterface( DataBaseManagerInterface dataBaseManagerInterface )
    {
        this.dataBaseManagerInterface = dataBaseManagerInterface;
    }
        
    //--------------------------------------------------------------------------
    // Input/Output
    //--------------------------------------------------------------------------

    private String id;
    
    public void setId( String id )
    {
        this.id = id;
    }
    
    private Map<String, String> llDataValuesMap;
    
    public Map<String, String> getLlDataValuesMap()
    {
        return llDataValuesMap;
    }
   
    private List<LineListElement> lineListElements;
    
    public List<LineListElement> getLineListElements()
    {
        return lineListElements;
    }

    //--------------------------------------------------------------------------
    // Action Implementation
    //--------------------------------------------------------------------------
  
    public String execute()
    {
        System.out.println("Inside UPDATE EMPLOYEE POST FORM:::::::");
        OrganisationUnit orgUnit = selectedStateManager.getSelectedOrganisationUnit();
        LineListGroup llGroup = selectedStateManager.getSelectedLineListGroup();
        lineListElements = new ArrayList<LineListElement>( llGroup.getLineListElements() );
        List<LineListDataValue> llDataValuesList = new ArrayList<LineListDataValue>();
        
        Map<String, String> llDataValueMap = new HashMap<String, String>();
        
        // HardCoding Columan name
        String pdsCodeColName = "pdscode";
        String lastWorkingDateColumnName  = "lastworkingdate";
        
        llDataValueMap.put( pdsCodeColName, id );
        llDataValueMap.put( lastWorkingDateColumnName, "null" );
        
        llDataValuesList = dataBaseManagerInterface.getLLValuesFilterByLLElements( llGroup.getShortName(), llDataValueMap, orgUnit );
        
        if ( llDataValuesList != null)
        {
            LineListDataValue llDataValue;
            llDataValue = llDataValuesList.get( 0 );
            llDataValuesMap = llDataValue.getLineListValues();
        }
        
        return SUCCESS;
    }

}

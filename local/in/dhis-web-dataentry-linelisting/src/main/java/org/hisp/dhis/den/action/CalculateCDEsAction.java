package org.hisp.dhis.den.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.den.state.SelectedStateManager;
import org.hisp.dhis.den.state.StatefulDataValueSaver;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

import com.opensymphony.xwork.Action;

public class CalculateCDEsAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private StatefulDataValueSaver statefulDataValueSaver;

    public void setStatefulDataValueSaver( StatefulDataValueSaver statefulDataValueSaver )
    {
        this.statefulDataValueSaver = statefulDataValueSaver;
    }
    
    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }
    
    private DataElementCategoryComboService dataElementCategoryComboService;

    public void setDataElementCategoryComboService( DataElementCategoryComboService dataElementCategoryComboService )
    {
        this.dataElementCategoryComboService = dataElementCategoryComboService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<Integer, String> cdeValueMap;

    public Map<Integer, String> getCdeValueMap()
    {
        return cdeValueMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        OrganisationUnit organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        Period period = selectedStateManager.getSelectedPeriod();
        
        Collection<DataElement> dataElements = selectedStateManager.getSelectedDataSet().getDataElements();
        
        if ( dataElements.size() > 0 )
        {           	
        
        	Collection<CalculatedDataElement> cdes = dataElementService
            	.getCalculatedDataElementsByDataElements( dataElements );
        	
        	//Look for the existence of CDEs in the form itself.
        	Iterator<DataElement> iterator = dataElements.iterator();            

            while ( iterator.hasNext() )
            {
                DataElement dataElement = iterator.next();

                if ( dataElement instanceof CalculatedDataElement )
                {                	
                	cdes.add( (CalculatedDataElement) dataElement );  
                }
            }            
        	
        	cdeValueMap = new HashMap<Integer, String>();

        	String value = null;

        	for ( CalculatedDataElement cde : cdes )
        	{        		
        		value = expressionService.getExpressionValue( cde.getExpression(), period, organisationUnit, false ).toString();        			
        		
        		if ( value == null )
        		{
        			continue;
        		}

        		// Should the value be updated in Data Entry?
        		if ( dataElements.contains( cde ) )
        		{        		
        			cdeValueMap.put( cde.getId(), value );        			
        		}

        		// Should the value be saved to the database?
        		if ( cde.isSaved() )
        		{        			
        			DataElementCategoryCombo catCombo = dataElementCategoryComboService.getDataElementCategoryComboByName("default");
        			DataElementCategoryOptionCombo optionCombo = catCombo.getOptionCombos().iterator().next();
        			statefulDataValueSaver.saveValue( cde.getId(), optionCombo.getId(), "" + value );
        		}

        		value = null;
        	}
        }

        return SUCCESS;
    }

}

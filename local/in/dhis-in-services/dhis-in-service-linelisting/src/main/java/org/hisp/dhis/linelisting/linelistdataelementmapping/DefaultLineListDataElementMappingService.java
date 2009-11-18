package org.hisp.dhis.linelisting.linelistdataelementmapping;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.linelisting.LineListElement;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListOption;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultLineListDataElementMappingService
    implements LineListDataElementMappingService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListDataElementMappingStore lineListDataElementMappingStore;

    public void setLineListDataElementMappingStore( LineListDataElementMappingStore lineListDataElementMappingStore )
    {
        this.lineListDataElementMappingStore = lineListDataElementMappingStore;
    }

    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

    // -------------------------------------------------------------------------
    // LineListDataElementMapping
    // -------------------------------------------------------------------------

    public int addLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        int id = lineListDataElementMappingStore.addLineListDataElementMapping( lineListDataElementMapping );

        i18nService.addObject( lineListDataElementMapping );

        return id;
    }

    public void deleteLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        i18nService.removeObject( lineListDataElementMapping );

        lineListDataElementMappingStore.deleteLineListDataElementMapping( lineListDataElementMapping );
    }

    public LineListDataElementMapping getLineListDataElementMapping( int id )
    {
        return lineListDataElementMappingStore.getLineListDataElementMapping( id );
    }

    public Collection<LineListDataElementMapping> getAllLineListDataElementMappings()
    {
        return lineListDataElementMappingStore.getAllLineListDataElementMappings();
    }

    public void updateLineListDataElementMapping( LineListDataElementMapping lineListDataElementMapping )
    {
        lineListDataElementMappingStore.updateLineListDataElementMapping( lineListDataElementMapping );
    }

    // -------------------------------------------------------------------------
    // Supporting methods
    // -------------------------------------------------------------------------

    public String convertDataElementExpression( String arg0, Map<Object, Integer> arg1, Map<Object, Integer> arg2 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String convertLineListDataElementMapping( String arg0, Map<Object, Integer> arg1, Map<Object, Integer> arg2,
        Map<Object, Integer> arg3 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int expressionIsValid( String arg0, String arg1 )
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public Set<DataElementCategoryOptionCombo> getCategoryOptionCombosInExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDataElementExpressionDescription( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<DataElement> getDataElementsInExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLineListDataElementMappingDescription( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListElement> getLineListElementsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListGroup> getLineListGroupsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListOption> getLineListOptionsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Operand> getOperandsInDataElementExpression( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<LineListOperand> getOperandsInLineListDataElementMapping( String arg0 )
    {
        // TODO Auto-generated method stub
        return null;
    }

}

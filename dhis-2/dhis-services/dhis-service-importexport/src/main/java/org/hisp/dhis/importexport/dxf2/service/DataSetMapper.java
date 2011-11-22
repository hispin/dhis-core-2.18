package org.hisp.dhis.importexport.dxf2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.importexport.dxf2.model.CategoryOptionCombo;
import org.hisp.dhis.importexport.dxf2.model.DataElement;
import org.hisp.dhis.importexport.dxf2.model.DataSet;

public class DataSetMapper
{
    private LinkBuilder linkBuilder = new LinkBuilderImpl();

    public DataSet convert( org.hisp.dhis.dataset.DataSet dataSet )
    {
        DataSet dxfDataSet = new DataSet();

        dxfDataSet.setCode( dataSet.getCode() );
        dxfDataSet.setName( dataSet.getName() );
        dxfDataSet.setPeriodType( dataSet.getPeriodType().getName() );
        dxfDataSet.setShortName( dataSet.getShortName() );
        dxfDataSet.setId( dataSet.getUid() );

        dxfDataSet.setDataElements( getDataElements( dataSet.getDataElements() ) );
        dxfDataSet.setOrgUnitLinks( linkBuilder.getLinks( dataSet.getSources() ) );

        return dxfDataSet;
    }

    private List<DataElement> getDataElements( Collection<org.hisp.dhis.dataelement.DataElement> dataElements )
    {
        List<DataElement> dxfElements = new ArrayList<DataElement>();

        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            dxfElements.add( getDataElement( dataElement ) );
        }
        return dxfElements;
    }

    private DataElement getDataElement( org.hisp.dhis.dataelement.DataElement dataElement )
    {
        DataElement dxfElement = new DataElement();

        dxfElement.setId( dataElement.getUid() );
        dxfElement.setName( dataElement.getName() );
        dxfElement.setType( dataElement.getType() );

        Set<DataElementCategoryOptionCombo> optionCombos = dataElement.getCategoryCombo().getOptionCombos();

        if ( optionCombos.size() > 1 )
        {
            List<CategoryOptionCombo> categoryOptionCombos = getOptionCombos( optionCombos );

            dxfElement.setCategoryOptionCombos( categoryOptionCombos );
        }

        return dxfElement;
    }

    private List<CategoryOptionCombo> getOptionCombos( Set<DataElementCategoryOptionCombo> optionCombos )
    {
        List<CategoryOptionCombo> dxfCombos = new ArrayList<CategoryOptionCombo>();
        for ( DataElementCategoryOptionCombo optionCombo : optionCombos )
        {
            CategoryOptionCombo dxfCombo = new CategoryOptionCombo();
            dxfCombo.setId( optionCombo.getUid() );
            dxfCombo.setName( optionCombo.getName() );
            dxfCombos.add( dxfCombo );
        }
        
        return dxfCombos;
    }
}

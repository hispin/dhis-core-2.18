package org.hisp.dhis.importexport.dxf2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.importexport.dxf2.model.DataElement;
import org.hisp.dhis.importexport.dxf2.model.DataSet;

public class DataSetMapper
{

    public static DataSet convert(org.hisp.dhis.dataset.DataSet dataSet) {
        DataSet dxfDataSet = new DataSet();
        
        dxfDataSet.setCode( dataSet.getCode() );
        dxfDataSet.setMembers( getDataElements(dataSet.getDataElements()) );
        dxfDataSet.setName( dataSet.getName() );
        dxfDataSet.setPeriodType( dataSet.getPeriodType().getName() );
        dxfDataSet.setShortName( dataSet.getShortName() );
        dxfDataSet.setUuid( dataSet.getUuid() );
        
        return dxfDataSet;
    }

    public static List<DataElement> getDataElements( Collection<org.hisp.dhis.dataelement.DataElement> dataElements )
    {
        List<DataElement> dxfDataElements = new ArrayList<DataElement>();
        
        for ( org.hisp.dhis.dataelement.DataElement dataElement : dataElements )
        {
            DataElement dxfDataElement = new DataElement();
            dxfDataElement.setName( dataElement.getName() );
            dxfDataElement.setUuid( dataElement.getUuid() );
            
            dxfDataElements.add( dxfDataElement );
        }
        return dxfDataElements;
    }
}

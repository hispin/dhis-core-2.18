package org.hisp.dhis.importexport.dxf2.service;

import org.hisp.dhis.importexport.dxf2.model.DataSet;

public class DataSetMapper
{
    private LinkBuilder linkBuilder = new LinkBuilderImpl();
    
    public DataSet convert(org.hisp.dhis.dataset.DataSet dataSet) {
        DataSet dxfDataSet = new DataSet();
        
        dxfDataSet.setCode( dataSet.getCode() );
        dxfDataSet.setName( dataSet.getName() );
        dxfDataSet.setPeriodType( dataSet.getPeriodType().getName() );
        dxfDataSet.setShortName( dataSet.getShortName() );
        dxfDataSet.setId( dataSet.getUuid() );

        dxfDataSet.setDataElementLinks( linkBuilder.getLinks( dataSet.getDataElements()) );
        dxfDataSet.setOrgUnitLinks( linkBuilder.getLinks(dataSet.getOrganisationUnits()) );
        
        return dxfDataSet;
    }

}

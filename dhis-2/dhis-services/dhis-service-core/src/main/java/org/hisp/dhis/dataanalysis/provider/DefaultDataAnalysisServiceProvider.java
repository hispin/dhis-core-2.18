package org.hisp.dhis.dataanalysis.provider;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.dataanalysis.DataAnalysisService;
import org.hisp.dhis.dataanalysis.DataAnalysisServiceProvider;

public class DefaultDataAnalysisServiceProvider
    implements DataAnalysisServiceProvider
{
    private Map<String, DataAnalysisService> dataAnalysisServices = new HashMap<String, DataAnalysisService>();
    
    public void setDataAnalysisServices( Map<String, DataAnalysisService> dataAnalysisServices )
    {
        this.dataAnalysisServices = dataAnalysisServices;
    }

    public DataAnalysisService provide( String key )
    {
        return dataAnalysisServices.get( key );
    }
}

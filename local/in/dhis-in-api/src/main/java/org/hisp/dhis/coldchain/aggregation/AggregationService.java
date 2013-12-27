package org.hisp.dhis.coldchain.aggregation;

import java.util.Map;

public interface AggregationService
{
    String ID = AggregationService.class.getName();  
    
    String getQueryTemplate(String lookupName, Map<String,String> params);
}

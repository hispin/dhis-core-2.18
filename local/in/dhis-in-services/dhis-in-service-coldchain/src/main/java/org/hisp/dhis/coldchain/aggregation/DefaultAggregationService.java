package org.hisp.dhis.coldchain.aggregation;

import java.util.Map;

import org.hisp.dhis.lookup.Lookup;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultAggregationService implements AggregationService
{
	// -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
	@Override
	public String getQueryTemplate(String lookupName, Map<String,String> params) {
		String tempQuery = null;
		if(lookupName.equalsIgnoreCase(Lookup.WS_REF_TYPE))
		{
			String equipmenttypeid = params.get("equipmenttypeid");
			String modelName = params.get("modelName");
			String equipmentattributevalue = params.get("equipmentattributevalue");
			tempQuery = "SELECT COUNT(*) FROM modelattributevalue "+
						"INNER JOIN equipment ON modelattributevalue.modelid = equipment.modelid "+
						"INNER JOIN equipmentattributevalue ON equipmentattributevalue.equipmentid = equipment.equipmentid"+
						" WHERE " +
                        " equipment.equipmenttypeid = "+equipmenttypeid+" AND " +
                        " modelattributevalue.value = "+modelName+" AND " +
                        " equipment.organisationunitid IN ( ? ) AND "+
                        " equipmentattributevalue.value IN ( "+equipmentattributevalue+" )"+
                        " GROUP BY modelattributevalue.value";					
		}
		return tempQuery;
	}

    

  
}

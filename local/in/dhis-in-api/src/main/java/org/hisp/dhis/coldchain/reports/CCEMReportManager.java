package org.hisp.dhis.coldchain.reports;

import java.util.List;
import java.util.Map;

public interface CCEMReportManager
{
    String getOrgunitIdsByComma( List<Integer> selOrgUnitList, List<Integer> orgunitGroupList );
    
    Map<String,String> getCCEMSettings();
    
    List<CCEMReportDesign> getCCEMReportDesign( String designXMLFile );
    
    CCEMReport getCCEMReportByReportId( String selReportId );
    
    Map<String, Integer> getCatalogTypeAttributeValue( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId );
    
    Map<String, Integer> getCatalogTypeAttributeValueByAge( String orgUnitIdsByComma, Integer inventoryTypeId, Integer catalogTypeAttributeId, Integer yearInvTypeAttId, Integer ageStart, Integer ageEnd );
    
    List<String> getDistinctDataElementValue( Integer dataelementID, Integer optComboId, Integer periodId );
    
    List<Integer> getOrgunitIds( List<Integer> selOrgUnitList, Integer orgUnitGroupId );
    
    Map<String, Integer> getDataValueCountforDataElements( String dataElementIdsByComma, String optComboIdsByComma, Integer periodId, String orgUnitIdsBycomma );
    
    Integer getPeriodId( String startDate, String periodType );
    
    Map<String, Integer> getFacilityWiseEquipmentRoutineData( String orgUnitIdsByComma, String periodIdsByComma, String dataElementIdsByComma, String optComboIdsByComma );
}

package org.hisp.dhis.rbf.api;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface PartnerStore
{
    String ID = PartnerStore.class.getName();
    
    void addPartner( Partner partner );
    
    void updatePartner( Partner partner );
    
    void deletePartner( Partner partner );
    
    Partner getPartner( OrganisationUnit organisationUnit, DataSet dataSet, DataElement dataElement, Date startDate, Date endDate );
    
    Collection<Partner> getAllPartner();
    
    Collection<Partner> getPartner( OrganisationUnit organisationUnit, DataSet dataSet );
    
    Collection<String> getStartAndEndDate( Integer dataSetId, Integer dataElementId, Integer optionId );
    
    Map<String, Integer> getOrgUnitCountFromPartner( Integer dataSetId, Integer dataElementId, Integer optionId, String startDate, String endDate );
    
}

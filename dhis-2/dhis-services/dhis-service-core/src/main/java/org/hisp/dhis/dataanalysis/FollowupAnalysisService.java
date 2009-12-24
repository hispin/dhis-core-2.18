package org.hisp.dhis.dataanalysis;

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public class FollowupAnalysisService
    implements DataAnalysisService
{
    private DataAnalysisStore dataAnalysisStore;
    
    public void setDataAnalysisStore( DataAnalysisStore dataAnalysisStore )
    {
        this.dataAnalysisStore = dataAnalysisStore;
    }

    @Override
    public Collection<DeflatedDataValue> analyse( OrganisationUnit organisationUnit,
        Collection<DataElement> dataElements, Collection<Period> periods, Double stdDevFactor )
    {
        return dataAnalysisStore.getDataValuesMarkedForFollowup();
    }
}

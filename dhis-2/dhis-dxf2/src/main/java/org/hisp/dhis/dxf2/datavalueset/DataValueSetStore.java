package org.hisp.dhis.dxf2.datavalueset;

import java.io.OutputStream;
import java.util.Date;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface DataValueSetStore
{
    public void writeDataValueSet( DataSet dataSet, Date completeDate, OrganisationUnit orgUnit, Period period, 
        Set<DataElement> dataElements, Set<Period> periods, Set<OrganisationUnit> orgUnits, OutputStream out );
}

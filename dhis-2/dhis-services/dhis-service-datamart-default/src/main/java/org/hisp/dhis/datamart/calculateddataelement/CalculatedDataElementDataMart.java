package org.hisp.dhis.datamart.calculateddataelement;

import java.util.Collection;

import org.hisp.dhis.dataelement.Operand;

public interface CalculatedDataElementDataMart
{
    int exportCalculatedDataElements( Collection<Integer> calculatedDataElementIds, 
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, Collection<Operand> operands );
}

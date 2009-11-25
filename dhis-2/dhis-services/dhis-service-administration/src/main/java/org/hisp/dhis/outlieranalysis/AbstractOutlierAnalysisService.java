package org.hisp.dhis.outlieranalysis;

/*
 * Copyright (c) 2004-${year}, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Dag Haavi Finstad
 * @version $Id: AbstractStdDevOutlierAnalysisService.java 1020 2009-06-05 01:30:07Z daghf $
 */
public abstract class AbstractOutlierAnalysisService
    implements OutlierAnalysisService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    // -------------------------------------------------------------------------
    // OutlierAnalysisService implementation
    // -------------------------------------------------------------------------

    public final Collection<OutlierValue> findOutliers( OrganisationUnit organisationUnit,
        Collection<DataElement> dataElements, Collection<Period> periods, Double stdDevFactor )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() );
        
        Map<Integer, DataElement> dataElementMap = ConversionUtils.getIdentifierMap( dataElements );
        Map<Integer, Period> periodMap = ConversionUtils.getIdentifierMap( periods );
        Map<Integer, OrganisationUnit> organisationUnitMap = ConversionUtils.getIdentifierMap( units );
        
        Collection<OutlierValue> outlierCollection = new ArrayList<OutlierValue>();
        
        for ( OrganisationUnit unit : units )
        {
            for ( DataElement dataElement : dataElements )
            {
                if ( dataElement.getType().equals( DataElement.VALUE_TYPE_INT ) )
                {                    
                    Collection<DataElementCategoryOptionCombo> categoryOptionCombos = dataElement.getCategoryCombo().getOptionCombos();
                    
                    Map<Integer, DataElementCategoryOptionCombo> categoryOptionComboMap = ConversionUtils.getIdentifierMap( categoryOptionCombos );
                    
                    for ( DataElementCategoryOptionCombo categoryOptionCombo : categoryOptionCombos )
                    {
                        outlierCollection.addAll( findOutliers( unit, dataElement, categoryOptionCombo, periods, stdDevFactor,
                            dataElementMap, periodMap, organisationUnitMap, categoryOptionComboMap ) );
                    }
                }
            }
        }

        return outlierCollection;
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract Collection<OutlierValue> findOutliers( OrganisationUnit organisationUnit, 
        DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, Collection<Period> periods, Double stdDevFactor,
        Map<Integer, DataElement> dataElementMap, Map<Integer, Period> periodMap, Map<Integer, OrganisationUnit> organisationUnitMap,
        Map<Integer, DataElementCategoryOptionCombo> categoryOptionComboMap );
}

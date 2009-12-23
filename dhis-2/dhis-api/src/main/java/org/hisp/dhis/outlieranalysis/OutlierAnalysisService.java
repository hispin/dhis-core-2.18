package org.hisp.dhis.outlieranalysis;

/*
 * Copyright (c) 2004-2009, University of Oslo
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

import java.util.Collection;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;


/**
 * Defines service functionality for finding outlier DataValues.
 * 
 * @author Dag Haavi Finstad
 * @version $Id: StdDevOutlierAnalysisService.java 882 2009-05-14 23:09:31Z daghf $
 * 
 */
public interface OutlierAnalysisService
{
    String ID = OutlierAnalysisService.class.getName();

    /**
     * Finds possible outliers for a collection of sources, dataelements and periods.
     * 
     * Checks for every combination in the cartesian product of sources,
     * dataelements and periods if the corresponding datavalues are possible
     * outliers.
     * 
     * @param organisationUnits The organisation units.
     * @param dataElement The DataElement.
     * @param period The period.
     * @param stdDevFactor The standard deviation factor.
     * @return A collection of OutlierValue objects. If no values were found, an
     *         empty collection is returned.
     */
    Collection<DeflatedDataValue> findOutliers( OrganisationUnit organisationUnit, Collection<DataElement> dataElements,
        Collection<Period> periods, Double stdDevFactor );

}

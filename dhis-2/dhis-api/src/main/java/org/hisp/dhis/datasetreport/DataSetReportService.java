package org.hisp.dhis.datasetreport;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.Map;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Abyot Asalefew
 * @author Lars Helge Overland
 */
public interface DataSetReportService
{
    /**
     * Generates a map with aggregated data values or regular data values (depending
     * on the selectedUnitOnly argument) mapped to a DataElemenet operand identifier.
     * 
     * @param dataSet the DataSet containing the DataElement to include in the map.
     * @param unit the OrganisationUnit.
     * @param period the Period.
     * @param selectedUnitOnly whether to include aggregated or regular data in the map.
     * @return a map.
     */
    Map<String, String> getAggregatedValueMap( DataSet dataSet, OrganisationUnit unit, Period period, boolean selectedUnitOnly, I18nFormat format );

    /**
     * Puts in aggregated datavalues in the custom dataentry form and returns
     * whole report text.
     * 
     * @param dataEntryFormCode the data entry form HTML code.
     * @param a map with aggregated data values mapped to data element operands.
     * @return data entry form HTML code populated with aggregated data in the
     *         input fields.
     */
    String prepareReportContent( String dataEntryFormCode, Map<String, String> dataValues );
}

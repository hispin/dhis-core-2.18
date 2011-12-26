package org.hisp.dhis.tallysheet;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Haavard Tegelsrud, Oddmund Stroemme, Joergen Froeysadal, Ruben
 *         Wangberg
 * @version $Id$
 */
public class DefaultTallySheetService
    implements TallySheetService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    // -------------------------------------------------------------------------
    // TallySheetService implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public TallySheet createTallySheet( OrganisationUnit organisationUnit, List<DataElement> dataElements,
        boolean a3Format, boolean displayFacilityName, DataSet selectedDataSet, String tallySheetName )
    {
        PeriodType periodType = selectedDataSet.getPeriodType();

        Collection<DataValue> dataValues = new HashSet<DataValue>();

        for ( DataElement dataElement : dataElements )
        {

            DataValue dataValue = dataValueService.getLatestDataValues( dataElement, periodType, organisationUnit );

            if ( dataValue != null )
            {
                dataValues.add( dataValue );
            }

        }

        return internalCreateTallySheet( organisationUnit, dataElements, dataValues, a3Format, displayFacilityName,
            tallySheetName );
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private TallySheet internalCreateTallySheet( OrganisationUnit organisationUnit, List<DataElement> dataElements,
        Collection<DataValue> dataValues, boolean a3Format, boolean displayFacilityName, String tallySheetName )
    {
        TallySheet tallySheet = new TallySheet();

        tallySheet.setTallySheetName( tallySheetName );
        tallySheet.setA3Format( a3Format );
        tallySheet.setDisplayFacilityName( displayFacilityName );
        tallySheet.setOrganisationUnit( organisationUnit );

        List<TallySheetTuple> tallySheetTuples = new ArrayList<TallySheetTuple>();

        for ( DataElement dataElement : dataElements )
        {
            int calculatedNumberOfElements = 0;

            for ( DataValue dataValue : dataValues )
            {

                if ( dataValue.getSource().equals( organisationUnit )
                    && dataValue.getDataElement().equals( dataElement ) )
                {
                    try
                    {
                        calculatedNumberOfElements = Integer.parseInt( dataValue.getValue() );
                    }
                    catch ( NumberFormatException e )
                    {
                        continue;
                    }

                    break;
                }
            }

            TallySheetTuple tallySheetTuple = new TallySheetTuple();
            tallySheetTuple.setTallySheetTuple( calculatedNumberOfElements, dataElement, tallySheet.getRowWidth() );
            tallySheetTuples.add( tallySheetTuple );
        }

        tallySheet.setTallySheetTuples( tallySheetTuples );

        return tallySheet;
    }
}

package org.hisp.dhis.importexport.synchronous;

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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.StoreIterator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;

/**
 * Exports pivot view synchronously (using calling thread)
 *
 * TODO: use exportparams and abstract service
 *
 * @author bobj
 */
public class ExportPivotViewService {

    private static final Log log = LogFactory.getLog( ExportPivotViewService.class );

    // service can export either aggregated datavalues or aggregated indicator values
    public enum RequestType { DATAVALUE, INDICATORVALUE };

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private  OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void execute (OutputStream out, RequestType requestType, Date startDate, Date endDate, int level, int root)
        throws IOException
    {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));

        Collection<Period> periods 
            = periodService.getIntersectingPeriodsByPeriodType( new MonthlyPeriodType(), startDate, endDate );

        if (periods.isEmpty())
        {
            log.info( "no periods to export");
            return;
        }

        OrganisationUnit rootOrgUnit = organisationUnitService.getOrganisationUnit( root );

        if (rootOrgUnit == null)
        {
            log.info( "no orgunit root to export with id = " + rootOrgUnit);
            return;
        }

        rootOrgUnit.setLevel(organisationUnitService.getLevelOfOrganisationUnit( rootOrgUnit ));
        
        log.info("Orgunit: " + rootOrgUnit.getName());
        log.info("Orgunit level: " + rootOrgUnit.getLevel());
        
        OrganisationUnitLevel orgUnitLevel = organisationUnitService.getOrganisationUnitLevelByLevel( level );

        if (orgUnitLevel == null)
        {
            log.info( "no level with level id = " + orgUnitLevel);
            return;
        }

        log.info( "Exporting for " + rootOrgUnit.getName() + " at level: " + orgUnitLevel.getName());

        if (requestType == RequestType.DATAVALUE)
        {
            processDataValues(writer, rootOrgUnit , orgUnitLevel, periods );
        }
       else
        {
            processIndicatorValues(writer, rootOrgUnit , orgUnitLevel, periods );
        }

    }

     void processDataValues(Writer writer, OrganisationUnit rootOrgUnit , OrganisationUnitLevel orgUnitLevel, Collection<Period> periods )
         throws IOException
     {
        StoreIterator<AggregatedDataValue> Iterator
            = aggregatedDataValueService.getAggregateDataValuesAtLevel( rootOrgUnit , orgUnitLevel, periods );

        AggregatedDataValue adv = Iterator.next();

        writer.write("# period, orgunit, dataelement, catoptcombo, value\n");
        while (adv != null)
        {
            // process adv ..
            int periodId = adv.getPeriodId();
            String period = periodService.getPeriod( periodId).getIsoDate();

            writer.write( "'" + period + "',");
            writer.write( adv.getOrganisationUnitId() + ",");
            writer.write( adv.getDataElementId() + ",");
            writer.write( adv.getCategoryOptionComboId() + ",");
            writer.write( adv.getValue() + "\n");

            adv = Iterator.next();
        }

        writer.flush();

     }


     void processIndicatorValues(Writer writer, OrganisationUnit rootOrgUnit , OrganisationUnitLevel orgUnitLevel, Collection<Period> periods )
         throws IOException
     {
        StoreIterator<AggregatedIndicatorValue> Iterator
            = aggregatedDataValueService.getAggregateIndicatorValuesAtLevel( rootOrgUnit , orgUnitLevel, periods );

        AggregatedIndicatorValue aiv = Iterator.next();

        writer.write("# period, orgunit, indicator, factor, numerator, denominator, annualized, value\n");
        while (aiv != null)
        {
            // process adv ..
            int periodId = aiv.getPeriodId();
            String period = periodService.getPeriod( periodId).getIsoDate();

            writer.write( "'" + period + "',");
            writer.write( aiv.getOrganisationUnitId() + ",");
            writer.write( aiv.getIndicatorId() + ",");
            writer.write( aiv.getFactor() + ",");
            writer.write( aiv.getNumeratorValue() + ",");
            writer.write( aiv.getDenominatorValue() + ",");
            writer.write( "'" + aiv.getAnnualized() + "',");
            writer.write( aiv.getValue() + "\n");

            aiv = Iterator.next();
        }

        writer.flush();

     }
}

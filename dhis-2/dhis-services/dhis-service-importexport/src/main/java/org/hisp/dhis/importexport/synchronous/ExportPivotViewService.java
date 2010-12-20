/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.importexport.synchronous;


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
import org.hisp.dhis.aggregation.AggregatedDataValueStore;
import org.hisp.dhis.aggregation.AggregatedDataValueStoreIterator;
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
 * TODO: implement indicator and width-wise queries
 *
 * @author bobj
 */

public class ExportPivotViewService {

    private static final Log log = LogFactory.getLog( ExportPivotViewService.class );

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

    public void execute (OutputStream out, Date startDate, Date endDate, int level, int root)
        throws IOException
    {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));

        Collection<Period> periods 
            = periodService.getIntersectingPeriodsByPeriodType( new MonthlyPeriodType(), startDate, endDate );

        if (periods.size() == 0)
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

        AggregatedDataValueStoreIterator advIterator = aggregatedDataValueService.getAggregateDataValuesAtLevel( rootOrgUnit , orgUnitLevel, periods );

        AggregatedDataValue adv = advIterator.next();

        while (adv != null)
        {
            // process adv ..
            int periodId = adv.getPeriodId();
            String period = periodService.getPeriod( periodId).getIsoDate();

            writer.write( period + ",");
            writer.write( adv.getOrganisationUnitId() + ",");
            writer.write( adv.getDataElementId() + ",");
            writer.write( adv.getCategoryOptionComboId() + ",");
            writer.write( adv.getLevel() + "\n");

            adv = advIterator.next();
        }

        writer.flush();
    }

}

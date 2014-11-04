package org.hisp.dhis.dataapproval.hibernate;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import static org.hisp.dhis.dataapproval.DataApprovalState.*;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL;
import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataapproval.DataApproval;
import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.dataapproval.DataApprovalState;
import org.hisp.dhis.dataapproval.DataApprovalStatus;
import org.hisp.dhis.dataapproval.DataApprovalStore;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Jim Grace
 */
public class HibernateDataApprovalStore
    extends HibernateGenericStore<DataApproval>
    implements DataApprovalStore
{
    private static final Log log = LogFactory.getLog( HibernateDataApprovalStore.class );
    
    private static Cache<Integer, DataElementCategoryOptionCombo> OPTION_COMBO_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess( 10, TimeUnit.MINUTES ).initialCapacity( 10000 )
        .maximumSize( 50000 ).build();

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataApprovalLevelService dataApprovalLevelService;

    public void setDataApprovalLevelService( DataApprovalLevelService dataApprovalLevelService )
    {
        this.dataApprovalLevelService = dataApprovalLevelService;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    // -------------------------------------------------------------------------
    // DataApproval
    // -------------------------------------------------------------------------

    @Override
    public void addDataApproval( DataApproval dataApproval )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );

        save( dataApproval );
    }

    @Override
    public void updateDataApproval( DataApproval dataApproval )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );

        update ( dataApproval );
    }

    @Override
    public void deleteDataApproval( DataApproval dataApproval )
    {
        dataApproval.setPeriod( periodService.reloadPeriod( dataApproval.getPeriod() ) );

        delete( dataApproval );
    }

    @Override
    public DataApproval getDataApproval( DataApprovalLevel dataApprovalLevel, DataSet dataSet, Period period,
        OrganisationUnit organisationUnit, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        Period storedPeriod = periodService.reloadPeriod( period );

        Criteria criteria = getCriteria();
        criteria.add( Restrictions.eq( "dataApprovalLevel", dataApprovalLevel ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "attributeOptionCombo", attributeOptionCombo ) );

        return (DataApproval) criteria.uniqueResult();
    }

    @Override
    public List<DataApprovalStatus> getDataApprovals( OrganisationUnit orgUnit, Set<DataSet> dataSets, Period period, DataElementCategoryOptionCombo attributeOptionCombo )
    {
        final User user = currentUserService.getCurrentUser();

        if ( CollectionUtils.isEmpty( dataSets ) )
        {
            return new ArrayList<>();
        }

        PeriodType dataSetPeriodType = dataSets.iterator().next().getPeriodType();

        Collection<Period> periods;

        if ( period.getPeriodType() == dataSetPeriodType )
        {
            periods = org.hisp.dhis.system.util.CollectionUtils.asSet( period );
        }
        else if ( period.getPeriodType().getFrequencyOrder() > dataSetPeriodType.getFrequencyOrder() )
        {
            periods = periodService.getPeriodsBetweenDates(
                    dataSetPeriodType,
                    period.getStartDate(),
                    period.getEndDate() );
        }
        else
        {
            return new ArrayList<>();
        }

        final String minDate = DateUtils.getMediumDateString( period.getStartDate() );
        final String maxDate = DateUtils.getMediumDateString( period.getEndDate() );

        String periodIds = "";

        for ( Period p : periods )
        {
            periodIds += ( periodIds.isEmpty() ? "" : ", " ) + periodService.reloadPeriod( p ).getId();
        }

        Set<Integer> categoryComboIds = new HashSet<>();

        for ( DataSet ds : dataSets )
        {
            if ( ds.isApproveData() )
            {
                categoryComboIds.add( ds.getCategoryCombo().getId() );
            }
        }

        final String dataSetIds = getCommaDelimitedString( getIdentifiers( DataSet.class, dataSets ) );
        final String dataSetCcIds = TextUtils.getCommaDelimitedString( categoryComboIds );

        final int orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );

        boolean isSuperUser = currentUserService.currentUserIsSuper();

        DataApprovalLevel lowestApprovalLevelForOrgUnit = null;

        String orgUnitAncestorLevels = "";

        for ( int i = 1; i < orgUnitLevel; i++ )
        {
            orgUnitAncestorLevels += ", ous.idlevel" + i;
        }

        String readyBelowSubquery = "true"; // Ready below if this is the lowest (highest number) approval orgUnit level.

        int orgUnitLevelAbove = 0;

        for ( DataApprovalLevel dal : dataApprovalLevelService.getAllDataApprovalLevels() )
        {
            if ( dal.getOrgUnitLevel() < orgUnitLevel )
            {
                orgUnitLevelAbove = dal.getOrgUnitLevel(); // Keep getting the lowest org unit level above ours.
            }

            if ( dal.getOrgUnitLevel() == orgUnitLevel )
            {
                lowestApprovalLevelForOrgUnit = dal;
            }

            if ( dal.getOrgUnitLevel() > orgUnitLevel ) // If there is a lower (higher number) approval orgUnit level:
            {
                boolean acceptanceRequiredForApproval = (Boolean) systemSettingManager.getSystemSetting( KEY_ACCEPTANCE_REQUIRED_FOR_APPROVAL, false );

                readyBelowSubquery = "not exists (select 1 from _orgunitstructure ous " +
                        "left join dataapproval da on da.organisationunitid = ous.organisationunitid " +
                        "and da.dataapprovallevelid = " + dal.getLevel() + " and da.periodid in (" + periodIds + ") " +
                        "and da.datasetid in (" + dataSetIds + ") " +
                        "where ous.idlevel" + orgUnitLevel + " = " + orgUnit.getId() + " " +
                        "and ous.level = " + dal.getOrgUnitLevel() + " " +
                        "and ( da.dataapprovalid is null " + ( acceptanceRequiredForApproval ? "or not da.accepted " : "" ) + ") )";
                break;
            }
        }

        String approvedAboveSubquery = "false"; // Not approved above if this is the highest (lowest number) approval orgUnit level.

        if ( orgUnitLevelAbove > 0 )
        {
            OrganisationUnit ancestor = orgUnit;

            for ( int i = orgUnitLevelAbove; i < orgUnitLevel; i++ )
            {
                ancestor = ancestor.getParent();
            }
            approvedAboveSubquery = "exists(select 1 from dataapproval da join dataapprovallevel dal on dal.dataapprovallevelid = da.dataapprovallevelid " +
                    "where da.periodid in (" + periodIds + ") and da.datasetid in (" + dataSetIds + ") and da.organisationunitid = " + ancestor.getId() + ")";
        }

        final String sql =
                "select ccoc.categoryoptioncomboid, " +
                "(select min(coalesce(dal.level, 0)) from period p " +
                    "join dataset ds on ds.datasetid in (" + dataSetIds + ") " +
                    "left join dataapproval da on da.datasetid = ds.datasetid and da.periodid = p.periodid and da.organisationunitid = " + orgUnit.getId() + " " +
                    "left join dataapprovallevel dal on dal.dataapprovallevelid = da.dataapprovallevelid " +
                    "where p.periodid in (" + periodIds + ") " +
                ") as highest_approved_level, " +
                "(select substring(min(concat(100000 + coalesce(dal.level, 0), coalesce(da.accepted, FALSE))) from 7) from period p " +
                    "join dataset ds on ds.datasetid in (" + dataSetIds + ") " +
                    "left join dataapproval da on da.datasetid = ds.datasetid and da.periodid = p.periodid and da.organisationunitid = " + orgUnit.getId() + " " +
                    "left join dataapprovallevel dal on dal.dataapprovallevelid = da.dataapprovallevelid " +
                    "where p.periodid in (" + periodIds + ") " +
                ") as accepted_at_highest_level, " +
                readyBelowSubquery + " as ready_below, " +
                approvedAboveSubquery + " as approved_above " +
                "from categoryoptioncombo coc " +
                "join categorycombos_optioncombos ccoc on ccoc.categoryoptioncomboid = coc.categoryoptioncomboid and ccoc.categorycomboid in (" + dataSetCcIds + ") " +
                "where coc.categoryoptioncomboid in ( " + // subquery for category option restriction by date, organisation unit, and sharing
                    "select distinct coc1.categoryoptioncomboid " +
                    "from categoryoptioncombo coc1 " +
                    "join categoryoptioncombos_categoryoptions cocco on cocco.categoryoptioncomboid = coc1.categoryoptioncomboid " +
                    "join dataelementcategoryoption co on co.categoryoptionid = cocco.categoryoptionid " +
                    "and (co.startdate is null or co.startdate <= '" + maxDate + "') and (co.enddate is null or co.enddate >= '" + minDate + "') " +
                    "left join categoryoption_organisationunits coo on coo.categoryoptionid = co.categoryoptionid " +
                    "left join _orgunitstructure ous on ous.idlevel" + orgUnitLevel + " = " + orgUnit.getId() + " " +
                    "and coo.organisationunitid in ( ous.organisationunitid" + orgUnitAncestorLevels + " ) " +
                    "left join dataelementcategoryoptionusergroupaccesses couga on couga.categoryoptionid = cocco.categoryoptionid " +
                    "left join usergroupaccess uga on uga.usergroupaccessid = couga.usergroupaccessid " +
                    "left join usergroupmembers ugm on ugm.usergroupid = uga.usergroupid " +
                    "where ( coo.categoryoptionid is null or ous.organisationunitid is not null ) " + // no org unit assigned, or matching org unit assigned
                    ( isSuperUser || user == null ? "" : "and ( ugm.userid = " + user.getId() + " or co.userid = " + user.getId() + " or left(co.publicaccess, 1) = 'r' ) " ) +
                    ( attributeOptionCombo == null ? "" : "and ccoc.categoryoptioncomboid = " + attributeOptionCombo.getId() + " " ) +
                ")"; // End of subquery

        log.info( "Get approval SQL: " + sql );

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        Map<Integer, DataApprovalLevel> levelMap = dataApprovalLevelService.getDataApprovalLevelMap();
        
        List<DataApprovalStatus> statusList = new ArrayList<>();

        DataSet dataSet = ( dataSets.size() == 1 ? dataSets.iterator().next() : null );

        try
        {
            while ( rowSet.next() )
            {
                final Integer aoc = rowSet.getInt( 1 );
                final Integer level = rowSet.getInt( 2 );
                final String acceptedString = rowSet.getString( 3 );
                final boolean readyBelow = rowSet.getBoolean( 4 );
                final boolean approvedAbove = rowSet.getBoolean( 5 );

                final boolean accepted = ( acceptedString == null ? false : acceptedString.substring( 0, 1 ).equalsIgnoreCase( "t" ) );

                DataApprovalLevel dataApprovalLevel = ( level == null || level == 0 ? lowestApprovalLevelForOrgUnit : levelMap.get( level ) );
                
                DataElementCategoryOptionCombo optionCombo = ( aoc == null || aoc == 0 ? null : OPTION_COMBO_CACHE.get( aoc, new Callable<DataElementCategoryOptionCombo>()
                {
                    public DataElementCategoryOptionCombo call() throws ExecutionException
                    {
                        return categoryService.getDataElementCategoryOptionCombo( aoc );
                    }
                } ) );

                DataApproval da = new DataApproval( dataApprovalLevel, dataSet, period, orgUnit, optionCombo, accepted, null, null );

                DataApprovalState state = (
                        level == null || level == 0 ?
                                readyBelow ?
                                        UNAPPROVED_READY :
                                        UNAPPROVED_WAITING :
                                approvedAbove ?
                                        APPROVED_ABOVE :
                                        accepted ?
                                                ACCEPTED_HERE :
                                                APPROVED_HERE );

                statusList.add( new DataApprovalStatus( state, da, dataApprovalLevel, null ) );

                log.debug( "Get approval result: level " + level + " dataApprovalLevel " + dataApprovalLevel
                        + " readyBelow " + readyBelow + " approvedAbove " + approvedAbove
                        + " accepted " + accepted + " state " + state.name() + " " + da );
            }
        }
        catch ( ExecutionException ex )
        {
            throw new RuntimeException( ex );
        }

        return statusList;
    }
}

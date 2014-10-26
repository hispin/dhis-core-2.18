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

import java.util.Date;
import java.util.HashSet;
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
import org.hisp.dhis.dataapproval.DataApprovalStore;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.hibernate.HibernateGenericStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.TextUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
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
    
    private static Cache<Integer, Period> PERIOD_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess( 10, TimeUnit.MINUTES ).initialCapacity( 1000 )
        .maximumSize( 2000 ).build();

    private static Cache<Integer, DataElementCategoryOptionCombo> OPTION_COMBO_CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess( 10, TimeUnit.MINUTES ).initialCapacity( 10000 )
        .maximumSize( 50000 ).build();

    private static Cache<Integer, OrganisationUnit> ORGANISATION_UNIT_CACHE = CacheBuilder.newBuilder()
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

    private UserService userService;

    public void setUserService( UserService userService )
    {
        this.userService = userService;
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
    public Set<DataApproval> getUserDataApprovals( Set<DataSet> dataSets, Set<Period> periods)
    {
        User user = currentUserService.getCurrentUser();

        boolean canSeeDefaultOptionCombo = CollectionUtils.isEmpty( userService.getCoDimensionConstraints( user.getUserCredentials() ) )
                && CollectionUtils.isEmpty( userService.getCogDimensionConstraints( user.getUserCredentials() ) );

        Date minDate = null;
        Date maxDate = null;

        for ( Period p : periods )
        {
            if ( minDate == null || p.getStartDate().before( minDate ) )
            {
                minDate = p.getStartDate();
            }
            if ( maxDate == null || p.getEndDate().after( maxDate ) )
            {
                maxDate = p.getEndDate();
            }
        }

        String sPeriods = "";

        for ( Period p : periods )
        {
            sPeriods += ( sPeriods.isEmpty() ? "" : ", " ) + periodService.reloadPeriod( p ).getId();
        }

        Set<Integer> categoryComboIds = new HashSet<>();

        for ( DataSet ds : dataSets )
        {
            categoryComboIds.add( ds.getCategoryCombo().getId() );
        }

        String sDataSetCCs = TextUtils.getCommaDelimitedString( categoryComboIds );

        String limitCategoryOptionByOrgUnit = "";
        String limitApprovalByOrgUnit = "";

        for ( OrganisationUnit orgUnit : user.getOrganisationUnits() )
        {
            if ( orgUnit.getParent() == null ) // User has root org unit access
            {
                limitCategoryOptionByOrgUnit = "";
                limitApprovalByOrgUnit = "";
                break;
            }

            int level = organisationUnitService.getLevelOfOrganisationUnit( orgUnit );
            limitCategoryOptionByOrgUnit += "ous.idlevel" + level + " = " + orgUnit.getId() + " or ";
            limitApprovalByOrgUnit += "ousda.idlevel" + level + " = " + orgUnit.getId() + " or ";
        }

        if ( !limitCategoryOptionByOrgUnit.isEmpty() )
        {
            limitCategoryOptionByOrgUnit = "and (" + limitCategoryOptionByOrgUnit + "coo.categoryoptionid is null) ";
            limitApprovalByOrgUnit = "and (" + limitApprovalByOrgUnit + "ousda.organisationunitid is null) ";
        }

        String limitBySharing = "";

        if ( !currentUserService.currentUserIsSuper() )
        {
            limitBySharing = "and (ugm.userid = " + user.getId() + " or left(co.publicaccess,1) = 'r') ";
        }

        String sql = "select ccoc.categoryoptioncomboid, da.periodid, dal.level, coo.organisationunitid, da.accepted " +
                "from categorycombos_optioncombos ccoc " +
                "join categoryoptioncombos_categoryoptions cocco on cocco.categoryoptioncomboid = ccoc.categoryoptioncomboid " +
                "join dataelementcategoryoption co on co.categoryoptionid = cocco.categoryoptionid " +
                "left outer join cateogryoption_organisationunits coo on coo.categoryoptionid = cocco.categoryoptionid " +
                "left outer join _orgunitstructure ous on ous.organisationunitid = coo.organisationunitid " +
                "left outer join dataelementcategoryoptionusergroupaccesses couga on couga.categoryoptionid = cocco.categoryoptionid " +
                "left outer join usergroupaccess uga on uga.usergroupaccessid = couga.usergroupaccessid " +
                "left outer join usergroupmembers ugm on ugm.usergroupid = uga.usergroupid " +
                "left outer join dataapproval da on da.categoryoptioncomboid = ccoc.categoryoptioncomboid and da.periodid in (" + sPeriods + ") " +
                "left outer join dataapprovallevel dal on dal.dataapprovallevelid = da.dataapprovallevelid " +
                "left outer join _orgunitstructure ousda on ousda.organisationunitid = da.organisationunitid " +
                "where ccoc.categorycomboid in (" + sDataSetCCs + ") " +
                "and (co.startdate is null or co.startdate <= '" + DateUtils.getMediumDateString( maxDate ) + "') " +
                "and (co.enddate is null or co.enddate >= '" + DateUtils.getMediumDateString( minDate ) + "') " +
                limitCategoryOptionByOrgUnit +
                limitApprovalByOrgUnit +
                limitBySharing +
                "group by ccoc.categoryoptioncomboid, da.periodid, dal.level, coo.organisationunitid, da.accepted " +
                "order by ccoc.categoryoptioncomboid, da.periodid, dal.level";

        log.info( "Get approval SQL: " + sql );
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );

        int previousAttributeOptionComboId = 0;
        int previousPeriodId = 0;
        int previousLevel = 0;

        DataElementCategoryOptionCombo defaultOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        Map<Integer, DataApprovalLevel> levelMap = dataApprovalLevelService.getDataApprovalLevelMap();
        
        Set<DataApproval> userDataApprovals = new HashSet<>();

        try
        {
            while ( rowSet.next() )
            {
                final Integer aoc = rowSet.getInt( 1 );
                final Integer pe = rowSet.getInt( 2 );
                final Integer level = rowSet.getInt( 3 );
                final Integer ou = rowSet.getInt( 4 );
                final Boolean accepted = rowSet.getBoolean( 5 );
    
                if ( aoc == previousAttributeOptionComboId && pe == previousPeriodId && level > previousLevel )
                {
                    continue; // Skip the lower-level approvals for the same categoryOptionCombo & period.
                }
    
                previousAttributeOptionComboId = aoc;
                previousPeriodId = pe;
                previousLevel = level;
    
                DataApprovalLevel dataApprovalLevel = ( level == null ? null : levelMap.get( level ) );
                
                DataElementCategoryOptionCombo optionCombo = ( aoc == null || aoc == 0 ? null : OPTION_COMBO_CACHE.get( aoc, new Callable<DataElementCategoryOptionCombo>()
                {
                    public DataElementCategoryOptionCombo call() throws ExecutionException
                    {
                        return categoryService.getDataElementCategoryOptionCombo( aoc );
                    }
                } ) );
                
                Period period = ( pe == null || pe == 0 ? null : PERIOD_CACHE.get( pe, new Callable<Period>()
                {
                    public Period call() throws ExecutionException
                    {
                        return periodService.getPeriod( pe );
                    }
                } ) );
                
                OrganisationUnit orgUnit = ( ou == null || ou == 0 ? null : ORGANISATION_UNIT_CACHE.get( ou, new Callable<OrganisationUnit>()
                {
                    public OrganisationUnit call() throws ExecutionException
                    {
                        return organisationUnitService.getOrganisationUnit( ou );
                    }
                } ) );
    
                //TODO: currently special cased for PEFPAR's requirements. Can we make it more generic?
                if ( ( level == null || level != 1 ) && optionCombo.equals( defaultOptionCombo ) )
                {
                    if ( canSeeDefaultOptionCombo )
                    {
                        for ( OrganisationUnit unit : getUserOrgsAtLevel( 3 ) )
                        {
                            DataApproval da = new DataApproval( dataApprovalLevel, null, period, unit, optionCombo, accepted, null, null );

                            userDataApprovals.add( da );
                        }
                    }

                    continue;
                }
                
                DataApproval da = new DataApproval( dataApprovalLevel, null, period, orgUnit, optionCombo, accepted, null, null );
    
                userDataApprovals.add( da );
            }
        }
        catch ( ExecutionException ex )
        {
            throw new RuntimeException( ex );
        }

        return userDataApprovals;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<OrganisationUnit> getUserOrgsAtLevel( int desiredLevel )
    {
        Set<OrganisationUnit> orgUnits = new HashSet<>();

        for ( OrganisationUnit orgUnit : currentUserService.getCurrentUser().getOrganisationUnits() )
        {
            orgUnits.addAll( getOrgsAtLevel( orgUnit, desiredLevel, organisationUnitService.getLevelOfOrganisationUnit( orgUnit ) ) );
        }

        return orgUnits;
    }

    private Set<OrganisationUnit> getOrgsAtLevel( OrganisationUnit orgUnit, int desiredLevel, int thisLevel )
    {
        Set<OrganisationUnit> orgUnits = new HashSet<>();

        if ( thisLevel < desiredLevel )
        {
            for ( OrganisationUnit child : orgUnit.getChildren() )
            {
                orgUnits.addAll( getOrgsAtLevel( child, desiredLevel, thisLevel + 1 ) );
            }
        }
        else if ( thisLevel == desiredLevel )
        {
            orgUnits.add( orgUnit );
        }

        return orgUnits;
    }
}

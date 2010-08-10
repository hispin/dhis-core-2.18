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
package org.hisp.dhis.dataadmin.action.lock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.datalock.DataSetLock;
import org.hisp.dhis.datalock.DataSetLockService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.source.Source;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author
 * @version
 */
public class CollectiveDataLockingAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetLockService dataSetLockService;

    public void setDataSetLockService( DataSetLockService dataSetLockService )
    {
        this.dataSetLockService = dataSetLockService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Collection<Integer> selectedPeriodIds = new ArrayList<Integer>();

    public void setPeriodId( Collection<Integer> selectedPeriodIds )
    {
        this.selectedPeriodIds = selectedPeriodIds;
    }

    private Collection<Integer> dataSetIds = new ArrayList<Integer>();

    public void setDataSetIds( Collection<Integer> DataSetIds )
    {
        this.dataSetIds = DataSetIds;
    }
   /*
    private Collection<Integer> organisationUnitIds = new ArrayList<Integer>();

    public void setOrganisationUnitIds( Collection<Integer> organisationUnitIds )
    {
        this.organisationUnitIds = organisationUnitIds;
    }
 */
    private String selectionValue = new String();

    public void setSelectionValue( String selectionValue )
    {
        this.selectionValue = selectionValue;
    }

    private String selectBetweenLockUnlock;

    public void setSelectBetweenLockUnlock( String selectBetweenLockUnlock )
    {
        this.selectBetweenLockUnlock = selectBetweenLockUnlock;
    }

    private Integer levelId;

    public void setLevelId( Integer levelId )
    {
        this.levelId = levelId;
    }

    private Integer orgGroup;

    public void setOrgGroup( Integer orgGroup )
    {
        this.orgGroup = orgGroup;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    Collection<Period> selectedPeriods = new ArrayList<Period>();

    Collection<DataSet> dataSets = new ArrayList<DataSet>();

    private String selected = "selected";

    private String childtree = "childtree";

    private String lock = "lock";

    private String unlock = "unlock";

    private String select_all_at_level = "Select all at level";

    private String unselect_all_at_level = "Unselect all at level";

    private String select_all_in_group = "Select all in group";

    private String unselect_all_in_group = "Unselect all in group";

    public String execute()
    {
        if ( selectedPeriodIds != null && selectedPeriodIds.size() != 0 )
        {
            for ( Integer periodId : selectedPeriodIds )
            {
                selectedPeriods.add( periodService.getPeriod( periodId.intValue() ) );
            }
        }
        else
        {
            message = i18n.getString( "period_not_selected" );

            return SUCCESS;
        }

        if ( dataSetIds != null && dataSetIds.size() != 0 )
        {
            for ( Integer dataSetId : dataSetIds )
            {
                dataSets.add( dataSetService.getDataSet( dataSetId.intValue() ) );
            }
        }
        else
        {
            message = i18n.getString( "dataset_not_selected" );

            return SUCCESS;
        }

        Collection<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        Set<Source> selectedOrganisationUnitsSource = new HashSet<Source>();

        if ( selectBetweenLockUnlock.equalsIgnoreCase( select_all_at_level ) )
        {
            organisationUnits = organisationUnitService.getOrganisationUnitsAtLevel( levelId.intValue() );
            selectedOrganisationUnitsSource = getCurrentUserOrgnaisationUnits();
            selectedOrganisationUnitsSource.retainAll( convert( organisationUnits ) );
            applyCollectiveDataLock( selectedOrganisationUnitsSource );

            message = i18n.getString( "select_all_at_level_saved" );

            return SUCCESS;

        }
        else if ( selectBetweenLockUnlock.equalsIgnoreCase( unselect_all_at_level ) )
        {
            organisationUnits = organisationUnitService.getOrganisationUnitsAtLevel( levelId.intValue() );
            selectedOrganisationUnitsSource = getCurrentUserOrgnaisationUnits();
            selectedOrganisationUnitsSource.retainAll( convert( organisationUnits ) );
            removeCollectiveDataLock( selectedOrganisationUnitsSource );

            message = i18n.getString( "unselect_all_at_level_saved" );

            return SUCCESS;
        }

        if ( selectBetweenLockUnlock.equalsIgnoreCase( select_all_in_group ) )
        {
            organisationUnits = organisationUnitGroupService.getOrganisationUnitGroup( orgGroup.intValue() )
                .getMembers();
            selectedOrganisationUnitsSource = getCurrentUserOrgnaisationUnits();
            selectedOrganisationUnitsSource.retainAll( convert( organisationUnits ) );
            applyCollectiveDataLock( selectedOrganisationUnitsSource );

            message = i18n.getString( "select_all_in_group_saved" );

            return SUCCESS;
        }
        else if ( selectBetweenLockUnlock.equalsIgnoreCase( unselect_all_in_group ) )
        {
            organisationUnits = organisationUnitGroupService.getOrganisationUnitGroup( orgGroup.intValue() )
                .getMembers();
            selectedOrganisationUnitsSource = getCurrentUserOrgnaisationUnits();
            selectedOrganisationUnitsSource.retainAll( convert( organisationUnits ) );
            removeCollectiveDataLock( selectedOrganisationUnitsSource );

            message = i18n.getString( "unselect_all_in_group_saved" );

            return SUCCESS;
        }

        organisationUnits = selectionTreeManager.getSelectedOrganisationUnits();

        if ( organisationUnits == null || organisationUnits.size() == 0 )
        {
            message = i18n.getString( "organisation_not_selected" );

            return SUCCESS;
        }

        if ( selectionValue.equalsIgnoreCase( selected ) )
        {
            selectedOrganisationUnitsSource = convert( organisationUnits );

            if ( selectBetweenLockUnlock.equalsIgnoreCase( lock ) )
            {
                applyCollectiveDataLock( selectedOrganisationUnitsSource );

                message = i18n.getString( "information_successfully_locked" );

            }
            else if ( selectBetweenLockUnlock.equalsIgnoreCase( unlock ) )
            {
                removeCollectiveDataLock( selectedOrganisationUnitsSource );

                message = i18n.getString( "information_successfully_unlocked" );
            }

        }
        else if ( selectionValue.equalsIgnoreCase( childtree ) )
        {
            selectedOrganisationUnitsSource = new HashSet<Source>();
            
            for ( OrganisationUnit organisationUnitsElement : organisationUnits )
            {
                selectedOrganisationUnitsSource.addAll( convert( organisationUnitService
                    .getOrganisationUnitWithChildren( organisationUnitsElement.getId() ) ) );
            }

            if ( selectBetweenLockUnlock.equalsIgnoreCase( lock ) )
            {
                applyCollectiveDataLock( selectedOrganisationUnitsSource );

                message = i18n.getString( "information_successfully_locked" );
            }
            else if ( selectBetweenLockUnlock.equalsIgnoreCase( unlock ) )
            {
                removeCollectiveDataLock( selectedOrganisationUnitsSource );

                message = i18n.getString( "information_successfully_unlocked" );
            }
        }

        return SUCCESS;
    }

    private Set<Source> convert( Collection<OrganisationUnit> organisationUnits )
    {
        Set<Source> sources = new HashSet<Source>();

        for ( OrganisationUnit organisationUnit : organisationUnits )
        {
            sources.add( (Source) organisationUnit );
        }

        return sources;
    }

    private void applyCollectiveDataLock( Set<Source> selectedOrganisationUnitsSource )
    {
        for ( DataSet dataSet : dataSets )
        {
            Set<Source> dataSetOrganisationUnits = dataSet.getSources();            
            Set<Source> selOrgUnitSource = new HashSet<Source>();
            
            selOrgUnitSource.addAll( selectedOrganisationUnitsSource );
            
            selOrgUnitSource.retainAll( dataSetOrganisationUnits );
            
            for ( Period period : selectedPeriods )
            {
                DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );
                if ( dataSetLock != null )
                {
                    Set<Source> lockedOrganisationUnitsSource = dataSetLock.getSources();
                    selOrgUnitSource.removeAll( lockedOrganisationUnitsSource );
                    dataSetLock.getSources().addAll( selOrgUnitSource );
                    dataSetLock.setTimestamp( new Date() );
                    dataSetLock.setStoredBy( currentUserService.getCurrentUsername() );
                    dataSetLockService.updateDataSetLock( dataSetLock );
                }
                else
                {
                    dataSetLock = new DataSetLock();
                    dataSetLock.setPeriod( period );
                    dataSetLock.setSources( selOrgUnitSource );
                    dataSetLock.setDataSet( dataSet );
                    dataSetLock.setTimestamp( new Date() );
                    dataSetLock.setStoredBy( currentUserService.getCurrentUsername() );
                    dataSetLockService.addDataSetLock( dataSetLock );
                }
            }
        }
    }

    private void removeCollectiveDataLock( Set<Source> selectedOrganisationUnitsSource )
    {
        for ( DataSet dataSet : dataSets )
        {
            Set<Source> dataSetOrganisationUnits = dataSet.getSources();
            Set<Source> selOrgUnitSource = new HashSet<Source>();
            
            selOrgUnitSource.addAll( selectedOrganisationUnitsSource );
            selOrgUnitSource.retainAll( dataSetOrganisationUnits );

            for ( Period period : selectedPeriods )
            {
                DataSetLock dataSetLock = dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSet, period );
                if ( dataSetLock != null )
                {
                    Set<Source> lockedOrganisationUnitsSource = dataSetLock.getSources();
                    selOrgUnitSource.retainAll( lockedOrganisationUnitsSource );
                    dataSetLock.getSources().removeAll( selOrgUnitSource );
                    dataSetLock.setTimestamp( new Date() );
                    dataSetLock.setStoredBy( currentUserService.getCurrentUsername() );
                    dataSetLockService.updateDataSetLock( dataSetLock );
                }
            }
        }
    }

    // Returns the OrgUnitTree for which Root is the orgUnit

    public List<OrganisationUnit> getChildOrgUnitTree( OrganisationUnit orgUnit )
    {
        List<OrganisationUnit> orgUnitTree = new ArrayList<OrganisationUnit>();
        orgUnitTree.add( orgUnit );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );

        Collections.sort( children, new OrganisationUnitNameComparator() );

        Iterator<OrganisationUnit> childIterator = children.iterator();
        OrganisationUnit child;
        while ( childIterator.hasNext() )
        {
            child = (OrganisationUnit) childIterator.next();
            orgUnitTree.addAll( getChildOrgUnitTree( child ) );
        }
        return orgUnitTree;
    }

    public Set<Source> getCurrentUserOrgnaisationUnits()
    {
        Set<Source> selectedOrganisationUnitsSource = new HashSet<Source>();
        for ( OrganisationUnit organisationUnit : currentUserService.getCurrentUser().getOrganisationUnits() )
        {
            selectedOrganisationUnitsSource.addAll( convert( getChildOrgUnitTree( organisationUnit ) ) );
        }
        return selectedOrganisationUnitsSource;
    }
}
package org.hisp.dhis.reporting.dataapproval.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataapproval.DataApprovalLevel;
import org.hisp.dhis.dataapproval.DataApprovalLevelService;
import org.hisp.dhis.dataelement.CategoryOptionGroup;
import org.hisp.dhis.dataelement.CategoryOptionGroupSet;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.util.Filter;
import org.hisp.dhis.system.util.FilterUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class GetCategoryOptionGroupsAction
    implements Action
{
    @Autowired
    private DataApprovalLevelService approvalLevelService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private DataElementCategoryService categoryService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String ou;
    
    public void setOu( String ou )
    {
        this.ou = ou;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<CategoryOptionGroup> categoryOptionGroups;

    public List<CategoryOptionGroup> getCategoryOptionGroups()
    {
        return categoryOptionGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    /**
     * Category option groups will be filtered by i) group sets which are available
     * to the current user through approval levels and ii) the organisation unit
     * level selected.
     */
    @Override
    public String execute()
        throws Exception
    {
        if ( ou != null )
        {
            int orgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ou );
            
            List<DataApprovalLevel> approvalLevels = approvalLevelService.getUserDataApprovalLevels();

            FilterUtils.filter( approvalLevels, new DataApprovalLevelOrgUnitLevelFilter( orgUnitLevel ) );
            
            Set<CategoryOptionGroupSet> groupSets = getCategoryOptionGroupSets( approvalLevels );
            
            categoryOptionGroups = new ArrayList<CategoryOptionGroup>( categoryService.getAllCategoryOptionGroups() );
            
            FilterUtils.filter( categoryOptionGroups, new CategoryOptionGroupGroupSetFilter( groupSets ) );        
        }
        
        return SUCCESS;    
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns the category option group sets associated with the given list of
     * data approval levels.
     * 
     * @param approvalLevels the collection of data approval levels.
     * @return a set of category option group sets.
     */
    private Set<CategoryOptionGroupSet> getCategoryOptionGroupSets( Collection<DataApprovalLevel> approvalLevels )
    {
        Set<CategoryOptionGroupSet> groupSets = new HashSet<CategoryOptionGroupSet>();
        
        for ( DataApprovalLevel level : approvalLevels )
        {
            if ( level != null && level.hasCategoryOptionGroupSet() )
            {
                groupSets.add( level.getCategoryOptionGroupSet() );
            }
        }
        
        return groupSets;
    }
    
    /**
     * Filter for org unit level on data approval levels.
     */
    class DataApprovalLevelOrgUnitLevelFilter
        implements Filter<DataApprovalLevel>
    {
        private int orgUnitLevel;
        
        public DataApprovalLevelOrgUnitLevelFilter( int orgUnitLevel )
        {
            this.orgUnitLevel = orgUnitLevel;
        }
        
        @Override
        public boolean retain( DataApprovalLevel level )
        {
            return level != null && level.getOrgUnitLevel() == orgUnitLevel;
        }
    }
    
    /**
     * Filter for group set on category option groups.
     */
    class CategoryOptionGroupGroupSetFilter
        implements Filter<CategoryOptionGroup>
    {
        private Set<CategoryOptionGroupSet> groupSets;
        
        public CategoryOptionGroupGroupSetFilter( Set<CategoryOptionGroupSet> groupSets )
        {
            this.groupSets = groupSets;
        }
        
        @Override
        public boolean retain( CategoryOptionGroup group )
        {
            return groupSets != null && groupSets.contains( group.getGroupSet() );
        }
    }
}

package org.hisp.dhis.dataapproval;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.CategoryOptionGroupSet;

/**
 * Records the approval of DataSet values for a given OrganisationUnit and
 * Period.
 *
 * @author Jim Grace
 */
public class DataApprovalLevel
        implements Serializable
{
    private static final long serialVersionUID = -8424400562969386167L;

    /**
     * Identifies the data approval level instance.
     */
    private int id;

    /**
     * The data approval level, 1=highest level, max=lowest level.
     */
    private int level;

    /**
     * The organisation unit level for this data approval level.
     */
    private int orgUnitLevel;

    /**
     * The category option group set (optional) for this data approval level.
     */
    private CategoryOptionGroupSet categoryOptionGroupSet;

    /**
     * The Date (including time) when the data approval level was created.
     */
    private Date created;

    /**
     * The Date (including time) when the data approval level was last updated.
     */
    private Date updated;

    /**
     * The name of the organisation unit level (derived through the service.)
     */
    private String orgUnitLevelName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public DataApprovalLevel()
    {
    }

    public DataApprovalLevel( int orgUnitLevel,
                              CategoryOptionGroupSet categoryOptionGroupSet )
    {
        this.orgUnitLevel = orgUnitLevel;
        this.categoryOptionGroupSet = categoryOptionGroupSet;
    }

    public DataApprovalLevel( int level, int orgUnitLevel,
                              CategoryOptionGroupSet categoryOptionGroupSet,
                              Date created, Date updated )
    {
        this.level = level;
        this.orgUnitLevel = orgUnitLevel;
        this.categoryOptionGroupSet = categoryOptionGroupSet;
        this.created = created;
        this.updated = updated;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Constructs a name that can refer to this data approval level.
     *
     * @return name of this data approval level.
     */
    public String getName()
    {
        String name = orgUnitLevel
                + ( categoryOptionGroupSet == null ? "" : ( " - " + categoryOptionGroupSet.getName() ) );

        return name;
    }

    /**
     * Returns the name of the category option group set for this data approval
     * level, or an empty string if there is no category option group set.
     *
     * @return name of this approval level's category option group set.
     */
    public String getCategoryOptionGroupSetName()
    {
        String categoryOptionGroupSetName = ( categoryOptionGroupSet == null ? "" : categoryOptionGroupSet.getName() );

        return categoryOptionGroupSetName;
    }

    // -------------------------------------------------------------------------
    // Getters and Setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public int getOrgUnitLevel()
    {
        return orgUnitLevel;
    }

    public void setOrgUnitLevel( int orgUnitLevel )
    {
        this.orgUnitLevel = orgUnitLevel;
    }

    public CategoryOptionGroupSet getCategoryOptionGroupSet()
    {
        return categoryOptionGroupSet;
    }

    public void setCategoryOptionGroupSet( CategoryOptionGroupSet categoryOptionGroupSet )
    {
        this.categoryOptionGroupSet = categoryOptionGroupSet;
    }

    public Date getCreated()
    {
        return created;
    }

    public void setCreated( Date created )
    {
        this.created = created;
    }

    public Date getUpdated()
    {
        return updated;
    }

    public void setUpdated( Date updated )
    {
        this.updated = updated;
    }

    public String getOrgUnitLevelName()
    {
        return orgUnitLevelName;
    }

    public void setOrgUnitLevelName( String orgUnitLevelName )
    {
        this.orgUnitLevelName = orgUnitLevelName;
    }

}

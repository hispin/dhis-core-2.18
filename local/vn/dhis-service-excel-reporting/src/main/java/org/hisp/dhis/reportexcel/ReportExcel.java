package org.hisp.dhis.reportexcel;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.UserAuthorityGroup;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public abstract class ReportExcel
{
    private int id;

    private String name;

    private Integer periodRow;

    private Integer periodColumn;

    private Integer organisationRow;

    private Integer organisationColumn;

    private Set<ReportExcelItem> reportExcelItems;

    private Set<OrganisationUnit> organisationAssocitions;

    private String group;

    private String excelTemplateFile;

    private Collection<UserAuthorityGroup> userRoles;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ReportExcel()
    {
        this.reportExcelItems = new HashSet<ReportExcelItem>();
        this.organisationAssocitions = new HashSet<OrganisationUnit>();
    }

    public Collection<ReportExcelItem> getExportItemBySheet( Integer sheetNo )
    {
        Set<ReportExcelItem> results = new HashSet<ReportExcelItem>();

        for ( ReportExcelItem exportItem : this.reportExcelItems )
        {
            if ( exportItem.getSheetNo() == sheetNo )
            {
                results.add( exportItem );
            }
        }

        return results;
    }

    public abstract boolean isCategory();

    public abstract boolean isOrgUnitGroupListing();

    public abstract boolean isPeriodColumnListing();

    public abstract boolean isNormal();

    public abstract List<String> getItemTypes();

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    public abstract String getReportType();

    // -------------------------------------------------------------------------
    // Internal classes
    // -------------------------------------------------------------------------

    public static class TYPE
    {
        public static final String NORMAL = "NORMAL";

        public static final String CATEGORY = "CATEGORY";

        public static final String PERIOD_COLUMN_LISTING = "PERIOD_COLUMN_LISTING";

        public static final String ORGANIZATION_GROUP_LISTING = "ORGANIZATION_GROUP_LISTING";
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ReportExcel other = (ReportExcel) obj;
        if ( !name.equals( other.name ) )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
    }

    // -------------------------------------------------------------------------
    // Support method
    // -------------------------------------------------------------------------

    public boolean exportItemIsExist( String name, int sheetNo )
    {
        return getExportItemByName( name, sheetNo ) != null;
    }

    public boolean rowAndColumnIsExist( int sheet, int row, int column )
    {
        return getExportItemBySheetRowColumn( sheet, row, column ) != null;
    }

    public ReportExcelItem getExportItemByName( String name, int sheetNo )
    {
        for ( ReportExcelItem exportItem : this.reportExcelItems )
        {
            if ( exportItem.getName().equalsIgnoreCase( name ) && exportItem.getSheetNo() == sheetNo )
            {
                return exportItem;
            }
        }

        return null;
    }

    public ReportExcelItem getExportItemBySheetRowColumn( int sheet, int row, int column )
    {
        for ( ReportExcelItem e : this.reportExcelItems )
        {
            if ( e.getSheetNo() == sheet && e.getRow() == row && e.getColumn() == column )
            {
                return e;
            }
        }

        return null;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public Integer getPeriodRow()
    {
        return periodRow;
    }

    public void setPeriodRow( Integer periodRow )
    {
        this.periodRow = periodRow;
    }

    public Integer getPeriodColumn()
    {
        return periodColumn;
    }

    public void setPeriodColumn( Integer periodColumn )
    {
        this.periodColumn = periodColumn;
    }

    public Integer getOrganisationRow()
    {
        return organisationRow;
    }

    public void setOrganisationRow( Integer organisationRow )
    {
        this.organisationRow = organisationRow;
    }

    public Integer getOrganisationColumn()
    {
        return organisationColumn;
    }

    public void setOrganisationColumn( Integer organisationColumn )
    {
        this.organisationColumn = organisationColumn;
    }

    public Set<ReportExcelItem> getReportExcelItems()
    {
        return reportExcelItems;
    }

    public void setReportExcelItems( Set<ReportExcelItem> exportItems )
    {
        this.reportExcelItems = exportItems;
    }

    public Set<OrganisationUnit> getOrganisationAssocitions()
    {
        return organisationAssocitions;
    }

    public void setOrganisationAssocitions( Set<OrganisationUnit> organisationAssocitions )
    {
        this.organisationAssocitions = organisationAssocitions;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getExcelTemplateFile()
    {
        return excelTemplateFile;
    }

    public void setExcelTemplateFile( String excelTemplateFile )
    {
        this.excelTemplateFile = excelTemplateFile;
    }

    public Collection<UserAuthorityGroup> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles( Collection<UserAuthorityGroup> userRoles )
    {
        this.userRoles = userRoles;
    }

}

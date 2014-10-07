package org.hisp.dhis.eventreport;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import org.hisp.dhis.common.BaseAnalyticalObject;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.EventAnalyticalObject;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.DimensionalView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@JacksonXmlRootElement( localName = "eventReport", namespace = DxfNamespaces.DXF_2_0 )
public class EventReport
    extends BaseAnalyticalObject
    implements EventAnalyticalObject
{
    public static final String DATA_TYPE_AGGREGATED_VALUES = "aggregated_values";

    public static final String DATA_TYPE_INDIVIDUAL_CASES = "individual_cases";

    public static final String COUNT_TYPE_EVENTS = "events";

    public static final String COUNT_TYPE_TRACKED_ENTITY_INSTANCES = "tracked_entity_instances";

    /**
     * Program. Required.
     */
    private Program program;

    /**
     * Program stage.
     */
    private ProgramStage programStage;

    /**
     * Start date.
     */
    private Date startDate;

    /**
     * End date.
     */
    private Date endDate;

    /**
     * Type of data, can be aggregated values and individual cases.
     */
    private String dataType;

    /**
     * Dimensions to crosstabulate / use as columns.
     */
    private List<String> columnDimensions = new ArrayList<>();

    /**
     * Dimensions to use as rows.
     */
    private List<String> rowDimensions = new ArrayList<>();

    /**
     * Dimensions to use as filter.
     */
    private List<String> filterDimensions = new ArrayList<>();

    /**
     * Indicates rendering of sub-totals for the table.
     */
    private boolean rowTotals;

    /**
     * Indicates rendering of sub-totals for the table.
     */
    private boolean colTotals;

    /**
     * Indicates rendering of row sub-totals for the table.
     */
    private boolean rowSubTotals;

    /**
     * Indicates rendering of column sub-totals for the table.
     */
    private boolean colSubTotals;

    /**
     * Indicates count type.
     */
    private String countType;

    /**
     * Indicates rendering of empty rows for the table.
     */
    private boolean hideEmptyRows;

    /**
     * Indicates rendering of empty rows for the table.
     */
    private boolean showHierarchy;

    /**
     * The display density of the text in the table.
     */
    private String displayDensity;

    /**
     * The font size of the text in the table.
     */
    private String fontSize;

    /**
     * The font size of the text in the table.
     */
    private boolean showDimensionLabels;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EventReport()
    {
    }

    public EventReport( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // AnalyticalObject
    // -------------------------------------------------------------------------

    @Override
    public void init( User user, Date date, OrganisationUnit organisationUnit,
        List<OrganisationUnit> organisationUnitsAtLevel, List<OrganisationUnit> organisationUnitsInGroups,
        I18nFormat format )
    {
    }

    @Override
    public void populateAnalyticalProperties()
    {
        for ( String column : columnDimensions )
        {
            columns.addAll( getDimensionalObjectList( column ) );
        }

        for ( String row : rowDimensions )
        {
            rows.addAll( getDimensionalObjectList( row ) );
        }

        for ( String filter : filterDimensions )
        {
            filters.addAll( getDimensionalObjectList( filter ) );
        }
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    @JsonProperty
    @JsonSerialize( as = BaseIdentifiableObject.class )
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDataType()
    {
        return dataType;
    }

    public void setDataType( String dataType )
    {
        this.dataType = dataType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "columnDimensions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "columnDimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getColumnDimensions()
    {
        return columnDimensions;
    }

    public void setColumnDimensions( List<String> columnDimensions )
    {
        this.columnDimensions = columnDimensions;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "rowDimensions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "rowDimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getRowDimensions()
    {
        return rowDimensions;
    }

    public void setRowDimensions( List<String> rowDimensions )
    {
        this.rowDimensions = rowDimensions;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlElementWrapper( localName = "filterDimensions", namespace = DxfNamespaces.DXF_2_0 )
    @JacksonXmlProperty( localName = "filterDimension", namespace = DxfNamespaces.DXF_2_0 )
    public List<String> getFilterDimensions()
    {
        return filterDimensions;
    }

    public void setFilterDimensions( List<String> filterDimensions )
    {
        this.filterDimensions = filterDimensions;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isRowTotals()
    {
        return rowTotals;
    }

    public void setRowTotals( boolean rowTotals )
    {
        this.rowTotals = rowTotals;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isColTotals()
    {
        return colTotals;
    }

    public void setColTotals( boolean colTotals )
    {
        this.colTotals = colTotals;
    }

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isRowSubTotals()
    {
        return rowSubTotals;
    }

    public void setRowSubTotals( boolean rowSubTotals )
    {
        this.rowSubTotals = rowSubTotals;
    }    

    @JsonProperty
    @JsonView( {DetailedView.class, ExportView.class, DimensionalView.class} )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0)
    public boolean isColSubTotals()
    {
        return colSubTotals;
    }

    public void setColSubTotals( boolean colSubTotals )
    {
        this.colSubTotals = colSubTotals;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isHideEmptyRows()
    {
        return hideEmptyRows;
    }

    public void setHideEmptyRows( boolean hideEmptyRows )
    {
        this.hideEmptyRows = hideEmptyRows;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getCountType()
    {
        return countType;
    }

    public void setCountType( String countType )
    {
        this.countType = countType;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isShowHierarchy()
    {
        return showHierarchy;
    }

    public void setShowHierarchy( boolean showHierarchy )
    {
        this.showHierarchy = showHierarchy;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDisplayDensity()
    {
        return displayDensity;
    }

    public void setDisplayDensity( String displayDensity )
    {
        this.displayDensity = displayDensity;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getFontSize()
    {
        return fontSize;
    }

    public void setFontSize( String fontSize )
    {
        this.fontSize = fontSize;
    }
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class, DimensionalView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public boolean isShowDimensionLabels()
    {
        return showDimensionLabels;
    }

    public void setShowDimensionLabels( boolean showDimensionLabels )
    {
        this.showDimensionLabels = showDimensionLabels;
    }    

    @Override
    public void mergeWith( IdentifiableObject other )
    {
        super.mergeWith( other );

        if ( other.getClass().isInstance( this ) )
        {
            EventReport eventReport = (EventReport) other;

            dataType = eventReport.getDataType();
            program = eventReport.getProgram();
            programStage = eventReport.getProgramStage();
            startDate = eventReport.getStartDate();
            endDate = eventReport.getEndDate();
            rowTotals = eventReport.isRowTotals();
            colTotals = eventReport.isColTotals();
            rowSubTotals = eventReport.isRowSubTotals();
            colSubTotals = eventReport.isColSubTotals();
            hideEmptyRows = eventReport.isHideEmptyRows();
            countType = eventReport.getCountType();
            showHierarchy = eventReport.isShowHierarchy();
            showDimensionLabels = eventReport.isShowDimensionLabels();
            displayDensity = eventReport.getDisplayDensity();
            fontSize = eventReport.getFontSize();

            columnDimensions.clear();
            columnDimensions.addAll( eventReport.getColumnDimensions() );

            rowDimensions.clear();
            rowDimensions.addAll( eventReport.getRowDimensions() );

            filterDimensions.clear();
            filterDimensions.addAll( eventReport.getFilterDimensions() );
        }
    }
}

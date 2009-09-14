/**
 * @author DANG DUY HIEU
 */

/*
 * Copyright (c) 2004-2007, University of Oslo
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

package org.hisp.dhis.vn.report.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryComboService;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.vn.report.ReportExcel;
import org.hisp.dhis.vn.report.ReportExcelCategory;
import org.hisp.dhis.vn.report.ReportExcelService;

import com.opensymphony.xwork2.Action;

public class UpdateElementMembersAndCateComboReportCategoryAction
    implements Action
{

    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    private DataElementService dataElementService;

    private DataElementCategoryComboService dataElementCategoryComboService;

    private DataElementCategoryOptionComboService dataElementCategoryOptionComboService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private Integer updateId;

    private String categoryComboList;

    private List<String> selectedDataElements;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    public void setDataElementCategoryComboService( DataElementCategoryComboService dataElementCategoryComboService )
    {
        this.dataElementCategoryComboService = dataElementCategoryComboService;
    }

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryOptionComboService dataElementCategoryOptionComboService )
    {

        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    public Integer getUpdateId()
    {
        return updateId;
    }

    public void setUpdateId( Integer updateId )
    {
        this.updateId = updateId;
    }

    public void setCategoryComboList( String categoryComboList )
    {

        this.categoryComboList = categoryComboList;
    }

    public void setSelectedDataElements( List<String> selectedDataElements )
    {

        this.selectedDataElements = selectedDataElements;
    }

    /**
     * ------------------------------------------------------------------------
     * --------------- Action Implementation
     * ------------------------------------
     * -------------------------------------------------------
     */
    public String execute()
        throws Exception
    {

        ReportExcelCategory report = (ReportExcelCategory) reportService.getReport( updateId.intValue() );

        if ( report.getReportType().equalsIgnoreCase( ReportExcel.TYPE.CATEGORY ) )
        {

            System.out.println( "update report_category" );

            if ( selectedDataElements == null )
            {

                return ERROR;
            }

            List<DataElement> elementMembers = new ArrayList<DataElement>( selectedDataElements.size() );

            for ( String id : selectedDataElements )
            {

                elementMembers.add( dataElementService.getDataElement( Integer.parseInt( id ) ) );
            }
            // Generate OptionCombos for the specified CategoryCombo //
            DataElementCategoryCombo dataElementCategoryCombo = dataElementCategoryComboService
                .getDataElementCategoryCombo( Integer.parseInt( categoryComboList ) );

            if ( dataElementCategoryCombo.getOptionCombos() == null )
            {

                dataElementCategoryOptionComboService.generateOptionCombos( dataElementCategoryCombo );
            }

            //((ReportExcelCategory) report).setDataElements( elementMembers );
            ((ReportExcelCategory) report).setCategoryCombo( dataElementCategoryCombo );

        }

        reportService.updateReport( report );

        return SUCCESS;
    }
}

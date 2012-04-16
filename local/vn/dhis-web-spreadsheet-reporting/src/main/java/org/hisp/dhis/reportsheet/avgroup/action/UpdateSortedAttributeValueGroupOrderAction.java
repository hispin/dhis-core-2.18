package org.hisp.dhis.reportsheet.avgroup.action;

/*
 * Copyright (c) 2004-2011, University of Oslo
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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.reportsheet.AttributeValueGroupOrder;
import org.hisp.dhis.reportsheet.AttributeValueGroupOrderService;
import org.hisp.dhis.reportsheet.ExportReport;
import org.hisp.dhis.reportsheet.ExportReportAttribute;
import org.hisp.dhis.reportsheet.ExportReportService;
import org.hisp.dhis.reportsheet.action.ActionSupport;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class UpdateSortedAttributeValueGroupOrderAction
    extends ActionSupport
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private AttributeValueGroupOrderService attributeValueGroupOrderService;

    public void setAttributeValueGroupOrderService( AttributeValueGroupOrderService attributeValueGroupOrderService )
    {
        this.attributeValueGroupOrderService = attributeValueGroupOrderService;
    }

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer reportId;

    public Integer getReportId()
    {
        return reportId;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    private String clazzName;

    public void setClazzName( String clazzName )
    {
        this.clazzName = clazzName;
    }

    private List<String> attributeValueGroupOrderId = new ArrayList<String>();

    public void setAttributeValueGroupOrderId( List<String> attributeValueGroupOrderId )
    {
        this.attributeValueGroupOrderId = attributeValueGroupOrderId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        List<AttributeValueGroupOrder> attributeValueGroupOrders = new ArrayList<AttributeValueGroupOrder>();

        for ( String id : this.attributeValueGroupOrderId )
        {
            AttributeValueGroupOrder daElementGroupOrder = attributeValueGroupOrderService
                .getAttributeValueGroupOrder( Integer.parseInt( id ) );

            attributeValueGroupOrders.add( daElementGroupOrder );
        }

        if ( clazzName.equals( ExportReport.class.getSimpleName() ) )
        {
            ExportReportAttribute exportReportAttribute = (ExportReportAttribute) exportReportService
                .getExportReport( reportId );

            exportReportAttribute.setAttributeValueOrders( attributeValueGroupOrders );

            exportReportService.updateExportReport( exportReportAttribute );
        }

        message = i18n.getString( "update_successful" );

        return SUCCESS;
    }

}

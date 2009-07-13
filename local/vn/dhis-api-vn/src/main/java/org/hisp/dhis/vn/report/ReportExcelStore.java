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
package org.hisp.dhis.vn.report;

import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public interface ReportExcelStore
{
    String ID = ReportExcelStore.class.getName();
    
    // --------------------------------------
    // Service of Report
    // --------------------------------------

    public void addReport( ReportExcelInterface report );

    public void updateReport( ReportExcelInterface report );

    public void deleteReport( int id );

    public ReportExcelInterface getReport( int id );
    
    public ReportExcelInterface getReport( String name );
    
    public Collection<ReportExcelInterface> getReportsByOrganisationUnit(OrganisationUnit organisationUnit);

    public Collection<ReportExcelInterface> getALLReport();

  
    // --------------------------------------
    // Service of Report Item
    // --------------------------------------

    public void addReportItem( ReportItem reportItem );

    public void updateReportItem( ReportItem reportItem );

    public void deleteReportItem( int id );
    
    public ReportItem getReportItem( int id );
    
    public ReportItem getReportItem( String name );

    public Collection<ReportItem> getALLReportItem();
    
    public Collection<ReportItem> getReportItem(String itemType, ReportExcelNormal reportExcelNormal);
    
    public Collection<ReportItem> getReportItem(int sheetNo, Integer reportId);
    
    public Collection<Integer> getSheets(Integer reportId);
    
    
}

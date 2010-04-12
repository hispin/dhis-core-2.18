package org.hisp.dhis.dataprune;

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

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.chart.ChartService;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.reportexcel.ReportExcel;
import org.hisp.dhis.reportexcel.ReportExcelService;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemGroup;
import org.hisp.dhis.reportexcel.excelitem.ExcelItemService;
import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserStore;
import org.springframework.transaction.annotation.Transactional;

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Quang Nguyen
 * @version Apr 6, 2010 5:48:15 PM
 */

public class DefaultDataPruneService
    implements DataPruneService
{
    private static final Log log = LogFactory.getLog( DefaultDataPruneService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ReportTableService reportTableService;
    
    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    private ReportExcelService reportExcelService;
    
    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    private ExcelItemService excelItemService;

    public void setExcelItemService( ExcelItemService excelItemService )
    {
        this.excelItemService = excelItemService;
    }

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    public void setCompleteDataSetRegistrationService( CompleteDataSetRegistrationService completeDataSetRegistrationService )
    {
        this.completeDataSetRegistrationService = completeDataSetRegistrationService;
    }
    
    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;
    
    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private UserStore userStore;
    
    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    
    private PatientIdentifierService patientIdentifierService;
    
    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }
    
    private ChartService chartService;
    
    public void setChartService( ChartService chartService )
    {
        this.chartService = chartService;
    }
    
    // -------------------------------------------------------------------------
    // DataPruneService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void pruneOrganisationUnit( OrganisationUnit organisationUnit )
    {
        if(organisationUnit.getParent() != null) {
            deleteSiblings(organisationUnit);

            deleteParents(organisationUnit);
        }
    }

    private void deleteParents( OrganisationUnit organisationUnit )
    {
        /*
         * Not implemented yet.
         */
    }

    @SuppressWarnings( "unchecked" )
    private void deleteSiblings( OrganisationUnit organisationUnit )
    {
        List<OrganisationUnit> copiedSiblingList = new CopyOnWriteArrayList(organisationUnit.getParent().getChildren());
        
        for ( OrganisationUnit sibling : copiedSiblingList )
        {
            if ( !sibling.equals( organisationUnit ) )
            //if ( sibling.getId() == 988 )
            {   
                System.out.println("delete sibling: " + sibling.getName());
                deleteABranch( sibling );
            }
        }

    }

    private void deleteABranch(OrganisationUnit organisationUnit) {
        if(!organisationUnit.getChildren().isEmpty()) {
            Set<OrganisationUnit> tmp = organisationUnit.getChildren();
            Object[] childrenAsArray = tmp.toArray();
            
            for ( Object eachChild : childrenAsArray )
            {
                deleteABranch( (OrganisationUnit)eachChild );
            }
        }
        try
        {
            removeOrganisationUnitAndBelonging( organisationUnit );
        }
        catch ( HierarchyViolationException e )
        {
            System.err.println(e.getMessage());
        }
    }

    private void removeOrganisationUnitAndBelonging( OrganisationUnit organisationUnit )
        throws HierarchyViolationException
    {
        removeOganisationUnitFromReportTable( organisationUnit );
        removeOrganisationUnitFromReportExcel( organisationUnit );
        removeOrganisationUnitFromExcelItemGroup( organisationUnit );
        removeCompleteDataSetRegistrationByOganisationUnit( organisationUnit );
        removeOrganisationUnitFromOrganisationUnitGroup( organisationUnit );
        removeOrganisationUnitFromUser( organisationUnit );
        removePatientIdentifierByOrganisationUnit( organisationUnit );
        removeOrganisationUnitFromChart( organisationUnit );
        dataValueService.deleteDataValuesBySource( organisationUnit );
        organisationUnitService.deleteOrganisationUnit( organisationUnit );
    }

    private void removeCompleteDataSetRegistrationByOganisationUnit( OrganisationUnit organisationUnit )
    {
        for ( CompleteDataSetRegistration each : completeDataSetRegistrationService.getAllCompleteDataSetRegistrations())
        {
            if(each.getSource().getId() == organisationUnit.getId())
            {
                completeDataSetRegistrationService.deleteCompleteDataSetRegistration( each );
            }
        }

    }

    private void removeOganisationUnitFromReportTable( OrganisationUnit organisationUnit )
    {
        for ( ReportTable each : reportTableService.getAllReportTables() )
        {
            if(each.getUnits().contains( organisationUnit )) {
                each.getAllUnits().remove( organisationUnit );
                reportTableService.saveReportTable( each );
            }
        }

    }
    
    private void removeOrganisationUnitFromReportExcel( OrganisationUnit organisationUnit )
    {
        for(ReportExcel each : reportExcelService.getALLReportExcel())
        {
            if(each.getOrganisationAssocitions().contains( organisationUnit ))
            {
                each.getOrganisationAssocitions().remove( organisationUnit );
                reportExcelService.updateReportExcel( each );
            }
        }
    }
    
    private void removeOrganisationUnitFromExcelItemGroup( OrganisationUnit organisationUnit )
    {
        for(ExcelItemGroup each : excelItemService.getAllExcelItemGroup())
        {
            if(each.getOrganisationAssocitions().contains( organisationUnit ))
            {
                each.getOrganisationAssocitions().remove( organisationUnit );
                excelItemService.updateExcelItemGroup( each );
            }
        }
    }

    private void removeOrganisationUnitFromOrganisationUnitGroup( OrganisationUnit organisationUnit )
    {
        for(OrganisationUnitGroup each : organisationUnitGroupService.getAllOrganisationUnitGroups())
        {
            if(each.getMembers().contains( organisationUnit ))
            {
                each.getMembers().remove( organisationUnit );
                organisationUnitGroupService.updateOrganisationUnitGroup( each );
            }
        }
    }
    
    private void removeOrganisationUnitFromUser( OrganisationUnit organisationUnit )
    {
        for(User each : userStore.getAllUsers())
        {
            if(each.getOrganisationUnits().contains( organisationUnit ))
            {
                each.getOrganisationUnits().remove( organisationUnit );
                userStore.updateUser( each );
            }
        }
    }
    
    private void removePatientIdentifierByOrganisationUnit( OrganisationUnit organisationUnit ) {
        for(PatientIdentifier each : patientIdentifierService.getPatientIdentifiersByOrgUnit( organisationUnit ))
        {
            patientIdentifierService.deletePatientIdentifier( each );
        }
    }
    
    private void removeOrganisationUnitFromChart( OrganisationUnit organisationUnit ) {
        for(Chart each : chartService.getAllCharts())
        {
            if(each.getOrganisationUnits().contains( organisationUnit ))
            {
                each.getOrganisationUnits().remove( organisationUnit );
                chartService.saveChart( each );
            }
        }
    }
}

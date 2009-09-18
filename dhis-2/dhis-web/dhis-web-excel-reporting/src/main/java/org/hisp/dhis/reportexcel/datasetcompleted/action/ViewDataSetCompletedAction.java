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
package org.hisp.dhis.reportexcel.datasetcompleted.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.comparator.DataSetNameComparator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportexcel.action.ActionSupport;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class ViewDataSetCompletedAction
    extends ActionSupport{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private PeriodService periodService;

    private OrganisationUnitSelectionManager organisationUnitSelectionManager;

    private DataSetService dataSetService;

    private CompleteDataSetRegistrationService completeDataSetRegistrationService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String viewBy;

    private Set<String> selectedPeriods = new HashSet<String>();

    private Set<String> selectedDataSets = new HashSet<String>();

    private String htmlCode;
    
    private String htmlEmbed;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------
    
    

    public String getHtmlCode()
    {
        return htmlCode;
    }

    public String getHtmlEmbed()
    {
        return htmlEmbed;
    }

    public void setSelectedPeriods( Set<String> selectedPeriods )
    {
        this.selectedPeriods = selectedPeriods;
    }

    public void setSelectedDataSets( Set<String> selectedDataSets )
    {
        this.selectedDataSets = selectedDataSets;
    }

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public void setOrganisationUnitSelectionManager( OrganisationUnitSelectionManager organisationUnitSelectionManager )
    {
        this.organisationUnitSelectionManager = organisationUnitSelectionManager;
    }

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    public void setViewBy( String viewBy )
    {
        this.viewBy = viewBy;
    }

    public void setCompleteDataSetRegistrationService(
        CompleteDataSetRegistrationService completeDataSetRegistrationService )
    {
        this.completeDataSetRegistrationService = completeDataSetRegistrationService;
    }

    public String execute()
        throws Exception
    {
        
        
        List<Period> listPeriod = new ArrayList<Period>();

        for ( String id : this.selectedPeriods )
        {
            listPeriod.add( periodService.getPeriod( Integer.parseInt( id ) ) );
        }
        Collections.sort( listPeriod, new PeriodComparator() );

        List<DataSet> listDataSet = new ArrayList<DataSet>();

        for ( String id : this.selectedDataSets )
        {
            listDataSet.add( dataSetService.getDataSet( Integer.parseInt( id ) ) );
        }

        Collections.sort( listDataSet, new DataSetNameComparator() );

        List<OrganisationUnit> children = new ArrayList<OrganisationUnit>( organisationUnitSelectionManager
            .getSelectedOrganisationUnit().getChildren() );

        Collections.sort( children, new OrganisationUnitNameComparator() );       
       
        
        if ( viewBy.equalsIgnoreCase( "period" ) )
        {
            htmlCode = "<p>";
            htmlCode += "<h3>" + i18n.getString( "dataset" ) + ":" + listDataSet.get( 0 ).getName() + "</h3>";
            htmlCode += "</p>";
            
            htmlEmbed = "<p>";
            htmlEmbed += "<h3>" + i18n.getString( "dataset" ) + ":" + listDataSet.get( 0 ).getName() + "</h3>";
            htmlEmbed += "</p>";
        }else{
            htmlCode = "<p>";
            htmlCode += "<h3>" + i18n.getString( "period" ) + ":" + format.formatPeriod(listPeriod.get( 0 )) + "</h3>";
            htmlCode += "</p>";
            
            htmlEmbed = "<p>";
            htmlEmbed += "<h3>" + i18n.getString( "period" ) + ":" + format.formatPeriod(listPeriod.get( 0 )) + "</h3>";
            htmlEmbed += "</p>";
        }

        htmlCode += "<table class=\"list\" cellspacing=\"1\">";
        
        htmlEmbed += "<table class=\"list\" cellspacing=\"1\">";

        if ( viewBy.equalsIgnoreCase( "period" ) )
        {
            htmlCode += "<thead>";
            htmlCode += "<tr>";
            htmlCode += "<th></th>";
            
            htmlEmbed += "<thead>";
            htmlEmbed += "<tr>";
            htmlEmbed += "<th>"+i18n.getString( "period" )+"</th>";
            for ( Period period : listPeriod )
            {
                htmlCode += "<th>";
                htmlCode += format.formatPeriod( period );
                htmlCode += "</th>";
                
                htmlEmbed += "<th>";
                htmlEmbed += format.formatPeriod( period );
                htmlEmbed += "</th>";
            }
            htmlCode += "</tr>";
            htmlCode += "</thead>";
            htmlCode += "<tbody>";
            
            htmlEmbed += "</tr>";
            htmlEmbed += "</thead>";
            htmlEmbed += "<tbody>";

            for ( OrganisationUnit organisationUnit : children )
            {
                htmlCode += "<tr>";
                htmlCode += "<th>" + organisationUnit.getName() + "</th>";
                
                htmlEmbed += "<tr>";
                htmlEmbed += "<th>" + organisationUnit.getName() + "</th>";
                for ( Period period : listPeriod )
                {
                    CompleteDataSetRegistration completeDataSetRegistration = completeDataSetRegistrationService
                        .getCompleteDataSetRegistration( listDataSet.get( 0 ), period, organisationUnit );
                    if ( completeDataSetRegistration != null )
                    {
                        htmlCode += "<td class=\"completed\">";
                        htmlCode += "<a href=\"javascript:viewData("+listDataSet.get( 0 ).getId()+","+period.getId()+","+organisationUnit.getId()+")\">";                        
                        htmlCode += format.formatDate( completeDataSetRegistration.getDate() );
                        htmlCode += "</a>";                        
                        htmlCode += "</td>";   
                        
                        htmlEmbed += "<td class=\"completed\">";     
                        htmlEmbed += format.formatDate( completeDataSetRegistration.getDate() );
                        htmlEmbed += "</td>";   
                    }
                    else
                    {
                        htmlCode += "<td class=\"un_completed\">";                      
                        htmlCode += "<a href=\"javascript:viewData("+listDataSet.get( 0 ).getId()+","+period.getId()+","+organisationUnit.getId()+")\">";
                        htmlCode += i18n.getString( "view_data" );
                        htmlCode += "</a>";                        
                        htmlCode += "</td>";
                        
                        htmlEmbed += "<td class=\"un_completed\">";  
                        htmlEmbed += "</td>";
                        
                    }

                }
                htmlCode += "</tr>";
                htmlEmbed += "</tr>";
            }
            htmlCode += "</tbody>";
            htmlEmbed += "</tbody>";

        }
        else
        {
            htmlCode += "<thead>";
            htmlCode += "<tr>";
            htmlCode += "<th></th>";
            
            htmlEmbed += "<thead>";
            htmlEmbed += "<tr>";
            htmlEmbed += "<th>" + i18n.getString( "dataset" ) + "</th>";
            for ( DataSet dataSet : listDataSet )
            {
                htmlCode += "<th>";
                htmlCode += dataSet.getName();
                htmlCode += "</th>";
                
                htmlEmbed += "<th>";
                htmlEmbed += dataSet.getName();
                htmlEmbed += "</th>";
            }
            htmlCode += "</tr>";
            htmlCode += "</thead>";
            htmlCode += "<tbody>";
            
            htmlEmbed += "</tr>";
            htmlEmbed += "</thead>";
            htmlEmbed += "<tbody>";

            for ( OrganisationUnit organisationUnit : children )
            {
                htmlCode += "<tr>";
                htmlCode += "<th>" + organisationUnit.getName() + "</th>";
                
                htmlEmbed += "<tr>";
                htmlEmbed += "<th>" + organisationUnit.getName() + "</th>";
                for ( DataSet dataSet : listDataSet )
                {
                    CompleteDataSetRegistration completeDataSetRegistration = completeDataSetRegistrationService
                        .getCompleteDataSetRegistration( dataSet, listPeriod.get( 0 ), organisationUnit );
                    if ( completeDataSetRegistration != null )
                    {
                        htmlCode += "<td class=\"completed\">";                       
                        htmlCode += "<a href=\"javascript:viewData("+dataSet.getId()+","+listPeriod.get( 0 ).getId()+","+organisationUnit.getId()+")\">";
                        htmlCode += format.formatDate( completeDataSetRegistration.getDate() );
                        htmlCode += "</a>";                        
                        htmlCode += "</td>";     
                        
                        htmlEmbed += "<td class=\"completed\">";     
                        htmlEmbed += format.formatDate( completeDataSetRegistration.getDate() );
                        htmlEmbed += "</td>";   
                    }
                    else
                    {
                        htmlCode += "<td class=\"un_completed\">";                       
                        htmlCode += "<a href=\"javascript:viewData("+dataSet.getId()+","+listPeriod.get( 0 ).getId()+","+organisationUnit.getId()+")\">";
                        htmlCode += i18n.getString( "view_data" );
                        htmlCode += "</a>";                        
                        htmlCode += "</td>";  
                        
                        htmlEmbed += "<td class=\"un_completed\">";  
                        htmlEmbed += "</td>";
                    }

                }
                htmlCode += "</tr>";
                htmlEmbed += "</tr>";
            }
            htmlCode += "</tbody>";
            htmlEmbed += "</tbody>";

        }

        htmlCode += "</table>";
        htmlEmbed += "</table>";

        return SUCCESS;
    }
}

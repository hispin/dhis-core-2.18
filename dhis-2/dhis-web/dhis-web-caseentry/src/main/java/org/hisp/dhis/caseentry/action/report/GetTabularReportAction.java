/*
 * Copyright (c) 2004-2012, University of Oslo
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

package org.hisp.dhis.caseentry.action.report;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patientreport.PatientTabularReport;
import org.hisp.dhis.patientreport.PatientTabularReportService;
import org.hisp.dhis.program.ProgramStage;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $DeleteTabularReportAction.java May 7, 2012 4:11:43 PM$
 */
public class GetTabularReportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientTabularReportService tabularReportService;

    public void setTabularReportService( PatientTabularReportService tabularReportService )
    {
        this.tabularReportService = tabularReportService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private PatientTabularReport tabularReport;

    public PatientTabularReport getTabularReport()
    {
        return tabularReport;
    }

    private ProgramStage programStage;

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    private List<String> selectedFixedAttributes = new ArrayList<String>();

    public List<String> getSelectedFixedAttributes()
    {
        return selectedFixedAttributes;
    }

    private List<DataElement> selectedDataElements = new ArrayList<DataElement>();

    public List<DataElement> getSelectedDataElements()
    {
        return selectedDataElements;
    }

    private List<PatientAttribute> selectedAttributes = new ArrayList<PatientAttribute>();

    public List<PatientAttribute> getSelectedAttributes()
    {
        return selectedAttributes;
    }

    private List<PatientIdentifierType> selectedIdentifierTypes = new ArrayList<PatientIdentifierType>();

    public List<PatientIdentifierType> getSelectedIdentifierTypes()
    {
        return selectedIdentifierTypes;
    }

    private List<String> fixedAttributeFilters = new ArrayList<String>();

    public List<String> getFixedAttributeFilters()
    {
        return fixedAttributeFilters;
    }

    private List<String> patientAttributeFilters = new ArrayList<String>();

    public List<String> getPatientAttributeFilters()
    {
        return patientAttributeFilters;
    }

    private List<String> identifierTypeFilters = new ArrayList<String>();

    public List<String> getIdentifierTypeFilters()
    {
        return identifierTypeFilters;
    }

    private List<String> dataelementFilters = new ArrayList<String>();

    public List<String> getDataelementFilters()
    {
        return dataelementFilters;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        tabularReport = tabularReportService.getPatientTabularReportByUid( id );

        programStage = tabularReport.getProgramStage();

        for ( String filterValue : tabularReport.getFilterValues() )
        {
            String[] values = filterValue.split( "_" );
            String filter = "";
            if ( values.length == 5 )
            {
                filter =  values[3] + "_" + values[4].trim().substring( 1, values[4].length() - 1 );
            }

            if ( values[0].equals( PatientTabularReport.PREFIX_FIXED_ATTRIBUTE ) )
            {
                selectedFixedAttributes.add( values[1] );
                fixedAttributeFilters.add( filter );
            }
            else
            {
                int id = Integer.parseInt( values[1] );

                if ( values[0].equals( PatientTabularReport.PREFIX_IDENTIFIER_TYPE ) )
                {
                    selectedIdentifierTypes.add( patientIdentifierTypeService.getPatientIdentifierType( id ) );
                    identifierTypeFilters.add( filter );
                }
                else if ( values[0].equals( PatientTabularReport.PREFIX_PATIENT_ATTRIBUTE ) )
                {
                    selectedAttributes.add( patientAttributeService.getPatientAttribute( id ) );
                    patientAttributeFilters.add( filter );
                }
                else if ( values[0].equals( PatientTabularReport.PREFIX_DATA_ELEMENT ) )
                {
                    selectedDataElements.add( dataElementService.getDataElement( id ) );
                    dataelementFilters.add( filter );
                }
            }
        }

        return SUCCESS;
    }
}

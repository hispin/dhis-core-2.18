/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient.action.patientchart;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.patientchart.PatientChart;
import org.hisp.dhis.patientchart.PatientChartService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version $ UpdatePatientChartAction.java Sep 5, 2011 9:13:29 AM $
 * 
 */
public class UpdatePatientChartAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientChartService patientChartService;

    public void setPatientChartService( PatientChartService patientChartService )
    {
        this.patientChartService = patientChartService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String title;

    private String type;

    private String size;

    private boolean regression;
    
    private Integer programId;

    private Integer dataElementId;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    public void setTitle( String title )
    {
        this.title = title;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public void setSize( String size )
    {
        this.size = size;
    }

    public void setRegression( boolean regression )
    {
        this.regression = regression;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        PatientChart patientChart = patientChartService.getPatientChart( id );

        patientChart.setTitle( title );
        patientChart.setType( type );
        patientChart.setSize( size );
        patientChart.setRegression( regression );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        patientChart.setDataElement( dataElement );

        programId = patientChart.getProgram().getId();
        
        patientChartService.updatePatientChart( patientChart );

        return SUCCESS;
    }

}

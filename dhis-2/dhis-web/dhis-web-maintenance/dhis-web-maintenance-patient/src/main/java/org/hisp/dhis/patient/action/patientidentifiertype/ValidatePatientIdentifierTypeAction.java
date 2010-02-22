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

package org.hisp.dhis.patient.action.patientidentifiertype;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Viet
 * @version $Id$
 */

public class ValidatePatientIdentifierTypeAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------


    private String nameField;

    private String message;
    
    private String description;

    private I18n i18n;
    
    private Integer id;


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        if ( StringUtils.isBlank( nameField ) )
        {
            message = i18n.getString( "please_specify_a_name" );

            return INPUT;
        }
        else
        {

            PatientIdentifierType match = patientIdentifierTypeService.getPatientIdentifierType( nameField );

            if ( match != null && (id == null || match.getId() != id.intValue()) )
            {
                message = i18n.getString( "name_in_use" );

                return INPUT;
            }
        }

        if (  StringUtils.isBlank(description) )
        {
            message = i18n.getString( "please_specify_a_description" );

            return INPUT;
        }

        else
        {
            description = description.trim();

            if ( description.length() == 0 )
            {
                message = i18n.getString( "please_specify_a_description" );

                return INPUT;
            }
        }

        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;

    }

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------


    public String getMessage()
    {
        return message;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }


    public void setNameField( String nameField )
    {
        this.nameField = nameField;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }


    public void setId( Integer id )
    {
        this.id = id;
    }

}

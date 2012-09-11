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

package org.hisp.dhis.caseentry.action.patient;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ShowAddPatientFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<User> healthWorkers;
    
    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    private Boolean addNewForm;

    public Boolean getAddNewForm()
    {
        return addNewForm;
    }

    public void setAddNewForm( Boolean addNewForm )
    {
        this.addNewForm = addNewForm;
    }

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    private Collection<PatientIdentifierType> identifierTypes;

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Map<PatientAttributeGroup, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<PatientAttributeGroup, Collection<PatientAttribute>>();

    public Map<PatientAttributeGroup, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();
        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            identifierTypes.removeAll( program.getPatientIdentifierTypes() );
            patientAttributes.removeAll( program.getPatientAttributes() );
        }

        attributeGroupsMap = new HashMap<PatientAttributeGroup, Collection<PatientAttribute>>();
        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            PatientAttributeGroup attributeGroup = patientAttribute.getPatientAttributeGroup();
            if ( attributeGroup != null )
            {
                if ( attributeGroupsMap.containsKey( attributeGroup ) )
                {
                    Collection<PatientAttribute> attributes = attributeGroupsMap.get( attributeGroup );
                    attributes.add( patientAttribute );
                }
                else
                {
                    Collection<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                    attributes.add( patientAttribute );
                    attributeGroupsMap.put( attributeGroup, attributes );
                }
            }
            else
            {
                noGroupAttributes.add( patientAttribute );
            }
        }

        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        healthWorkers = organisationUnit.getUsers();
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

}

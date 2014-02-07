package org.hisp.dhis.caseentry.action.trackedentity;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOption;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOptionService;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdateTrackedEntityInstanceAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceService entityInstanceService;

    private TrackedEntityAttributeService attributeService;

    private TrackedEntityAttributeValueService attributeValueService;

    private TrackedEntityAttributeOptionService attributeOptionService;

    private OrganisationUnitSelectionManager selectionManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private Integer representativeId;

    private Integer relationshipTypeId;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private TrackedEntityInstance entityInstance;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = selectionManager.getSelectedOrganisationUnit();

        entityInstance = entityInstanceService.getTrackedEntityInstance( id );

        // ---------------------------------------------------------------------
        // Set location
        // ---------------------------------------------------------------------

        entityInstance.setOrganisationUnit( organisationUnit );

        // ---------------------------------------------------------------------
        // Save Tracked Entity Instance Attributes
        // ---------------------------------------------------------------------

        HttpServletRequest request = ServletActionContext.getRequest();

        Collection<TrackedEntityAttribute> attributes = attributeService.getAllTrackedEntityAttributes();

        List<TrackedEntityAttributeValue> valuesForSave = new ArrayList<TrackedEntityAttributeValue>();
        List<TrackedEntityAttributeValue> valuesForUpdate = new ArrayList<TrackedEntityAttributeValue>();
        Collection<TrackedEntityAttributeValue> valuesForDelete = null;

        TrackedEntityAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            valuesForDelete = attributeValueService.getTrackedEntityAttributeValues( entityInstance );

            for ( TrackedEntityAttribute attribute : attributes )
            {
                String value = request.getParameter( AddTrackedEntityInstanceAction.PREFIX_ATTRIBUTE + attribute.getId() );

                if ( StringUtils.isNotBlank( value ) )
                {
                    if ( attribute.getValueType().equals( TrackedEntityAttribute.TYPE_AGE ) )
                    {
                        value = format.formatDate( TrackedEntityAttribute.getDateFromAge( Integer.parseInt( value ) ) );
                    }

                    attributeValue = attributeValueService.getTrackedEntityAttributeValue( entityInstance, attribute );

                    if ( attributeValue == null )
                    {
                        attributeValue = new TrackedEntityAttributeValue();
                        attributeValue.setEntityInstance( entityInstance );
                        attributeValue.setAttribute( attribute );
                        attributeValue.setValue( value.trim() );
                        if ( TrackedEntityAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            TrackedEntityAttributeOption option = attributeOptionService.get( Integer
                                .parseInt( value ) );
                            if ( option != null )
                            {
                                attributeValue.setAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        valuesForSave.add( attributeValue );
                    }
                    else
                    {
                        if ( TrackedEntityAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            TrackedEntityAttributeOption option = attributeOptionService.get( NumberUtils.toInt(
                                value, 0 ) );
                            if ( option != null )
                            {
                                attributeValue.setAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }
                        else
                        {
                            attributeValue.setValue( value.trim() );
                        }

                        valuesForUpdate.add( attributeValue );
                        valuesForDelete.remove( attributeValue );
                    }
                }
            }
        }

        entityInstanceService.updateTrackedEntityInstance( entityInstance, representativeId, relationshipTypeId, valuesForSave, valuesForUpdate,
            valuesForDelete );

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setentityInstanceService( TrackedEntityInstanceService entityInstanceService )
    {
        this.entityInstanceService = entityInstanceService;
    }

    public void setattributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    public void setattributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public TrackedEntityInstance getEntityInstance()
    {
        return entityInstance;
    }

    public void setattributeOptionService( TrackedEntityAttributeOptionService attributeOptionService )
    {
        this.attributeOptionService = attributeOptionService;
    }

    public void setRepresentativeId( Integer representativeId )
    {
        this.representativeId = representativeId;
    }

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

}

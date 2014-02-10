package org.hisp.dhis.trackedentity.action.trackedentityattribute;

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

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOption;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeOptionService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;

import com.opensymphony.xwork2.Action;

/**
 * @author Viet
 */
public class RemoveAttributeOptionAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityAttributeOptionService attributeOptionService;

    private TrackedEntityAttributeValueService attributeValueService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    private String message;

    private I18n i18n;

    // -------------------------------------------------------------------------
    // Getter && Setter
    // -------------------------------------------------------------------------

    public void setAttributeOptionService( TrackedEntityAttributeOptionService attributeOptionService )
    {
        this.attributeOptionService = attributeOptionService;
    }

    public void setAttributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        TrackedEntityAttributeOption attributeOption = attributeOptionService.get( id );

        if ( attributeOption != null )
        {
            int count = attributeValueService.countByAttributeOption( attributeOption );
            if ( count > 0 )
            {
                message = i18n.getString( "warning_delete_tracked_entity_attribute_option" );
                return INPUT;
            }
            else
            {
                attributeOptionService.deleteTrackedEntityAttributeOption( attributeOption );
                message = i18n.getString( "success_delete_tracked_entity_attribute_option" );
                return SUCCESS;
            }
        }
        else
        {
            message = i18n.getString( "error_delete_tracked_entity_attribute_option" );
            return ERROR;
        }

    }

}

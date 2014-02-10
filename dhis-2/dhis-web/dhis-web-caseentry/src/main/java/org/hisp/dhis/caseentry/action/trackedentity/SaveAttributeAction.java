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

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
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
 * @author Chau Thu Tran
 * 
 * @version $SaveAttributeAction.java Mar 29, 2012 10:33:00 AM$
 */
public class SaveAttributeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityInstanceService entityInstanceService;

    private TrackedEntityAttributeValueService attributeValueService;

    private TrackedEntityAttributeService attributeService;

    private TrackedEntityAttributeOptionService attributeOptionService;

    private ProgramService programService;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    private Integer entityInstanceId;

    private Integer statusCode;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    public void setEntityInstanceService( TrackedEntityInstanceService entityInstanceService )
    {
        this.entityInstanceService = entityInstanceService;
    }

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    public void setAttributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    public void setAttributeOptionService( TrackedEntityAttributeOptionService attributeOptionService )
    {
        this.attributeOptionService = attributeOptionService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
    }

    public Integer getEntityInstanceId()
    {
        return entityInstanceId;
    }

    public void setEntityInstanceId( Integer entityInstanceId )
    {
        this.entityInstanceId = entityInstanceId;
    }

    public Integer getStatusCode()
    {
        return statusCode;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        TrackedEntityInstance entityInstance = entityInstanceService.getTrackedEntityInstance( entityInstanceId );
        Program program = programService.getProgram( programId );

        saveAttributeValues( entityInstance, program );

        statusCode = 0;

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void saveAttributeValues( TrackedEntityInstance entityInstance, Program program )
    {
        HttpServletRequest request = ServletActionContext.getRequest();

        String value = null;

        Collection<TrackedEntityAttribute> attributes = attributeService.getAllTrackedEntityAttributes();
        
        TrackedEntityAttributeValue attributeValue = null;

        if ( attributes != null && attributes.size() > 0 )
        {
            for ( TrackedEntityAttribute attribute : attributes )
            {
                value = request.getParameter( AddTrackedEntityInstanceAction.PREFIX_ATTRIBUTE + attribute.getId() );
 System.out.println("\n\n\n ====== \n value : " + value );
 System.out.println("\n params : " + AddTrackedEntityInstanceAction.PREFIX_ATTRIBUTE + attribute.getId() );
                attributeValue = attributeValueService.getTrackedEntityAttributeValue( entityInstance, attribute );
                
                if ( StringUtils.isNotBlank( value ) )
                {
                    if ( attributeValue == null )
                    {
                        attributeValue = new TrackedEntityAttributeValue();
                        attributeValue.setEntityInstance( entityInstance );
                        attributeValue.setAttribute( attribute );
                        attributeValue.setValue( value.trim() );

                        if ( attribute.getValueType().equals( TrackedEntityAttribute.TYPE_AGE ) )
                        {
                            value = format
                                .formatDate( TrackedEntityAttribute.getDateFromAge( Integer.parseInt( value ) ) );
                        }
 System.out.println("\n\n attribute.getValueType() : " + attribute.getValueType() );

                        if ( TrackedEntityAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            TrackedEntityAttributeOption option = attributeOptionService
                                .get( Integer.parseInt( value ) );

                            System.out.println("\n\n option : " + option );
                            
                            if ( option != null )
                            {  System.out.println("\n\n option : " + option );
                            
                                attributeValue.setAttributeOption( option );
                                attributeValue.setValue( option.getName() );
                            }
                        }

                        attributeValueService.saveTrackedEntityAttributeValue( attributeValue );
                        entityInstance.getAttributeValues().add( attributeValue );
                    }
                    else
                    {
                        if ( TrackedEntityAttribute.TYPE_COMBO.equalsIgnoreCase( attribute.getValueType() ) )
                        {
                            TrackedEntityAttributeOption option = attributeOptionService.get( NumberUtils.toInt( value,
                                0 ) );
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
                        attributeValueService.updateTrackedEntityAttributeValue( attributeValue );
                        entityInstance.getAttributeValues().add( attributeValue );
                    }
                }
                else if ( attributeValue != null )
                {
                    entityInstance.getAttributeValues().remove( attributeValue );
                    attributeValueService.deleteTrackedEntityAttributeValue( attributeValue );
                }
            }
        }

        entityInstanceService.updateTrackedEntityInstance( entityInstance );
    }

}

package org.hisp.dhis.trackedentity.action.trackedentityattribute;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class AddAttributeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    @Autowired
    private OptionService optionService;

    @Autowired
    private PeriodService periodService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String valueType;

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    private Boolean mandatory;

    public void setMandatory( Boolean mandatory )
    {
        this.mandatory = mandatory;
    }
    
    private Boolean inherit;

    public void setInherit( Boolean inherit )
    {
        this.inherit = inherit;
    }

    private String expression;

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    private Boolean unique;

    public void setUnique( Boolean unique )
    {
        this.unique = unique;
    }

    private Integer optionSetId;

    public void setOptionSetId( Integer optionSetId )
    {
        this.optionSetId = optionSetId;
    }

    // For Local ID type

    private Boolean orgunitScope;

    public void setOrgunitScope( Boolean orgunitScope )
    {
        this.orgunitScope = orgunitScope;
    }

    private Boolean programScope;

    public void setProgramScope( Boolean programScope )
    {
        this.programScope = programScope;
    }

    private String periodTypeName;

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        TrackedEntityAttribute attribute = new TrackedEntityAttribute();

        attribute.setName( name );
        attribute.setCode( StringUtils.isEmpty( code.trim() ) ? null : code );
        attribute.setDescription( description );
        attribute.setValueType( valueType );
        attribute.setExpression( expression );
        attribute.setDisplayOnVisitSchedule( false );

        mandatory = (mandatory == null) ? false : true;
        attribute.setMandatory( mandatory );

        unique = (unique == null) ? false : true;
        attribute.setUnique( unique );

        inherit = (inherit == null) ? false : true;
        attribute.setInherit( inherit );

        if ( valueType.equals( TrackedEntityAttribute.VALUE_TYPE_LOCAL_ID ) )
        {
            orgunitScope = (orgunitScope == null) ? false : orgunitScope;
            programScope = (programScope == null) ? false : programScope;

            if ( !StringUtils.isEmpty( periodTypeName ) )
            {
                PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
                periodType = periodService.reloadPeriodType( periodType );
                attribute.setPeriodType( periodType );
            }
            else
            {
                attribute.setPeriodType( null );
            }

            attribute.setOrgunitScope( orgunitScope );
            attribute.setProgramScope( programScope );
        }
        else if ( valueType.equals( TrackedEntityAttribute.TYPE_COMBO ) )
        {
            attribute.setOptionSet( optionService.getOptionSet( optionSetId ) );
        }

        attributeService.addTrackedEntityAttribute( attribute );

        return SUCCESS;
    }
}
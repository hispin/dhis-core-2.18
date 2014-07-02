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
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdateAttributeAction
    implements Action
{
    private final Integer SCOPE_ORGUNIT = 1;

    private final Integer SCOPE_PROGRAM = 2;

    private final Integer SCOPE_PROGRAM_IN_ORGUNIT = 3;

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

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
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

    private Integer scope;

    public void setScope( Integer scope )
    {
        this.scope = scope;
    }

    private Boolean confidential;
    
    public void setConfidential( Boolean confidential )
    {
        this.confidential = confidential;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        TrackedEntityAttribute attribute = attributeService.getTrackedEntityAttribute( id );

        attribute.setName( name );
        attribute.setShortName( shortName );
        attribute.setCode( StringUtils.isEmpty( code.trim() ) ? null : code );
        attribute.setDescription( description );
        attribute.setValueType( valueType );
        attribute.setExpression( expression );
        attribute.setDisplayOnVisitSchedule( false );

        unique = (unique == null) ? false : true;
        attribute.setUnique( unique );

        inherit = (inherit == null) ? false : true;
        attribute.setInherit( inherit );

        confidential = (confidential == null) ? false : true;
        attribute.setConfidential( confidential );

        if ( unique )
        {
            boolean orgunitScope = false;
            boolean programScope = false;
            if ( scope != null && (scope == SCOPE_ORGUNIT || scope == SCOPE_PROGRAM_IN_ORGUNIT) )
            {
                orgunitScope = true;
            }

            if ( scope != null && (scope == SCOPE_PROGRAM || scope == SCOPE_PROGRAM_IN_ORGUNIT) )
            {
                programScope = true;
            }

            attribute.setOrgunitScope( orgunitScope );
            attribute.setProgramScope( programScope );
        }
        else if ( valueType.equals( TrackedEntityAttribute.TYPE_OPTION_SET ) )
        {
            attribute.setOptionSet( optionService.getOptionSet( optionSetId ) );
        }

        attributeService.updateTrackedEntityAttribute( attribute );

        return SUCCESS;
    }
}

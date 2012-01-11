/*
 * Copyright (c) 2004-2010, University of Oslo
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

package org.hisp.dhis.patient.action.program;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.program.ProgramAttribute;
import org.hisp.dhis.program.ProgramAttributeOption;
import org.hisp.dhis.program.ProgramAttributeOptionService;
import org.hisp.dhis.program.ProgramAttributeService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version AddProgramAttributeAction.java Oct 31, 2010 11:48:29 PM
 */
public class UpdateProgramAttributeAction
    implements Action
{
    public static final String PREFIX_ATTRIBUTE_OPTION = "attrOption";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramAttributeService programAttributeService;

    public void setProgramAttributeService( ProgramAttributeService programAttributeService )
    {
        this.programAttributeService = programAttributeService;
    }

    private ProgramAttributeOptionService programAttributeOptionService;

    public void setProgramAttributeOptionService( ProgramAttributeOptionService programAttributeOptionService )
    {
        this.programAttributeOptionService = programAttributeOptionService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    private String name;

    private String description;

    private String valueType;

    private List<String> attrOptions;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    public void setAttrOptions( List<String> attrOptions )
    {
        this.attrOptions = attrOptions;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        ProgramAttribute programAttribute = programAttributeService.getProgramAttribute( id );

        programAttribute.setName( name );
        programAttribute.setDescription( description );
        programAttribute.setValueType( valueType );
        
        HttpServletRequest request = ServletActionContext.getRequest();
        
        Collection<ProgramAttributeOption> attributeOptions = programAttributeOptionService.get( programAttribute );
        
        if ( attributeOptions != null && attributeOptions.size() > 0 )
        {
            String value = null;
            for( ProgramAttributeOption option : attributeOptions )
            {
                value = request.getParameter( PREFIX_ATTRIBUTE_OPTION + option.getId() );
                if ( StringUtils.isNotBlank( value ) )
                {
                    option.setName( value.trim() );
                    programAttributeOptionService.updateProgramAttributeOption( option );
                }
            }
        }
        
        if( attrOptions != null )
        {
            ProgramAttributeOption opt  = null;
            for( String optionName : attrOptions )
            {
                opt = programAttributeOptionService.get( programAttribute, optionName );
                if( opt == null )
                {
                    opt = new ProgramAttributeOption();
                    opt.setName( optionName );
                    opt.setProgramAttribute( programAttribute );
                    programAttribute.addAttributeOptions( opt );
                }
            }
        }
        
        programAttributeService.updateProgramAttribute( programAttribute );
        
        return SUCCESS;
    }
}

package org.hisp.dhis.web.webapi.v1.validation.constraint;

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

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.web.webapi.v1.domain.Identifier;
import org.hisp.dhis.web.webapi.v1.validation.constraint.annotation.ValidIdentifiers;
import org.hisp.dhis.web.webapi.v1.validation.group.Create;
import org.hisp.dhis.web.webapi.v1.validation.group.Update;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class IdentifiersValidator implements ConstraintValidator<ValidIdentifiers, List<Identifier>>
{
    @Autowired
    private IdentifiableObjectManager identifiableObjectManager;

    private boolean isCreating = true;

    @Override
    public void initialize( ValidIdentifiers constraintAnnotation )
    {
        List<Class<?>> groups = Arrays.asList( constraintAnnotation.groups() );

        if ( groups.contains( Create.class ) )
        {
            isCreating = true;
        }
        else if ( groups.contains( Update.class ) )
        {
            isCreating = false;
        }
    }

    @Override
    public boolean isValid( List<Identifier> value, ConstraintValidatorContext context )
    {
        boolean returnValue = true;

        for ( Identifier identifier : value )
        {
            // only dhis2 codes are supported by this validator for now
            if ( identifier.getAgency().equalsIgnoreCase( Identifier.DHIS2_AGENCY )
                && identifier.getContext().equalsIgnoreCase( Identifier.DHIS2_CODE_CONTEXT ) )
            {
                OrganisationUnit organisationUnit = identifiableObjectManager.getByCode( OrganisationUnit.class, identifier.getId() );

                if ( organisationUnit != null )
                {
                    if ( isCreating )
                    {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate( "An object already exists with that identifier." )
                            .addNode( identifier.getContext() )
                            .addConstraintViolation();

                        returnValue = false;
                    }
                }
                else
                {
                    // TODO for this to work properly, we would need to have access to the current object being validated.
                    /*
                    if ( !isCreating )
                    {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate( "An object already exists with that identifier." )
                            .addNode( identifier.getContext() )
                            .addConstraintViolation();
                    }
                    */
                }
            }
        }

        return returnValue;
    }
}

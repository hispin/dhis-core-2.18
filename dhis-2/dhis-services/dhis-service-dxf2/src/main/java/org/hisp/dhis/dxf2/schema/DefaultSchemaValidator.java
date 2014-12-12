package org.hisp.dhis.dxf2.schema;

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

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageStatus;
import org.hisp.dhis.dxf2.webmessage.responses.ValidationViolation;
import org.hisp.dhis.dxf2.webmessage.responses.ValidationViolationsWebMessageResponse;
import org.hisp.dhis.schema.Property;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.system.util.ReflectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultSchemaValidator implements SchemaValidator
{
    @Autowired
    private SchemaService schemaService;

    @Override
    public <T extends IdentifiableObject> WebMessage validate( T object )
    {
        Assert.notNull( object, "SchemaValidator.validate requires a non-null object to validate." );

        Schema schema = schemaService.getSchema( object.getClass() );

        Assert.notNull( schema, "Could not validate object, no schema exists for objects of this type." );

        WebMessage message = new WebMessage( WebMessageStatus.OK, HttpStatus.OK.value() );
        ValidationViolationsWebMessageResponse validationViolations = new ValidationViolationsWebMessageResponse();

        for ( Property property : schema.getProperties() )
        {
            if ( !property.isPersisted() )
            {
                continue;
            }

            Object value = ReflectionUtils.invokeMethod( object, property.getGetterMethod() );

            if ( !property.isNullable() && value == null )
            {
                validationViolations.getValidationViolations().add( new ValidationViolation( "Property '" + property.getName() + "' can not be null." ) );
                continue;
            }

            if ( String.class.isInstance( value ) && property.getMaxLength() < ((String) value).length() )
            {
                validationViolations.getValidationViolations().add( new ValidationViolation( "Property '" + property.getName() + "' is too long ("
                    + ((String) value).length() + "), maximum length is " + property.getMaxLength() ) );
                continue;
            }
        }

        if ( !validationViolations.getValidationViolations().isEmpty() )
        {
            message.setStatus( WebMessageStatus.ERROR );
            message.setHttpStatusCode( HttpStatus.BAD_REQUEST.value() );
            message.setResponse( validationViolations );
        }

        return message;
    }
}

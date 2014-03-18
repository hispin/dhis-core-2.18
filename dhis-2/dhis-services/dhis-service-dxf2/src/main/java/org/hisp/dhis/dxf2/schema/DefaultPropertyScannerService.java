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

import com.google.common.collect.Lists;
import org.hisp.dhis.system.util.ReflectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Default PropertyScannerService implementation that uses Reflection and Jackson annotations
 * for reading in properties.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultPropertyScannerService implements PropertyScannerService
{
    @Override
    public List<Property> getProperties( Class<?> klass )
    {
        return scanClass( klass );
    }

    private List<Property> scanClass( Class<?> klass )
    {
        List<Property> properties = Lists.newArrayList();

        Map<String, ReflectionUtils.PropertyDescriptor> classMap = ReflectionUtils.getJacksonClassMap( klass );

        // for now, just use the reflection utils directly
        for ( ReflectionUtils.PropertyDescriptor descriptor : classMap.values() )
        {
            Property property = new Property( descriptor.getMethod() );
            properties.add( property );

            property.setKlass( descriptor.getClazz() );
            property.setCollection( descriptor.isCollection() );
            property.setIdentifiableObject( descriptor.isIdentifiableObject() );
            property.setName( descriptor.getName() );
            property.setXmlName( descriptor.getXmlName() );
            property.setXmlCollectionName( descriptor.getXmlCollectionName() );
            property.setDescription( descriptor.getDescription() );
        }

        return properties;
    }
}

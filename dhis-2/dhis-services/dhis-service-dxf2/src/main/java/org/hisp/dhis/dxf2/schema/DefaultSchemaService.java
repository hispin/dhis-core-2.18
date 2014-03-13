package org.hisp.dhis.dxf2.schema;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class DefaultSchemaService implements SchemaService
{
    private Map<Class<?>, Schema> classSchemaMap = Maps.newHashMap();

    private Map<String, Schema> singularSchemaMap = Maps.newHashMap();

    @Autowired
    private PropertyScannerService propertyScannerService;

    @Autowired
    private List<SchemaDescriptor> descriptors = Lists.newArrayList();

    @PostConstruct
    public void init()
    {
        for ( SchemaDescriptor descriptor : descriptors )
        {
            Schema schema = descriptor.getSchema();
            schema.setProperties( propertyScannerService.getProperties( schema.getKlass() ) );

            classSchemaMap.put( schema.getKlass(), schema );
            singularSchemaMap.put( schema.getSingular(), schema );
        }
    }

    @Override
    public Schema getSchema( Class<?> klass )
    {
        return classSchemaMap.get( klass );
    }

    @Override
    public Schema getSchemaBySingularName( String name )
    {
        return singularSchemaMap.get( name );
    }

    @Override
    public List<Schema> getSchemas()
    {
        return Lists.newArrayList( classSchemaMap.values() );
    }
}

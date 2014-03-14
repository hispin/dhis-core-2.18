package org.hisp.dhis.dxf2.filter;

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

import com.google.common.collect.Maps;
import org.hisp.dhis.dxf2.filter.ops.EmptyCollectionOp;
import org.hisp.dhis.dxf2.filter.ops.EqOp;
import org.hisp.dhis.dxf2.filter.ops.GtOp;
import org.hisp.dhis.dxf2.filter.ops.GteOp;
import org.hisp.dhis.dxf2.filter.ops.LikeOp;
import org.hisp.dhis.dxf2.filter.ops.LtOp;
import org.hisp.dhis.dxf2.filter.ops.LteOp;
import org.hisp.dhis.dxf2.filter.ops.NeqOp;
import org.hisp.dhis.dxf2.filter.ops.NullOp;
import org.hisp.dhis.dxf2.filter.ops.Op;

import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class OpFactory
{
    protected static Map<String, Class<? extends Op>> register = Maps.newHashMap();

    static
    {
        register( "eq", EqOp.class );
        register( "neq", NeqOp.class );
        register( "like", LikeOp.class );
        register( "gt", GtOp.class );
        register( "gte", GteOp.class );
        register( "lt", LtOp.class );
        register( "lte", LteOp.class );
        register( "null", NullOp.class );
        register( "empty", EmptyCollectionOp.class );
    }

    public static void register( String type, Class<? extends Op> opClass )
    {
        register.put( type.toLowerCase(), opClass );
    }

    public static boolean canCreate( String type )
    {
        return register.containsKey( type.toLowerCase() );
    }

    public static Op create( String type )
    {
        Class<? extends Op> opClass = register.get( type.toLowerCase() );

        try
        {
            return opClass.newInstance();
        }
        catch ( InstantiationException ignored )
        {
        }
        catch ( IllegalAccessException ignored )
        {
        }

        return null;
    }
}

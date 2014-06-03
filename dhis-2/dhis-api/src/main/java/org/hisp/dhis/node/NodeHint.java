package org.hisp.dhis.node;

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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 */

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
final public class NodeHint
{
    public enum Type
    {
        /**
         * If the serializer supports namespacing, this hint can be used to set the namespace.
         */
        NAMESPACE,

        /**
         * If the serializer supports attributes, this hint can be used to hint that this
         * node is a attribute or not.
         */
        ATTRIBUTE,

        /**
         * If the serializer has a notion of wrapping collection (like XML), this hint can be used to
         * turn this feature on or off.
         */
        WRAP_COLLECTION,

        /**
         * If the serializer supports comments, this hint can be used to set a comment for a node.
         */
        COMMENT
    }

    private final Type type;

    private final Object value;

    public NodeHint( Type type, Object value )
    {
        this.type = type;
        this.value = value;
    }

    public Type getType()
    {
        return type;
    }

    public Object getValue()
    {
        return value;
    }
}

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hisp.dhis.node.exception.InvalidTypeException;

import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractNode implements Node
{
    private final String name;

    private final NodeType nodeType;

    private final List<Node> nodes = Lists.newArrayList();

    private final Map<NodeHint.Type, NodeHint> nodeHints = Maps.newHashMap();

    protected AbstractNode( String name, NodeType nodeType )
    {
        this.name = name;
        this.nodeType = nodeType;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public NodeType getType()
    {
        return nodeType;
    }

    @Override
    public <T extends Node> T addNode( T node ) throws InvalidTypeException
    {
        if ( node == null )
        {
            return null;
        }

        nodes.add( node );
        return node;
    }

    @Override
    public List<Node> getNodes()
    {
        return nodes;
    }

    @Override
    public NodeHint addHint( NodeHint.Type type, Object value )
    {
        return addHint( new NodeHint( type, value ) );
    }

    @Override
    public NodeHint addHint( NodeHint nodeHint )
    {
        nodeHints.put( nodeHint.getType(), nodeHint );
        return nodeHint;
    }

    @Override
    public NodeHint getHint( NodeHint.Type type )
    {
        if ( haveHint( type ) )
        {
            return nodeHints.get( type );
        }

        return null;
    }

    @Override
    public boolean haveHint( NodeHint.Type type )
    {
        return nodeHints.containsKey( type );
    }
}

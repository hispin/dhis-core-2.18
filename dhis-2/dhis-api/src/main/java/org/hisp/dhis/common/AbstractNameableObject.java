package org.hisp.dhis.common;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
/**
 * @author Bob Jolliffe
 */
public class AbstractNameableObject
    extends AbstractIdentifiableObject implements NameableObject
{

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 714136796552146362L;

    private final static Log log = LogFactory.getLog( AbstractNameableObject.class );

    /**
     * An alternative name of this Object. Optional but unique.
     */
    protected String alternativeName;

    /**
     * An short name representing this Object. Optional but unique.
     */
    protected String shortName;

    /**
     * An code representing this Object. Optional but unique.
     */
    protected String code;

    /**
     * Description of this Object.
     */
    protected String description;

    public AbstractNameableObject()
    {
    }

    public AbstractNameableObject( int id, String uuid, String name, String alternativeName, String shortName,
        String code, String description )
    {
        super( id, uuid, name );
        this.alternativeName = alternativeName;
        this.shortName = shortName;
        this.code = code;
        this.description = description;
    }

    public String getAlternativeName()
    {
        return alternativeName;
    }

    public void setAlternativeName( String alternativeName )
    {
        this.alternativeName = alternativeName;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    /**
     * Get a map of codes to internal identifiers
     *
     * @param objects the NameableObjects to put in the map
     * @return the map
     */
    public static Map<String, Integer> getCodeMap( Collection<NameableObject> objects )
    {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for ( NameableObject object : objects )
        {
            String code = object.getCode();
            int internalId = object.getId();

            // NOTE: its really not good that duplicate codes are possible
            // Best we can do here is severe log and remove the item
            if ( map.containsKey( code ) )
            {
                log.warn( object.getClass() + ": Duplicate code " + code );
                map.remove( code );
            } else
            {
                map.put( code, internalId );
            }
        }
        return map;
    }
}

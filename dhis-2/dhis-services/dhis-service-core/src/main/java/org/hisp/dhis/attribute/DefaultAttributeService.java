/*
 * Copyright (c) 2004-2010, University of Oslo All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the HISP project nor the names
 * of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission. THIS SOFTWARE IS
 * PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.attribute;

import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author mortenoh
 */
@Transactional
public class DefaultAttributeService
    implements AttributeService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private AttributeStore attributeStore;

    public AttributeStore getAttributeStore()
    {
        return attributeStore;
    }

    public void setAttributeStore( AttributeStore attributeStore )
    {
        this.attributeStore = attributeStore;
    }

    private AttributeOptionStore attributeOptionStore;

    public AttributeOptionStore getAttributeOptionStore()
    {
        return attributeOptionStore;
    }

    public void setAttributeOptionStore( AttributeOptionStore attributeOptionStore )
    {
        this.attributeOptionStore = attributeOptionStore;
    }

    private AttributeValueStore attributeValueStore;

    public AttributeValueStore getAttributeValueStore()
    {
        return attributeValueStore;
    }

    public void setAttributeValueStore( AttributeValueStore attributeValueStore )
    {
        this.attributeValueStore = attributeValueStore;
    }

    // -------------------------------------------------------------------------
    // Attribute implementation
    // -------------------------------------------------------------------------

    @Override
    public void addAttribute( Attribute attribute )
    {
        attributeStore.save( attribute );
    }

    @Override
    public void updateAttribute( Attribute attribute )
    {
        attributeStore.update( attribute );
    }

    @Override
    public void deleteAttribute( Attribute attribute )
    {
        attributeStore.delete( attribute );
    }

    @Override
    public Attribute getAttribute( int id )
    {
        return attributeStore.get( id );
    }

    @Override
    public Set<Attribute> getAllAttributes()
    {
        return new HashSet<Attribute>( attributeStore.getAll() );
    }

    // -------------------------------------------------------------------------
    // AttributeOption implementation
    // -------------------------------------------------------------------------
    
    @Override
    public void addAttributeOption( AttributeOption attributeOption )
    {
        attributeOptionStore.save( attributeOption );
    }

    @Override
    public void updateAttributeOption( AttributeOption attributeOption )
    {
        attributeOptionStore.update( attributeOption );
    }

    @Override
    public void deleteAttributeOption( AttributeOption attributeOption )
    {
        attributeOptionStore.delete( attributeOption );
    }

    @Override
    public AttributeOption getAttributeOption( int id )
    {
        return attributeOptionStore.get( id );
    }

    @Override
    public Set<AttributeOption> getAllAttributeOptions()
    {
        return new HashSet<AttributeOption>( attributeOptionStore.getAll() );
    }

    // -------------------------------------------------------------------------
    // AttributeValue implementation
    // -------------------------------------------------------------------------
    
    @Override
    public void addAttributeValue( AttributeValue attributeValue )
    {
        attributeValueStore.save( attributeValue );
    }

    @Override
    public void updateAttributeValue( AttributeValue attributeValue )
    {
        attributeValueStore.update( attributeValue );
    }

    @Override
    public void deleteAttributeValue( AttributeValue attributeValue )
    {
        attributeValueStore.delete( attributeValue );
    }

    @Override
    public AttributeValue getAttributeValue( int id )
    {
        return attributeValueStore.get( id );
    }

    @Override
    public Set<AttributeValue> getAllAttributeValues()
    {
        return new HashSet<AttributeValue>( attributeValueStore.getAll() );
    }

}

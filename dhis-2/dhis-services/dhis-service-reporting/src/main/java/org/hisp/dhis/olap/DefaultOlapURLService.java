package org.hisp.dhis.olap;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Transactional
public class DefaultOlapURLService
    implements OlapURLService
{
    private final static String MAP_FORM = "map.html";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private GenericIdentifiableObjectStore<OlapURL> olapURLStore;

    public void setOlapURLStore( GenericIdentifiableObjectStore<OlapURL> olapURLStore )
    {
        this.olapURLStore = olapURLStore;
    }
    
    // -------------------------------------------------------------------------
    // OlapURLService implementation
    // -------------------------------------------------------------------------

    public String getMapURL( String application, String cube, String indicator, 
        String organisationUnit, String year, String month, String level )
    {
        StringBuffer url = new StringBuffer();

        url.append( application ).append( "/" + MAP_FORM );
        url.append( "?cube=" ).append( cube );
        url.append( "&columns=[Indicator].[" ).append( indicator ).append( "]" );
        url.append( "&rows=[Location].[" ).append( organisationUnit ).append( "]," );
        url.append( "[Period].[" ).append( year ).append( "].[" ).append( month ).append( "]" );
        url.append( "&aggregate=[Location].[" ).append( level ).append( "]" );
        url.append( "&filter=&slicer=" );
        
        return url.toString();
    }
    
    public int saveOlapURL( OlapURL olapURL )
    {
        return olapURLStore.save( olapURL );
    }
    
    public void updateOlapURL( OlapURL olapURL )
    {
        olapURLStore.update( olapURL );
    }
    
    public OlapURL getOlapURL( int id )
    {
        return olapURLStore.get( id );
    }
    
    public Collection<OlapURL> getOlapURLs( Collection<Integer> identifiers )
    {
        if ( identifiers == null )
        {
            return getAllOlapURLs();
        }
        
        Collection<OlapURL> urls = new ArrayList<OlapURL>();
        
        for ( Integer id : identifiers )
        {
            urls.add( getOlapURL( id ) );
        }
        
        return urls;
    }
    
    public void deleteOlapURL( OlapURL olapURL )
    {
        olapURLStore.delete( olapURL );
    }
    
    public Collection<OlapURL> getAllOlapURLs()
    {
        return olapURLStore.getAll();
    }    

    public OlapURL getOlapURLByName( String name )
    {
        return olapURLStore.getByName( name );
    }
}

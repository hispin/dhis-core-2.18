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

var dhis2 = dhis2 || {};
dhis2['util'] = dhis2['util'] || {};

/**
 * Creates namespace object based on path
 * 
 * @param path {String} The path of the namespace, i.e. 'a.b.c'
 * 
 * @returns {object} Namespace object
 */
dhis2.util.namespace = function( path )
{
    var parts = path.split( '.' );
    var parent = window;
    var currentPart = '';

    for ( var i = 0, length = parts.length; i < length; i++ )
    {
        currentPart = parts[i];
        parent[currentPart] = parent[currentPart] || {};
        parent = parent[currentPart];
    }

    return parent;
};

/**
 * adds ':containsNoCase' to filtering.
 * $(sel).find(':containsNC(key)').doSomething();
 */
$.expr[":"].containsNC = function( el, i, m )
{
    // http://www.west-wind.com/weblog/posts/2008/Oct/24/Using-jQuery-to-search-Content-and-creating-custom-Selector-Filters
    var search = m[3];

    if ( !search )
        return false;

    return eval( '/' + search + '/i' ).test( $( el ).text() );
};

/**
 * Returns an array of the keys in a given object. Will use ES5 Object.keys() if
 * available, if not it will provide a pure javascript implementation.
 * 
 * @returns array of keys
 */
if ( !Object.keys )
{
    Object.keys = function( obj )
    {
        var keys = new Array();
        for ( k in obj )
            if ( obj.hasOwnProperty( k ) )
                keys.push( k );
        return keys;
    };
}

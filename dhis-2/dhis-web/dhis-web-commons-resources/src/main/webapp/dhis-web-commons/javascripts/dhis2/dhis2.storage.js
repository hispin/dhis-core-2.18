"use strict";

/*
 * Copyright (c) 2004-2013, University of Oslo
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

dhis2.util.namespace( 'dhis2.storage' );

dhis2.storage.Store = function ( options ) {
    var self = this;

    if ( !(this instanceof dhis2.storage.Store) ) {
        return new dhis2.storage.Store( options );
    }

    if ( typeof options.name === 'undefined' ) {
        throw Error( 'Constructor needs a valid database name as a argument' );
    }

    if ( typeof options.objectStores === 'undefined' || !$.isArray( options.objectStores ) || options.objectStores.length == 0 ) {
        throw Error( 'Constructor needs a valid objectStores array as a argument' );
    }

    options.keyPath = options.keyPath || 'id';
    options.version = options.version || '1';

    if ( !JSON ) throw 'JSON unavailable! Include http://www.json.org/json2.js to fix.';

    var Adapter;

    for ( var i = 0, len = options.adapters.length; i < len; i++ ) {
        if ( dhis2.storage.Store.verifyAdapter( options.adapters[i] ) && options.adapters[i].isSupported() ) {
            Adapter = options.adapters[i];
            break;
        }
    }

    if ( !Adapter ) throw 'No valid adapter.';

    var adapter = new Adapter( options );

    Object.defineProperty( this, 'adapter', {
        value: adapter
    } );

    $.each( dhis2.storage.Store.adapterMethods, function ( idx, item ) {
        var descriptor = Object.getOwnPropertyDescriptor( Adapter, item ) || Object.getOwnPropertyDescriptor( Adapter.prototype, item );
        Object.defineProperty( self, item, descriptor );
    } );

    $.each( dhis2.storage.Store.adapterProperties, function ( idx, item ) {
        var descriptor = Object.getOwnPropertyDescriptor( adapter, item );
        Object.defineProperty( self, item, descriptor );
    } );

    if ( typeof adapter.customApi !== 'undefined' ) {
        $.each( adapter.customApi, function ( idx, item ) {
            self[item] = adapter[item];
        } );
    }
};

dhis2.storage.Store.adapterMethods = "open set setAll get getAll getKeys count contains clear close delete destroy".split( ' ' );
dhis2.storage.Store.adapterProperties = "name version objectStoreNames keyPath".split( ' ' );

dhis2.storage.Store.verifyAdapter = function ( Adapter ) {
    var failed = [];

    if ( typeof Adapter === 'undefined' ) {
        return false;
    }

    $.each( dhis2.storage.Store.adapterMethods, function ( idx, item ) {
        // should probably go up the prototype chain here
        var descriptor = Object.getOwnPropertyDescriptor( Adapter, item ) || Object.getOwnPropertyDescriptor( Adapter.prototype, item );

        if ( typeof descriptor.value !== 'function' ) {
            failed.push( item );
        }
    } );

    return failed.length === 0;
};

/*
var STUDENT_STORE = 'students';
var COURSE_STORE = 'courses';

var store1 = new dhis2.storage.Store( {
    name: 'store',
    adapters: [ dhis2.storage.InMemoryAdapter ],
    objectStores: [ STUDENT_STORE ]
} );

var store2 = new dhis2.storage.Store( {
    name: 'store',
    adapters: [ dhis2.storage.InMemoryAdapter ],
    objectStores: [ COURSE_STORE ]
} );

var students = [
    {'id': 'abc1', name: 'Morten 1'},
    {'id': 'abc2', name: 'Morten 2'},
    {'id': 'abc3', name: 'Morten 3'},
    {'id': 'abc4', name: 'Morten 4'},
];

var courses = [
    {'id': 'abc1', name: 'Morten 1'},
    {'id': 'abc2', name: 'Morten 2'},
    {'id': 'abc3', name: 'Morten 3'},
    {'id': 'abc4', name: 'Morten 4'},
];

store1.open().done( function () {
    store1.setAll( STUDENT_STORE, students ).then( function () {
        store1.count( STUDENT_STORE ).done( function ( n ) {
            console.log( n );
        } );
    } );
} );

store2.open().done( function () {
    store2.setAll( COURSE_STORE, courses ).then( function () {
        store2.count( COURSE_STORE ).done( function ( n ) {
            console.log( n );
        } );
    } );
} );
*/
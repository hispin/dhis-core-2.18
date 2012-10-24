/*
 * Copyright (c) 2004-2012, University of Oslo
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
dhis2['storage'] = dhis2['storage'] || {};

dhis2.storage.FormManager = function ( args ) {
    this._organisationUnits = undefined;
    this._forms = undefined;
    this._id = _.uniqueId('formManager');
};

dhis2.storage.FormManager.prototype.getMetaData = function () {
    return $.ajax({
        url      : '../api/currentUser/forms',
        dataType : 'json'
    }).success(function ( data ) {
        localStorage['organisationUnits'] = JSON.stringify(data.organisationUnits);
        localStorage['forms'] = JSON.stringify(data.forms);
    });
};

dhis2.storage.FormManager.prototype.organisationUnits = function () {
    if ( this._organisationUnits === undefined ) {
        this._organisationUnits = JSON.parse(localStorage['organisationUnits']);
    }

    return this._organisationUnits;
};

dhis2.storage.FormManager.prototype.organisationUnit = function (id) {
    return this.organisationUnits()[id];
};

dhis2.storage.FormManager.prototype.dataSets = function (id) {
    var ou = this.organisationUnits()[id];
    return ou.dataSets;
};

dhis2.storage.FormManager.prototype.forms = function () {
    if( this._forms === undefined ) {
        this._forms = JSON.parse( localStorage['forms'] );
    }

    return this._forms;
};

dhis2.storage.FormManager.prototype.form = function ( id ) {
    return this.forms()[id]
};

dhis2.storage.FormManager.prototype.dataValueSets = function() {
    var dataValueSets = localStorage['dataValueSets'];

    if(dataValueSets !== undefined )
    {
        dataValueSets = JSON.parse( dataValueSets );
    } else {
        dataValueSets = {};
    }

    return dataValueSets;
};

dhis2.storage.makeUploadDataValueSetRequest = function( dataValueSet ) {
    return $.ajax({
        url: '../api/dataValueSets',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify( dataValueSet )
    });
};

dhis2.storage.getUniqueKey = function( dataValueSet ) {
    return dataValueSet.orgUnit + '-' + dataValueSet.dataSet + '-' + dataValueSet.period;
};

dhis2.storage.FormManager.prototype.getDataValueSetValues = function( dataValueSet ) {
    var dataValueSets = this.dataValueSets();
    return dataValueSets[ dhis2.storage.getUniqueKey( dataValueSet )];
};

dhis2.storage.FormManager.prototype.saveDataValueSet = function( dataValueSet ) {
    var dataValueSets = this.dataValueSets();

    return dhis2.storage.makeUploadDataValueSetRequest( dataValueSet ).error(function() {
        // add to local dataValueSets
        dataValueSets[dhis2.storage.getUniqueKey(dataValueSet)] = dataValueSet;
        localStorage['dataValueSets'] = JSON.stringify( dataValueSets );
    });
};

dhis2.storage.FormManager.prototype.uploadDataValueSets = function() {
    var dataValueSets = this.dataValueSets();
    var deferreds = [];

    _.each(dataValueSets, function( value, key ) {
        deferreds.push(dhis2.storage.makeUploadDataValueSetRequest( value ).success(function() {
                delete dataValueSets[key];
            })
        );
    });

    return $.when.apply( null, deferreds ).always(function() {
        localStorage['dataValueSets'] = JSON.stringify( dataValueSets );
    });
};

// global storage manager instance
(function () {
    window.fm = new dhis2.storage.FormManager();
}).call();

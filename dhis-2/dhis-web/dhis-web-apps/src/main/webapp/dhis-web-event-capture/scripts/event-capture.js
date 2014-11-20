
dhis2.util.namespace('dhis2.ec');

// whether current user has any organisation units
dhis2.ec.emptyOrganisationUnits = false;

var EC_STORE_NAME = "dhis2ec";
var i18n_no_orgunits = 'No organisation unit attached to current user, no data entry possible';
var i18n_offline_notification = 'You are offline, data will be stored locally';
var i18n_online_notification = 'You are online';
var i18n_need_to_sync_notification = 'There is data stored locally, please upload to server';
var i18n_sync_now = 'Upload';
var i18n_sync_success = 'Upload to server was successful';
var i18n_sync_failed = 'Upload to server failed, please try again later';
var i18n_uploading_data_notification = 'Uploading locally stored data to the server';

var PROGRAMS_METADATA = 'EVENT_PROGRAMS';

var EVENT_VALUES = 'EVENT_VALUES';

//var ecDAO = {};

dhis2.ec.store = null;
dhis2.ec.memoryOnly = $('html').hasClass('ie7') || $('html').hasClass('ie8');
var adapters = [];    
if( dhis2.ec.memoryOnly ) {
    adapters = [ dhis2.storage.InMemoryAdapter ];
} else {
    adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ];
}

dhis2.ec.store = new dhis2.storage.Store({
    name: EC_STORE_NAME,
    objectStores: [
        {
            name: 'ecPrograms',
            adapters: adapters
        },
        {
            name: 'programStages',
            adapters: adapters
        },
        {
            name: 'geoJsons',
            adapters: adapters
        },
        {
            name: 'optionSets',
            adapters: adapters
        },
        {
            name: 'events',
            adapters: adapters
        }
    ]        
});

(function($) {
    $.safeEach = function(arr, fn)
    {
        if (arr)
        {
            $.each(arr, fn);
        }
    };
})(jQuery);


/**
 * Page init. The order of events is:
 *
 * 1. Load ouwt 2. Load meta-data (and notify ouwt) 3. Check and potentially
 * download updated forms from server
 */
$(document).ready(function()
{
    downloadMetaData();
    
    $.ajaxSetup({
        type: 'POST',
        cache: false
    });

    $('#loaderSpan').show();

    $('#orgUnitTree').one('ouwtLoaded', function()
    { 
        downloadMetaData();
        
    });
    
    $(document).bind('dhis2.online', function(event, loggedIn)
    {
        if (loggedIn)
        {   
            var OfflineStorageService = angular.element('body').injector().get('OfflineStorageService');
            
            OfflineStorageService.hasLocalData().then(function(localData){
                if(localData){
                    var message = i18n_need_to_sync_notification
                        + ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

                    setHeaderMessage(message);

                    $('#sync_button').bind('click', uploadLocalData);
                }
                else{
                    if (dhis2.ec.emptyOrganisationUnits) {
                        setHeaderMessage(i18n_no_orgunits);
                    }
                    else {
                        setHeaderDelayMessage(i18n_online_notification);
                    }
                }
            });
        }
        else
        {
            var form = [
                '<form style="display:inline;">',
                '<label for="username">Username</label>',
                '<input name="username" id="username" type="text" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<label for="password">Password</label>',
                '<input name="password" id="password" type="password" style="width: 70px; margin-left: 10px; margin-right: 10px" size="10"/>',
                '<button id="login_button" type="button">Login</button>',
                '</form>'
            ].join('');

            setHeaderMessage(form);
            ajax_login();
        }
    });

    $(document).bind('dhis2.offline', function()
    {
        if (dhis2.ec.emptyOrganisationUnits) {
            setHeaderMessage(i18n_no_orgunits);
        }
        else {
            setHeaderMessage(i18n_offline_notification);
        }
    });
   
    dhis2.availability.startAvailabilityCheck();
    
});

function ajax_login()
{
    $('#login_button').bind('click', function()
    {
        var username = $('#username').val();
        var password = $('#password').val();

        $.post('../dhis-web-commons-security/login.action', {
            'j_username': username,
            'j_password': password
        }).success(function()
        {
            var ret = dhis2.availability.syncCheckAvailability();

            if (!ret)
            {
                alert(i18n_ajax_login_failed);
            }
        });
    });
}

function downloadMetaData(){    
    
    var def = $.Deferred();
    var promise = def.promise();
    
    promise = promise.then( dhis2.ec.store.open );
    promise = promise.then( getUserProfile );
    promise = promise.then( getCalendarSetting );
    promise = promise.then( getLoginDetails );
    promise = promise.then( getOrgUnitLevels );
    //promise = promise.then( getGeoJsonsByLevel );
    promise = promise.then( getMetaPrograms );     
    promise = promise.then( getPrograms );     
    promise = promise.then( getProgramStages );
    promise = promise.then( getOptionSets );
    promise.done( function() {           
        selection.responseReceived();
    });         

    def.resolve();
}

function getUserProfile()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/me/profile',
        type: 'GET'
    }).done( function(response) {            
        localStorage['USER_PROFILE'] = JSON.stringify(response);           
        def.resolve();
    });
    
    return def.promise(); 
}

function getCalendarSetting()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/systemSettings?key=keyCalendar&key=keyDateFormat',
        type: 'GET'
    }).done(function(response) {
        localStorage['CALENDAR_SETTING'] = JSON.stringify(response);
        def.resolve();
    });

    return def.promise();
}

function getLoginDetails()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/me',
        type: 'GET'
    }).done( function(response) {            
        localStorage['LOGIN_DETAILS'] = JSON.stringify(response);           
        def.resolve();
    });
    
    return def.promise(); 
}

function getOrgUnitLevels()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/organisationUnitLevels.json',
        type: 'GET',
        data:'filter=level:gt:1&fields=id,name,level&paging=false'
    }).done( function(response) {      
        var ouLevels = [];
        if(response.organisationUnitLevels){
            ouLevels = _.sortBy(response.organisationUnitLevels, function(ouLevel){
                return ouLevel.level;
            });
        }
        def.resolve( ouLevels );
    });
    
    return def.promise();    
}

function getGeoJsonsByLevel( ouLevels )
{
    if( !ouLevels ){
        return;
    }

    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();

    _.each( _.values( ouLevels ), function ( ouLevel ) {
        if(ouLevel.level){
            build = build.then(function() {
                var d = $.Deferred();
                var p = d.promise();
                dhis2.ec.store.get('geoJsons', ouLevel.level).done(function(obj) {
                    if(!obj) {
                        promise = promise.then( getGeoJson( ouLevel.level ) );
                    }
                    d.resolve();
                });

                return p;
            });
        }        
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve();
        } );
    });

    builder.resolve();

    return mainPromise;
}

function getGeoJson( level )
{
    return function() {
        return $.ajax( {
            url: '../api/organisationUnits.geojson',
            type: 'GET',
            data: 'level=' + level
        }).done( function( response ){
            
            var geojson = {};
            geojson = response;
            geojson.id = level;            
            dhis2.ec.store.set( 'geoJsons', geojson );
        });
    };
}

function getMetaPrograms()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/programs.json',
        type: 'GET',
        data:'filter=type:eq:3&paging=false&fields=id,name,version,programStages[id,version,programStageSections[id],programStageDataElements[dataElement[id,optionSet[id,version]]]]'
    }).done( function(response) {          
        var programs = [];
        _.each( _.values( response.programs ), function ( program ) { 
            programs.push(program);            
        });
        
        def.resolve( programs );
    });
    
    return def.promise(); 
}

function getPrograms( programs )
{
    if( !programs ){
        return;
    }
    
    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();

    _.each( _.values( programs ), function ( program ) {
        build = build.then(function() {
            var d = $.Deferred();
            var p = d.promise();
            dhis2.ec.store.get('ecPrograms', program.id).done(function(obj) {
                if(!obj || obj.version !== program.version) {
                    promise = promise.then( getProgram( program.id ) );
                }

                d.resolve();
            });

            return p;
        });
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    });

    builder.resolve();

    return mainPromise;
}

function getProgram( id )
{
    return function() {
        return $.ajax( {
            url: '../api/programs.json?filter=id:eq:' + id +'&fields=id,name,version,dataEntryMethod,dateOfEnrollmentDescription,dateOfIncidentDescription,displayIncidentDate,ignoreOverdueEvents,organisationUnits[id,name],programStages[id,name,version]',
            type: 'GET'
        }).done( function( response ){
            
            _.each( _.values( response.programs ), function ( program ) { 
                
                var ou = {};
                _.each(_.values( program.organisationUnits), function(o){
                    ou[o.id] = o.name;
                });

                program.organisationUnits = ou;

                var ur = {};
                _.each(_.values( program.userRoles), function(u){
                    ur[u.id] = u.name;
                });

                program.userRoles = ur;

                dhis2.ec.store.set( 'ecPrograms', program );

            });         
        });
    };
}

function getProgramStages( programs )
{
    if( !programs ){
        return;
    }
    
    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();

    _.each( _.values( programs ), function ( program ) {

        build = build.then(function() {
            var d = $.Deferred();
            var p = d.promise();
            dhis2.ec.store.get('programStages', program.programStages[0].id).done(function(obj) {
                if(!obj || obj.version !== program.programStages[0].version) {
                    promise = promise.then( getProgramStage( program.programStages[0].id ) );
                }

                d.resolve();
            });

            return p;
        });
              
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    });

    builder.resolve();

    return mainPromise;    
}

function getProgramStage( id )
{
    return function() {
        return $.ajax( {
            url: '../api/programStages.json?filter=id:eq:' + id +'&fields=id,name,version,description,reportDateDescription,captureCoordinates,dataEntryForm,minDaysFromStart,repeatable,preGenerateUID,programStageSections[id,name,programStageDataElements[dataElement[id]]],programStageDataElements[displayInReports,sortOrder,allowProvidedElsewhere,allowFutureDate,compulsory,dataElement[id,name,type,formName,optionSet[id]]]',
            type: 'GET'
        }).done( function( response ){            
            _.each( _.values( response.programStages ), function( programStage ) {                
                dhis2.ec.store.set( 'programStages', programStage );
            });
        });
    };
}

function getOptionSets( programs )
{
    if( !programs ){
        return;
    }
    
    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();    

    _.each( _.values( programs ), function ( program ) {
        _.each(_.values( program.programStages[0].programStageDataElements), function(prStDe){
            if( prStDe.dataElement.optionSet && prStDe.dataElement.optionSet.id ){
                build = build.then(function() {
                    var d = $.Deferred();
                    var p = d.promise();
                    dhis2.ec.store.get('optionSets', prStDe.dataElement.optionSet.id).done(function(obj) {                    
                        if(!obj || obj.version !== prStDe.dataElement.optionSet.version) {
                            promise = promise.then( getOptionSet( prStDe.dataElement.optionSet.id ) );
                        }
                        d.resolve();
                    });

                    return p;
                });
            }            
        });                      
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    });

    builder.resolve();

    return mainPromise;    
}

function getOptionSet( id )
{
    return function() {
        return $.ajax( {
            url: '../api/optionSets.json?filter=id:eq:' + id +'&fields=id,name,version,options[id,name,code]',
            type: 'GET'
        }).done( function( response ){            
            _.each( _.values( response.optionSets ), function( optionSet ) {                
                dhis2.ec.store.set( 'optionSets', optionSet );
            });
        });
    };
}

function uploadLocalData()
{
    var OfflineStorageService = angular.element('body').injector().get('OfflineStorageService');
    setHeaderWaitMessage(i18n_uploading_data_notification);
     
    OfflineStorageService.uploadLocalData().then(function(){
        dhis2.ec.store.removeAll( 'events' );
        log( 'Successfully uploaded local events' );      
        setHeaderDelayMessage( i18n_sync_success );
        selection.responseReceived(); //notify angular
    });
}


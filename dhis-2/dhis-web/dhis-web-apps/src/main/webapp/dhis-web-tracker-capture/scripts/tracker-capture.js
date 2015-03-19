dhis2.util.namespace('dhis2.tc');

// whether current user has any organisation units
dhis2.tc.emptyOrganisationUnits = false;

var i18n_no_orgunits = 'No organisation unit attached to current user, no data entry possible';
var i18n_offline_notification = 'You are offline, data will be stored locally';
var i18n_online_notification = 'You are online';
var i18n_need_to_sync_notification = 'There is data stored locally, please upload to server';
var i18n_sync_now = 'Upload';
var i18n_sync_success = 'Upload to server was successful';
var i18n_sync_failed = 'Upload to server failed, please try again later';
var i18n_uploading_data_notification = 'Uploading locally stored data to the server';

var optionSetsInPromise = [];
var attributesInPromise = [];

dhis2.tc.store = null;
dhis2.tc.memoryOnly = $('html').hasClass('ie7') || $('html').hasClass('ie8');
var adapters = [];    
if( dhis2.tc.memoryOnly ) {
    adapters = [ dhis2.storage.InMemoryAdapter ];
} else {
    adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ];
}

dhis2.tc.store = new dhis2.storage.Store({
    name: 'dhis2tc',
    adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
    objectStores: ['programs', 'programStages', 'trackedEntities', 'trackedEntityForms', 'attributes', 'relationshipTypes', 'optionSets']      
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
    $.ajaxSetup({
        type: 'POST',
        cache: false
    });

    $('#loaderSpan').show();
});

$(document).bind('dhis2.online', function(event, loggedIn)
{
    if (loggedIn)
    {
        if (dhis2.tc.emptyOrganisationUnits) {
            setHeaderMessage(i18n_no_orgunits);
        }
        else {
            setHeaderDelayMessage(i18n_online_notification);
        }
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
    if (dhis2.tc.emptyOrganisationUnits) {
        setHeaderMessage(i18n_no_orgunits);
    }
    else {
        setHeaderMessage(i18n_offline_notification);
    }
});

$(".select-dropdown-button").on('click', function(e) {
    $("#selectDropDown").width($("#selectDropDownParent").width());
    e.stopPropagation();
    $("#selectDropDown").dropdown('toggle');
});  

$(".select-dropdown-caret").on('click', function(e) {
    $("#selectDropDown").width($("#selectDropDownParent").width());
    e.stopPropagation();
    $("#selectDropDown").dropdown('toggle');
}); 

$(".search-dropdown-button").on('click', function() {
    $("#searchDropDown").width($("#searchDropDownParent").width());
}); 

$('#searchDropDown').on('click', "[data-stop-propagation]", function(e) {
    e.stopPropagation();
});

//stop date picker's event bubling
$(document).on('click.dropdown touchstart.dropdown.data-api', '#ui-datepicker-div', function (e) { 
    e.stopPropagation(); 
});

$(window).resize(function() {
    $("#selectDropDown").width($("#selectDropDownParent").width());
    $("#searchDropDown").width($("#searchDropDownParent").width());
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

// -----------------------------------------------------------------------------
// Metadata downloading
// -----------------------------------------------------------------------------

function downloadMetaData()
{
    console.log('Loading required meta-data');
    var def = $.Deferred();
    var promise = def.promise();

    promise = promise.then( dhis2.tc.store.open );    
    promise = promise.then( getCalendarSetting );
    promise = promise.then( getRelationships );       
    promise = promise.then( getTrackedEntities );
    promise = promise.then( getMetaPrograms );     
    promise = promise.then( getPrograms );     
    promise = promise.then( getProgramStages );    
    promise = promise.then( getOptionSetsForDataElements );
    promise = promise.then( getTrackedEntityAttributes );
    promise = promise.then( getOptionSetsForAttributes );
    promise = promise.then( getMetaTrackedEntityForms );
    promise = promise.then( getTrackedEntityForms );        
    promise.done(function() {
        
        //Enable ou selection after meta-data has downloaded
        $( "#orgUnitTree" ).removeClass( "disable-clicks" );
        
        console.log( 'Finished loading meta-data' );        
        selection.responseReceived(); 
    });

    def.resolve();
    
}


function getCalendarSetting()
{
    if(localStorage['CALENDAR_SETTING']){
       return; 
    }
    
    var def = $.Deferred();

    $.ajax({
        url: '../api/systemSettings?key=keyCalendar&key=keyDateFormat',
        type: 'GET'
    }).done(function(response) {
        localStorage['CALENDAR_SETTING'] = JSON.stringify(response);
        def.resolve();
    }).fail(function(){
        def.resolve();
    });

    return def.promise();
}

function getRelationships()
{    
    dhis2.tc.store.getKeys( 'relationshipTypes').done(function(res){        
        if(res.length > 0){
            return;
        }
        var def = $.Deferred();

        $.ajax({
            url: '../api/relationshipTypes.json?paging=false&fields=id,name,aIsToB,bIsToA,displayName',
            type: 'GET'
        }).done(function(response) {        
            dhis2.tc.store.setAll( 'relationshipTypes', response.relationshipTypes );
            def.resolve();        
        }).fail(function(){
            def.resolve();
        });

        return def.promise();
    });    
}

function getTrackedEntities()
{
    dhis2.tc.store.getKeys('trackedEntities').done(function(res){
        if(res.length > 0){
            return;
        }
        
        var def = $.Deferred();

        $.ajax({
            url: '../api/trackedEntities',
            type: 'GET',
            data: 'viewClass=detailed&paging=false'
        }).done(function(response) {
            dhis2.tc.store.setAll( 'trackedEntities', response.trackedEntities );        
            def.resolve();
        }).fail(function(){
            def.resolve();
        });

        return def.promise();
    });    
}

function getMetaPrograms()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/programs.json',
        type: 'GET',
        data:'filter=type:eq:1&paging=false&fields=id,version,programTrackedEntityAttributes[trackedEntityAttribute[id,optionSet[id,version]]],programStages[id,version,programStageDataElements[dataElement[optionSet[id,version]]]]'
    }).done( function(response) {          
        var programs = [];
        _.each( _.values( response.programs ), function ( program ) { 
            programs.push(program);
        });
        
        def.resolve( programs );
    }).fail(function(){
        def.resolve( null );
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
            dhis2.tc.store.get('programs', program.id).done(function(obj) {
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
    }).fail(function(){
        mainDef.resolve( null );
    });

    builder.resolve();

    return mainPromise;
}

function getProgram( id )
{
    return function() {
        return $.ajax( {
            url: '../api/programs.json',
            type: 'GET',
            data: 'paging=false&filter=id:eq:' + id +'&fields=id,name,type,version,dataEntryMethod,dateOfEnrollmentDescription,dateOfIncidentDescription,displayIncidentDate,ignoreOverdueEvents,selectEnrollmentDatesInFuture,selectIncidentDatesInFuture,onlyEnrollOnce,externalAccess,displayOnAllOrgunit,registration,relationshipText,relationshipFromA,relatedProgram[id,name],relationshipType[id,name],trackedEntity[id,name,description],userRoles[id,name],organisationUnits[id,name],programStages[id,name,sortOrder,version,minDaysFromStart,reportDateDescription,repeatable,autoGenerateEvent,generatedByEnrollmentDate],programTrackedEntityAttributes[displayInList,mandatory,allowFutureDate,trackedEntityAttribute[id,unique]]'
        }).done( function( response ){
            
            _.each( _.values( response.programs ), function ( program ) { 
                
                var ou = {};
                if(program.organisationUnits){
                    _.each(_.values( program.organisationUnits), function(o){
                        ou[o.id] = o.name;
                    });
                }

                program.organisationUnits = ou;

                var ur = {};
                
                if(program.userRoles){
                    _.each(_.values( program.userRoles), function(u){
                        ur[u.id] = u.name;
                    });
                }                

                program.userRoles = ur;

                dhis2.tc.store.set( 'programs', program );

            });         
        });
    };
}

function getTrackedEntityAttributes( programs )
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
        
        if(program.programTrackedEntityAttributes){
            _.each(_.values(program.programTrackedEntityAttributes), function(teAttribute){
                build = build.then(function() {
                    var d = $.Deferred();
                    var p = d.promise();
                    dhis2.tc.store.get('attributes', teAttribute.trackedEntityAttribute.id).done(function(obj) {
                        if(!obj || obj.version !== teAttribute.trackedEntityAttribute.version) {
                            promise = promise.then( getAttribute( teAttribute.trackedEntityAttribute.id ) );
                        }
                        d.resolve();
                    });
                    return p;
                });            
            });
        }                             
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    }).fail(function(){
        mainDef.resolve( null );
    });

    builder.resolve();

    return mainPromise;    
}

function getAttribute( id )
{
    return function() {
        return $.ajax( {
            url: '../api/trackedEntityAttributes.json',
            type: 'GET',
            data: 'filter=id:eq:' + id +'&fields=id,name,code,version,description,valueType,confidential,inherit,sortOrderInVisitSchedule,sortOrderInListNoProgram,displayOnVisitSchedule,displayInListNoProgram,unique,optionSet[id,version]'
        }).done( function( response ){            
            _.each( _.values( response.trackedEntityAttributes ), function( teAttribute ) {
                dhis2.tc.store.set( 'attributes', teAttribute );
            });
        });
    };
}


function getOptionSetsForAttributes( programs )
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
        
        if(program.programTrackedEntityAttributes){
            _.each(_.values( program.programTrackedEntityAttributes), function( teAttribute) {           
                if( teAttribute.trackedEntityAttribute.optionSet && teAttribute.trackedEntityAttribute.optionSet.id ){
                    build = build.then(function() {
                        var d = $.Deferred();
                        var p = d.promise();
                        dhis2.tc.store.get('optionSets', teAttribute.optionSet.id).done(function(obj) {                            
                            if((!obj || obj.version !== teAttribute.optionSet.version) && optionSetsInPromise.indexOf(teAttribute.optionSet.id) === -1) {                                
                                optionSetsInPromise.push(teAttribute.optionSet.id);
                                promise = promise.then( getOptionSet( teAttribute.optionSet.id ) );
                            }
                            d.resolve();
                        });

                        return p;
                    });
                }            
            });
        }                                      
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    }).fail(function(){
        mainDef.resolve( null );
    });

    builder.resolve();

    return mainPromise;    
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
        
        if(program.programStages){
            _.each(_.values(program.programStages), function(programStage){
                build = build.then(function() {
                    var d = $.Deferred();
                    var p = d.promise();
                    dhis2.tc.store.get('programStages', programStage.id).done(function(obj) {
                        if(!obj || obj.version !== programStage.version) {
                            promise = promise.then( getProgramStage( programStage.id ) );
                        }
                        d.resolve();
                    });
                    return p;
                });            
            });
        }                             
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    }).fail(function(){
        mainDef.resolve( null );
    });

    builder.resolve();

    return mainPromise;    
}

function getProgramStage( id )
{
    return function() {
        return $.ajax( {
            url: '../api/programStages.json',
            type: 'GET',
            data: 'filter=id:eq:' + id +'&fields=id,name,sortOrder,version,dataEntryForm,captureCoordinates,blockEntryForm,autoGenerateEvent,generatedByEnrollmentDate,reportDateDescription,minDaysFromStart,repeatable,programStageSections[id,name,programStageDataElements[dataElement[id]]],programStageDataElements[displayInReports,allowProvidedElsewhere,allowFutureDate,compulsory,dataElement[id,code,name,formName,type,optionSet[id]]]'
        }).done( function( response ){            
            _.each( _.values( response.programStages ), function( programStage ) {
                dhis2.tc.store.set( 'programStages', programStage );
            });
        });
    };
}

function getOptionSetsForDataElements( programs )
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
        
        if(program.programStages){
            _.each(_.values( program.programStages), function( programStage) {
                
                if(programStage.programStageDataElements){
                    _.each(_.values( programStage.programStageDataElements), function(prStDe){            
                        if( prStDe.dataElement.optionSet && prStDe.dataElement.optionSet.id ){
                            build = build.then(function() {
                                var d = $.Deferred();
                                var p = d.promise();
                                dhis2.tc.store.get('optionSets', prStDe.dataElement.optionSet.id).done(function(obj) {                                    
                                    if( (!obj || obj.version !== prStDe.dataElement.optionSet.version) && optionSetsInPromise.indexOf(prStDe.dataElement.optionSet.id) === -1) {                                
                                        optionSetsInPromise.push( prStDe.dataElement.optionSet.id );
                                        promise = promise.then( getOptionSet( prStDe.dataElement.optionSet.id ) );
                                    }
                                    d.resolve();
                                });

                                return p;
                            });
                        }            
                    });
                }                
            });
        }                                      
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve( programs );
        } );
    }).fail(function(){
        mainDef.resolve( null );
    });

    builder.resolve();

    return mainPromise;    
}

function getOptionSet( id )
{
    return function() {
        return $.ajax( {
            url: '../api/optionSets.json',
            type: 'GET',
            data: 'filter=id:eq:' + id +'&fields=id,name,version,options[id,name,code]'
        }).done( function( response ){            
            _.each( _.values( response.optionSets ), function( optionSet ) {                
                dhis2.tc.store.set( 'optionSets', optionSet );
            });
        });
    };
}


function getMetaTrackedEntityForms()
{
    var def = $.Deferred();

    $.ajax({
        url: '../api/trackedEntityForms.json',
        type: 'GET',
        data:'paging=false&fields=id,program[id]'
    }).done( function(response) {          
        var trackedEntityForms = [];
        _.each( _.values( response.trackedEntityForms ), function ( trackedEntityForm ) { 
            if( trackedEntityForm &&
                trackedEntityForm.id &&
                trackedEntityForm.program &&
                trackedEntityForm.program.id ) {
            
                trackedEntityForms.push( trackedEntityForm );
            }  
            
        });
        
        def.resolve( trackedEntityForms );
        
    }).fail(function(){
        def.resolve( null );
    });
    
    return def.promise(); 
    
}

function getTrackedEntityForms( trackedEntityForms )
{
    if( !trackedEntityForms ){
        return;
    }
    
    var mainDef = $.Deferred();
    var mainPromise = mainDef.promise();

    var def = $.Deferred();
    var promise = def.promise();

    var builder = $.Deferred();
    var build = builder.promise();

    _.each( _.values( trackedEntityForms ), function ( trackedEntityForm ) {
        build = build.then(function() {
            var d = $.Deferred();
            var p = d.promise();
            dhis2.tc.store.get('trackedEntityForms', trackedEntityForm.program.id).done(function(obj) {
                if(!obj) {
                    promise = promise.then( getTrackedEntityForm( trackedEntityForm.id ) );
                }
                d.resolve();
            });

            return p;
        });
    });

    build.done(function() {
        def.resolve();

        promise = promise.done( function () {
            mainDef.resolve();
        } );
    }).fail(function(){
        mainDef.resolve();
    });

    builder.resolve();

    return mainPromise;
}

function getTrackedEntityForm( id )
{
    return function() {
        return $.ajax( {
            url: '../api/trackedEntityForms.json',
            type: 'GET',
            data: 'paging=false&filter=id:eq:' + id +'&fields=id,program[id,name],dataEntryForm[name,htmlCode]'
        }).done( function( response ){
            
            _.each( _.values( response.trackedEntityForms ), function ( trackedEntityForm ) { 
                
                if( trackedEntityForm &&
                    trackedEntityForm.id &&
                    trackedEntityForm.program &&
                    trackedEntityForm.program.id ) {

                    trackedEntityForm.id = trackedEntityForm.program.id;
                    dhis2.tc.store.set( 'trackedEntityForms', trackedEntityForm );
                }
            });
        });
    };
}
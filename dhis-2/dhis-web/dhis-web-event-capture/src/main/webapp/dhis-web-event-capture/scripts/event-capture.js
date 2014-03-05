
dhis2.util.namespace('dhis2.ec');

// whether current user has any organisation units
dhis2.ec.emptyOrganisationUnits = false;

// Instance of the StorageManager
dhis2.ec.storageManager = new StorageManager();

// Indicates whether current form is multi org unit
dhis2.ec.multiOrganisationUnit = false;

// "organisationUnits" object inherited from ouwt.js

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#fff';
var COLOR_GREY = '#ccc';

var COLOR_BORDER_ACTIVE = '#73ad72';
var COLOR_BORDER = '#aaa';

var DEFAULT_TYPE = 'int';
var DEFAULT_NAME = '[unknown]';

var FORMTYPE_CUSTOM = 'custom';
var FORMTYPE_SECTION = 'section';
var FORMTYPE_MULTIORG_SECTION = 'multiorg_section';
var FORMTYPE_DEFAULT = 'default';

var EVENT_FORM_LOADED = "dhis-web-dataentry-form-loaded";

var MAX_DROPDOWN_DISPLAYED = 30;

var DAO = DAO || {};

var i18n_offline_notification = 'You are offline, data will be stored locally';
var i18n_online_notification = 'You are online';

var PROGRAMS_METADATA = 'PROGRAMS';

DAO.store = new dhis2.storage.Store({
    name: 'dhis2',
    adapters: [dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
    objectStores: ['optionSets']
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

    $('#orgUnitTree').one('ouwtLoaded', function()
    {        
        var def = $.Deferred();
        var promise = def.promise();
        
        promise = promise.then( getUserProfile );
        promise = promise.then( getMetaPrograms );     
        promise = promise.then( getPrograms );      
        promise = promise.then( getProgramStages );
        
        promise.done( function() {           
            selection.responseReceived();                      
        });           
        
        def.resolve();
        
    });

    $(document).bind('dhis2.online', function(event, loggedIn)
    {        
        if (loggedIn)
        {
            if (dhis2.ec.storageManager.hasLocalData())
            {
                var message = i18n_need_to_sync_notification
                        + ' <button id="sync_button" type="button">' + i18n_sync_now + '</button>';

                setHeaderMessage(message);

                $('#sync_button').bind('click', uploadLocalData);
            }
            else
            {
                if (dhis2.ec.emptyOrganisationUnits) {
                    setHeaderMessage(i18n_no_orgunits);
                }
                else {
                    setHeaderDelayMessage(i18n_online_notification);
                }
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


function getMetaPrograms()
{
    var PROGRAMS_METADATA = 'PROGRAMS';
    var def = $.Deferred();

    $.ajax({
        url: '../api/programs.json',
        type: 'GET',
        data:'type=3'
    }).done( function(response) {            
        localStorage[PROGRAMS_METADATA] = JSON.stringify(response.programs);           
        def.resolve( response.programs );
    });
    
    return def.promise(); 
}

function getPrograms( programs )
{
    if( !programs ){
        return;
    }
    
    var def = $.Deferred();
    var promise = def.promise();

    _.each( _.values( programs ), function ( program ) {        
        promise = promise.then( getProgram( program.href ) );
    });
    
    promise = promise.then(function() {
        return $.Deferred().resolve( programs );
    });
    
    def.resolve( programs );
    
    return promise;   
}

function getProgram( url )
{   

    return function() {
        return $.ajax( {
            url: url,
            type: 'GET'
        }).done( function( program ){     
            
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
          
            localStorage[program.id] = JSON.stringify(program);
        });
    };
}

function getProgramStages( programs )
{
    if( !programs ){
        return;
    }
    
    var def = $.Deferred();
    var promise = def.promise();

    _.each( _.values( programs ), function ( program ) {  
        program = JSON.parse( localStorage[program.id] );
        _.each( _.values( program.programStages ), function( programStage ) {
            promise = promise.then( getProgramStage( programStage.href ) );
        });        
    });
    
    promise = promise.then(function() {
        return def.resolve();
    });
    
    def.resolve();
    
    return promise; 
}

function getProgramStage( url )
{
    return function() {
        return $.ajax( {
            url: url,
            type: 'GET'
        }).done( function( programStage ){
            localStorage[programStage.id] = JSON.stringify(programStage);
        });
    };
}



// TODO break if local storage is full

// -----------------------------------------------------------------------------
// StorageManager
// -----------------------------------------------------------------------------

/**
 * This object provides utility methods for localStorage and manages data entry
 * forms and data values.
 */
function StorageManager()
{
    var MAX_SIZE = new Number(2600000);
    var MAX_SIZE_FORMS = new Number(1600000);

    var KEY_FORM_PREFIX = 'form-';
    var KEY_FORM_VERSIONS = 'formversions';
    var KEY_DATAVALUES = 'datavalues';
    var KEY_COMPLETEDATASETS = 'completedatasets';

    /**
     * Returns the total number of characters currently in the local storage.
     *
     * @return number of characters.
     */
    this.totalSize = function()
    {
        var totalSize = new Number();

        for (var i = 0; i < localStorage.length; i++)
        {
            var value = localStorage.key(i);

            if (value)
            {
                totalSize += value.length;
            }
        }

        return totalSize;
    };

    /**
     * Returns the total numbers of characters in stored forms currently in the
     * local storage.
     *
     * @return number of characters.
     */
    this.totalFormSize = function()
    {
        var totalSize = new Number();

        for (var i = 0; i < localStorage.length; i++)
        {
            if (localStorage.key(i).substring(0, KEY_FORM_PREFIX.length) == KEY_FORM_PREFIX)
            {
                var value = localStorage.key(i);

                if (value)
                {
                    totalSize += value.length;
                }
            }
        }

        return totalSize;
    };

    /**
     * Return the remaining capacity of the local storage in characters, ie. the
     * maximum size minus the current size.
     */
    this.remainingStorage = function()
    {
        return MAX_SIZE - this.totalSize();
    };

    /**
     * Saves the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param html the form HTML content.
     * @return true if the form saved successfully, false otherwise.
     */
    this.saveForm = function(dataSetId, html)
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        try
        {
            localStorage[id] = html;

            log('Successfully stored form: ' + dataSetId);
        }
        catch (e)
        {
            log('Max local storage quota reached, ignored form: ' + dataSetId);
            return false;
        }

        if (MAX_SIZE_FORMS < this.totalFormSize())
        {
            this.deleteForm(dataSetId);

            log('Max local storage quota for forms reached, ignored form: ' + dataSetId);
            return false;
        }

        return true;
    };

    /**
     * Gets the content of a data entry form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the content of a data entry form.
     */
    this.getForm = function(dataSetId)
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        return localStorage[id];
    };

    /**
     * Removes a form.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    this.deleteForm = function(dataSetId)
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        localStorage.removeItem(id);
    };

    /**
     * Returns an array of the identifiers of all forms.
     *
     * @return array with form identifiers.
     */
    this.getAllForms = function()
    {
        var formIds = [];

        var formIndex = 0;

        for (var i = 0; i < localStorage.length; i++)
        {
            var key = localStorage.key(i);

            if (key.substring(0, KEY_FORM_PREFIX.length) == KEY_FORM_PREFIX)
            {
                var id = key.split('-')[1];

                formIds[formIndex++] = id;
            }
        }

        return formIds;
    };

    /**
     * Indicates whether a form exists.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return true if a form exists, false otherwise.
     */
    this.formExists = function(dataSetId)
    {
        var id = KEY_FORM_PREFIX + dataSetId;

        return localStorage[id] != null;
    };

    /**
     * Downloads the form for the data set with the given identifier from the
     * remote server and saves the form locally. Potential existing forms with
     * the same identifier will be overwritten. Updates the form version.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param formVersion the version of the form of the remote data set.
     */
    this.downloadForm = function(dataSetId, formVersion)
    {
        $.ajax({
            url: 'loadForm.action',
            data:
                    {
                        dataSetId: dataSetId
                    },
            dataSetId: dataSetId,
            formVersion: formVersion,
            dataType: 'text',
            success: function(data, textStatus, jqXHR)
            {
                dhis2.ec.storageManager.saveForm(this.dataSetId, data); //TODO
                dhis2.ec.storageManager.saveFormVersion(this.dataSetId, this.formVersion);
            }
        });
    };

    /**
     * Saves a version for a form.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @param formVersion the version of the form.
     */
    this.saveFormVersion = function(dataSetId, formVersion)
    {
        var formVersions = {};

        if (localStorage[KEY_FORM_VERSIONS] != null)
        {
            formVersions = JSON.parse(localStorage[KEY_FORM_VERSIONS]);
        }

        formVersions[dataSetId] = formVersion;

        try
        {
            localStorage[KEY_FORM_VERSIONS] = JSON.stringify(formVersions);

            log('Successfully stored form version: ' + dataSetId);
        }
        catch (e)
        {
            log('Max local storage quota reached, ignored form version: ' + dataSetId);
        }
    };

    /**
     * Returns the version of the form of the data set with the given
     * identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     * @return the form version.
     */
    this.getFormVersion = function(dataSetId)
    {
        if (localStorage[KEY_FORM_VERSIONS] != null)
        {
            var formVersions = JSON.parse(localStorage[KEY_FORM_VERSIONS]);

            return formVersions[dataSetId];
        }

        return null;
    };

    /**
     * Deletes the form version of the data set with the given identifier.
     *
     * @param dataSetId the identifier of the data set of the form.
     */
    this.deleteFormVersion = function(dataSetId)
    {
        if (localStorage[KEY_FORM_VERSIONS] != null)
        {
            var formVersions = JSON.parse(localStorage[KEY_FORM_VERSIONS]);

            if (formVersions[dataSetId] != null)
            {
                delete formVersions[dataSetId];
                localStorage[KEY_FORM_VERSIONS] = JSON.stringify(formVersions);
            }
        }
    };

    this.getAllFormVersions = function()
    {
        return localStorage[KEY_FORM_VERSIONS] != null ? JSON.parse(localStorage[KEY_FORM_VERSIONS]) : null;
    };

    /**
     * Saves a data value.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    this.saveDataValue = function(dataValue)
    {
        var id = this.getDataValueIdentifier(dataValue.de,
                dataValue.co, dataValue.pe, dataValue.ou);

        var dataValues = {};

        if (localStorage[KEY_DATAVALUES] != null)
        {
            dataValues = JSON.parse(localStorage[KEY_DATAVALUES]);
        }

        dataValues[id] = dataValue;

        try
        {
            localStorage[KEY_DATAVALUES] = JSON.stringify(dataValues);

            log('Successfully stored data value');
        }
        catch (e)
        {
            log('Max local storage quota reached, not storing data value locally');
        }
    };

    /**
     * Gets the value for the data value with the given arguments, or null if it
     * does not exist.
     *
     * @param de the data element identifier.
     * @param co the category option combo identifier.
     * @param pe the period identifier.
     * @param ou the organisation unit identifier.
     * @return the value for the data value with the given arguments, null if
     *         non-existing.
     */
    this.getDataValue = function(de, co, pe, ou)
    {
        var id = this.getDataValueIdentifier(de, co, pe, ou);

        if (localStorage[KEY_DATAVALUES] != null)
        {
            var dataValues = JSON.parse(localStorage[KEY_DATAVALUES]);

            return dataValues[id];
        }

        return null;
    };

    /**
     * Returns the data values for the given period and organisation unit 
     * identifiers as an array.
     * 
     * @param json object with periodId and organisationUnitId properties.
     */
    this.getDataValuesInForm = function(json)
    {
        var dataValues = this.getDataValuesAsArray();
        var valuesInForm = new Array();

        for (var i = 0; i < dataValues.length; i++)
        {
            var val = dataValues[i];

            if (val.pe == json.periodId && val.ou == json.organisationUnitId)
            {
                valuesInForm.push(val);
            }
        }

        return valuesInForm;
    }

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param dataValue The datavalue and identifiers in json format.
     */
    this.clearDataValueJSON = function(dataValue)
    {
        this.clearDataValue(dataValue.de, dataValue.co, dataValue.pe,
                dataValue.ou);
    };

    /**
     * Removes the given dataValue from localStorage.
     *
     * @param de the data element identifier.
     * @param co the category option combo identifier.
     * @param pe the period identifier.
     * @param ou the organisation unit identifier.
     */
    this.clearDataValue = function(de, co, pe, ou)
    {
        var id = this.getDataValueIdentifier(de, co, pe, ou);
        var dataValues = this.getAllDataValues();

        if (dataValues != null && dataValues[id] != null)
        {
            delete dataValues[id];
            localStorage[KEY_DATAVALUES] = JSON.stringify(dataValues);
        }
    };

    /**
     * Returns a JSON associative array where the keys are on the form <data
     * element id>-<category option combo id>-<period id>-<organisation unit
     * id> and the data values are the values.
     *
     * @return a JSON associative array.
     */
    this.getAllDataValues = function()
    {
        return localStorage[KEY_DATAVALUES] != null ? JSON.parse(localStorage[KEY_DATAVALUES]) : null;
    };

    /**
     * Returns all data value objects in an array. Returns an empty array if no
     * data values exist. Items in array are guaranteed not to be undefined.
     */
    this.getDataValuesAsArray = function()
    {
        var values = new Array();
        var dataValues = this.getAllDataValues();

        if (undefined === dataValues)
        {
            return values;
        }

        for (i in dataValues)
        {
            if (dataValues.hasOwnProperty(i) && undefined !== dataValues[i])
            {
                values.push(dataValues[i]);
            }
        }

        return values;
    }

    /**
     * Generates an identifier.
     */
    this.getDataValueIdentifier = function(de, co, pe, ou)
    {
        return de + '-' + co + '-' + pe + '-' + ou;
    };

    /**
     * Generates an identifier.
     */
    this.getCompleteDataSetId = function(json)
    {
        return json.ds + '-' + json.pe + '-' + json.ou;
    };

    /**
     * Returns current state in data entry form as associative array.
     *
     * @return an associative array.
     */
    this.getCurrentCompleteDataSetParams = function()
    {
        var params = {
            'ds': $('#selectedDataSetId').val(),
            'pe': $('#selectedPeriodId').val(),
            'ou': getCurrentOrganisationUnit(),
            'multiOu': dhis2.ec.multiOrganisationUnit
        };

        return params;
    };

    /**
     * Gets all complete data set registrations as JSON.
     *
     * @return all complete data set registrations as JSON.
     */
    this.getCompleteDataSets = function()
    {
        if (localStorage[KEY_COMPLETEDATASETS] != null)
        {
            return JSON.parse(localStorage[KEY_COMPLETEDATASETS]);
        }

        return null;
    };

    /**
     * Saves a complete data set registration.
     *
     * @param json the complete data set registration as JSON.
     */
    this.saveCompleteDataSet = function(json)
    {
        var completeDataSets = this.getCompleteDataSets();
        var completeDataSetId = this.getCompleteDataSetId(json);

        if (completeDataSets != null)
        {
            completeDataSets[completeDataSetId] = json;
        }
        else
        {
            completeDataSets = {};
            completeDataSets[completeDataSetId] = json;
        }

        try
        {
            localStorage[KEY_COMPLETEDATASETS] = JSON.stringify(completeDataSets);

            log('Successfully stored complete registration');
        }
        catch (e)
        {
            log('Max local storage quota reached, not storing complete registration locally');
        }
    };

    /**
     * Indicates whether a complete data set registration exists for the given
     * argument.
     * 
     * @param json object with periodId, dataSetId, organisationUnitId properties.
     */
    this.hasCompleteDataSet = function(json)
    {
        var id = this.getCompleteDataSetId(json);
        var registrations = this.getCompleteDataSets();

        if (null != registrations && undefined !== registrations && undefined !== registrations[id])
        {
            return true;
        }

        return false;
    }

    /**
     * Removes the given complete data set registration.
     *
     * @param json the complete data set registration as JSON.
     */
    this.clearCompleteDataSet = function(json)
    {
        var completeDataSets = this.getCompleteDataSets();
        var completeDataSetId = this.getCompleteDataSetId(json);

        if (completeDataSets != null)
        {
            delete completeDataSets[completeDataSetId];

            if (completeDataSets.length > 0)
            {
                localStorage.removeItem(KEY_COMPLETEDATASETS);
            }
            else
            {
                localStorage[KEY_COMPLETEDATASETS] = JSON.stringify(completeDataSets);
            }
        }
    };

    /**
     * Indicates whether there exists data values or complete data set
     * registrations in the local storage.
     *
     * @return true if local data exists, false otherwise.
     */
    this.hasLocalData = function()
    {
        var dataValues = this.getAllDataValues();
        var completeDataSets = this.getCompleteDataSets();

        if (dataValues == null && completeDataSets == null)
        {
            return false;
        }
        else if (dataValues != null)
        {
            if (Object.keys(dataValues).length < 1)
            {
                return false;
            }
        }
        else if (completeDataSets != null)
        {
            if (Object.keys(completeDataSets).length < 1)
            {
                return false;
            }
        }

        return true;
    };
}


// Identifiers for which zero values are insignificant, also used in entry.js
var significantZeros = [];

// Array with associative arrays for each data element, populated in select.vm
var dataElements = [];

// Associative array with [indicator id, expression] for indicators in form,
// also used in entry.js
var indicatorFormulas = [];

// Array with associative arrays for each data set, populated in select.vm
var dataSets = [];

// Associative array with identifier and array of assigned data sets
var dataSetAssociationSets = [];

// Associate array with mapping between organisation unit identifier and data
// set association set identifier
var organisationUnitAssociationSetMap = [];

// Array with keys on form {dataelementid}-{optioncomboid}-min/max with min/max
// values
var currentMinMaxValueMap = [];

// Indicates whether any data entry form has been loaded
var dataEntryFormIsLoaded = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

// Currently selected data set identifier
var currentDataSetId = null;

// Current offset, next or previous corresponding to increasing or decreasing
// value with one
var currentPeriodOffset = 0;

// Period type object
var periodTypeFactory = new PeriodType();

// Instance of the StorageManager
var storageManager = new StorageManager();

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';

/**
 * Page init. The order of events is:
 * 
 * 1. Load ouwt
 * 2. Load meta-data
 * 3. Upload potential locally stored data values to server
 * 4. Check and potentially download updated forms from server 
 */
$( document ).ready( function()
{
    selection.setListenerFunction( organisationUnitSelected );

    $( '#orgUnitTree' ).one( 'ouwtLoaded', function() {
    	console.log( 'Ouwt loaded' );    	
    	loadMetaData();
    } );

    $( document ).bind( 'dhis2.online', function( event, loggedIn ) {
        if ( loggedIn ) {
            if( isHeaderMessageVisible() ) {
                updateHeaderMessage( i18n_online_notification );
            } else {
                setHeaderMessage( i18n_online_notification );
            }
        } 
        else {
            if ( isHeaderMessageVisible() ) {
                updateHeaderMessage( '<form style="display: inline;"><label for="username">Username</label><input name="username" id="username" type="text" size="10"/><label for="password">Password</label><input name="password" id="password" type="password" size="10"/><button id="login_button" type="button">Login</button></form>' );
                ajax_login();
            } else {
                setHeaderMessage( '<form style="display: inline;"><label for="username">Username</label><input name="username" id="username" type="text" size="10"/><label for="password">Password</label><input name="password" id="password" type="password" size="10"/><button id="login_button" type="button">Login</button></form>' );
                ajax_login();
            }
        }
    } );

    $( document ).bind( 'dhis2.offline', function() {
        if ( isHeaderMessageVisible() ) {
            updateHeaderMessage( i18n_offline_notification );
        } else {
            setHeaderMessage( i18n_offline_notification );
        }
    } );

    dhis2.availability.startAvailabilityCheck();
} );

function loadMetaData()
{
	$.getJSON( 'getMetaData.action', function( json ) {
		significantZeros = json.metaData.significantZeros;
		dataElements = json.metaData.dataElements;
		indicatorFormulas = json.metaData.indicatorFormulas;
		dataSets = json.metaData.dataSets;
		dataSetAssociationSets = json.metaData.dataSetAssociationSets;
		organisationUnitAssociationSetMap = json.metaData.organisationUnitAssociationSetMap;
		
		console.log( 'Meta-data loaded' );		
		uploadLocalData();
	} );
}

function uploadLocalData() 
{
    var dataValues = storageManager.getAllDataValues();
    var completeDataSets = getCompleteDataSetsLocalVariable();

    var oldHeaderMessage = getHeaderMessage();
    setHeaderWaitMessage( i18n_uploading_data_notification );

    for ( var dataValueKey in dataValues ) 
    {
        var dataValue = dataValues[dataValueKey];

        $.ajax( {
            url: 'saveValue.action',
            data: dataValue,
            dataType: 'json',
            dataValue: dataValue,
            async: false,
            success: function( data, textStatus, jqXHR ) {
                storageManager.clearDataValueJSON( this.dataValue );
                console.log( 'Successfully saved data value with value: ' + this.dataValue );
            }
        } );
    }

    for( var completeDataSetKey in completeDataSets )
    {
        var completeDataSet = completeDataSets[completeDataSetKey];

        $.ajax({
            url: 'registerCompleteDataSet.action',
            data: completeDataSet,
            dataType: 'json',
            completeDataSet: completeDataSet,
            async: false,
            success: function( data, textStatus, jqXHR ) {
                clearCompleteDataSetLocally(this.completeDataSet);
            }
        } );
    }

    if ( oldHeaderMessage.length > 0 ) {
        setHeaderMessage( oldHeaderMessage );
    } else {
        hideHeaderMessage();
    }
    
    updateForms();
}

function ajax_login() 
{
    $( '#login_button' ).bind( 'click', function() {
        var username = $( '#username' ).val();
        var password = $( '#password' ).val();

        $.post( '../dhis-web-commons-security/login.action', {
            'j_username': username,
            'j_password': password
        } ).success( function() {
            alert( 'Login attempt successful, TODO check if login was successful with checkAvailability' );
        } );
    } );
}

function addEventListeners()
{
    $( '[name="ef"]' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var dataElementId = id.split( '-' )[0];
        var optionComboId = id.split( '-' )[1];
        var type = dataElements[dataElementId].type;

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'blur' );
        $( this ).unbind( 'change' );
        $( this ).unbind( 'dblclick' );
        $( this ).unbind( 'keyup' );

        $( this ).focus( valueFocus );

        $( this ).blur( valueBlur );

        $( this ).change( function()
        {
            saveVal( dataElementId, optionComboId );
        } );

        $( this ).dblclick( function()
        {
            viewHist( dataElementId, optionComboId );
        } );

        $( this ).keyup( function()
        {
            keyPress( event, this );
        } );

        $( this ).css( 'width', '100%' );
        $( this ).css( 'text-align', 'center' );

        if ( type == 'date' )
        {
            $( this ).css( 'width', '80%' );
            datePicker( id );
        }
    } );

    $( '[name="es"]' ).each( function( i )
    {
        var id = $( this ).attr( 'id' );
        var dataElementId = id.split( '-' )[0];
        var optionComboId = id.split( '-' )[1];

        $( this ).unbind( 'focus' );
        $( this ).unbind( 'change' );

        $( this ).focus( valueFocus );

        $( this ).change( function()
        {
            saveBoolean( dataElementId, optionComboId );
        } );

        $( this ).css( 'width', '100%' );
    } );
}

function clearPeriod()
{
    clearListById( 'selectedPeriodId' );
    clearEntryForm();
}

function clearEntryForm()
{
    $( '#contentDiv' ).html( '' );

    currentPeriodOffset = 0;

    dataEntryFormIsLoaded = false;
    
    $( '#completenessDiv' ).css( 'display', 'none' );
}

function loadForm( dataSetId )
{
	if ( storageManager.formExists( dataSetId ) )
	{
		console.log( 'Loading form locally: ' + dataSetId );
		
		var html = storageManager.getForm( dataSetId );
		
		$( '#contentDiv' ).html( html );
		
		loadDataValues();
	}
	else
	{	
		console.log( 'Loading form remotely: ' + dataSetId );
		
	    $( '#contentDiv' ).load( 'loadForm.action', {
	        dataSetId : dataSetId
	    }, loadDataValues );
	}
}

// -----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

/**
 * Returns an array containing associative array elements with id and name
 * properties. The array is sorted on the element name property.
 */
function getSortedDataSetList()
{
    var associationSet = organisationUnitAssociationSetMap[currentOrganisationUnitId];
    var orgUnitDataSets = dataSetAssociationSets[associationSet];

    var dataSetList = [];

    for ( i in orgUnitDataSets )
    {
        var dataSetId = orgUnitDataSets[i];
        var dataSetName = dataSets[dataSetId].name;

        var row = [];
        row['id'] = dataSetId;
        row['name'] = dataSetName;
        dataSetList[i] = row;
    }

    dataSetList.sort( function( a, b )
    {
        return a.name > b.name ? 1 : a.name < b.name ? -1 : 0;
    } );

    return dataSetList;
}

function organisationUnitSelected( orgUnits, orgUnitNames )
{
    currentOrganisationUnitId = orgUnits[0];
    var organisationUnitName = orgUnitNames[0];

    $( '#selectedDataSetId' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

    $( '#selectedOrganisationUnit' ).val( organisationUnitName );
    $( '#currentOrganisationUnit' ).html( organisationUnitName );

    clearListById( 'selectedDataSetId' );

    addOptionById( 'selectedDataSetId', '-1', '[ ' + i18n_select_data_set + ' ]' );

    var dataSetList = getSortedDataSetList();

    var dataSetValid = false;

    for ( i in dataSetList )
    {
        addOptionById( 'selectedDataSetId', dataSetList[i].id, dataSetList[i].name );

        if ( dataSetId == dataSetList[i].id )
        {
            dataSetValid = true;
        }
    }

    if ( dataSetValid && dataSetId != null )
    {
        $( '#selectedDataSetId' ).val( dataSetId );

        if ( periodId && periodId != -1 && dataEntryFormIsLoaded )
        {
            showLoader();
            loadDataValues();
        }
    }
    else
    {
        clearPeriod();
    }
}

// -----------------------------------------------------------------------------
// Next/Previous Periods Selection
// -----------------------------------------------------------------------------

function nextPeriodsSelected()
{
    if ( currentPeriodOffset < 0 ) // Cannot display future periods
    {
        currentPeriodOffset++;
        displayPeriodsInternal();
    }
}

function previousPeriodsSelected()
{
    currentPeriodOffset--;
    displayPeriodsInternal();
}

function displayPeriodsInternal()
{
    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodType = dataSets[dataSetId].periodType;
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    clearListById( 'selectedPeriodId' );

    addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );

    for ( i in periods )
    {
        addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
    }
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    $( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();
    var periodType = dataSets[dataSetId].periodType;
    var periods = periodTypeFactory.get( periodType ).generatePeriods( currentPeriodOffset );
    periods = periodTypeFactory.filterFuturePeriods( periods );

    if ( dataSetId && dataSetId != -1 )
    {
        clearListById( 'selectedPeriodId' );

        addOptionById( 'selectedPeriodId', '-1', '[ ' + i18n_select_period + ' ]' );

        for ( i in periods )
        {
            addOptionById( 'selectedPeriodId', periods[i].id, periods[i].name );
        }

        var previousPeriodType = currentDataSetId ? dataSets[currentDataSetId].periodType : null;

        if ( periodId && periodId != -1 && previousPeriodType && previousPeriodType == periodType )
        {
            showLoader();
            $( '#selectedPeriodId' ).val( periodId );
            loadForm( dataSetId );
        }
        else
        {
            clearEntryForm();
        }

        currentDataSetId = dataSetId;
    }
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
    var periodName = $( '#selectedPeriodId :selected' ).text();
    var dataSetId = $( '#selectedDataSetId' ).val();

    $( '#currentPeriod' ).html( periodName );

    var periodId = $( '#selectedPeriodId' ).val();

    if ( periodId && periodId != -1 )
    {
        showLoader();

        if ( dataEntryFormIsLoaded )
        {
            loadDataValues();
        }
        else
        {
            loadForm( dataSetId );
        }
    }
}

// -----------------------------------------------------------------------------
// Form
// -----------------------------------------------------------------------------

function loadDataValues()
{
    $( '#completeButton' ).removeAttr( 'disabled' );
    $( '#undoButton' ).attr( 'disabled', 'disabled' );

    insertDataValues();
    displayEntryFormCompleted();
}

function insertDataValues()
{
    var dataValueMap = new Array();

    var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();

    // Clear existing values and colors

    $( '[name="ef"]' ).val( '' );
    $( '[name="es"]' ).val( '' );

    $( '[name="ef"]' ).css( 'background-color', COLOR_WHITE );
    $( '[name="es"]' ).css( 'background-color', COLOR_WHITE );

    $( '[name="min"]' ).html( '' );
    $( '[name="max"]' ).html( '' );

    $.getJSON( 'getDataValues.action', {
            periodId : periodId,
        	dataSetId : dataSetId,
        	organisationUnitId: currentOrganisationUnitId
        },
        function( json )
        {
            // Set data values, works for select lists too as data
            // value = select value

            $.each( json.dataValues, function( i, value )
            {
                var fieldId = '#' + value.id + '-val';

                if ( $( fieldId ) )
                {
                    $( fieldId ).val( value.val );
                }

                dataValueMap[value.id] = value.val;
            } );

            // Set min-max values and colorize violation fields

            $.each( json.minMaxDataElements, function( i, value ) 
            {
                var minId = value.id + '-min';
                var maxId = value.id + '-max';

                var valFieldId = '#' + value.id + '-val';

                var dataValue = dataValueMap[value.id];

                if ( dataValue && 
                	( ( value.min && new Number( dataValue ) < new Number( value.min ) ) 
                	|| ( value.max && new Number( dataValue ) > new Number( value.max ) ) ) )
                {
                    $( valFieldId ).css( 'background-color', COLOR_ORANGE );
                }

                currentMinMaxValueMap[minId] = value.min;
                currentMinMaxValueMap[maxId] = value.max;
            } );

            // Update indicator values in form

            updateIndicators();
            
            // Set completeness button
            
            if ( json.complete )
            {
            	$( '#completeButton' ).attr( 'disabled', 'disabled' );
            	$( '#undoButton' ).removeAttr( 'disabled' );
            }
            else
            {
            	$( '#completeButton' ).removeAttr( 'disabled' );
            	$( '#undoButton' ).attr( 'disabled', 'disabled' );
            }
            
            // TODO locking
        } );
}

function displayEntryFormCompleted()
{
    addEventListeners();

    $( '#validationButton' ).removeAttr( 'disabled' );
    $( '#defaultForm' ).removeAttr( 'disabled' );

    dataEntryFormIsLoaded = true;
    hideLoader();
    
    $( '#completenessDiv' ).css( 'display', 'block' );
}

function valueFocus( e )
{
    var id = e.target.id;

    var dataElementId = id.split( '-' )[0];
    var optionComboId = id.split( '-' )[1];

    var dataElementName = dataElements[dataElementId].name;
    var optionComboName = $( '#' + optionComboId + '-optioncombo' ).text();

    $( '#currentDataElement' ).html( dataElementName + ' ' + optionComboName );

    $( '#' + dataElementId + '-cell' ).addClass( 'currentRow' );
}

function valueBlur( e )
{
    var id = e.target.id;

    var dataElementId = id.split( '-' )[0];

    $( '#' + dataElementId + '-cell' ).removeClass( 'currentRow' );
}

function keyPress( event, field )
{
    var key = event.keyCoe || event.charCode || event.which;

    var focusField = ( key == 13 || key == 40 ) ? getNextEntryField( field )
            : ( key == 38 ) ? getPreviousEntryField( field ) : false;

    if ( focusField )
    {
        focusField.focus();
    }
}

function getNextEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="ef"][tabindex="' + ( ++index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="ef"][tabindex="' + ( ++index ) + '"]' );
        }
        else
        {
            return field;
        }
    }
}

function getPreviousEntryField( field )
{
    var index = field.getAttribute( 'tabindex' );

    field = $( 'input[name="ef"][tabindex="' + ( --index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="ef"][tabindex="' + ( --index ) + '"]' );
        }
        else
        {
            return field;
        }
    }
}

// -----------------------------------------------------------------------------
// Data completeness
// -----------------------------------------------------------------------------

var KEY_COMPLETEDATASETS = 'completedatasets';

function getCompleteDataSetId(json)
{
    return json.periodId + "-" + json.dataSetId + "-" + json.organisationUnitId;
}

function getCurrentCompleteDataSetParams() {
    var params = {
            'periodId': $( '#selectedPeriodId' ).val(),
            'dataSetId': $( '#selectedDataSetId' ).val(),
            'organisationUnitId': currentOrganisationUnitId
    };
    
    return params;
}

function getCompleteDataSetsLocalVariable()
{
    var completeDataSets;

    if( localStorage[KEY_COMPLETEDATASETS] == null )
    {
        completeDataSets = {};
    } else {
        completeDataSets = JSON.parse( localStorage[KEY_COMPLETEDATASETS] );
    }

    return completeDataSets;
}

function storeCompleteDataSetLocally(json)
{
    var completeDataSets = getCompleteDataSetsLocalVariable();
    var completeDataSetId = getCompleteDataSetId(json);

    completeDataSets[completeDataSetId] = json;

    localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
}

function clearCompleteDataSetLocally(json)
{
    var completeDataSets = getCompleteDataSetsLocalVariable();
    var completeDataSetId = getCompleteDataSetId(json);

    delete completeDataSets[completeDataSetId];

    localStorage[KEY_COMPLETEDATASETS] = JSON.stringify( completeDataSets );
}

function validateCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_complete );

    if ( confirmed )
    {
        var params = getCurrentCompleteDataSetParams();
        
        disableCompleteButton();

        $.getJSON( 'getValidationViolations.action', params).success( function( data ) {
            registerCompleteDataSet( data );
        } ).error( function() {
            // no response from server, fake a positive result and save it anyway
            registerCompleteDataSet({
                'response': 'success'
            });
        } );
    }
}

function registerCompleteDataSet( json )
{
    var params = getCurrentCompleteDataSetParams();

    if ( json.response == 'success' )
    {
        storeCompleteDataSetLocally(params);

        $.getJSON( 'registerCompleteDataSet.action', params).success(function() {
            clearCompleteDataSetLocally(params);
        } ).error( function() {
        } );
    }
    else
    {
    	disableUndoButton();
    	
        validate();
    }
}

function undoCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_undo );
    var params = getCurrentCompleteDataSetParams();
    
    if ( confirmed )
    {
        disableUndoButton();

        $.getJSON( 'undoCompleteDataSet.action', params).success(function() {
            clearCompleteDataSetLocally(params);
        } ).error( function()
        {
            clearCompleteDataSetLocally(params);
        } );
    }
}

function disableUndoButton()
{
	$( '#completeButton' ).removeAttr( 'disabled' );
    $( '#undoButton' ).attr( 'disabled', 'disabled' );
}

function disableCompleteButton()
{
	$( '#completeButton' ).attr( 'disabled', 'disabled' );
    $( '#undoButton' ).removeAttr( 'disabled' );
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function displayValidationDialog()
{
    $( '#validationDiv' ).dialog( {
        modal : true,
        title : 'Validation',
        width : 800,
        height : 400
    } );
}

function validate()
{
    var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();

    $( '#validationDiv' ).load( 'validate.action', {
        periodId : periodId,
        dataSetId : dataSetId,
        organisationUnitId: currentOrganisationUnitId
    },
    function( response, status, xhr ) 
    {
    	if ( status == 'error' )
    	{
    		window.alert( i18n_operation_not_available_offline );
    	}
    	else
    	{
    		displayValidationDialog();
    	}
    } );
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function displayHistoryDialog( operandName )
{
    $( '#historyDiv' ).dialog( {
        modal : true,
        title : operandName,
        width : 580,
        height : 710
    } );
}

function viewHist( dataElementId, optionComboId )
{
    var periodId = $( '#selectedPeriodId' ).val();

    var dataElementName = dataElements[dataElementId].name;
    var optionComboName = $( '#' + optionComboId + '-optioncombo' ).html();
    var operandName = dataElementName + ' ' + optionComboName;

    $( '#historyDiv' ).load( 'viewHistory.action', {
        dataElementId: dataElementId,
        optionComboId: optionComboId,
        periodId: periodId,
        organisationUnitId: currentOrganisationUnitId 
    },
    function( response, status, xhr ) 
    {
    	if ( status == 'error' )
    	{
    		window.alert( i18n_operation_not_available_offline );
    	}
    	else
    	{
        	displayHistoryDialog( operandName );
    	}
    } );
}

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}

// -----------------------------------------------------------------------------
// Local storage of forms
// -----------------------------------------------------------------------------

function updateForms()
{
	purgeLocalForms();
	updateExistingLocalForms();	
	downloadRemoteForms();
}

function purgeLocalForms()
{
	var formIds = storageManager.getAllForms();
	
	for ( i in formIds )
	{
		var localId = formIds[i];
		
		if ( dataSets[localId] == null )
		{
			storageManager.deleteForm( localId );
			console.log( 'Deleted locally stored form: ' + localId );
		}
	}
	
	console.log( 'Purged local forms' );
}

function updateExistingLocalForms()
{
	var formIds = storageManager.getAllForms();	
	var formVersions = storageManager.getAllFormVersions();
	
	for ( i in formIds )
	{
		var dataSetId = formIds[i];
		
		var remoteVersion = dataSets[dataSetId].version;
		var localVersion = formVersions[dataSetId];
		
		if ( remoteVersion == null || localVersion == null || remoteVersion != localVersion )
		{
			storageManager.downloadForm( dataSetId, remoteVersion ); 
		}
	}
}

function downloadRemoteForms()
{
	for ( dataSetId in dataSets )
	{
		var remoteVersion = dataSets[dataSetId].version;
		
		if ( !storageManager.formExists( dataSetId ) )
		{
			storageManager.downloadForm( dataSetId, remoteVersion );
		}
	}
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
	var MAX_SIZE = new Number( 2600000 );
	var MAX_SIZE_FORMS = new Number( 1600000 );

	var KEY_FORM_PREFIX = 'form-';
	var KEY_FORM_VERSIONS = 'formversions';
	var KEY_DATAVALUES = 'datavalues';

	/**
	 * Returns the total number of characters currently in the local storage.
	 * 
	 * @return number of characters.
	 */
	this.totalSize = function()
	{
		var totalSize = new Number();
		
		for ( var i = 0; i < localStorage.length; i++ )
		{
  			var value = localStorage.key(i);
  			
  			if ( value )
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
		
		for ( var i = 0; i < localStorage.length; i++ )
		{
			if ( localStorage.key(i).substring( 0, KEY_FORM_PREFIX.length ) == KEY_FORM_PREFIX )
			{
				var value = localStorage.key(i);
				
				if ( value )
				{
					totalSize += value.length;
				}
			}
		}
		
		return totalSize;
	};
	
	/**
	 * Return the remaining capacity of the local storage in characters, ie.
	 * the maximum size minus the current size.
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
	this.saveForm = function( dataSetId, html )
	{
		var id = KEY_FORM_PREFIX + dataSetId;
		
		try
		{
			localStorage[id] = html;
			
			console.log( 'Successfully stored form: ' + dataSetId );
		}
		catch ( e )
		{
			console.log( 'Max local storage quota reached, ignored form: ' + dataSetId );			
			return false;
		}
		
		if ( MAX_SIZE_FORMS < this.totalFormSize() )
		{
			this.deleteForm( dataSetId );
			
			console.log( 'Max local storage quota for forms reached, ignored form: ' + dataSetId );
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
	this.getForm = function( dataSetId )
	{
		var id = KEY_FORM_PREFIX + dataSetId;
		
		return localStorage[id];
	};
	
	/**
	 * Removes a form.
	 * 
	 * @param dataSetId the identifier of the data set of the form.
	 */
	this.deleteForm = function( dataSetId )
	{
		localStorage.removeItem( dataSetId );
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
		
		for ( var i = 0; i < localStorage.length; i++ )
		{
			var key = localStorage.key(i);
			
			if ( key.substring( 0, KEY_FORM_PREFIX.length ) == KEY_FORM_PREFIX )
			{
				var id = key.split( '-' )[1];
				
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
	this.formExists = function( dataSetId )
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
	this.downloadForm = function( dataSetId, formVersion )
	{
		$.ajax( {
			url: 'loadForm.action',
			data: { dataSetId: dataSetId },
			dataType: 'text',
			dataSetId: dataSetId,
			formVersion: formVersion,
			success: function( data, textStatus, jqXHR ) {					
				storageManager.saveForm( this.dataSetId, data );
				storageManager.saveFormVersion( this.dataSetId, this.formVersion );
			}
		} );
	};

	/**
	 * Saves a version for a form.
	 * 
	 * @param the identifier of the data set of the form.
	 * @param formVersion the version of the form.
	 */
	this.saveFormVersion = function( dataSetId, formVersion )
	{
		var formVersions = {};
		
		if ( localStorage[KEY_FORM_VERSIONS] != null )
		{
			formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );
		}
		
		formVersions[dataSetId] = formVersion;
		
		try
		{
			localStorage[KEY_FORM_VERSIONS] = JSON.stringify( formVersions );
			
			console.log( 'Successfully stored form version: ' + dataSetId );
		}
		catch ( e )
		{
			console.log( 'Max local storage quota reached, ignored form version: ' + dataSetId );
		}
	};
	
	/**
	 * Returns the version of the form of the data set with the given identifier.
	 * 
	 * @param dataSetId the identifier of the data set of the form.
	 * @return the form version.
	 */
	this.getFormVersion = function( dataSetId )
	{
		if ( localStorage[KEY_FORM_VERSIONS] != null )
		{
			var formVersions = JSON.parse( localStorage[KEY_FORM_VERSIONS] );
			
			return formVersions[dataSetId];
		}
		
		return null;
	};
	
	this.getAllFormVersions = function()
	{
		return localStorage[KEY_FORM_VERSIONS] != null ?  JSON.parse( localStorage[KEY_FORM_VERSIONS] ) : null;
	};
	
	/**
	 * Saves a data value.
	 * 
     * @param dataValue The datavalue and identifiers in json format.
	 */
	this.saveDataValue = function( dataValue )
	{
		var id = this.getDataValueIdentifier( dataValue.dataElementId, dataValue.optionComboId, dataValue.periodId, dataValue.organisationUnitId );

		var dataValues = {};

		if ( localStorage[KEY_DATAVALUES] != null )
		{
			dataValues = JSON.parse( localStorage[KEY_DATAVALUES] );
		}

		dataValues[id] = dataValue;

		try
		{
			localStorage[KEY_DATAVALUES] = JSON.stringify( dataValues );
			
			console.log( 'Successfully stored data value' );
		}
		catch ( e )
		{
			console.log( 'Max local storage quota reached, ignored data value' );
		}
	};

	/**
	 * Gets the value for the data value with the given arguments, or null if it
	 * does not exist.
	 * 
	 * @param dataElementId the data element identifier.
	 * @param categoryOptionComboId the category option combo identifier.
	 * @param periodId the period identifier.
	 * @param organisationUnitId the organisation unit identifier.
	 * @return the value for the data value with the given arguments, null if non-existing.
	 */
	this.getDataValue = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
	{
		var id = this.getDataValueIdentifier( dataElementId, categoryOptionComboId, periodId, organisationUnitId );

		if ( localStorage[KEY_DATAVALUES] != null )
		{
			var dataValues = JSON.parse( localStorage[KEY_DATAVALUES] );
			
			return dataValues[id];
		}

		return null;
	};

	/**
	 * Removes the wanted dataValue from localStorage.
	 * 
	 * @param dataValue The datavalue and identifiers in json format.
	 */
	this.clearDataValueJSON = function( dataValue )
	{
	    this.clearDataValue( dataValue.dataElementId, dataValue.optionComboId, dataValue.periodId, dataValue.organisationUnitId );
	};

	/**
     * Removes the wanted dataValue from localStorage.
     * 
     * @param dataElementId the data element identifier.
     * @param categoryOptionComboId the category option combo identifier.
     * @param periodId the period identifier.
     * @param organisationUnitId the organisation unit identifier.
     */
	this.clearDataValue = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
	{
	    var id = this.getDataValueIdentifier( dataElementId, categoryOptionComboId, periodId, organisationUnitId );
	    var dataValues = this.getAllDataValues();

	    if ( dataValues[id] != null ) 
	    {
	        delete dataValues[id];
	        localStorage[KEY_DATAVALUES] = JSON.stringify( dataValues );
	    }
	};
	
	/**
	 * Returns a JSON associative array where the keys are on the form
	 * <data element id>-<category option combo id>-<period id>-<organisation unit id>
	 * and the data values are the values.
	 * 
	 * @return  a JSON associative array.
	 */
	this.getAllDataValues = function()
	{
		return localStorage[KEY_DATAVALUES] != null ? JSON.parse( localStorage[KEY_DATAVALUES] ) : null;
	};
	
	/**
	 * Supportive method.
	 */
	this.getDataValueIdentifier = function( dataElementId, categoryOptionComboId, periodId, organisationUnitId )
	{
		return dataElementId + "-" + categoryOptionComboId + "-" + periodId + "-" + organisationUnitId;
	};
}

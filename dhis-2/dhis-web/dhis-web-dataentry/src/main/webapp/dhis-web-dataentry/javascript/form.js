// Identifiers for which zero values are insignificant, also used in entry.js, populated in select.vm
var significantZeros = [];

// Array with associative arrays for each data element, populated in select.vm
var dataElements = [];

// Associative array with [indicator id, expression] for indicators in form, also used in entry.js, populated in select.vm
var indicatorFormulas = [];

// Array with associative arrays for each data set, populated in select.vm
var dataSets = [];

// Associative array with identifier and array of assigned data sets, populated in select.vm
var dataSetAssociationSets = [];

// Associate array with mapping between organisation unit identifier and data set association set identifier, populated in select.vm
var organisationUnitAssociationSetMap = [];

// Array with keys on form {dataelementid}-{optioncomboid}-min/max with min/max values
var currentMinMaxValueMap = [];

// Indicates whether any data entry form has been loaded
var dataEntryFormIsLoaded = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

// Currently selected data set identifier
var currentDataSetId = null;

// Current offset, next or previous corresponding to increasing or decreasing value with one
var currentPeriodOffset = 0;

// Period type object
var periodTypeFactory = new PeriodType();

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';

//Page init

$( document ).ready( function() {
	selection.setListenerFunction( organisationUnitSelected );
} );

function addEventListeners()
{
    $( '[name="entryfield"]' ).each( function( i ) 
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
    	
    	$( this ).change( function() {
    		saveVal( dataElementId, optionComboId );
    	} );
    	
    	$( this ).dblclick( function() {
    		viewHist( dataElementId, optionComboId );
    	} );
    	
    	$( this ).keyup( function() {
    		keyPress( event, this );
    	} );
    	
    	$( this ).css( 'width', '100%' );
    	$( this ).css( 'text-align', 'center' );
    	
    	if ( type == 'date' ) {
    		$( this ).css( 'width', '80%' );    		
    		datePicker( id );
    	}
    } );
    
    $( '[name="entryselect"]' ).each( function( i )
    {
    	var id = $( this ).attr( 'id' );    	
		var dataElementId = id.split( '-' )[0];
		var optionComboId = id.split( '-' )[1];	
		
    	$( this ).unbind( 'focus' );
    	$( this ).unbind( 'change' );
    	
    	$( this ).focus( valueFocus );
    	
    	$( this ).change( function() {
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
}

function loadForm( periodId, dataSetId )
{
	var defaultForm = $( '#defaultForm' ).is( ':checked' );
	
	$( '#contentDiv' ).load( 'loadForm.action', { periodId:periodId, dataSetId:dataSetId, defaultForm:defaultForm }, loadDataValues );
}

function loadDefaultForm()
{
    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodId = $( '#selectedPeriodId' ).val();

	loadForm( periodId, dataSetId );
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
	
	dataSetList.sort( function( a, b ) {
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

    var url = 'loadDataSets.action';

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

        if ( periodId && periodId != -1 && dataEntryFormIsLoaded ) //TODO if period valid
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
            loadForm( periodId, dataSetId );
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
        	loadForm( periodId, dataSetId );
        }
    }
}

// -----------------------------------------------------------------------------
// Form
// -----------------------------------------------------------------------------

function loadDataValues()
{
	insertDataValues();
	displayEntryFormCompleted();
}

function insertDataValues()
{
	var dataValueMap = new Array();
	
	var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();
	
	// Clear existing values and colors
	
	$( '[name="entryfield"]' ).val( '' );
	$( '[name="entryselect"]' ).val( '' );
	
	$( '[name="entryfield"]' ).css( 'background-color', COLOR_WHITE );
	$( '[name="entryselect"]' ).css( 'background-color', COLOR_WHITE );
	
	$( '[name="min"]' ).html( '' );
	$( '[name="max"]' ).html( '' );
	
	$.getJSON( 'getDataValues.action', { periodId:periodId, dataSetId:dataSetId }, function( json ) 
	{
		// Set data values, works for select lists too as data value = select value
	
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
			
			if ( dataValue && ( ( value.min && new Number( dataValue ) < new Number( value.min ) ) 
				|| ( value.max && new Number( dataValue ) > new Number( value.max ) ) ) )
			{
				$( valFieldId ).css( 'background-color', COLOR_ORANGE );
			}
			
			currentMinMaxValueMap[minId] = value.min;
			currentMinMaxValueMap[maxId] = value.max;
		} );
		
		// Update indicator values in form
		
		updateIndicators();
	} );
}

function displayEntryFormCompleted()
{
    addEventListeners();
    
    $( '#validationButton' ).removeAttr( 'disabled' );
    $( '#defaultForm' ).removeAttr( 'disabled' );
    
    dataEntryFormIsLoaded = true;
    hideLoader();
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

    field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( ++index ) + '"]' );
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

    field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );

    while ( field )
    {
        if ( field.is( ':disabled' ) || field.is( ':hidden' ) )
        {
            field = $( 'input[name="entryfield"][tabindex="' + ( --index ) + '"]' );
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

function validateCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_complete );

    if ( confirmed )
    {
		var periodId = $( '#selectedPeriodId' ).val();
    	var dataSetId = $( '#selectedDataSetId' ).val();
		
        $( '#completeButton' ).attr( 'disabled', 'disabled' );
        $( '#undoButton' ).removeAttr( 'disabled' );

        $.getJSON( 'getValidationViolations.action', { periodId:periodId, dataSetId:dataSetId }, registerCompleteDataSet ).error( function()
        {
            $( '#completeButton' ).removeAttr( 'disabled' );
            $( '#undoButton' ).attr( 'disabled', 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    }
}

function registerCompleteDataSet( json )
{
	var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();
		
    if ( json.response == 'success' )
    {
        $.getJSON( 'registerCompleteDataSet.action', { periodId:periodId, dataSetId:dataSetId }, function()
        {
        } ).error( function()
        {
            $( '#completeButton' ).removeAttr( 'disabled' );
            $( '#undoButton' ).attr( 'disabled', 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    } 
    else
    {
    	validate();
    }
}

function undoCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_undo );

    if ( confirmed )
    {
		var periodId = $( '#selectedPeriodId' ).val();
    	var dataSetId = $( '#selectedDataSetId' ).val();
		
        $( '#completeButton' ).removeAttr( 'disabled' );
        $( '#undoButton' ).attr( 'disabled', 'disabled' );

        $.getJSON( 'undoCompleteDataSet.action', { periodId:periodId, dataSetId:dataSetId }, function()
        {
        } ).error( function()
        {
            $( '#completeButton' ).attr( 'disabled', 'disabled' );
            $( '#undoButton' ).removeAttr( 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    }
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function displayValidationDialog()
{
	$( '#validationDiv' ).dialog( {
	    modal: true,
	   	title: 'Validation',
	   	width: 800,
	   	height: 400
	} );
}

function validate()
{
	var periodId = $( '#selectedPeriodId' ).val();
    var dataSetId = $( '#selectedDataSetId' ).val();
	
	$( '#validationDiv' ).load( 'validate.action', {
		periodId: periodId, dataSetId: dataSetId },	
		displayValidationDialog
	);
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function displayHistoryDialog( operandName )
{
	$( '#historyDiv' ).dialog( {
	    modal: true,
	   	title: operandName,
	   	width: 580,
	   	height: 710
	} );
}

function viewHist( dataElementId, optionComboId )
{
	var periodId = $( '#selectedPeriodId' ).val();
	
	var dataElementName = dataElements[dataElementId].name;
	var optionComboName = $( '#' + optionComboId + '-optioncombo' ).html();
	var operandName = dataElementName + ' ' + optionComboName;
	
    $( '#historyDiv' ).load( 'viewHistory.action', {
    	dataElementId: dataElementId, optionComboId: optionComboId, periodId: periodId }, function() {
    		displayHistoryDialog( operandName );
    	}
    );
}

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}

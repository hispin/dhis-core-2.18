// Identifiers for which zero values are, insignificant, also used in entry.js, populated in select.vm
var significantZeros = [];

// Associative array with [indicator id, expression] for indicators in form, also used in entry.js
var indicatorFormulas = [];

// Indicates whether any data entry form has been loaded
var dataEntryFormIsLoaded = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';

function addEventListeners()
{
    $( '[name="entryfield"]' ).focus( valueFocus );
    $( '[name="entryselect"]' ).focus( valueFocus );
}

function clearPeriod()
{
    clearListById( 'selectedPeriodIndex' );
    clearEntryForm();
}

function clearEntryForm()
{
    $( '#contentDiv' ).html( '' );
    
    dataEntryFormIsLoaded = false;
}

// -----------------------------------------------------------------------------
// OrganisationUnit Selection
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnits )
{
	currentOrganisationUnitId = orgUnits[0];
	
    $( '#selectedDataSetId' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();

    var url = 'loadDataSets.action';

    clearListById( 'selectedDataSetId' );

    $.getJSON( url, function( json )
    {
        $( '#selectedOrganisationUnit' ).val( json.organisationUnit.name );
        $( '#currentOrganisationUnit' ).html( json.organisationUnit.name );

        addOptionById( 'selectedDataSetId', '-1', '[ ' + i18n_select_data_set + ' ]' );

        for ( i in json.dataSets )
        {
            addOptionById( 'selectedDataSetId', json.dataSets[i].id, json.dataSets[i].name );
        }

        if ( json.dataSetValid && dataSetId != null )
        {
            $( '#selectedDataSetId' ).val( dataSetId );

            if ( json.periodValid && dataEntryFormIsLoaded )
            {
                showLoader();
                loadDataValues();
            }
        } 
        else
        {
            clearPeriod();
        }
    } );
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// Next/Previous Periods Selection
// -----------------------------------------------------------------------------

function nextPeriodsSelected()
{
    displayPeriodsInternal( true, false );
}

function previousPeriodsSelected()
{
    displayPeriodsInternal( false, true );
}

function displayPeriodsInternal( next, previous )
{
    disableNextPrevButtons();

    var url = 'loadNextPreviousPeriods.action?next=' + next + '&previous=' + previous;

    clearListById( 'selectedPeriodIndex' );

    $.getJSON( url, function( json )
    {
        addOptionById( 'selectedPeriodIndex', '-1', '[ ' + i18n_select_period + ' ]' );

        for ( i in json.periods )
        {
            addOptionById( 'selectedPeriodIndex', i, json.periods[i].name );
        }

        enableNextPrevButtons();
    } );
}

function disableNextPrevButtons()
{
    $( '#nextButton' ).attr( 'disabled', 'disabled' );
    $( '#prevButton' ).attr( 'disabled', 'disabled' );
}

function enableNextPrevButtons()
{
    $( '#nextButton' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
}

// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

function dataSetSelected()
{
    $( '#selectedPeriodIndex' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    var periodIndex = $( '#selectedPeriodIndex' ).val();

    if ( dataSetId && dataSetId != -1 )
    {
        var url = 'loadPeriods.action?dataSetId=' + dataSetId;

        clearListById( 'selectedPeriodIndex' );

        $.getJSON( url, function( json )
        {
            indicatorFormulas = json.indicatorFormulas;

            addOptionById( 'selectedPeriodIndex', '-1', '[ ' + i18n_select_period + ' ]' );

            for ( i in json.periods )
            {
                addOptionById( 'selectedPeriodIndex', i, json.periods[i].name );
            }

            if ( json.periodValid && periodIndex != null )
            {
                showLoader();
                $( '#selectedPeriodIndex' ).val( periodIndex );
                $( '#contentDiv' ).load( 'loadForm.action', loadDataValuesAndDisplayModes );
            } 
            else
            {
                clearEntryForm();
            }
        } );
    }
}

// -----------------------------------------------------------------------------
// DisplayMode Selection
// -----------------------------------------------------------------------------

function displayModeSelected()
{
    showLoader();

    var url = 'loadForm.action?displayMode=' + $( "input[name='displayMode']:checked" ).val();

    $( '#contentDiv' ).load( url, loadDataValues );
}

// -----------------------------------------------------------------------------
// Period Selection
// -----------------------------------------------------------------------------

function periodSelected()
{
    var periodName = $( '#selectedPeriodIndex :selected' ).text();

    $( '#currentPeriod' ).html( periodName );

    var periodIndex = $( '#selectedPeriodIndex' ).val();
    
    if ( periodIndex && periodIndex != -1 )
    {
        showLoader();
        
        if ( dataEntryFormIsLoaded )
        {
        	loadDataValuesAndDisplayModes();
        }
        else
        {
        	var url = 'loadForm.action?selectedPeriodIndex=' + periodIndex;
        	
        	$( '#contentDiv' ).load( url, loadDataValuesAndDisplayModes );
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

function loadDataValuesAndDisplayModes()
{
	insertDataValues();
	setDisplayModes();
	displayEntryFormCompleted();
}

function insertDataValues()
{
	var valueMap = new Array();
	
	var periodIndex = $( '#selectedPeriodIndex' ).val();
	
	// Clear existing values and colors
	
	$( '[name="entryfield"]' ).val( '' );
	$( '[name="entryselect"]' ).val( '' );
	
	$( '[name="entryfield"]' ).css( 'background-color', COLOR_WHITE );
	$( '[name="entryselect"]' ).css( 'background-color', COLOR_WHITE );
	
	$( '[name="min"]' ).html( '' );
	$( '[name="max"]' ).html( '' );
	
	$.getJSON( 'getDataValues.action', { selectedPeriodIndex:periodIndex }, function( json ) 
	{
		// Set data values, works for select lists too as data value = select value
	
		$.each( json.dataValues, function( i, value )
		{
			var fieldId = '#' + value.id + '-val';
			
			if ( $( fieldId ) )
			{
				$( fieldId ).val( value.val );
			}
			
			valueMap[value.id] = value.val;
		} );
		
		// Set min-max values and colorize violation fields
		
		$.each( json.minMaxDataElements, function( i, value )
		{
			var minFieldId = '#' + value.id + '-min';
			var maxFieldId = '#' + value.id + '-max';
			var valFieldId = '#' + value.id + '-val';
			
			if ( $( minFieldId ) )
			{
				$( minFieldId ).html( value.min );
			}
			
			if ( $( maxFieldId ) )
			{
				$( maxFieldId ).html( value.max );
			}
			
			var dataValue = valueMap[value.id];
			
			if ( dataValue && ( ( value.min && new Number( dataValue ) < new Number( value.min ) ) 
				|| ( value.max && new Number( dataValue ) > new Number( value.max ) ) ) )
			{
				$( valFieldId ).css( 'background-color', COLOR_ORANGE );
			}
		} );
	} );
}

function setDisplayModes()
{
    $.getJSON( 'loadDisplayModes.action', function( json )
    {
        $( '#displayModeCustom' ).removeAttr( 'disabled' );
        $( '#displayModeSection' ).removeAttr( 'disabled' );
        $( '#displayModeDefault' ).removeAttr( 'disabled' );

        $( '#displayModeCustom' ).removeAttr( 'checked' );
        $( '#displayModeSection' ).removeAttr( 'checked' );
        $( '#displayModeDefault' ).removeAttr( 'checked' );

        if ( json.displayMode == 'customform' )
        {
            $( '#displayModeCustom' ).attr( 'checked', 'checked' );
        } 
        else if ( json.displayMode == 'sectionform' )
        {
            $( '#displayModeSection' ).attr( 'checked', 'checked' );
        } 
        else
        {
            $( '#displayModeDefault' ).attr( 'checked', 'checked' );
        }

        if ( !json.customForm )
        {
            $( '#displayModeCustom' ).attr( 'disabled', 'disabled' );
        }
        if ( !json.sectionForm )
        {
            $( '#displayModeSection' ).attr( 'disabled', 'disabled' );
        }
    } );
}

function displayEntryFormCompleted()
{
    addEventListeners();
    enable( 'validationButton' );
    updateIndicators();
    dataEntryFormIsLoaded = true;
    hideLoader();
}

function valueFocus( e )
{
	var id = e.target.id;

	var dataElementId = id.split( '-' )[0];
	var optionComboId = id.split( '-' )[1];
	
	var dataElementName = $( '#' + dataElementId + '-dataelement' ).text();
	var optionComboName = $( '#' + optionComboId + '-optioncombo' ).text();
	
	$( "#currentDataElement" ).html( dataElementName + ' ' + optionComboName );
}

function keyPress( event, field )
{
    var key = event.keyCode || event.charCode || event.which;

    var focusField = ( key == 13 || key == 40 ) ? getNextEntryField( field )
            : ( key == 38 ) ? getPreviousEntryField( field ) : false;

    if ( focusField )
    {
        focusField.focus();
    }

    return true;
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
        $( '#completeButton' ).attr( 'disabled', 'disabled' );
        $( '#undoButton' ).removeAttr( 'disabled' );

        $.getJSON( 'getValidationViolations.action', registerCompleteDataSet ).error( function()
        {
            $( '#completeButton' ).removeAttr( 'disabled' );
            $( '#undoButton' ).attr( 'disabled', 'disabled' );

            alert( i18n_no_response_from_server );
        } );
    }
}

function registerCompleteDataSet( json )
{
    if ( json.response == 'success' )
    {
        $.getJSON( 'registerCompleteDataSet.action', function()
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
        window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
    }
}

function undoCompleteDataSet()
{
    var confirmed = confirm( i18n_confirm_undo );

    if ( confirmed )
    {
        $( '#completeButton' ).removeAttr( 'disabled' );
        $( '#undoButton' ).attr( 'disabled', 'disabled' );

        $.getJSON( 'undoCompleteDataSet.action', function()
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

function validate()
{
    window.open( 'validate.action', '_blank', 'width=800, height=400, scrollbars=yes, resizable=yes' );
}

// -----------------------------------------------------------------------------
// History
// -----------------------------------------------------------------------------

function viewHist( dataElementId, optionComboId )
{
    viewHistory( dataElementId, optionComboId, true );
}

function viewHistory( dataElementId, optionComboId, showComment )
{
    window.open( 'viewHistory.action?dataElementId=' + dataElementId + '&optionComboId=' + optionComboId
            + '&showComment=' + showComment, '_blank', 'width=580,height=710,scrollbars=yes' );
}

function closeCurrentSelection()
{
    $( '#currentSelection' ).fadeOut();
}

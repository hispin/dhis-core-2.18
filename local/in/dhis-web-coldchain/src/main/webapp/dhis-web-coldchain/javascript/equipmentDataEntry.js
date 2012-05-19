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

// Indicates whether meta data is loaded
var metaDataIsLoaded = false;

// Currently selected organisation unit identifier
var currentOrganisationUnitId = null;

// Currently selected data set identifier
var currentDataSetId = null;

// Current offset, next or previous corresponding to increasing or decreasing
// value with one
var currentPeriodOffset = 0;

// Username of user who marked the current data set as complete if any
var currentCompletedByUser = null;

// Period type object
var periodTypeFactory = new PeriodType();


var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_RED = '#ff8a8a';
var COLOR_ORANGE = '#ff6600';
var COLOR_WHITE = '#ffffff';
var COLOR_GREY = '#cccccc';

var DEFAULT_TYPE = 'int';
var DEFAULT_NAME = '[unknown]';

var FORMTYPE_CUSTOM = 'custom';
var FORMTYPE_SECTION = 'section';
var FORMTYPE_DEFAULT = 'default';



//dataSets = dataSets



function loadMetaData()
{
    var KEY_METADATA = 'metadata';

    $.ajax( {
    	url: 'getMetaData.action',
    	dataType: 'json',
    	success: function( json )
	    {
	        sessionStorage[KEY_METADATA] = JSON.stringify( json.metaData );
	    },
	    complete: function()
	    {
	        var metaData = JSON.parse( sessionStorage[KEY_METADATA] );

	        significantZeros = metaData.significantZeros;
	        dataElements = metaData.dataElements;
	        indicatorFormulas = metaData.indicatorFormulas;
	        dataSets = metaData.dataSets;
	        dataSetAssociationSets = metaData.dataSetAssociationSets;
	        organisationUnitAssociationSetMap = metaData.organisationUnitAssociationSetMap;

	        metaDataIsLoaded = true;
	        selection.responseReceived(); // Notify that meta data is loaded
	        $( '#loaderSpan' ).hide();
	        log( 'Meta-data loaded' );

	        updateForms();
	    }
	} );
}



// -----------------------------------------------------------------------------
// DataSet Selection
// -----------------------------------------------------------------------------

//function dataSetSelected()
function getPeriods( periodType, periodId, periodId, timespan )
{
    $( '#selectedPeriodId' ).removeAttr( 'disabled' );
    $( '#prevButton' ).removeAttr( 'disabled' );
    $( '#nextButton' ).removeAttr( 'disabled' );

    var dataSetId = $( '#selectedDataSetId' ).val();
    
    var dataSetPeriod = dataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];

	// var periodId = $( '#selectedPeriodId' ).val();
	
	$( "#periodId" ).removeAttr( "disabled" );
	
	//var periodId = "";
	
	//alert( periodId );
	//var periodId = $( '#periodId' );
	getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
   
	//var periodType = dataSets[dataSetId].periodType;
   
	/*
	var periodType = "monthly";
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
    */
}

function getAvailablePeriodsPre( selectedDataSetId, periodId, periodId, timespan )
{
	var dataSetId = $( '#selectedDataSetId' ).val();
    
    var dataSetPeriod = dataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];

	getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
 
}

function getAvailablePeriodsNext( selectedDataSetId, periodId, periodId, timespan )
{
	var dataSetId = $( '#selectedDataSetId' ).val();

	var dataSetPeriod = dataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];

	getAvailablePeriodsTemp( periodTypeId, periodId, periodId, timespan );
 
}

function getAvailablePeriodsTemp( periodTypeId, availablePeriodsId, selectedPeriodsId, year )
{
	$.getJSON( "../dhis-web-commons-ajax-json/getAvailablePeriods.action", {
		"periodType": periodTypeId ,
		"year": year },
		function( json ) {
			var availableList = document.getElementById( availablePeriodsId );
			var selectedList = document.getElementById( selectedPeriodsId );
			clearList( availableList );
			
			for ( var i = 0; i < json.periods.length; i++ )
			{
				if ( listContains( selectedList, json.periods[i].externalId ) == false )
				{
					addValue( availableList, json.periods[i].name, json.periods[i].externalId );
				}
			}			
		} );
}


function dataEntryForm()
{
    
    var tempDataSetId = $( '#selectedDataSetId' ).val();
    
    var dataSetPeriod = tempDataSetId.split(":");
	
	var dataSetId = dataSetPeriod[0];
	var periodTypeId = dataSetPeriod[1];
    
	var periodId = $( '#periodId' ).val();
    
    if ( periodId && periodId != -1 )
    {
        showLoader();

        //if ( dataEntryFormIsLoaded )
        //{
            //loadDataValues();
        //}
        //else
        //{
            loadForm( dataSetId );
        //}
    }
}

function loadForm( dataSetId )
{
	//window.location.href = "loadForm.action?dataSetId=" + dataSetId;
	
	
	$( '#contentDiv' ).load( 'loadForm.action', {
        dataSetId : dataSetId
    } );
	
	
	
	
	/*
	if ( storageManager.formExists( dataSetId ) )
    {
        log( 'Loading form locally: ' + dataSetId );

        var html = storageManager.getForm( dataSetId );

        $( '#contentDiv' ).html( html );

        loadDataValues();
    }
    else
    {
        log( 'Loading form remotely: ' + dataSetId );

        $( '#contentDiv' ).load( 'loadForm.action', {
            dataSetId : dataSetId
        }, loadDataValues );
    }
    */
}










/*
function getAvailablePeriods( periodTypeId, availablePeriodsId, selectedPeriodsId, year )
{
	$.getJSON( "../dhis-web-commons-ajax-json/getAvailablePeriods.action", {
		"periodType": $( "#" + periodTypeId ).val(),
		"year": year },
		function( json ) {
			var availableList = document.getElementById( availablePeriodsId );
			var selectedList = document.getElementById( selectedPeriodsId );
			clearList( availableList );
			
			for ( var i = 0; i < json.periods.length; i++ )
			{
				if ( listContains( selectedList, json.periods[i].externalId ) == false )
				{
					addValue( availableList, json.periods[i].name, json.periods[i].externalId );
				}
			}			
		} );
}
*/

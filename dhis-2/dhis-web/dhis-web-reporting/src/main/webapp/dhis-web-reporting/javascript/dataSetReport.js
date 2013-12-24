
dhis2.util.namespace( 'dhis2.dsr' );

dhis2.dsr.currentPeriodOffset = 0;
dhis2.dsr.periodTypeFactory = new PeriodType();

//------------------------------------------------------------------------------
// Get and set methods
//------------------------------------------------------------------------------

function getDataSetReport()
{
    var dataSetReport = {
        ds: $( "#dataSetId" ).val(),
        periodType: $( "#periodType" ).val(),
        pe: $( "#periodId" ).val(),
        ou: selectionTreeSelection.getSelectedUid()[0],
        selectedUnitOnly: $( "#selectedUnitOnly" ).is( ":checked" ),
        offset: dhis2.dsr.currentPeriodOffset
    };
    
    var dims = [];
    
    $( "[name='groupSet']" ).each( function( index, value ) {
    	var dim = $( this ).data( "uid" );
    	var item = $( this ).val();
    	
    	if ( dim && item )
    	{
    		var dimQuery = dim + ":" + item;
    		dims.push( dimQuery );
    	}
    } );
    
    dataSetReport.dimension = dims;
    
    return dataSetReport;
}

function setDataSetReport( dataSetReport )
{
	$( "#dataSetId" ).val( dataSetReport.dataSet );
	$( "#periodType" ).val( dataSetReport.periodType );
	
	dhis2.dsr.currentPeriodOffset = dataSetReport.offset;
	
	displayPeriods();
	$( "#periodId" ).val( dataSetReport.period );
	
	selectionTreeSelection.setMultipleSelectionAllowed( false );
	selectionTree.buildSelectionTree();
	
	$( "body" ).on( "oust.selected", function() 
	{
		$( "body" ).off( "oust.selected" );
		generateDataSetReport();
	} );
}

//------------------------------------------------------------------------------
// Period
//------------------------------------------------------------------------------

function displayPeriods()
{
    var periodType = $( "#periodType" ).val();
    var periods = dhis2.dsr.periodTypeFactory.get( periodType ).generatePeriods( dhis2.dsr.currentPeriodOffset );
    periods = dhis2.dsr.periodTypeFactory.reverse( periods );
    periods = dhis2.dsr.periodTypeFactory.filterFuturePeriodsExceptCurrent( periods );

    $( "#periodId" ).removeAttr( "disabled" );
    clearListById( "periodId" );

    for ( i in periods )
    {
        addOptionById( "periodId", periods[i].iso, periods[i].name );
    }
}

function displayNextPeriods()
{
    if ( dhis2.dsr.currentPeriodOffset < 0 ) // Cannot display future periods
    {
        dhis2.dsr.currentPeriodOffset++;
        displayPeriods();
    }
}

function displayPreviousPeriods()
{
    dhis2.dsr.currentPeriodOffset--;
    displayPeriods();
}

//------------------------------------------------------------------------------
// Run report
//------------------------------------------------------------------------------

//TODO rewrite to use uid only

function drillDownDataSetReport( orgUnitId, orgUnitUid )
{
	selectionTree.clearSelectedOrganisationUnits();
	selectionTreeSelection.select( orgUnitId );
	
	var dataSetReport = getDataSetReport();
	dataSetReport["ou"] = orgUnitUid;
	displayDataSetReport( dataSetReport );
}

function generateDataSetReport()
{
	var dataSetReport = getDataSetReport();
	displayDataSetReport( dataSetReport );
}

function displayDataSetReport( dataSetReport )
{	
    if ( !dataSetReport.ds )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !dataSetReport.pe )
    {
        setHeaderMessage( i18n_select_period );
        return false;
    }
    if ( !selectionTreeSelection.isSelected() )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }
    
    hideHeaderMessage();
    hideCriteria();
    hideContent();
    showLoader();
	
    delete dataSetReport.periodType;
    delete dataSetReport.offset;
    
    var url = "generateDataSetReport.action?ds=" + dataSetReport.ds +
    	"&pe=" + dataSetReport.pe + "&ou=" + dataSetReport.ou +
    	"&selectedUnitOnly=" + dataSetReport.selectedUnitOnly;
    
    $.each( dataSetReport.dimension, function( inx, val ) {
    	url += "&dimension=" + val;
    } );
    
    $.get( url, function( data ) {
    	$( '#content' ).html( data );
    	hideLoader();
    	showContent();
    	setTableStyles();
    } );
}

function exportDataSetReport( type )
{
	var dataSetReport = getDataSetReport();
	
	var url = "generateDataSetReport.action" + 
		"?ds=" + dataSetReport.ds +
	    "&pe=" + dataSetReport.pe +
	    "&selectedUnitOnly=" + dataSetReport.selectedUnitOnly +
	    "&ou=" + dataSetReport.ou +
	    "&type=" + type;
	    
	window.location.href = url;
}

function setUserInfo( username )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?username=" + username, function() {
		$( "#userInfo" ).dialog( {
	        modal : true,
	        width : 350,
	        height : 350,
	        title : "User"
	    } );
	} );	
}

function showCriteria()
{
	$( "#criteria" ).show( "fast" );
}

function hideCriteria()
{
	$( "#criteria" ).hide( "fast" );
}

function showContent()
{
	$( "#content" ).show( "fast" );
	$( ".downloadButton" ).show();
	$( "#interpretationArea" ).autogrow();
}

function hideContent()
{
	$( "#content" ).hide( "fast" );
	$( ".downloadButton" ).hide();
}

function showAdvancedOptions()
{
	$( "#advancedOptionsLink" ).hide();
	$( "#advancedOptions" ).show();
}

//------------------------------------------------------------------------------
// Share
//------------------------------------------------------------------------------

function viewShareForm() // Not in use
{
	$( "#shareForm" ).dialog( {
		modal : true,
		width : 550,
		resizable: false,
		title : i18n_share_your_interpretation
	} );
}

function shareInterpretation()
{
	var dataSetReport = getDataSetReport();
    var text = $( "#interpretationArea" ).val();
    
    if ( text.length && $.trim( text ).length )
    {
    	text = $.trim( text );
    	
	    var url = "../api/interpretations/dataSetReport/" + $( "#currentDataSetId" ).val() +
	    	"?pe=" + dataSetReport.pe +
	    	"&ou=" + dataSetReport.ou;
	    	    
	    $.ajax( url, {
	    	type: "POST",
	    	contentType: "text/html",
	    	data: text,
	    	success: function() {	    		
	    		$( "#interpretationArea" ).val( "" );
	    		setHeaderDelayMessage( i18n_interpretation_was_shared );
	    	}    	
	    } );
    }
}

//------------------------------------------------------------------------------
// Hooks in custom forms - must be present to avoid errors in forms
//------------------------------------------------------------------------------

function onValueSave( fn )
{
	// Do nothing
}

function onFormLoad( fn )
{
	// Do nothing
}

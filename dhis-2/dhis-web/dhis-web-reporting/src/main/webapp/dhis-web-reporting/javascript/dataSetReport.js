
// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( typeof ( selectionTreeSelection ) != "undefined" )
{
    selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitIds );
}

function getPeriods( periodTypeList, availableList, selectedList, timespan )
{
    $( "#periodId" ).removeAttr( "disabled" );

    getAvailablePeriods( periodTypeList, availableList, selectedList, timespan );
}

function validateDataSetReport()
{
    if ( !$( "#dataSetId" ).val() )
    {
        setHeaderMessage( i18n_select_data_set );
        return false;
    }
    if ( !$( "#periodId" ).val() )
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
	
    var dataSetId = $( "#dataSetId" ).val();
    var periodId = $( "#periodId" ).val();
    var selectedUnitOnly = $( "#selectedUnitOnly" ).is( ":checked" ); 
    
    var currentParams = { dataSetId: dataSetId, periodId: periodId, selectedUnitOnly: selectedUnitOnly };
    
    $( '#content' ).load( 'generateDataSetReport.action', currentParams, function() {
    	hideLoader();
    	showContent();
    	pageInit();
    } );
}

function exportDataSetReport( type )
{
	var url = "generateDataSetReport.action?useLast=true" + 
		"&dataSetId=" + $( "#dataSetId" ).val() +
	    "&periodId=" + $( "#periodId" ).val() +
	    "&selectedUnitOnly=" + $( "#selectedUnitOnly" ).val() +
	    "&type=" + type;
	    
	window.location.href = url;
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
}

function hideContent()
{
	$( "#content" ).hide( "fast" );
}

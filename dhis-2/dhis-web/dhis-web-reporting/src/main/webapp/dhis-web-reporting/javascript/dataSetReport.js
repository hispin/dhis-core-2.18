// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

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
    if ( !selectedOrganisationUnitIds || !selectedOrganisationUnitIds.length )
    {
        setHeaderMessage( i18n_select_organisation_unit );
        return false;
    }
    
    hideHeaderMessage();
    hideCriteria();
	$( "#content" ).hide();
	hideContent();
	showLoader();
	
    var dataSetId = $( "#dataSetId" ).val();
    var periodId = $( "#periodId" ).val();
    
    $( '#content' ).load( 'generateDataSetReport.action', { dataSetId: dataSetId, periodId: periodId }, function() {
    	hideLoader();
    	showContent();
    	pageInit();
    } );
}

function exportDataSetReport( type )
{
    var url = "generateDataSetReport.action?useLast=true&dataSetId=" + $( "#dataSetId" ).val() + "&type=" + type;
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

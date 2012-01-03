
// -----------------------------------------------------------------------------
// Global variables
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;
var currentParams = null;

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
    if ( !selectedOrganisationUnitIds || !selectedOrganisationUnitIds.length )
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
    
    currentParams = { dataSetId: dataSetId, periodId: periodId, selectedUnitOnly: selectedUnitOnly };
    
    $( '#content' ).load( 'generateDataSetReport.action', currentParams, function() {
    	hideLoader();
    	showContent();
    	pageInit();
    } );
}

function exportDataSetReport( type )
{
	if ( currentParams != null )
	{
	    var url = "generateDataSetReport.action?useLast=true" + 
	    	"&dataSetId=" + currentParams.dataSetId +
	    	"&periodId=" + currentParams.periodId +
	    	"&selectedUnitOnly=" + currentParams.selectedUnitOnly +
	    	"&type=" + type;
	    
	    window.location.href = url;
	}
}

function showCriteria()
{
	$( "#criteria" ).show( "fast" );
	$( "#content" ).hide();
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

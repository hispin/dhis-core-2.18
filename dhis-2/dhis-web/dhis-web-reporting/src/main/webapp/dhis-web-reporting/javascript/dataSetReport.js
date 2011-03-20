
// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( selectionTreeSelection )
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
    if ( !getListValue( "dataSetId" ) )
    {
        setMessage( i18n_select_data_set );
        return false;
    }
    if ( !getListValue( "periodId" ) )
    {
        setMessage( i18n_select_period );
        return false;
    }
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    
    document.getElementById( "reportForm" ).submit();
}

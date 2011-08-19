//-----------------------------------------------------------------------------
// Step 1 functions
//----------------------------------------------------------------------------

function continueToStepTwo()
{
    if ( validateData() )
    {
        setHeaderWaitMessage( i18n_processing + "..." );
        byId( "tallySheetForm" ).submit();
    }
}

// -----------------------------------------------------------------------------
// Step 2 functions
// ----------------------------------------------------------------------------

function setChecked( id )
{
    var value = byId( "checkbox" + id ).checked;
    byId( "checked" + id ).value = value;

    if ( value == true )
    {
        if ( byId( "rows" + id ).value == 0 )
        {
            byId( "rows" + id ).value = 1;
        }
    }
}

function doRecalculate()
{
    byId( 'recalculate' ).value = true;
    byId( 'configureTallySheetForm' ).submit();
}

function selectAll()
{
    var length = document.configureTallySheetForm.checkbox.length;

    for ( var i = 0; i < length; i++ )
    {
        document.configureTallySheetForm.checkbox[i].checked = true;
        document.configureTallySheetForm.checked[i].value = true;
        if ( document.configureTallySheetForm.rows[i].value == 0 )
        {
            document.configureTallySheetForm.rows[i].value = 1;
        }
    }
}

function selectNone()
{
    var length = document.configureTallySheetForm.checkbox.length;

    for ( var i = 0; i < length; i++ )
    {
        document.configureTallySheetForm.checkbox[i].checked = false;
        document.configureTallySheetForm.checked[i].value = false;
    }
}

function generatePdf()
{
    byId( 'configureTallySheetForm' ).action = "generateTallySheetPDF.action";
    byId( 'configureTallySheetForm' ).submit();
    byId( 'configureTallySheetForm' ).action = "configureTallySheetGenerator.action";
}

// -----------------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------------

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

function validateData()
{
    var tallySheetName = byId( "tallySheetName" ).value;

    if ( !getListValue( "selectedDataSetId" ) || getListValue( "selectedDataSetId" ) == "null" )
    {
        setHeaderDelayMessage( i18n_select_data_set );
        return false;
    }

    if ( !tallySheetName )
    {
        setHeaderDelayMessage( i18n_type_tally_sheet_name );
        return false;
    }

    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setHeaderDelayMessage( i18n_select_organisation_unit );
        return false;
    }

    return true;
}
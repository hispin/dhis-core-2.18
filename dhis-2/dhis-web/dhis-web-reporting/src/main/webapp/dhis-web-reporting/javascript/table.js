
// -----------------------------------------------------------------------------
// Save ReportTable
// -----------------------------------------------------------------------------

function saveTable()
{
    if ( validateCollections() )
    {
        var url = "validateTable.action?id=" + getFieldValue( "tableId" ) + "&name=" + getFieldValue( "tableName" );
        
        var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( saveTableReceived );
        request.send( url );
    }
}

function saveTableReceived( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == "input" )
    {
        setMessage( message );
        
        return false;
    }
    else if ( type == "success" )
    {        
        selectTableForm();
        
        document.getElementById( "tableForm" ).submit();
    }
}

function selectTableForm()
{
    selectAllById( "selectedDataElements" );
    selectAllById( "selectedIndicators" );
    selectAllById( "selectedDataSets" );
    selectAllById( "selectedPeriods" );
    selectAllById( "selectedOrganisationUnits" );   
}

// -----------------------------------------------------------------------------
// Remove
// -----------------------------------------------------------------------------

function removeTable( tableId, tableName )
{
	removeItem( tableId, tableName, i18n_confirm_delete, "removeTable.action" );
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateCollections()
{
    if ( isChecked( "regression" ) && document.getElementById( "selectedOrganisationUnits" ).options.length > 1 )
    {
        setMessage( i18n_cannot_include_more_organisation_unit_regression );
        
        return false;
    }
        
    if ( !hasElements( "selectedDataElements" ) && !hasElements( "selectedIndicators" ) && !hasElements( "selectedDataSets" ) )
    {
        setMessage( i18n_must_select_at_least_one_indictor_data_element_data_set );
        
        return false;
    }
        
    if ( !hasElements( "selectedOrganisationUnits" ) && !organisationUnitReportParamsChecked() )
    {
        setMessage( i18n_must_select_at_least_one_unit );
        
        return false;
    }
    
    if ( bothOrganisationUnitReportParamsChecked() )
    {
    	setMessage( i18n_cannot_select_orgunit_and_parent_orgunit_param );
    	
    	return false;
    }
    
    if ( !hasElements( "selectedPeriods" ) && !relativePeriodsChecked() )
    {
        setMessage( i18n_must_select_at_least_one_period );
        
        return false;
    }
        
    return true;
}

function isTrue( elementId )
{
    var value = document.getElementById( elementId ).value;
    
    return value && value == "true" ? true : false;
}

function organisationUnitReportParamsChecked()
{
    if ( isChecked( "paramParentOrganisationUnit" ) == true ||
         isChecked( "paramOrganisationUnit" ) == true )
    {
        return true;
    }
    
    return false;
}

function bothOrganisationUnitReportParamsChecked()
{
	if ( isChecked( "paramParentOrganisationUnit" ) == true &&
         isChecked( "paramOrganisationUnit" ) == true )
    {
        return true;
    }
    
    return false;
}

// -----------------------------------------------------------------------------
// Details
// -----------------------------------------------------------------------------

function showTableDetails( tableId )
{
	var request = new Request();
    request.setResponseTypeXML( 'reportTable' );
    request.setCallbackSuccess( tableReceived );
    request.send( 'getTable.action?id=' + tableId );	
}

function tableReceived( xmlObject )
{
	setInnerHTML( 'nameField', getElementValue( xmlObject, 'name' ) );
	setInnerHTML( 'tableNameField', getElementValue( xmlObject, 'tableName' ) );
	setInnerHTML( 'indicatorsField', getElementValue( xmlObject, 'indicators' ) );
	setInnerHTML( 'periodsField', getElementValue( xmlObject, 'periods' ) );
	setInnerHTML( 'unitsField', getElementValue( xmlObject, 'units' ) );
	setInnerHTML( 'doIndicatorsField', parseBool( getElementValue( xmlObject, 'doIndicators' ) ) );
	setInnerHTML( 'doPeriodsField', parseBool( getElementValue( xmlObject, 'doPeriods' ) ) );
	setInnerHTML( 'doUnitsField', parseBool( getElementValue( xmlObject, 'doUnits' ) ) );
	
	showDetails();
}

function parseBool( bool )
{
	return ( bool == "true" ) ? i18n_yes : i18n_no;
}

// -----------------------------------------------------------------------------
// Regression
// -----------------------------------------------------------------------------

function toggleRegression()
{
    if ( document.getElementById( "regression" ).checked )
    {
        check( "doIndicators" );
        uncheck( "doOrganisationUnits" );
        uncheck( "doPeriods" );
        
        disable( "doOrganisationUnits" );
        disable( "doPeriods" );
    }
    else
    {
        enable( "doOrganisationUnits" );
        enable( "doPeriods" );
    }
}

// -----------------------------------------------------------------------------
// Dashboard
// -----------------------------------------------------------------------------

function addReportTableToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_report_table_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        request.send( "addReportTableToDashboard.action?id=" + id );
    }
}


// -----------------------------------------------------------------------------
// DataMartExport
// -----------------------------------------------------------------------------

function exportDataValue()
{
    if ( validateDataValueExportForm() )
    {
        var aggregatedData = getListValue( "aggregatedData" );
        
        if ( aggregatedData == "true" )
        {
            var request = new Request();
            request.setResponseTypeXML( 'message' );
            request.setCallbackSuccess( validateAggregatedExportCompleted );
            request.send( "validateAggregatedExport.action" );
        }
        else
        {
            submitDataValueExportForm();
        }
    }
}

function validateAggregatedExportCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var generateDataSource = getListValue( "generateDataSource" );
        
        if ( generateDataSource && generateDataSource == "true" )
        {
            var request = new Request();
            request.sendAsPost( getDataMartExportParams() );
            request.setCallbackSuccess( exportDataMartReceived );
            request.send( "exportDataMart.action" );   
        }
        else
        {
            submitDataValueExportForm();
        }
    }
    else if ( type == 'error' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function exportDataMartReceived( messageElement )
{
    getExportStatus();
}

function getExportStatus()
{
    var url = "getExportStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( exportStatusReceived );    
    request.send( url );
}

function exportStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, "statusMessage" );
    var finished = getElementValue( xmlObject, "finished" );
    
    if ( finished == "true" )
    {        
        submitDataValueExportForm();
    }
    else
    {
        setMessage( statusMessage );
        
        setTimeout( "getExportStatus();", 2000 );
    }
}

// -----------------------------------------------------------------------------
// Supportive methods
// -----------------------------------------------------------------------------

function getDataMartExportParams()
{
    var params = getParamString( "selectedDataSets", "selectedDataSets" );
    
    params += "startDate=" + document.getElementById( "startDate" ).value + "&";
    params += "endDate=" + document.getElementById( "endDate" ).value + "&";
    params += "dataSourceLevel=" + getListValue( "dataSourceLevel" );
    
    return params;
}

// -----------------------------------------------------------------------------
// Export
// -----------------------------------------------------------------------------

function submitDataValueExportForm()
{
    selectAll( document.getElementById( "selectedDataSets" ) );
	
	if ( validateDataValueExportForm() )
	{
	   document.getElementById( "exportForm" ).submit();
	}
}

function setDataType()
{
    var aggregatedData = getListValue( "aggregatedData" );
  
    if ( aggregatedData == "true" )
    {
        showById( "aggregatedDataDiv" );
        hideById( "regularDataDiv" );
    }
    else
    {
        hideById( "aggregatedDataDiv" );
        showById( "regularDataDiv" );
    }
}

// -----------------------------------------------------------------------------
// MetaDataExport
// -----------------------------------------------------------------------------

function submitMetaDataExportForm()
{
    if ( validateMetaDataExportForm() )
    {
       document.getElementById( "exportForm" ).submit();
    }
}

function toggle( knob )
{
    var toggle = (knob == "all" ? true : false);
	
	jQuery.each( jQuery("input[type=checkbox]"), function(i, item){
		item.checked = toggle;
	});
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateMetaDataExportForm()
{
	if ( jQuery("input:checked").length == 0 )
	{
		setMessage( i18n_select_one_or_more_object_types );
		return false;
	}
	
	hideMessage();
	return true;
}

function validateDataValueExportForm()
{    
    if ( selectedOrganisationUnitIds == null || selectedOrganisationUnitIds.length == 0 )
    {
        setMessage( i18n_select_organisation_unit );
        return false;
    }
    if ( !hasText( "startDate" ) )
    {
        setMessage( i18n_select_startdate );
        return false;
    }
    if ( !hasText( "endDate" ) )
    {
        setMessage( i18n_select_enddate );
        return false;
    }
    if ( !hasElements( "selectedDataSets" ) )
    {
        setMessage( i18n_select_datasets );
        return false;
    }
    
    hideMessage();
    return true;
}

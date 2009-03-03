
// -----------------------------------------------------------------------------
// DataMartExport
// -----------------------------------------------------------------------------

function exportDataValue()
{
    if ( validate() )
    {
        var aggregatedData = getListValue( "aggregatedData" );
        var generateDataSource = getListValue( "generateDataSource" );
        
        if ( aggregatedData == "true" && generateDataSource && generateDataSource == "true" )
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
    var params = getParamString( "selectedDataSets" );
    
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
	
	if ( validate() )
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
// Toggle
// -----------------------------------------------------------------------------

function toggle( knob )
{
    var toggle = false;
	
    if ( knob == "all" )
    {
        toggle = true;
    }
	
    document.getElementById( "dataElements" ).checked = toggle;
    document.getElementById( "dataElementGroups" ).checked = toggle;
    document.getElementById( "dataSets" ).checked = toggle;
    document.getElementById( "indicators" ).checked = toggle;
    document.getElementById( "indicatorGroups" ).checked = toggle;
    document.getElementById( "dataDictionaries" ).checked = toggle;
    document.getElementById( "organisationUnits" ).checked = toggle;
    document.getElementById( "organisationUnitGroups" ).checked = toggle;
    document.getElementById( "organisationUnitGroupSets" ).checked = toggle;
    document.getElementById( "organisationUnitLevels" ).checked = toggle;
    document.getElementById( "validationRules" ).checked = toggle;	
    document.getElementById( "reportTables" ).checked = toggle; 
    document.getElementById( "olapUrls" ).checked = toggle;      
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validate()
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
    
    return true;
}

var selectedOrganisationUnitIds = null;

function setSelectedOrganisationUnitIds( ids )
{
    selectedOrganisationUnitIds = ids;
}

if ( selectionTreeSelection )
{
    selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitIds );
}



var selectedOrganisationUnitId = null;

function setSelectedOrganisationUnitId( ids )
{
    if ( ids != null && ids.length == 1 )
    {
        selectedOrganisationUnitId = ids[0];
    }

    displayCompleteness();
}

selectionTreeSelection.setListenerFunction( setSelectedOrganisationUnitId );

function displayCompleteness()
{
    var criteria = $( "input[name='criteria']:checked" ).val();
    var dataSetList = document.getElementById( "dataSetId" );    
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex ].value;    
    var periodList = document.getElementById( "periodId" );
    var periodId = null;
    
    if ( periodList.disabled == false )
    {
        periodId = periodList.options[ periodList.selectedIndex ].value;
    }
    
    if ( periodId != null && selectedOrganisationUnitId != null )
    {
        clearTable( "resultTable" );
        
        showLoader();
        
        var request = new Request();        
        var url = null;
        
        request.setResponseTypeXML( "dataSetCompletenessResult" );
        
        if ( dataSetId == "ALL" )
        {            
            // -----------------------------------------------------------------
            // Display completeness by DataSets
            // -----------------------------------------------------------------
            
            url = "getDataCompleteness.action?periodId=" + periodId + 
                  "&organisationUnitId=" + selectedOrganisationUnitId +
                  "&criteria=" + criteria;
            
            request.setCallbackSuccess( displayCompletenessByDataSetReceived );
        }
        else
        {
            // -----------------------------------------------------------------
            // Display completeness by child OrganisationUnits for a DataSet
            // -----------------------------------------------------------------
            
            url = "getDataCompleteness.action?periodId=" + periodId + 
                  "&organisationUnitId=" + selectedOrganisationUnitId +
                  "&dataSetId=" + dataSetId +
                  "&criteria=" + criteria;
            
            request.setCallbackSuccess( displayCompletenessByOrganisationUnitReceived );
        }               
        
        request.send( url );
    }
}

function clearTable( tableId )
{
    var table = document.getElementById( tableId );
    
    while ( table.rows.length >  0 )
    {
        table.deleteRow( 0 );
    }
}

function displayCompletenessByDataSetReceived( xmlObject )
{
    var headerText = i18n_dataset;
    
    displayCompletenessTable( xmlObject, headerText );
}

function displayCompletenessByOrganisationUnitReceived( xmlObject )
{
    var headerText = i18n_organisation_unit;
    
    displayCompletenessTable( xmlObject, headerText );
}

function displayCompletenessTable( xmlObject, headerText )
{
    hideLoader();
    
    var table = document.getElementById( "resultTable" );
    
    // -------------------------------------------------------------------------
    // Adding header
    // -------------------------------------------------------------------------
    
    var headerRow = table.insertRow( 0 );
    
    var columnWidth = "55px";
    
    var headerA = document.createElement( "th" );
    headerA.innerHTML = headerText;
    headerRow.appendChild( headerA );
    
    var headerB = document.createElement( "th" );
    headerB.innerHTML = i18n_actual;
    headerB.width = columnWidth;
    headerRow.appendChild( headerB );
    
    var headerC = document.createElement( "th" );
    headerC.innerHTML = i18n_target;
    headerC.width = columnWidth;
    headerRow.appendChild( headerC );
    
    var headerD = document.createElement( "th" );
    headerD.innerHTML = i18n_percent;
    headerD.width = columnWidth;
    headerRow.appendChild( headerD );
    
    var headerE = document.createElement( "th" );
    headerE.innerHTML = i18n_on_time;
    headerE.width = columnWidth;
    headerRow.appendChild( headerE );
    
    var headerF = document.createElement( "th" );
    headerF.innerHTML = i18n_percent;
    headerF.width = columnWidth;
    headerRow.appendChild( headerF );    
    
    // -------------------------------------------------------------------------
    // Adding rows
    // -------------------------------------------------------------------------
    
    var results = xmlObject.getElementsByTagName( "dataSetCompletenessResult" );
    
    var mark = false;
    
    var rowIndex = 1;
    
    for ( var i = 0; i < results.length; i++ )
    {
        var resultName = results[i].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        var sources = results[i].getElementsByTagName( "sources" )[0].firstChild.nodeValue;
        var registrations = results[i].getElementsByTagName( "registrations" )[0].firstChild.nodeValue;
        var percentage = results[i].getElementsByTagName( "percentage" )[0].firstChild.nodeValue;
        var registrationsOnTime = results[i].getElementsByTagName( "registrationsOnTime" )[0].firstChild.nodeValue;
        var percentageOnTime = results[i].getElementsByTagName( "percentageOnTime" )[0].firstChild.nodeValue;
        
        var row = table.insertRow( rowIndex++ );
        
        var cellA = row.insertCell( 0 );
        cellA.style.height = "32px";
        cellA.innerHTML = resultName;
        cellA.className = mark ? "listAlternateRow" : "listRow" ;
        
        var cellB = row.insertCell( 1 );
        cellB.innerHTML = registrations;
        cellB.className = mark ? "listAlternateRow" : "listRow" ;
        
        var cellC = row.insertCell( 2 );
        cellC.innerHTML = sources;
        cellC.className = mark ? "listAlternateRow" : "listRow" ;
        
        var cellD = row.insertCell( 3 );
        cellD.innerHTML = percentage;
        cellD.className = mark ? "listAlternateRow" : "listRow" ;
        
        var cellE = row.insertCell( 4 );
        cellE.innerHTML = registrationsOnTime;
        cellE.className = mark ? "listAlternateRow" : "listRow" ;
                
        var cellF = row.insertCell( 5 );
        cellF.innerHTML = percentageOnTime;
        cellF.className = mark ? "listAlternateRow" : "listRow" ;
        
        mark = mark ? false : true;
	}
}

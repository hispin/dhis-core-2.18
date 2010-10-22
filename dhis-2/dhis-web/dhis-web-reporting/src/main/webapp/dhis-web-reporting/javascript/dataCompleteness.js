
function getPeriods( periodTypeList, availableList, selectedList, timespan )
{
	getAvailablePeriods( periodTypeList, availableList, selectedList, timespan );
	displayCompleteness();
}

function displayCompleteness()
{
    var criteria = $( "input[name='criteria']:checked" ).val();
    var dataSetList = byId( "dataSetId" );    
    var dataSetId = dataSetList.options[ dataSetList.selectedIndex ].value;    
    var periodList = byId( "periodId" );
    var periodId = null;
    
    if ( !periodList.disabled && (periodList.options.length > 0) )
    {
        periodId = periodList.options[ periodList.selectedIndex ].value;
    }
    
    if ( periodId != null )
    {
        clearTable( "resultTable" );
        showLoader();
        
        var request = new Request();   
        var url = "getDataCompleteness.action" 
				+ "?periodId=" + periodId 
				+ "&criteria=" + criteria;
        
        request.setResponseTypeXML( "dataSetCompletenessResult" );
                    
        if ( dataSetId == "ALL" )
        {            
            // -----------------------------------------------------------------
            // Display completeness by DataSets
            // -----------------------------------------------------------------
            
            request.setCallbackSuccess( displayCompletenessByDataSetReceived );
        }
        else
        {
            // -----------------------------------------------------------------
            // Display completeness by child OrganisationUnits for a DataSet
            // -----------------------------------------------------------------
            
            url += "&dataSetId=" + dataSetId;
            
            request.setCallbackSuccess( displayCompletenessByOrganisationUnitReceived );
        }
        request.send( url );
    }
}

function clearTable( tableId )
{
    var table = byId( tableId );
    
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
    
    var table = byId( "resultTable" );
    
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
    var className = "";
    
    for ( var i = 0; i < results.length; i++ )
    {
		className = mark ? "listAlternateRow" : "listRow" ;
	
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
        cellA.className = className
        
        var cellB = row.insertCell( 1 );
        cellB.innerHTML = registrations;
        cellB.className = className
        
        var cellC = row.insertCell( 2 );
        cellC.innerHTML = sources;
        cellC.className = className
        
        var cellD = row.insertCell( 3 );
        cellD.innerHTML = percentage;
        cellD.className = className
        
        var cellE = row.insertCell( 4 );
        cellE.innerHTML = registrationsOnTime;
        cellE.className = className
                
        var cellF = row.insertCell( 5 );
        cellF.innerHTML = percentageOnTime;
        cellF.className = className
        
        mark = !mark;
	}
}

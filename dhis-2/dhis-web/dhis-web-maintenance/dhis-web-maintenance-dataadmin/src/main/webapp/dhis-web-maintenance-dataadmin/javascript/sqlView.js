
function validateAddUpdateSqlView( mode )
{
	var name = $("#name").val(); 
	var sqlquery = $("#sqlquery").val(); 

	$.getJSON(
		"validateAddUpdateSqlView.action",
		{
			"name": name,
			"sqlquery": sqlquery,
			"mode": mode
		},
		function( json )
		{
			if ( json.response == "success" )
			{	
				if ( mode == "add" )
				{
					byId("addSqlViewForm").submit();
					return;
				}
				byId("updateSqlViewForm").submit();
			}
			else if ( json.response == "input" )
			{
				setMessage( json.message );
			}
		}
	);
}
 
function removeSqlViewObject( viewId, viewName )
{
	removeItem( viewId, viewName, i18n_confirm_delete, 'removeSqlViewObject.action' );
}

function showSqlViewDetails( viewId )
{
    var request = new Request();
    request.setResponseTypeXML( 'sqlViewObject' );
    request.setCallbackSuccess( sqlViewDetailsReceived );
    request.send( 'getSqlViewObject.action?id=' + viewId );
}

function sqlViewDetailsReceived( viewElement )
{
    setInnerHTML( 'nameField', getElementValue( viewElement, 'name' ) );
    
	var description = getElementValue( viewElement, 'description' );
    setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    setInnerHTML( 'sqlQueryField', getElementValue( viewElement, 'sqlquery' ) );
    	
	showDetails();
}

/**
 * Execute query to create a new view table
 * 
 * @param viewId the item identifier.
 */
function runSqlViewQuery( viewId )
{
	$.getJSON(
		"executeSqlViewQuery.action",
		{
			"id": viewId   
		},
		function( json )
		{
			setHeaderDelayMessage( json.message );
		}
	);
}

// -----------------------------------------------------------------------
// View data from the specified view table
// -----------------------------------------------------------------------

function showDataSqlViewForm( viewId )
{
	$.getJSON(
		"checkViewTableExistence.action",
		{
			"id": viewId
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				window.location.href = "showDataSqlViewForm.action?viewTableName=" + json.message;
			}
			else if ( json.response == "error" )
			{
				alert( json.message );
			}
		}
	);
}

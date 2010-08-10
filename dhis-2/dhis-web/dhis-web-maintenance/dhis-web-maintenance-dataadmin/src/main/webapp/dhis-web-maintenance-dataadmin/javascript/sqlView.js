/**
 * Sql View
 */

function validateAddUpdateSqlView( mode )
{
	var name = $("#name" ).val(); 
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
    setFieldValue( 'nameField', getElementValue( viewElement, 'name' ) );
    
	var description = getElementValue( viewElement, 'description' );
    setFieldValue( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    setFieldValue( 'sqlQueryField', getElementValue( viewElement, 'sqlquery' ) );
    
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

function selectOrUnselectALL()
{
	var listRadio = document.getElementsByName('resourceTableCheckBox');
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		listRadio.item(i).checked = checkingStatus;
	}
	
	// If true, its means the items unselected yet
	if ( checkingStatus )
	{
		$("#selectAllButton").val( i18n_unselect_all );
	}
	else
	{
		$("#selectAllButton").val( i18n_select_all );
	}
	checkingStatus = !checkingStatus;
}

// -----------------------------------------------------------------------
// Re-generating for the resource tables and the view ones
// -----------------------------------------------------------------------

function regenerateResourceTableAndViewTables()
{
	var organisationUnit = byId( "organisationUnit" ).checked;
    var groupSet = byId( "groupSet" ).checked;
    var dataElementGroupSetStructure = byId( "dataElementGroupSetStructure" ).checked;
    var indicatorGroupSetStructure = byId( "indicatorGroupSetStructure" ).checked;
    var organisationUnitGroupSetStructure = byId( "organisationUnitGroupSetStructure" ).checked;
    var categoryStructure = byId( "categoryStructure" ).checked;
    var categoryOptionComboName = byId( "categoryOptionComboName" ).checked;
    
    if ( organisationUnit || groupSet || dataElementGroupSetStructure || indicatorGroupSetStructure || 
        organisationUnitGroupSetStructure || categoryStructure || categoryOptionComboName )
    {
        setWaitMessage( i18n_regenerating_resource_tables_and_views );
		
        var url = "dropAllSqlViewTables.action";
        
        var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( regenerateResourceTableAndViewTablesReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function regenerateResourceTableAndViewTablesReceived( xmlObject )
{
	if ( xmlObject.getAttribute( 'type' ) == 'success' )
	{
		generateResourceTableForViews();
	}
	else
	{
		alert( i18n_regenerating_failed );
	}
}

function generateResourceTableForViews()
{
    var organisationUnit = byId( "organisationUnit" ).checked;
    var groupSet = byId( "groupSet" ).checked;
    var dataElementGroupSetStructure = byId( "dataElementGroupSetStructure" ).checked;
    var indicatorGroupSetStructure = byId( "indicatorGroupSetStructure" ).checked;
    var organisationUnitGroupSetStructure = byId( "organisationUnitGroupSetStructure" ).checked;
    var categoryStructure = byId( "categoryStructure" ).checked;
    var categoryOptionComboName = byId( "categoryOptionComboName" ).checked;
    
    if ( organisationUnit || groupSet || dataElementGroupSetStructure || indicatorGroupSetStructure || 
        organisationUnitGroupSetStructure || categoryStructure || categoryOptionComboName )
    {
        setWaitMessage( i18n_generating_resource_tables );
            
        var params = "organisationUnit=" + organisationUnit + 
            "&groupSet=" + groupSet + 
            "&dataElementGroupSetStructure=" + dataElementGroupSetStructure +
            "&indicatorGroupSetStructure=" + indicatorGroupSetStructure +
            "&organisationUnitGroupSetStructure=" + organisationUnitGroupSetStructure +
            "&categoryStructure=" + categoryStructure +
            "&categoryOptionComboName=" + categoryOptionComboName;
            
        var url = "generateResourceTable.action";
        
        var request = new Request();
        request.sendAsPost( params );
        request.setCallbackSuccess( generateResourceTableForViewsReceived );
        request.send( url );
    }
    else
    {
        setMessage( i18n_select_options );
    }
}

function generateResourceTableForViewsReceived()
{
	generateAllSqlViewTables();
}

function generateAllSqlViewTables()
{
	$.getJSON(
		"regenerateAllSqlViewTables.action",
		{
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				setMessage( json.message );
			}
			else if ( json.response == "error" )
			{
				setHeaderDelayMessage( json.message );
				
				hideMessage();
			}
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

// -------------------------------------------------------------------------------------
// Inits the first field
// -------------------------------------------------------------------------------------

htmlTables = "";
htmlSortTypes = "";

function initHtml( i18n_resourcetables, i18n_sorttypes )
{
	htmlTables = "<option value=\"null\">[ "+i18n_resourcetables+" ]</option>";
	htmlTables += "<option value=\"orgunitstructure\">orgunitstructure</option>";
	htmlTables += "<option value=\"orgunitgroupsetstructure\">orgunitgroupsetstructure</option>";
	htmlTables += "<option value=\"_dataelementgroupsetstructure\">_dataelementgroupsetstructure</option>";
	htmlTables += "<option value=\"_indicatorgroupsetstructure\">_indicatorgroupsetstructure</option>";
	htmlTables += "<option value=\"_organisationunitgroupsetstructure\">_organisationunitgroupsetstructure</option>";
	htmlTables += "<option value=\"_categorystructure\">_categorystructure</option>";
	htmlTables += "<option value=\"categoryoptioncomboname\">categoryoptioncomboname</option>";

	htmlSortTypes = "<option value=\"null\">[ "+i18n_sorttypes+" ]</option>";
	htmlSortTypes += "<option value=\"ASC\">ASCENDING</option>";
	htmlSortTypes += "<option value=\"DESC\">DESCENDING</option>";
}

function initField( tableComboId, sortComboId )
{
	createTableCombobox( tableComboId );
	createSortTypeCombobox( sortComboId );
}
	
function createTableCombobox( tableComboId )
{
	$("#"+tableComboId).append( htmlTables );
}

function createSortTypeCombobox( sortComboId )
{
	$("#"+sortComboId).append( htmlSortTypes );
}

function loadResourceProperties( tableName, fieldIndex )
{
	if ( tableName != "null" )
	{
		$.getJSON(
			"getResourceProperties.action",
			{
				"name": tableName
			},
			function( json )
			{	
				var list = byId( propertyComboId+fieldIndex );
				clearList( list );
				
				if ( json.message == "success" )
				{
					var properties = json.resourceProperties;
					
					addOptionToList(list, "*", "*");
					addOptionToList(list, "COUNT(*)", "COUNT(*)");

					for ( var i = 0 ; i < properties.length ; i++ )
					{
						addOptionToList(list, properties[i].name, properties[i].name);
					}
				}
			}
		);
	}
	else
	{
		clearList( byId( propertyComboId+fieldIndex ) );
	}
	
	setMessage(tableName);
}

function checkSelectedField( fieldValue, fieldIndex )
{
	var aliasElement = byId( aliasFieldId+fieldIndex );
	var sortElement = byId( sortComboId+fieldIndex );
	var criteriaANDElement = byId( criteriaANDFieldId+fieldIndex );
	var criteriaORElement = byId( criteriaORFieldId+fieldIndex );
	var groupbyElement = byId( groupbyCheckboxId+fieldIndex );

	if ( regexStar.test(fieldValue) )
	{
		aliasElement.value = "";
		aliasElement.disabled = true;
		sortElement.disabled = true;
		sortElement.options[0].selected = true;
		criteriaANDElement.value = "";
		criteriaORElement.value = "";
		criteriaANDElement.disabled = true;
		criteriaORElement.disabled = true;
		groupbyElement.disabled = true;
		groupbyElement.checked = false;
	}
	else if ( regexCountStar.test(fieldValue) )
	{
		aliasElement.disabled = false;
		sortElement.disabled = true;
		sortElement.options[0].selected = true;
		criteriaANDElement.disabled = false;
		criteriaORElement.disabled = false;
		groupbyElement.disabled = true;
		groupbyElement.checked = false;
	}
	else
	{
		aliasElement.disabled = false;
		sortElement.disabled = false;
		criteriaANDElement.disabled = false;
		criteriaORElement.disabled = false;
		groupbyElement.disabled = false;
	}
}

// -------------------------------------------------------------------------------------
// Designs query
// -------------------------------------------------------------------------------------

function showOrHideDesignQueryDiv()
{
	// if true its means div is showing
	if ( !advanceStatus )
	{
		$("#mainDesignQueryDiv").show("fast");
		$("#advance_button").val( i18n_hide_advance );
	}
	else
	{
		$("#mainDesignQueryDiv").hide("fast");
		$("#advance_button").val( i18n_show_advance );
	}
	advanceStatus = !advanceStatus;
}

function setUpQuery()
{
	var index = "";
	var alertMessage = "";
	var shows = document.getElementsByName( showCheckboxId );
	
	for (var i = 0; i < shows.length; i++)
	{
		index = fields[i];
		table = $("#"+resourceComboId+index).val();
		
		if ( table != "null" )
		{
			field = checkFieldValid( table, $("#"+propertyComboId+index).val().trim() );
			alias = $("#"+aliasFieldId+index).val().trim();
			criteriaAND = $("#"+criteriaANDFieldId+index).val().trim();
			criteriaOR = $("#"+criteriaORFieldId+index).val().trim();
			sorttype = $("#"+sortComboId+index).val().trim();
			groupby = $("#"+groupbyCheckboxId+index).is(':checked');
			
			/**
			 * SELECT keyword
			 */
			if ( shows[i].checked )
			{
				if ( selectQuery != "SELECT " )
				{
					selectQuery += ", ";
				}
				selectQuery += field + ((alias != "") == true ? " AS " + alias : alias);
			}
			
			if ( fromQuery != "FROM " )
			{
				fromQuery += ", ";
			}
			fromQuery += table;
			
			/**
			 * WHERE and/or HAVING keyword
			 */
			if ( criteriaAND != "" )
			{
				if ( regexCountOther.test(field) || regexSumOther.test(field) || regexMinOther.test(field) || regexMaxOther.test(field) || regexAvgOther.test(field) || regexAverageOther.test(field) )
				{
					if ( havingbyQuery != "HAVING " )
					{
						havingbyQuery += " AND ";
					}
					havingbyQuery += "("+ field + " " + criteriaAND +")" ;
				}
				else
				{
					if ( whereQuery != "WHERE " )
					{
						whereQuery += " AND ";
					}
					whereQuery += "("+ field + " " + criteriaAND +")" ;
				}
			}
			if ( criteriaOR != "" )
			{
				if ( regexCountOther.test(field) || regexSumOther.test(field) || regexMinOther.test(field) || regexMaxOther.test(field) || regexAvgOther.test(field) || regexAverageOther.test(field) )
				{
					if ( havingbyQuery != "HAVING " )
					{
						havingbyQuery += " OR ";
					}
					havingbyQuery += "("+ field + " " + criteriaOR +")" ;
				}
				else
				{
					if ( whereQuery != "WHERE " )
					{
						whereQuery += " OR ";
					}
					whereQuery += "("+ field + " " + criteriaOR +")" ;
				}
			}
			
			/**
			 * ORDER BY keyword
			 */
			if ( (sortQuery == "ORDER BY ") && ((sorttype == "ASC") || (sorttype == "DESC")) )
			{
				sortQuery += field + " " + sorttype;
			}
			
			/**
			 * GROUP BY keyword
			 */
			if ( groupby )
			{
				if ( groupbyQuery != "GROUP BY " )
				{
					groupbyQuery += ", ";
				}
				groupbyQuery += "(" + field + ")";
			}
		}
		else
		{
			alertMessage += i18n_resourcetable_at_position + " [ " + (i+1) + " ] " + i18n_unselected_yet + "\n";
		}
	}
	
	if ( alertMessage != "" )
	{
		alert( alertMessage );
		return;
	}
	
	combineQuery();
	resetQuery();
}

function checkFieldValid( tableName, fieldValue )
{
	if ( regexCountStar.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexCountStar, "COUNT(*)") );
	}
	else if ( regexCountOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexCountOther, "COUNT(") );
	}
	
	if ( regexSumOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexSumOther, "SUM(") );
	}
	else if ( regexMinOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexMinOther, "MIN(") );
	}
	else if ( regexMaxOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexMaxOther, "MAX(") );
	}
	else if ( regexAvgOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexAvgOther, "AVG(") );
	}
	else if (  regexAverageOther.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexAverageOther, "AVG(") );
	}

	return tableName + "." + fieldValue;
}

function makeUpField( fieldValue )
{
	return fieldValue.replace(/\s+/g, " ").replace(/\s*\)/g, ")" );
}

function combineQuery()
{
	var curValue = $("#sqlquery").val().trim();
	var result = selectQuery + "\n" + fromQuery + "\n";
	result += (whereQuery == "WHERE ") == true ? "" : whereQuery + "\n";
	result += (sortQuery == "ORDER BY ") == true ? "" : sortQuery + "\n";
	result += (havingbyQuery == "HAVING ") == true ? "" : havingbyQuery + "\n";
	result += (groupbyQuery == "GROUP BY ") == true ? "" : groupbyQuery + "\n";
	
	if ( curValue != "SELECT" )
	{
		$("#sqlquery").html(curValue + "; \n\n" + result);
	}
	else
	{
		$("#sqlquery").html(result);
	}
}

function resetQuery()
{
	selectQuery = "SELECT ";
	fromQuery = "FROM ";
	whereQuery = "WHERE ";
	sortQuery = "ORDER BY ";
	havingbyQuery = "HAVING ";
	groupbyQuery = "GROUP BY ";

	table = "";
	field = "";
	alias = "";
	sorttype = "";
}

function generateQueryColumn( mainId, insertPos )
{
	columnIndex ++;
	curAppendedColumnId = "appendedColumn"+columnIndex;
	
	htmlString = "<td id=\""+curAppendedColumnId+"\"><table>";
	htmlString += "<tr><td align=\"center\"><label style=\"color:white\" class=\"ui-widget-header ui-corner-all\">"+i18n_header_field+" "+columnIndex+"</label>";
	htmlString += "<a href=\"javascript:closeAppendedField("+columnIndex+")\" title='"+i18n_close+"'>";
	htmlString += "<img src=\"../images/close.png\" alt='"+i18n_close+"' align=\"right\"/></a></td></tr>";	
	htmlString += "<tr><td><select id=\""+resourceComboId+columnIndex+"\" style=\"width:180px\" onchange=\"loadResourceProperties(this.value, "+columnIndex+")\"></select></td></tr>";
	htmlString += "<tr><td><select id=\""+propertyComboId+columnIndex+"\" style=\"width:180px\" onchange=\"checkSelectedField(this.value, "+columnIndex+")\" onkeyup=\"\" onblur=\"\"></select></td></tr>";
	htmlString += "<tr><td><input id=\""+aliasFieldId+columnIndex+"\" style=\"width:175px\" disabled/></td></tr>";
	htmlString += "<tr><td align=\"center\"><input type=\"checkbox\" name=\""+showCheckboxId+"\"/></td></tr>";
	htmlString += "<tr><td><select id=\""+sortComboId+columnIndex+"\" style=\"width:180px\" disabled></select></td></tr>";
	htmlString += "<tr><td><input type=\"text\" id=\""+criteriaANDFieldId+columnIndex+"\" style=\"width:175px\" disabled/></td></tr>";
	htmlString += "<tr><td><input type=\"text\" id=\""+criteriaORFieldId+columnIndex+"\" style=\"width:175px\" disabled/></td></tr>";
	htmlString += "<tr><td align=\"center\"><input type=\"checkbox\" id=\""+groupbyCheckboxId+columnIndex+"\" disabled/>";
	htmlString += "<a href=\"javascript:appendField("+columnIndex+")\" title='"+i18n_append+"'>";
	htmlString += "<img src=\"../images/add_small.png\" alt='"+i18n_append+"' align=\"right\"/></a></td></tr>";
	htmlString += "</table></td>";
	
	if ( insertPos == insertType )
	{
		$("#appendedColumn"+mainId).after( htmlString );
		fields.splice( getIndexByValue(mainId)+1, 0, columnIndex );
	}
	else
	{
		$("#"+mainId).append(htmlString);
		fields.push( columnIndex );
	}
	
	initField( resourceComboId+columnIndex, sortComboId+columnIndex );
	applyAutocompleteSupporting( criteria_operators, criteriaANDFieldId+columnIndex, $("#"+criteriaANDFieldId+columnIndex).width()+50 );
	applyAutocompleteSupporting( criteria_operators, criteriaORFieldId+columnIndex, $("#"+criteriaORFieldId+columnIndex).width()+50 );
}

function getIndexById( elementId )
{
	var number = eval(elementId.slice(14));
	return $.inArray(number, fields);
}

function getIndexByValue( value )
{
	return $.inArray(value, fields);
}

function applyAutocompleteSupporting(data, fieldElementId, customWidth)
{
	jQuery("#"+fieldElementId).autocomplete(data, {
		multiple: true,
		//autoFill: true,
		width: customWidth
	});
}

function removeAllQueryColumn()
{
	if ( columnIndex == 1 )
	{
		alert( i18n_cannot_remove_any_more );
	}
	else
	{
		while ( columnIndex > 1 )
		{
			if ( byId(curAppendedColumnId) != null )
			{
				$("#"+curAppendedColumnId).remove();
			}
			columnIndex --;
			curAppendedColumnId = "appendedColumn"+columnIndex;
		}
			
		fields = Array("1");
	}
}

function closeAppendedField( removedIndex )
{
	$("#appendedColumn"+removedIndex).remove();
	
	fields = jQuery.grep( fields, function(value) {
				return value != removedIndex
	});
}

function appendField( previousIndex )
{
	generateQueryColumn( previousIndex, insertType );
}
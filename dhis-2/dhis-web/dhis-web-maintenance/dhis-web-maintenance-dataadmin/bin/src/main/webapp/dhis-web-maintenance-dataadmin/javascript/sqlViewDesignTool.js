/**
 * Sql View Design Tool
 */

// -------------------------------------------------------------------------------------
// Inits the first field
// -------------------------------------------------------------------------------------

htmlTables = "";
htmlSortTypes = "";

function initHtml( i18n_resourcetables, i18n_sorttypes )
{
	htmlTables = "<option value=\"null\">[ "+i18n_resourcetables+" ]</option>";
	htmlTables += "<option value=\"_cocn\">_CategoryOptionComboname</option>";
	htmlTables += "<option value=\"_ous\">_OrgUnitStructure</option>";
	htmlTables += "<option value=\"_cs\">_CategoryStructure</option>";
	htmlTables += "<option value=\"_degss\">_DataElementGroupSetStructure</option>";
	htmlTables += "<option value=\"_icgss\">_IndicatorGroupSetStructure</option>";
	htmlTables += "<option value=\"_oustgss\">_OrganisationUnitGroupSetStructure</option>";
	
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

function getJoinTable()
{
	var alertMessage = "";
	
	for (var i = 0; i < fields.length; i++)
	{
		table = $("#"+resourceComboId+fields[i]).val();
		
		if ( table != "null" )
		{	
			/**
			 * FROM keyword
			 */
			if ( getIndexByValue(table, tableList) == -1 )
			{
				tableList.push(table);
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
	else
	{
		getJoinQuery();
	}
}

function getJoinQuery()
{
	$.getJSON(
		"autoJoinResourceTables.action",
		{
			"tableList": tableList
		},
		function( json )
		{
			fromQuery = json.result;
			setUpQuery();
		}
	);
}

function setUpQuery()
{
	var index = "";
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
	}

	combineQuery();
	resetQuery();
}

function checkFieldValid( tableName, fieldValue )
{
	if ( regexCountStar.test(fieldValue) )
	{
		return makeUpField( fieldValue.replace(regexCountStar, "COUNT("+tableName+".*)") );
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
	var result = selectQuery + "\n" + fromQuery;
	result += (whereQuery == "WHERE ") == true ? "" : whereQuery + "\n";
	result += (groupbyQuery == "GROUP BY ") == true ? "" : groupbyQuery + "\n";
	result += (havingbyQuery == "HAVING ") == true ? "" : havingbyQuery + "\n";
	result += (sortQuery == "ORDER BY ") == true ? "" : sortQuery + "\n";
	
	if ( (curValue != "SELECT") && (curValue != '') )
	{
		$("#sqlquery").val(curValue + "; \n\n" + result);
	}
	else
	{
		$("#sqlquery").val(result);
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

	tableList = [];
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
		fields.splice( getIndexByValue(mainId, fields)+1, 0, columnIndex );
	}
	else
	{
		$("#"+mainId).append(htmlString);
		fields.push( columnIndex );
	}
	
	initField( resourceComboId+columnIndex, sortComboId+columnIndex );
	applyAutocompleteSupporting( criteria_operators, criteriaANDFieldId+columnIndex, 225 );
	applyAutocompleteSupporting( criteria_operators, criteriaORFieldId+columnIndex, 225 );
}

function getIndexById( elementId, array )
{
	var number = eval(elementId.slice(14));
	return $.inArray(number, array);
}

function getIndexByValue( value, array )
{
	return $.inArray(value, array);
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
		tableList = Array();
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

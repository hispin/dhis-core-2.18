// -----------------------------------------------------------------------------
// ProgramStage
// -----------------------------------------------------------------------------

function selectDataElements()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availableList").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onclick='select(this)'>" + item.text + "</td>";
			html += "<td><input type='checkbox' name='compulsory' value='" + item.value + "'></td>";
			html += "</tr>";
			selectedList.append( html );
			jQuery( item ).remove();
		}
	});
}

function unSelectDataElements()
{
	var availableList = jQuery("#availableList");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{		
			availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
			item.remove();
		}
	});
}

function unSelectDataElement( element )
{
	element = jQuery(element);	
	jQuery("#availableList").append( "<option value='" + element.attr( "id" ) + "' selected='true'>" + element.find("td:first").text() + "</option>" );
	element.remove();
}

function select( element )
{
	element = jQuery( element ).parent();
	if( element.hasClass( 'selected') ) element.removeClass( 'selected' );
	else element.addClass( 'selected' );
}

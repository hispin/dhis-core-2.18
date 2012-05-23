
function getStageByProgram( programId )
{
	window.location.href = "programStage.action?id=" + programId;
}

function addProgramStage()
{
	var programId = document.getElementById('id').value;

	if( programId == "null"  || programId == "" )
	{
		showWarningMessage( i18n_please_select_program );
	}
	else
	{
		window.location.href="showAddProgramStageForm.action?id=" + programId;
	}
}

function showSortProgramStage()
{
	var programId = document.getElementById('id').value;
	
	if( programId == "null"  || programId == "" )
	{
		showWarningMessage( i18n_please_select_program );
	}
	else
	{
		window.location.href="showSortProgramStageForm.action?id=" + programId;
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramStageDetails( programStageId )
{
	jQuery.getJSON( 'getProgramStage.action', { id: programStageId }, function ( json ) {
		setInnerHTML( 'nameField', json.programStage.name );	
		setInnerHTML( 'descriptionField', json.programStage.description );
		setInnerHTML( 'stageInProgramField', json.programStage.stageInProgram );   
		setInnerHTML( 'scheduledDaysFromStartField', json.programStage.minDaysFromStart ); 

		var irregular = (json.programStage.irregular=='true') ? i18n_yes : i18n_no;
		setInnerHTML( 'irregularField', irregular );  
		setInnerHTML( 'standardIntervalField', json.programStage.standardInterval );  
		
		setInnerHTML( 'dataElementCountField', json.programStage.dataElementCount );   
	   
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// select data-elements
// -----------------------------------------------------------------------------

function selectDataElements()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availableList").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onclick='select(this)'>" + item.text + "</td>";
			html += "<td align='center'><input type='checkbox' name='compulsory' value='" + item.value + "'></td>";
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

//-----------------------------------------------------------------------------
//Move Table Row Up and Down
//-----------------------------------------------------------------------------


function moveUpDataElement()
{
	var selectedList = jQuery("#selectedList");

	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{
			var prev = item.prev('#selectedList tr');
			if (prev.length == 1) 
			{ 
				prev.before(item);
			}
		}
	});
}

function moveDownDataElement()
{
	var selectedList = jQuery("#selectedList");

	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{
			var next = item.next('#selectedList tr');
			if (next.length == 1) 
			{ 
				next.after(item);
			}
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

function repeatableOnChange()
{
	var checked = byId('irregular').checked;
	if( checked )
	{
		enable('standardInterval');
	}
	else
	{
		disable('standardInterval');
		setFieldValue('standardInterval', '0');
	}
}
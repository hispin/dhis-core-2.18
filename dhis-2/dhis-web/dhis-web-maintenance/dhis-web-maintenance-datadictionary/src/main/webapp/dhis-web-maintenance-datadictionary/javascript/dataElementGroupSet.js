// -----------------------------------------------------------------------------
// Java Script Data Element Group Set Object
// -----------------------------------------------------------------------------
function DataElementGroupSet(id, name){
	this.id = id;
	this.name = name;
}

// -----------------------------------------------------------------------------
// Validate Update Data Element Group
// -----------------------------------------------------------------------------

function validateUpdateDataElementGroupSet(){
	$.post("validateDataElementGroupSet.action",
	{
		name:$("#name").val(),
		id:$("#id").val()
	},
	function(message){
		message = message.getElementsByTagName("message")[0];
		var type = message.getAttribute("type");
		if(type=="success"){
			selectAllById("groupMembers");
			document.forms['updateDataElementGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
}

// -----------------------------------------------------------------------------
// Validate Add Data Element Group
// -----------------------------------------------------------------------------

function validateAddDataElementGroupSet(){	

	$.post("validateDataElementGroupSet.action",
	{name:$("#name").val()},
	function(message){
		message = message.getElementsByTagName("message")[0];
		var type = message.getAttribute("type");
		if(type=="success"){
			selectAllById("groupMembers");
			document.forms['addDataElementGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
	
}

// -----------------------------------------------------------------------------
// Delete Data Element Group
// -----------------------------------------------------------------------------

function deleteDataElementGroupSet( id ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteDataElementGroupSet.action?id=" + id;
	}
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDetails( id ){
	$.post("showDataElementGroupSetDetails.action",
	{id:id},
	function( xml ){
		var dataElementGroupSet = xml.getElementsByTagName("dataElementGroupSet")[0];
		var name = dataElementGroupSet.getElementsByTagName("name")[0].firstChild.nodeValue;
		var memberCount = dataElementGroupSet.getElementsByTagName("memberCount")[0].firstChild.nodeValue;
		
		$("#nameField").html(name);
		$("#memberCountField").html(memberCount);
		
		$("#detailsArea").slideDown();	
		
		
	},"xml");
}


// -----------------------------------------------------------------------------
// Filter list data element group set by name
// -----------------------------------------------------------------------------

function filterDataElementSet( value ){	
	
	var html = "";
	var mark = false;
	for(var i=0;i<dataElementGroupSets.length;i++){
		
		var dataElementGroup = dataElementGroupSets[i];
		
		if ( dataElementGroup.name.toLowerCase().indexOf( value.toLowerCase() ) != -1 )
        {	
			if(mark){
				mark=false;
				html += "<tr class='listAlternateRow'>";				
			}else{
				mark=true;
				html += "<tr class='listRow'>";
			}
			html += "<td>" + dataElementGroup.name +"</td>";
			html += "<td>";
				html += "<a href=\"openUpdateDataElementGroupSet.action?id=" + dataElementGroup.id + "\" title=\""+i18n_edit+"\"><img src=\"../images/edit.png\" alt=\""+i18n_edit+"\"></a>";
				html += "<a href=\"javascript:deleteDataElementGroupSet(" + dataElementGroup.id + ")\" title=\""+i18n_delete+"\"><img src=\"../images/delete.png\" alt=\""+i18n_delete+"\"></a>";
				html += "<a href=\"javascript:showDetails(" + dataElementGroup.id + ")\" title=\""+i18n_information+"\"><img src=\"../images/information.png\" alt=\""+i18n_information+"\"></a>";			
				
			html += "</td>";
			html += "</tr>";		
			
        }
	}
	
	$("#contents").html(html);
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initLists()
{
    var list = document.getElementById( 'groupMembers' );
    var id;

    for ( id in groupMembers )
    {
        list.add( new Option( groupMembers[id], id ), null );
    }

    list = document.getElementById( 'availableDataElementGroups' );

    for ( id in availableDataElementGroups )
    {
        list.add( new Option( availableDataElementGroups[id], id ), null );
    }
}

function filterAvailableDataElementGroups()
{
    var filter = document.getElementById( 'availableDataElementGroupsFilter' ).value;
    var list = document.getElementById( 'availableDataElementGroups' );
    
    list.options.length = 0;
    
    for ( var id in availableDataElementGroups )
    {
        var value = availableDataElementGroups[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterGroupMembers()
{
    var filter = document.getElementById( 'groupMembersFilter' ).value;
    var list = document.getElementById( 'groupMembers' );
    
    list.options.length = 0;
    
    for ( var id in groupMembers )
    {
        var value = groupMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addGroupMembers()
{
    var list = document.getElementById( 'availableDataElementGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableDataElementGroups[id];
        
        delete availableDataElementGroups[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElementGroups();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableDataElementGroups[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElementGroups();
}



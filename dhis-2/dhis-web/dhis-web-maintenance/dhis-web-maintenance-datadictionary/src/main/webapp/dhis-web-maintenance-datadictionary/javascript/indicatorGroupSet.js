// -----------------------------------------------------------------------------
// Javascript Indicator Group Set
// -----------------------------------------------------------------------------

function IndicatorGroupSet(id, name){
	this.id = id;
	this.name = name;
}

// -----------------------------------------------------------------------------
// Validate Update  Indicator Group Set
// -----------------------------------------------------------------------------

function validateUpdateIndicatorGroupSet(){
	$.post("validateIndicatorGroupSet.action",
	{
		name:$("#name").val(),
		id:$("#id").val()
	},
	function(message){
		message = message.getElementsByTagName("message")[0];
		var type = message.getAttribute("type");
		if(type=="success"){
			selectAllById("groupMembers");
			document.forms['updateIndicatorGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
}

// -----------------------------------------------------------------------------
// Validate Add Indicator Group Set
// -----------------------------------------------------------------------------

function validateAddIndicatorGroupSet(){	

	$.post("validateIndicatorGroupSet.action",
	{name:$("#name").val()},
	function(message){
		message = message.getElementsByTagName("message")[0];
		var type = message.getAttribute("type");
		if(type=="success"){
			selectAllById("groupMembers");
			document.forms['addIndicatorGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
	
}

// -----------------------------------------------------------------------------
// Delete Indicator Group Set
// -----------------------------------------------------------------------------

function deleteIndicatorGroupSet( id ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteIndicatorGroupSet.action?id=" + id;
	}
}

// -----------------------------------------------------------------------------
// Filter Indicator Group Set
// -----------------------------------------------------------------------------

function filterIndicatorGroupSet( value ){	
	
	var html = "";
	var mark = false;
	for(var i=0;i<indicatorGroupSets.length;i++){
		
		var indicatorGroupSet = indicatorGroupSets[i];
		
		if ( indicatorGroupSet.name.toLowerCase().indexOf( value.toLowerCase() ) != -1 )
        {	
			if(mark){
				mark=false;
				html += "<tr class='listAlternateRow'>";				
			}else{
				mark=true;
				html += "<tr class='listRow'>";
			}
			html += "<td>" + indicatorGroupSet.name +"</td>";
			html += "<td>";
				html += "<a href=\"openUpdateIndicatorGroupSet.action?id=" + indicatorGroupSet.id + "\" title=\""+i18n_edit+"\"><img src=\"../images/edit.png\" alt=\""+i18n_edit+"\"></a>";
				html += "<a href=\"javascript:deleteIndicatorGroupSet(" + indicatorGroupSet.id + ")\" title=\""+i18n_delete+"\"><img src=\"../images/delete.png\" alt=\""+i18n_delete+"\"></a>";
				html += "<a href=\"javascript:showDetails(" + indicatorGroupSet.id + ")\" title=\""+i18n_information+"\"><img src=\"../images/information.png\" alt=\""+i18n_information+"\"></a>";			
			html += "</td>";
			html += "</tr>";		
			
        }
	}
	
	$("#contents").html(html);
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDetails( id ){
	$.post("showIndicatorGroupSetDetails.action",
	{id:id},
	function( xml ){
		var indicatorGroupSet = xml.getElementsByTagName("indicatorGroupSet")[0];
		var name = indicatorGroupSet.getElementsByTagName("name")[0].firstChild.nodeValue;
		var memberCount = indicatorGroupSet.getElementsByTagName("memberCount")[0].firstChild.nodeValue;
		
		$("#nameField").html(name);
		$("#memberCountField").html(memberCount);
		
		$("#detailsArea").slideDown();	
		
		
	},"xml");
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

    list = document.getElementById( 'availableIndicatorGroups' );

    for ( id in availableIndicatorGroups )
    {
        list.add( new Option( availableIndicatorGroups[id], id ), null );
    }
}

function filterAvailableIndicatorGroups()
{
    var filter = document.getElementById( 'availableIndicatorGroupsFilter' ).value;
    var list = document.getElementById( 'availableIndicatorGroups' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicatorGroups )
    {
        var value = availableIndicatorGroups[id];
        
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
    var list = document.getElementById( 'availableIndicatorGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableIndicatorGroups[id];
        
        delete availableIndicatorGroups[id];        
    }
    
    filterGroupMembers();
    filterAvailableIndicatorGroups();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicatorGroups[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableIndicatorGroups();
}



function DataElementGroupSet(id, name){
	this.id = id;
	this.name = name;
}

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
			selectAllById("selectedDataElementGroups");
			document.forms['updateDataElementGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
}


function validateAddDataElementGroupSet(){	

	$.post("validateDataElementGroupSet.action",
	{name:$("#name").val()},
	function(message){
		message = message.getElementsByTagName("message")[0];
		var type = message.getAttribute("type");
		if(type=="success"){
			selectAllById("selectedDataElementGroups");
			document.forms['addDataElementGroupSet'].submit();
		}else{
			setMessage(message.firstChild.nodeValue);
		}
	}
	);	
	
}

function deleteDataElementGroupSet( id ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteDataElementGroupSet.action?id=" + id;
	}
}

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
    var list = document.getElementById( 'selectedDataElementGroups' );
    var id;

    for ( id in selectedDataElementGroups )
    {
        list.add( new Option( selectedDataElementGroups[id], id ), null );
    }

    list = document.getElementById( 'availableDataElementGroups' );

    for ( id in availableDataElementGroups )
    {
        list.add( new Option( availableDataElementGroups[id], id ), null );
    }
}

function filterAvailableDataElementGroups()
{
    var filter = document.getElementById( 'availableFilter' ).value;
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

function filterSelectedDataElementGroups()
{
    var filter = document.getElementById( 'selectedFilter' ).value;
    var list = document.getElementById( 'selectedDataElementGroups' );
    
    list.options.length = 0;
    
    for ( var id in selectedDataElementGroups )
    {
        var value = selectedDataElementGroups[id];
        
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

        selectedDataElementGroups[id] = availableDataElementGroups[id];
        
        delete availableDataElementGroups[id];        
    }
    
    filterSelectedDataElementGroups();
    filterAvailableDataElementGroups();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'selectedDataElementGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableDataElementGroups[id] = selectedDataElementGroups[id];
        
        delete selectedDataElementGroups[id];        
    }
    
    filterSelectedDataElementGroups();
    filterAvailableDataElementGroups();
}



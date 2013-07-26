
dhis2.util.namespace( 'dhis2.db' );

dhis2.db.current;
dhis2.db.currentItem;
dhis2.db.currentShareType;
dhis2.db.currentShareId;

$( document ).ready( function()
{
	$( "#interpretationArea" ).autogrow();
	
	$( document ).click( dhis2.db.hideSearch );

	$( "#searchField" ).focus( function() {
		$( "#searchDiv" ).css( "border-color", "#999" );
	} ).blur( function() {
		$( "#searchDiv" ).css( "border-color", "#aaa" );
	} );

	$( "#searchField" ).focus();
	$( "#searchField" ).keyup( dhis2.db.search );
	
	dhis2.db.renderDashboardListLoadFirst();
} );

//------------------------------------------------------------------------------
// Dashboard
//------------------------------------------------------------------------------

dhis2.db.tmpl = {
	openAddLink: "<li><a href='javascript:dhis2.db.openAddDashboardForm()'>Add new</a></li><li>" +
	             "<a href='javascript:dhis2.db.openManageDashboardForm()'>Manage</a></li>",
	
	dashboardLink: "<li id='dashboard-${id}'><a href='javascript:dhis2.db.renderDashboard( \"${id}\" )'>${name}</a></li>",
	
	moduleIntro: "<li><div class='dasboardIntro'>Click Add new to get started</div></li>",
	
	dashboardIntro: "<li><div class='dasboardIntro'>Add stuff by searching from the search field above</div></li>",
	
	hitHeader: "<li class='hitHeader'>${title}</li>",
	
	hitItem: "<li><a class='viewLink' href='${link}'><img src='../images/${img}.png'>${name}</a>" +
	         "<a class='addLink' href='javascript:dhis2.db.addItemContent( \"${type}\", \"${id}\" )'>Add</a></li>",
		         
	chartItem: "<li><div class='dropItem' id='drop${itemId}'></div></li><li><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>Remove</a>" +
	           "<a href='javascript:dhis2.db.viewImage( \"../api/charts/${id}/data?width=820&height=550\", \"${name}\" )'>View full size</a>" +
	           "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"chart\", \"${name}\" )'>Share</a></div>" +
	           "<img src='../api/charts/${id}/data?width=405&height=295' onclick='dhis2.db.exploreChart( \"${id}\" )' title='Click to explore'></div></li>",
	           
	mapItem: "<li><div class='dropItem' id='drop${itemId}'></div></li><li><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>Remove</a>" +
	         "<a href='javascript:dhis2.db.viewImage( \"../api/maps/${id}/data?width=690\", \"${name}\" )'>View full size</a>" +
	         "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"map\", \"${name}\" )'>Share</a></div>" +
		     "<img src='../api/maps/${id}/data?width=405' onclick='dhis2.db.exploreMap( \"${id}\" )' title='Click to explore'></div></li>"
};

dhis2.db.dashboardReady = function( id )
{
}

dhis2.db.openAddDashboardForm = function()
{
	$( "#addDashboardForm" ).dialog( {
		autoOpen: true,
		modal: true,
		width: 405,
		height: 100,
		resizable: false,
		title: "Add new dashboard"
	} );
}

dhis2.db.openManageDashboardForm = function()
{
	if ( undefined !== dhis2.db.current )
	{
		$.getJSON( "../api/dashboards/" + dhis2.db.current, function( data )
		{
			var name = data.name;
			$( "#dashboardRename" ).val( name );

			$( "#manageDashboardForm" ).dialog( {
				autoOpen: true,
				modal: true,
				width: 405,
				height: 275,
				resizable: false,
				title: name
			} );
		} );
	}
}

dhis2.db.addDashboard = function()
{
	var item = '{"name": "' + $( "#dashboardName" ).val() + '"}';

	$( "#addDashboardForm" ).dialog( "destroy" );
	
	$.ajax( {
		type: "post",
		url: "../api/dashboards",
		data: item,
		contentType: "application/json",
		success: function( data, text, xhr ) {
			$( "#dashboardName" ).val( "" );
			dhis2.db.renderDashboardListLoadFirst();
		}
	} );
}

dhis2.db.renameDashboard = function()
{
	var name = $( "#dashboardRename" ).val();
	
	$( "#manageDashboardForm" ).dialog( "destroy" );
	
	if ( undefined !== dhis2.db.current && undefined !== name && name.trim().length > 0 )
	{
		var data = "{ \"name\": \"" + name + "\"}";
		
		$.ajax( {
	    	type: "put",
	    	url: "../api/dashboards/" + dhis2.db.current,
	    	contentType: "application/json",
	    	data: data,
	    	success: function() {
	    		$( "#dashboardRename" ).val( "" );
	    		dhis2.db.renderDashboardListLoadFirst();
	    	}
	    } );
	}
}

dhis2.db.removeDashboard = function()
{
	if ( undefined !== dhis2.db.current )
	{
		$.ajax( {
			type: "delete",
			url: "../api/dashboards/" + dhis2.db.current,
	    	success: function() {
	    		$( "#manageDashboardForm" ).dialog( "destroy" );
	    		dhis2.db.renderDashboardListLoadFirst();
	    	}
		} );
	}
}

dhis2.db.renderDashboardListLoadFirst = function()
{
	var $l = $( "#dashboardList" );
	
	$l.empty().append( dhis2.db.tmpl.openAddLink );
	
	$.getJSON( "../api/dashboards.json?paging=false&links=false", function( data )
	{
		if ( undefined !== data.dashboards )
		{
			var first;
			
			$.each( data.dashboards, function( index, dashboard )
			{
				$l.append( $.tmpl( dhis2.db.tmpl.dashboardLink, { "id": dashboard.id, "name": dashboard.name } ) );
	
				if ( index == 0 )
				{
					first = dashboard.id;
				}
			} );

			if ( undefined == dhis2.db.current )
			{
				dhis2.db.current = first;
			}
			
			dhis2.db.renderDashboard( dhis2.db.current );		
		}
		else
		{
			dhis2.db.clearDashboard();
			$( "#contentList" ).append( $.tmpl( dhis2.db.tmpl.moduleIntro ) );			
		}
	} );
}

dhis2.db.clearDashboard = function()
{
	$d = $( "#contentList" ).empty();
}

dhis2.db.renderDashboard = function( id )
{
	$( "#dashboard-" + dhis2.db.current ).removeClass( "currentDashboard" );
	
	dhis2.db.current = id;
	
	$( "#dashboard-" + dhis2.db.current ).addClass( "currentDashboard" );
	
	$d = $( "#contentList" ).empty();
	
	$.getJSON( "../api/dashboards/" + id, function( data )
	{
		if ( undefined !== data.items )
		{
			$.each( data.items, function( index, item )
			{
				if ( "chart" == item.type )
				{
					$d.append( $.tmpl( dhis2.db.tmpl.chartItem, { "itemId": item.id, "id": item.chart.id, "name": item.chart.name } ) )
				}
				else if ( "map" == item.type )
				{
					$d.append( $.tmpl( dhis2.db.tmpl.mapItem, { "itemId": item.id, "id": item.map.id, "name": item.map.name } ) )
				}
				else if ( "users" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.users, "Users" );
				}
				else if ( "reportTables" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.reportTables, "Pivot tables" );
				}
				else if ( "reports" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.reports, "Reports" );
				}
				else if ( "resources" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.resources, "Resources" );
				}
			} );
		}
		else
		{
			$d.append( $.tmpl( dhis2.db.tmpl.dashboardIntro ) );
		}
		
		dhis2.db.dashboardReady( id );
	} );
}

dhis2.db.renderLinkItem = function( $d, itemId, contents, title )
{
	var html = 
		"<li><div class='dropItem' id='drop" + itemId + "'></div><div class='item' id='" + itemId + "'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"" + itemId + "\" )'>Remove</a></div>" +
		"<ul class='itemList'><li class='itemTitle'>" + title + "</li>";
	
	$.each( contents, function( index, content )
	{
		html += 
			"<li><a href=''>" + content.name + "</a><a class='removeItemLink' href='javascript:dhis2.db.removeItemContent( \"" + itemId + "\", \"" + content.id + "\" )' title='Remove'>" + 
			"<img src='../images/hide.png'></a></li>";
	} );
	
	html += "</ul></div></li>";
	
	$d.append( html );
}

dhis2.db.addItemContent = function( type, id )
{
	if ( undefined !== dhis2.db.current )
	{
		$.ajax( {
	    	type: "post",
	    	url: "../api/dashboards/" + dhis2.db.current + "/items/content",
	    	data: {
	    		type: type,
	    		id: id
	    	},
	    	success: function() {
	    		dhis2.db.renderDashboard( dhis2.db.current );
	    	}
	    } );
	}
}

dhis2.db.removeItem = function( id )
{
	$.ajax( {
    	type: "delete",
    	url: "../api/dashboards/" + dhis2.db.current + "/items/" + id,
    	success: function() {
    		dhis2.db.renderDashboard( dhis2.db.current );
    	}
    } );
}

dhis2.db.removeItemContent = function( itemId, contentId )
{
	$.ajax( {
    	type: "delete",
    	url: "../api/dashboards/" + dhis2.db.current + "/items/" + itemId + "/content/" + contentId,
    	success: function() {
    		dhis2.db.renderDashboard( dhis2.db.current );
    	}
    } );
}

//------------------------------------------------------------------------------
// Search
//------------------------------------------------------------------------------

dhis2.db.search = function()
{
	var query = $.trim( $( "#searchField" ).val() );
	
	if ( query.length == 0 )
	{
		dhis2.db.hideSearch();
		return false;
	}
	
	var hits = $.get( "../api/dashboards/q/" + query, function( data ) {
		var $h = $( "#hitDiv" );
		dhis2.db.renderSearch( data, $h );
		$h.show();
	} );		
}

dhis2.db.renderSearch = function( data, $h )
{
	$h.empty().append( "<ul>" );
	
	if ( data.searchCount > 0 )
	{
		if ( data.userCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Users" } ) );
			
			for ( var i in data.users )
			{
				var o = data.users[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "profile.action?id=" + o.id, "img": "user_small", "name": o.name, "type": "users", "id": o.id } ) );
			}
		}
		
		if ( data.chartCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Charts" } ) );
			
			for ( var i in data.charts )
			{
				var o = data.charts[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "../dhis-web-visualizer/app/index.html?id=" + o.id, "img": "chart_small", "name": o.name, "type": "chart", "id": o.id } ) );
			}
		}
		
		if ( data.mapCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Maps" } ) );
			
			for ( var i in data.maps )
			{
				var o = data.maps[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "../dhis-web-mapping/app/index.html?id=" + o.id, "img": "map_small", "name": o.name, "type": "map", "id": o.id } ) );
			}
		}
		
		if ( data.reportTableCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Pivot tables" } ) );
			
			for ( var i in data.reportTables )
			{
				var o = data.reportTables[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "../dhis-web-pivot/app/index.html?id=" + o.id, "img": "table_small", "name": o.name, "type": "reportTables", "id": o.id } ) );
			}
		}
		
		if ( data.reportCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Standard reports" } ) );
			
			for ( var i in data.reports )
			{
				var o = data.reports[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "../dhis-web-reporting/getReportParams.action?uid=" + o.id, "img": "standard_report_small", "name": o.name, "type": "reports", "id": o.id } ) );
			}
		}
		
		if ( data.resourceCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Resources" } ) );
			
			for ( var i in data.resources )
			{
				var o = data.resources[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "link": "../api/documents/" + o.id, "img": "document_small", "name": o.name, "type": "resources", "id": o.id } ) );
			}
		}
	}
	else
	{
		$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "No results found" } ) );
	}
}

dhis2.db.hideSearch = function()
{
	$( "#hitDiv" ).hide();
}

//------------------------------------------------------------------------------
// Share
//------------------------------------------------------------------------------

dhis2.db.viewShareForm = function( id, type, name )
{	
	dhis2.db.currentShareId = id;
	dhis2.db.currentShareType = type;
	
	var title = i18n_share_your_interpretation_of + " " + name;
	
	$( "#shareForm" ).dialog( {
		modal: true,
		width: 550,
		resizable: false,
		title: title
	} );
}

dhis2.db.shareInterpretation = function()
{
  var text = $( "#interpretationArea" ).val();
  
  if ( text.length && $.trim( text ).length )
  {
  	text = $.trim( text );
  	
	    var url = "../api/interpretations/" + dhis2.db.currentShareType + "/" + dhis2.db.currentShareId;
	    
	    // TODO url += ( ou && ou.length ) ? "?ou=" + ou : "";
	    
	    $.ajax( {
	    	type: "post",
	    	url: url,
	    	contentType: "text/html",
	    	data: text,
	    	success: function() {
	    		$( "#shareForm" ).dialog( "close" );
	    		$( "#interpretationArea" ).val( "" );
	    		setHeaderDelayMessage( i18n_interpretation_was_shared );
	    	}    	
	    } );
  }
}

dhis2.db.showShareHelp = function()
{
	$( "#shareHelpForm" ).dialog( {
		modal: true,
		width: 380,
		resizable: false,
		title: "Share your data interpretations"
	} );
}

//------------------------------------------------------------------------------
// Chart
//------------------------------------------------------------------------------

dhis2.db.viewImage = function( url, name )
{
  var width = 820;
  var height = 550;
  var title = i18n_viewing + " " + name;

  $( "#chartImage" ).attr( "src", url );
  $( "#chartView" ).dialog( {
      autoOpen : true,
      modal : true,
      height : height + 65,
      width : width + 25,
      resizable : false,
      title : title
  } );
}

dhis2.db.exploreChart = function( uid )
{
	window.location.href = "../dhis-web-visualizer/app/index.html?id=" + uid;
}

dhis2.db.exploreMap = function( uid )
{
	window.location.href = "../dhis-web-mapping/app/index.html?id=" + uid;
}


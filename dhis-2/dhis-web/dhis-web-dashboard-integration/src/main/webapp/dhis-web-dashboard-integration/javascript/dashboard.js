
dhis2.util.namespace( 'dhis2.db' );

dhis2.db.current;
dhis2.db.currentItem;
dhis2.db.currentItemPos;
dhis2.db.currentShareType;
dhis2.db.currentShareId;

// TODO remove position from template
// TODO support table as link and embedded
// TODO double horizontal size
// TODO dashboard list horizontal scroll
// TODO report type in link

//------------------------------------------------------------------------------
// Document ready
//------------------------------------------------------------------------------

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
	dashboardLink: "<li id='dashboard-${id}'><a href='javascript:dhis2.db.renderDashboard( \"${id}\" )'>${name}</a></li>",

	moduleIntro: "<li><div class='dasboardIntro'>${i18n_click}</div></li>",

	dashboardIntro: "<li><div class='dasboardIntro'>${i18n_add}</div>" +
			        "<div class='dasboardTip'>${i18n_arrange}</div></li>",
	
	hitHeader: "<li class='hitHeader'>${title}</li>",
	
	hitItem: "<li><a class='viewLink' href='${link}'><img src='../images/${img}.png'>${name}</a>" +
	         "{{if canManage}}<a class='addLink' href='javascript:dhis2.db.addItemContent( \"${type}\", \"${id}\" )'>Add</a>{{/if}}</li>",

	chartItem: "<li id='liDrop-${itemId}' class='liDropItem'><div class='dropItem' id='drop-${itemId}' data-item='${itemId}'></div></li>" +
	           "<li id='li-${itemId}' class='liItem'><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>${i18n_remove}</a>" +
	           "<a href='javascript:dhis2.db.viewImage( \"../api/charts/${id}/data?width=820&height=550\", \"${name}\" )'>${i18n_view}</a>" +
	           "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"chart\", \"${name}\" )'>${i18n_share}</a></div>" +
	           "<img src='../api/charts/${id}/data?width=405&height=294' onclick='dhis2.db.exploreChart( \"${id}\" )' title='${i18n_click}'></div></li>",
	           
	mapItem: "<li id='liDrop-${itemId}' class='liDropItem'><div class='dropItem' id='drop-${itemId}' data-item='${itemId}'></div></li>" +
	         "<li id='li-${itemId}' class='liItem'><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>${i18n_remove}</a>" +
	         "<a href='javascript:dhis2.db.viewImage( \"../api/maps/${id}/data?width=820&height=550\", \"${name}\" )'>${i18n_view}</a>" +
	         "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"map\", \"${name}\" )'>${i18n_share}</a></div>" +
		     "<img src='../api/maps/${id}/data?width=405&height=294' onclick='dhis2.db.exploreMap( \"${id}\" )' title='${i18n_click}'></div></li>",
		     
	reportTableItem: "<li id='liDrop-${itemId}' class='liDropItem'><div class='dropItem' id='drop-${itemId}' data-item='${itemId}'></div></li>" +
               "<li id='li-${itemId}' class='liItem'><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>${i18n_remove}</a>" +
               "<a href='javascript:dhis2.db.viewImage( \"../api/reportTables/${id}/data.html\", \"${name}\" )'>${i18n_view}</a>" +
               "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"table\", \"${name}\" )'>${i18n_share}</a></div>" +
               "<div id='pivot-${itemId}' onclick='dhis2.db.exploreReportTable( \"${id}\" )' title='${i18n_click}'></div>" +
               "<script type='text/javascript'>dhis2.db.renderReportTable( '${id}', '${itemId}' );</script></div></li>",
	
	patientTabularReport: "<li id='liDrop-${itemId}' class='liDropItem'><div class='dropItem' id='drop-${itemId}' data-item='${itemId}'></div></li>" +
               "<li id='li-${itemId}' class='liItem'><div class='item' id='${itemId}'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"${itemId}\" )'>${i18n_remove}</a>" +
               "<a href='javascript:dhis2.db.viewImage( \"../api/reportTables/${id}/data.html\", \"${name}\" )'>${i18n_view}</a>" +
               "<a href='javascript:dhis2.db.viewShareForm( \"${id}\", \"table\", \"${name}\" )'>${i18n_share}</a></div>" +
               "<div id='pivot-${itemId}' onclick='dhis2.db.exploreReportTable( \"${id}\" )' title='${i18n_click}'></div>" +
               "<script type='text/javascript'>dhis2.db.renderReportTable( '${id}', '${itemId}' );</script></div></li>"
};

dhis2.db.dashboardReady = function( id )
{
	$( ".item" ).draggable( {
	    containment: "#contentDiv",
	    helper: "clone",
	    stack: ".item",
	    revert: "invalid",
	    start: dhis2.db.dragStart,
	    stop: dhis2.db.dragStop
	} );
	
	$( ".item" ).droppable( {
		accept: ".item",
		over: dhis2.db.dropOver,
		out: dhis2.db.dropOut
	} );

	$( ".dropItem" ).droppable( {
		accept: ".item",
		drop: dhis2.db.dropItem
	} );
	
	$( ".lastDropItem" ).droppable( {
		accept: ".item",
		over: dhis2.db.lastDropOver,
		out: dhis2.db.lastDropOut
	} );	
}

dhis2.db.dragStart = function( event, ui ) {
	$( this ).hide();
	dhis2.db.currentItem = $( this ).attr( "id" );
	dhis2.db.currentItemPos = dhis2.db.getIndex( dhis2.db.currentItem );
}

dhis2.db.dragStop = function( event, ui ) {
	$( this ).show();
	$( ".dropItem" ).not( ".lastDropItem" ).hide();
	$( ".lastDropItem" ).removeClass( "blankDropItem" ).addClass( "blankDropItem" );
	dhis2.db.currentItem = undefined;
	dhis2.db.currentItemPos = undefined;
}

dhis2.db.dropOver = function( event, ui ) {
	var itemId = $( this ).attr( "id" );
	var dropItemId = "drop-" + itemId;
	$( "#" + dropItemId ).show();
}

dhis2.db.dropOut = function( event, ui ) {
	var itemId = $( this ).attr( "id" );
	var dropItemId = "drop-" + itemId;
	$( "#" + dropItemId ).hide();
}

dhis2.db.lastDropOver = function( event, ui ) {
	$( this ).removeClass( "blankDropItem" ).css( "display", "block" );
}

dhis2.db.lastDropOut = function( event, ui ) {
	$( this ).addClass( "blankDropItem" );
}

dhis2.db.dropItem = function( event, ui ) {
	var destItemId = $( this ).data( "item" );
	var position = dhis2.db.getIndex( destItemId );
	
	dhis2.db.moveItem( dhis2.db.currentItem, destItemId, position );
}

dhis2.db.scrollLeft = function()
{
	$( "#dashboardListWrapper" ).animate( {
		scrollTop: "-=29"
	}, 180 );
}

dhis2.db.scrollRight = function()
{
	$( "#dashboardListWrapper" ).animate( {
		scrollTop: "+=29"
	}, 180 );
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
			var location = xhr.getResponseHeader( "Location" );
			
			if ( location !== undefined && location.lastIndexOf( "/" ) != -1 )
			{
				var itemId = location.substring( ( location.lastIndexOf( "/" ) + 1 ) );
				dhis2.db.current = itemId;				
			}
			
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

function updateSharing( dashboard ) {
    $( '#dashboardMenu' ).data( 'canManage', dashboard.access.manage );

    if( dashboard.access.manage ) {
        var manageLink = $( '<a/>' )
            .addClass( 'bold' )
            .text( i18n_manage )
            .attr( 'href', 'javascript:dhis2.db.openManageDashboardForm()' );
        $( '#manageDashboard' ).html( manageLink );

        var sharingLink = $( '<a/>' )
            .addClass( 'bold' )
            .text( i18n_sharing )
            .attr( 'href', 'javascript:showSharingDialog("dashboard", "' + dashboard.id + '")' );
        $( '#manageSharing' ).html( sharingLink );
    } else {
        var manageLink = $( '<a/>' )
            .addClass( 'bold' )
            .css({
                'cursor': 'default',
                'text-decoration': 'none',
                'color': 'black'
            })
            .text( i18n_manage );
        $( '#manageDashboard' ).html( manageLink );

        var sharingLink = $( '<a/>' )
            .addClass( 'bold' )
            .css({
                'cursor': 'default',
                'text-decoration': 'none',
                'color': 'black'
            })
            .text( i18n_sharing );
        $( '#manageSharing' ).html( sharingLink );
    }
}

dhis2.db.renderDashboardListLoadFirst = function()
{
	var $l = $( "#dashboardList" );
	
	$l.empty();
	
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
			$( "#contentList" ).append( $.tmpl( dhis2.db.tmpl.moduleIntro, { "i18n_click": i18n_click_add_new_to_get_started } ) );			
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
            updateSharing( data );

			$.each( data.items, function( index, item )
			{
				var position = index - 1;
				
				if ( "chart" == item.type )
				{
					$d.append( $.tmpl( dhis2.db.tmpl.chartItem, { "itemId": item.id, "id": item.chart.id, "name": item.chart.name, "position": position, 
						"i18n_remove": i18n_remove, "i18n_view": i18n_view_full_size, "i18n_share": i18n_share, "i18n_click": i18n_click_to_explore_drag_to_new_position } ) )
				}
				else if ( "map" == item.type )
				{
					$d.append( $.tmpl( dhis2.db.tmpl.mapItem, { "itemId": item.id, "id": item.map.id, "name": item.map.name, "position": position,
						"i18n_remove": i18n_remove, "i18n_view": i18n_view_full_size, "i18n_share": i18n_share, "i18n_click": i18n_click_to_explore_drag_to_new_position } ) )
				}
				else if ( "reportTable" == item.type )
				{
					$d.append( $.tmpl( dhis2.db.tmpl.reportTableItem, { "itemId": item.id, "id": item.reportTable.id, "name": item.reportTable.name, "position": position,
						"i18n_remove": i18n_remove, "i18n_view": i18n_view_full_size, "i18n_share": i18n_share, "i18n_click": i18n_click_to_explore_drag_to_new_position } ) )
				}
				else if ( "users" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.users, "Users", position, "../dhis-web-dashboard-integration/profile.action?id=", "" );
				}
				else if ( "reportTables" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.reportTables, "Pivot tables", position, "../dhis-web-pivot/app/index.html?id=", "" );
				}
				else if ( "reports" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.reports, "Reports", position, "../dhis-web-reporting/getReportParams.action?mode=report&uid=", "" );
				}
				else if ( "resources" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.resources, "Resources", position, "../api/documents/", "/data" );
				}
				else if ( "patientTabularReports" == item.type )
				{
					dhis2.db.renderLinkItem( $d, item.id, item.patientTabularReports, "Person tabular reports", position, "../dhis-web-caseentry/app/index.html?type=patientTabularReport&id=", ""  );
				}
			} );
			
			dhis2.db.renderLastDropItem( $d, parseInt( data.items.length - 1 ) );
		}
		else
		{
			$d.append( $.tmpl( dhis2.db.tmpl.dashboardIntro, { "i18n_add": i18n_add_stuff_by_searching, "i18n_arrange": i18n_arrange_dashboard_by_dragging_and_dropping } ) );
		}
		
		dhis2.db.dashboardReady( id );
	} );
}

dhis2.db.renderLinkItem = function( $d, itemId, contents, title, position, baseUrl, urlSuffix )
{
	var html = 
		"<li id='liDrop-" + itemId + "' class='liDropItem'><div class='dropItem' id='drop-" + itemId + "' data-item='" + itemId + "'></div></li>" +
		"<li id='li-" + itemId + "' class='liItem'><div class='item' id='" + itemId + "'><div class='itemHeader'><a href='javascript:dhis2.db.removeItem( \"" + itemId + "\" )'>" + i18n_remove + "</a></div>" +
		"<ul class='itemList'><li class='itemTitle' title='" + i18n_drag_to_new_position + "'>" + title + "</li>";
	
	$.each( contents, function( index, content )
	{
		html += 
			"<li><a href='" + baseUrl + content.id + urlSuffix + "'>" + content.name + "</a><a class='removeItemLink' href='javascript:dhis2.db.removeItemContent( \"" + itemId + "\", \"" + content.id + "\" )' title='" + i18n_remove + "'>" + 
			"<img src='../images/hide.png'></a></li>";
	} );
	
	html += "</ul></div></li>";
	
	$d.append( html );
}

dhis2.db.renderLastDropItem = function( $d, position )
{
	var html = "<li id='liDrop-dropLast' class='liDropItem'><div class='dropItem lastDropItem blankDropItem' id='dropLast' data-item='dropLast'></div></li>";
	
	$d.append( html );
}

dhis2.db.moveItem = function( id, destItemId, position ) 
{
	var $targetDropLi = $( "#liDrop-" + dhis2.db.currentItem );
	var $targetLi = $( "#li-" + dhis2.db.currentItem );
	
	var $destLi = $( "#liDrop-" + destItemId );
	$targetDropLi.insertBefore( $destLi );
	$targetLi.insertBefore( $destLi );
		
	var url = "../api/dashboards/" + dhis2.db.current + "/items/" + id + "/position/" + position;
	
	$.post( url, function() {
	} );
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
    		dhis2.db.currentItem = undefined;
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

dhis2.db.getIndex = function( itemId )
{
	return parseInt( $( ".liDropItem" ).index( $( "#liDrop-" + itemId ) ) );
}

dhis2.db.exploreChart = function( uid )
{
	window.location.href = "../dhis-web-visualizer/app/index.html?id=" + uid;
}

dhis2.db.exploreMap = function( uid )
{
	window.location.href = "../dhis-web-mapping/app/index.html?id=" + uid;
}

dhis2.db.exploreReportTable = function( uid )
{
	window.location.href = "../dhis-web-pivot/app/index.html?id=" + uid;
}

dhis2.db.renderReportTable = function( tableId, itemId )
{
	$.get( "../api/reportTables/" + tableId + "/data.html", function( data ) {
		$( "#pivot-" + itemId ).html( data );
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
    var canManage = $( '#dashboardMenu' ).data( 'canManage' );

	if ( data.searchCount > 0 )
	{
		if ( data.userCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Users" } ) );
			
			for ( var i in data.users )
			{
				var o = data.users[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "profile.action?id=" + o.id, "img": "user_small", "name": o.name, "type": "users", "id": o.id } ) );
			}
		}
		
		if ( data.chartCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Charts" } ) );
			
			for ( var i in data.charts )
			{
				var o = data.charts[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../dhis-web-visualizer/app/index.html?id=" + o.id, "img": "chart_small", "name": o.name, "type": "chart", "id": o.id } ) );
			}
		}
		
		if ( data.mapCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Maps" } ) );
			
			for ( var i in data.maps )
			{
				var o = data.maps[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../dhis-web-mapping/app/index.html?id=" + o.id, "img": "map_small", "name": o.name, "type": "map", "id": o.id } ) );
			}
		}
		
		if ( data.reportTableCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Pivot tables" } ) );
			
			for ( var i in data.reportTables )
			{
				var o = data.reportTables[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../dhis-web-pivot/app/index.html?id=" + o.id, "img": "table_small", "name": o.name, "type": "reportTable", "id": o.id } ) );
			}
		}
		
		if ( data.reportCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Standard reports" } ) );
			
			for ( var i in data.reports )
			{
				var o = data.reports[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../dhis-web-reporting/getReportParams.action?uid=" + o.id, "img": "standard_report_small", "name": o.name, "type": "reports", "id": o.id } ) );
			}
		}
		
		if ( data.resourceCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Resources" } ) );
			
			for ( var i in data.resources )
			{
				var o = data.resources[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../api/documents/" + o.id, "img": "document_small", "name": o.name, "type": "resources", "id": o.id } ) );
			}
		}
		
		if ( data.patientTabularReportCount > 0 )
		{
			$h.append( $.tmpl( dhis2.db.tmpl.hitHeader, { "title": "Tabular reports" } ) );
			
			for ( var i in data.patientTabularReports )
			{
				var o = data.patientTabularReports[i];
				$h.append( $.tmpl( dhis2.db.tmpl.hitItem, { "canManage": canManage, "link": "../dhis-web-caseentry/generateTabularReport.action?uid=" + o.id, "img": "document_small", "name": o.name, "type": "patientTabularReports", "id": o.id} ) );
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

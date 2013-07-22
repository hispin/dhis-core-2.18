
$( document ).ready( function()
{
	$( "#interpretationArea" ).autogrow();
	
	$( document ).click( hideSearch );

	$( "#searchField" ).focus( function() {
		$( "#searchDiv" ).css( "border-color", "#999" );
	} ).blur( function() {
		$( "#searchDiv" ).css( "border-color", "#aaa" );
	} );

	$( "#searchField" ).focus();
	$( "#searchField" ).keyup( search );
	
	var viewportWidth = parseInt( $( window ).width() );
	var linkWidth = parseInt( 334 );
	var chartWidth = parseInt( 325 );
	
	if ( viewportWidth == undefined )
	{
		viewportWidth = parseInt( 1366 );
	}
	
	var noCharts = 2 * Math.floor( ( viewportWidth - linkWidth + 4 ) / chartWidth ); 
		
	$( "#contentDiv" ).load( "provideContent.action?noCharts=" + noCharts + "&_dc=" + getRandomNumber() );
} );

function setAreaItem( area, item )
{
    $.get( "setAreaItem.action", {
        'area' : area,
        'item' : item
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function clearArea( area )
{
    $.get( "clearArea.action", {
        'area' : area
    }, function()
    {
        window.location.href = "index.action";
    } );
}

function viewChart( url, name )
{
    var width = 820;
    var height = 520;
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

function explore( uid )
{
	window.location.href = "../dhis-web-visualizer/app/index.html?id=" + uid;
}

function viewShareForm( uid, name )
{	
	$( "#interpretationChartId" ).val( uid );
	
	var title = i18n_share_your_interpretation_of + " " + name;
	
	$( "#shareForm" ).dialog( {
		modal: true,
		width: 550,
		resizable: false,
		title: title
	} );
}

function shareInterpretation()
{
    var chartId = $( "#interpretationChartId" ).val();
    var text = $( "#interpretationArea" ).val();
    
    if ( text.length && $.trim( text ).length )
    {
    	text = $.trim( text );
    	
	    var url = "../api/interpretations/chart/" + chartId;
	    
	    // TODO url += ( ou && ou.length ) ? "?ou=" + ou : "";
	    
	    $.ajax( url, {
	    	type: "POST",
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

function showShareHelp()
{
	$( "#shareHelpForm" ).dialog( {
		modal: true,
		width: 380,
		resizable: false,
		title: "Share your data interpretations"
	} );
}

function search()
{
	var query = $.trim( $( "#searchField" ).val() );
	
	if ( query.length == 0 )
	{
		hideSearch();
		return false;
	}
	
	var hits = $.get( "../api/dashboards/search/" + query, function( data ) {
		$( "#hitDiv" ).show().html( getSearchResultList( data ) );		
	} );		
}

function getSearchResultList( data )
{
	var html = "<ul>";
	
	if ( data.searchCount > 0 )
	{
		if ( data.userCount > 0 )
		{
			html += "<li class='hitHeader'>Users</li>";			
			for ( var i in data.users )
			{
				var o = data.users[i];
				html += "<li><a href='profile.action?id=" + o.id + "'><img src='../images/user_small.png'>" + o.name + "</a></li>";
			}
		}
		
		if ( data.chartCount > 0 )
		{
			html += "<li class='hitHeader'>Charts</li>";
			for ( var i in data.charts )
			{
				var o = data.charts[i];
				html += "<li><a href='../dhis-web-visualizer/app/index.html?id=" + o.id + "'><img src='../images/chart_small.png'>" + o.name + "</a></li>";
			}
		}
		
		if ( data.mapCount > 0 )
		{
			html += "<li class='hitHeader'>Maps</li>";
			for ( var i in data.maps )
			{
				var o = data.maps[i];
				html += "<li><a href='../dhis-web-mapping/app/index.html?id=" + o.id + "'><img src='../images/map_small.png'>" + o.name + "</a></li>";
			}
		}
		
		if ( data.reportTableCount > 0 )
		{
			html += "<li class='hitHeader'>Pivot tables</li>";
			for ( var i in data.reportTables )
			{
				var o = data.reportTables[i];
				html += "<li><a href='../dhis-web-pivot/app/index.html?id=" + o.id + "'><img src='../images/table_small.png'>" + o.name + "</a></li>";
			}
		}
		
		if ( data.reportCount > 0 )
		{
			html += "<li class='hitHeader'>Standard reports</li>";
			for ( var i in data.reports )
			{
				var o = data.reports[i];
				html += "<li><a href='../dhis-web-reporting/getReportParams.action?uid=" + o.id + "&mode=report'><img src='../images/standard_report_small.png'>" + o.name + "</a></li>";
			}
		}
		
		if ( data.resourceCount > 0 )
		{
			html += "<li class='hitHeader'>Resources</li>";
			for ( var i in data.resources )
			{
				var o = data.resources[i];
				html += "<li><a href='../api/documents/" + o.id + "/data'><img src='../images/document_small.png'>" + o.name + "</a></li>";
			}
		}
	}
	else
	{
		html += "<li class='hitHeader'>No results found</li>";
	}
	
	html += "</ul>";
	
	return html;
}

function hideSearch()
{
	$( "#hitDiv" ).hide();
}

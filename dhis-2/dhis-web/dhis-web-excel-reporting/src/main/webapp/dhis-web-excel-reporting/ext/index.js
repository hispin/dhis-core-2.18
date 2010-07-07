var path = "";
var type = ".action";

var PIE_CHART_TYPE = "PIE_CHART_TYPE";
var LINE_CHART_TYPE = "LINE_CHART_TYPE";
var COLUMN_CHART_TYPE = "COLUMN_CHART_TYPE";
var FULL_CHART_TYPE = "FULL_CHART_TYPE";
var AXIS_X_DE = "AXIS_X_DE";
var AXIS_X_IN = "AXIS_X_IN";
Ext.chart.Chart.CHART_URL = 'ext/resources/charts.swf';

Ext.onReady(function()
{

	pieChartRadio.render( 'pie' );
	lineChartRadio.render( 'line' );
	columnChartRadio.render( 'column' );
	fullChartRadio.render( 'full' );

});


function viewFullChart( xTitle, title, colTitle, lineTitle, store, hasBookmark )
{

	if(hasBookmark==undefined) hasBookmark = true;
	
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
		
	chart = new Ext.chart.ColumnChart({				
				id:'full-chart' + random,	
				store: store,				
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',		
				region: 'center',	
				extraStyle: {
				   legend:
					{
						display: 'bottom',
						padding: 5,
						font:
						{
							family: 'Tahoma',
							size: 13
						}
					},
					xAxis: {
						labelRotation: -60
					}

				},
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle					
				}),							
				tipRenderer : function(chart, record, index, series){
					if(series.yField == 'total'){
						return Ext.util.Format.number(record.data.total, '0,0') + ' in ' + record.data.name;
					}else{
						a = Ext.util.Format.number(record.data.value, '0,0');
						b = Ext.util.Format.number(record.data.total, '0,0');
						return a + "/" + b + ' in ' + record.data.name;
					}
				},
				series: [{
					type: 'column',
					displayName: colTitle,
					yField: 'total',
					style: {
						image:'bar.gif',
						mode: 'stretch',
						color:0x99BBE8
						}
					},{
						type:'line',
						displayName: lineTitle,
						yField: 'value',
						style: {
							color: 0x15428B
						}
				}]

			});
	
	bookmark = new Ext.FormPanel({
            title: 'Bookmark',
			id: 'form-panel-fullchart-' + random,
            region: 'west',
            split: true,
            width: 200,
            collapsible: true,
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
			labelWidth: 75,
			items:[	
					{
						xtype: 'label',						
						fieldLabel: 'Name'														
					},{
						xtype: 'textfield',
						id: 'fullchart-bookmark-name-' + random,
						fieldLabel: 'Name',						
						hideLabel:true,
						width:190
					},{
						xtype: 'label',						
						fieldLabel: 'Descriptions',												
					},{
						xtype: 'textarea',
						id: 'fullchart-bookmark-descriptions-' + random,
						fieldLabel: 'Descriptions',						
						hideLabel:true,
						width:190	
					}
			],
			buttons: [{
				text: 'Save',
				handler: function() {
					name = Ext.getCmp('fullchart-bookmark-name-' + random).getValue();
					descriptions = Ext.getCmp('fullchart-bookmark-descriptions-' + random).getValue();
					bookmarkChart( xTitle, title, store, name, descriptions );
				}
			}]
        });
	
	var window = new Ext.Window(
	{		
		id:'view-line-chart-window'+random,	
		title:'<span>'+ title +'</span>',				
		defaults:{bodyStyle:'padding:2px; border:0px'},
		width:800,
		height:500,
		collapsible: true,
        maximizable: true,
		plain:true,
        layout: 'border'		
	});
	
	window.add(chart);
	
	if( hasBookmark ){
		window.add(bookmark);
	}
	
	window.show();
		

}

function viewPieChart( xTitle, title, store, hasBookmark )
{
	
	if(hasBookmark==undefined) hasBookmark = true;

	var random = (Math.floor(Math.random() *  11) + 1) * 100;	
	
	chart = new Ext.chart.PieChart({				
				id:'pie-chart' + random,	
				store: store,
				dataField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				categoryField: 'name',				
				extraStyle: {
				   legend:
					{
						display: 'bottom',
						padding: 5,
						font:
						{
							family: 'Tahoma',
							size: 13
						}
					}

				},
				region: 'center'
			});
	
	bookmark = new Ext.FormPanel({
            title: 'Bookmark',
			id: 'form-panel-piechart-' + random,
            region: 'west',
            split: true,
            width: 200,
            collapsible: true,
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
			labelWidth: 75,
			items:[	
					{
						xtype: 'label',						
						fieldLabel: 'Name'														
					},{
						xtype: 'textfield',
						id: 'piechart-bookmark-name-' + random,
						fieldLabel: 'Name',						
						hideLabel:true,
						width:190
					},{
						xtype: 'label',						
						fieldLabel: 'Descriptions',												
					},{
						xtype: 'textarea',
						id: 'piechart-bookmark-descriptions-' + random,
						fieldLabel: 'Descriptions',						
						hideLabel:true,
						width:190	
					}
			],
			buttons: [{
				text: 'Save',
				handler: function() {
					name = Ext.getCmp('piechart-bookmark-name-' + random).getValue();
					descriptions = Ext.getCmp('piechart-bookmark-descriptions-' + random).getValue();
					bookmarkChart( xTitle, title, store, name, descriptions );
				}
			}]
        });
	
	var window = new Ext.Window(
	{		
		id:'view-line-chart-window'+random,	
		title:'<span>'+ title +'</span>',				
		defaults:{bodyStyle:'padding:2px; border:0px'},
		width:800,
		height:500,
		collapsible: true,
        maximizable: true,
		plain:true,
        layout: 'border'		
	});
	
	window.add(chart);
	
	if( hasBookmark ){
		window.add(bookmark);
	}
	
	window.show();
		
}


function viewLineChart( xTitle, title, store, hasBookmark )
{
	if(hasBookmark==undefined) hasBookmark = true;
	
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	chart = new Ext.chart.LineChart({				
				id:'linechart' + random,	
				store: store,
				yField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle 
				}),
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				},
				region: 'center'
			});
	
	bookmark = new Ext.FormPanel({
            title: 'Bookmark',
			id: 'form-panel-linechart-' + random,
            region: 'west',
            split: true,
            width: 200,
            collapsible: true,
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
			labelWidth: 75,
			items:[	
					{
						xtype: 'label',						
						fieldLabel: 'Name'														
					},{
						xtype: 'textfield',
						id: 'linechart-bookmark-name-' + random,
						fieldLabel: 'Name',						
						hideLabel:true,
						width:190
					},{
						xtype: 'label',						
						fieldLabel: 'Descriptions',												
					},{
						xtype: 'textarea',
						id: 'linechart-bookmark-descriptions-' + random,
						fieldLabel: 'Descriptions',						
						hideLabel:true,
						width:190	
					}
			],
			buttons: [{
				text: 'Save',
				handler: function() {
					name = Ext.getCmp('linechart-bookmark-name-' + random).getValue();
					descriptions = Ext.getCmp('linechart-bookmark-descriptions-' + random).getValue();
					bookmarkChart( xTitle, title, store, name, descriptions );
				}
			}]
        });
	
	var window = new Ext.Window(
	{		
		id:'view-line-chart-window'+random,	
		title:'<span>'+ title +'</span>',				
		defaults:{bodyStyle:'padding:2px; border:0px'},
		width:800,
		height:500,
		collapsible: true,
        maximizable: true,
		plain:true,
        layout: 'border'		
	});
	
	window.add(chart);
	
	if( hasBookmark ){
		window.add(bookmark);
	}
	
	window.show();
		
}

function viewColumnChart( xTitle, title, store , hasBookmark )
{	
	if(hasBookmark==undefined) hasBookmark = true;
	
	var random = (Math.floor(Math.random() *  11) + 1) * 100;
	
	chart = new Ext.chart.ColumnChart({				
				id:'columnchart' + random,	
				store: store,
				yField: 'value',
				url: Ext.chart.Chart.CHART_URL,
				xField: 'name',
				xAxis: new Ext.chart.CategoryAxis({
					title: xTitle
				}),				
				extraStyle: {
				   xAxis: {
						labelRotation: -60
					}
				},
				region: 'center'
			});
	bookmark = new Ext.FormPanel({
            title: 'Bookmark',
			id: 'form-panel-column-' + random,
            region: 'west',
            split: true,
            width: 200,
            collapsible: true,
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
			labelWidth: 75,
			items:[	
					{
						xtype: 'label',						
						fieldLabel: 'Name'														
					},{
						xtype: 'textfield',
						id: 'columnchart-bookmark-name-' + random,
						fieldLabel: 'Name',						
						hideLabel:true,
						width:190
					},{
						xtype: 'label',						
						fieldLabel: 'Descriptions',												
					},{
						xtype: 'textarea',
						id: 'columnchart-bookmark-descriptions-' + random,
						fieldLabel: 'Descriptions',						
						hideLabel:true,
						width:190	
					}
			],
			buttons: [{
				text: 'Save',
				handler: function() {
					name = Ext.getCmp('columnchart-bookmark-name-' + random).getValue();
					descriptions = Ext.getCmp('columnchart-bookmark-descriptions-' + random).getValue();
					bookmarkChart( xTitle, title, store, name, descriptions );
				}
			}]
        });
	
	var window = new Ext.Window(
	{		
		id:'view-column-chart-window'+random,	
		title:'<span>'+ title +'</span>',				
		defaults:{bodyStyle:'padding:2px; border:0px'},
		width:800,
		height:500,
		collapsible: true,
        maximizable: true,
		plain:true,
        layout: 'border'			
	});
	
	window.add(chart);
	
	if( hasBookmark ){
		window.add(bookmark);
	}
	
	window.show();
		
		
}

function bookmarkChart( xTitle, title, store, name, descriptions)
{
	if( name=='' ){
		alert('Please enter name!');
	}else{
		size = store.getTotalCount();
		
		var json = '{"data":['
		store.each(function( item, i ){
			json += '{"name":"' + item.get('name') + '","value":' + item.get('value') + ', "total":' + item.get('total') + '}';
			if( i < size -1 ){json += ','}
		});
		json += ']}';	
		
		
		
		Ext.Ajax.request({
			url: path + 'bookmarkChart' + type,
			method: 'POST',
			params: { 
				xtitle: xTitle,
				title: title,
				json:  json,
				name: name,
				descriptions: descriptions
			},
			success: function( json ) {
				getAllBookmartChart();
			}
		});
	}
	
}

var pieChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'pie-chart',
	inputValue: PIE_CHART_TYPE	
});

var lineChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'line-chart',
	inputValue: LINE_CHART_TYPE	
});


var columnChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'column-chart',
	inputValue: COLUMN_CHART_TYPE,
	checked: true	
});

var fullChartRadio = new Ext.form.Radio({
	name: 'chart-type',
	id: 'full-chart',
	inputValue: FULL_CHART_TYPE	
});

//	===================================================================================
//	Data Element & Indicator Chart
//	===================================================================================

var dataElementGroupsStore = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStore = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStore = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStore = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

//	===================================================================================
//	Period  Chart
//	===================================================================================


var dataElementGroupsStorePc = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStorePc = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStorePc = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStorePc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

//	===================================================================================
//	Organisation Unit  Chart
//	===================================================================================


var dataElementGroupsStoreOc = new Ext.data.JsonStore({
	url: path + 'getDataElementGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'dataElementGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var dataElementsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataElements' + type,            
	root: 'dataElements',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});

var indicatorGroupsStoreOc = new Ext.data.JsonStore({
	url: path + 'getIndicatorGroups' + type,
	baseParams: { format: 'json', id: "ALL" },
	root: 'indicatorGroups',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var indicatorsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getIndicators' + type,            
	root: 'indicators',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false	
});
	
var periodTypesStoreOc = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStoreOc = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

var organisationUnitStoreOc = new Ext.data.JsonStore({
	url: path + 'getChildrenOrganisationUnit' + type,  
	baseParams: { format: 'json'},	
	root: 'organisationUnits',
	fields: ['id', 'name'],	
	autoLoad: true	
});



function switchAxisxDeIn( a, b)
{
	if($("#"+a).css("display")=='none'){
		$("#"+a).css("display", "block");
	}else{
		$("#"+a).css("display", "none");
	}
	
	if($("#"+b).css("display")=='none'){
		$("#"+b).css("display", "block");
	}else{
		$("#"+b).css("display", "none");
	}
	
}

function deleteBookmark( id )
{
	if(window.confirm(' Do you want delete ?')){
		Ext.Ajax.request({
			url: path + 'deleteBookmarkChart' + type,
			method: 'POST',
			params: {format:'json', id: id},
			success: function( json ) {
				getAllBookmartChart();
			}
		});
	}
}

function getAllBookmartChart()
{
	Ext.Ajax.request({
		url: path + 'getAllBookmarkChart' + type,
		method: 'POST',
		params: {format:'json'},
		success: function( json ) {
			var bookmarks =  Ext.util.JSON.decode(json.responseText).bookmarks;
			
			var html = '<table class="listTable" width="100%">';
			html += '<thead>';
			html += '<tr>';
			html += '<th> Name </th>';
			html += '<th>Descriptions</th>';
			html += '<th></th>';
			html += '</tr>';
			html += '</thead>';
			html += '<tbody id="list">';
			for (var i = 0; i < bookmarks.length; i++) 
			{
				
				html += '<tr class="listRow">';
				html += '<td>' + bookmarks[i].name + '</td>';
				html += '<td><div style="width:100%;height:30px;overflow:auto;">' + bookmarks[i].descriptions + '</div>';
				html += '<td width="100px"><a href="javascript:viewBookmarkChart(' + bookmarks[i].id + ');">';
				html += '<img src="images/chart-icon.png"/>';
				html += '</a>';
				html += '<a href="javascript:deleteBookmark(' + bookmarks[i].id + ');">';
				html += '<img src="../images/delete.png"/>';
				html += '</a>';
				html += '</td>';
				html += '</tr>';
			}			
			html += '</tbody>';	   		   
			html += '</table>';
			
			jQuery('#bookmark-content').html( html );
		}
	});
}

function viewBookmarkChart( id, xTitle, title )
{
	var store = new Ext.data.JsonStore({
		url: path + 'getBookmarkChart' + type,
		baseParams: { format: 'json', id: id },	
		root: 'data',		
		fields: ['name', 'value', 'total'],				
		autoLoad: true	
	});	
	
	var chartType = columnChartRadio.getGroupValue();
	
	if( chartType == FULL_CHART_TYPE ){
		viewFullChart( xTitle, "Full Style Chart", PARENT, ORGANISATION_UNIT, store, false );
	}else if( chartType == PIE_CHART_TYPE ){
		viewPieChart( xTitle, "Pie Chart", store, false );
	}else if( chartType == LINE_CHART_TYPE ){
		viewLineChart( xTitle , "Line Chart", store, false );
	}else{
		viewColumnChart( xTitle , "Column Chart", store, false );
	}
					
}




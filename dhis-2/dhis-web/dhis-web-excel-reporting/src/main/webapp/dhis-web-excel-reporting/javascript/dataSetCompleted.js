var path = "";
var type = ".action";
var MASK;

var periodTypesStoreP = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStoreP = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

var dataSetStoreP = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataSets' + type,  
	baseParams: { format: 'json'},	
	root: 'dataSets',
	fields: ['id', 'name'],	
	autoLoad: false	
});

var periodTypesStoreD = new Ext.data.JsonStore({
	url: path + 'getPeriodTypes' + type,
	baseParams: { format: 'json'},
	root: 'periodTypes',
	fields: ['id', 'name'],
	sortInfo: { field: 'name', direction: 'ASC' },
	autoLoad: false
});

var periodsStoreD = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getPeriods' + type,  
	baseParams: { format: 'json',name:'ALL'},	
	root: 'periods',
	fields: ['id', 'name'],	
	autoLoad: false	
});

var dataSetStoreD = new Ext.data.JsonStore({
	url: '../dhis-web-commons-ajax-json/getDataSets' + type,  
	baseParams: { format: 'json'},	
	root: 'dataSets',
	fields: ['id', 'name'],	
	autoLoad: false	
});

Ext.onReady(function()
{
	
	MASK = new Ext.LoadMask(Ext.getBody(),{msg: i18n_loading ,msgCls:'x-mask-loading2'});
	
	var periodTypesComboP = new Ext.form.ComboBox({
		store: periodTypesStoreP,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types-p',
		listeners: {
				'select': {
					fn: function() {												
						periodsStoreP.baseParams = { name: periodTypesComboP.getValue() };
						periodsStoreP.reload();	
						dataSetComboP.reset();
						dataSetStoreP.baseParams = { name: periodTypesComboP.getValue() };
						dataSetStoreP.reload();	
					},
					scope: this
				}
		}
	});
	
	var periodsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default		
		renderTo: 'periods-p',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-periods-p',
            name: 'selected-periods-p',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: periodsStoreP,
                displayField: 'name',
                valueField: 'id'				
            },{
                width: 250,
                height: 300,
                store: new Ext.data.SimpleStore({fields: ['id', 'name'],data: []}) , 
				displayField: 'name',
                valueField: 'id'	
            }]
        }]
	});
	
	
	var dataSetComboP = new Ext.form.ComboBox({
		store: dataSetStoreP,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		resizable: true,
		listWidth:400,		
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_dataset,
		selectOnFocus:true,
		applyTo: 'dataset-p'		
	});
	
	
	var viewByPeriod = new Ext.Button({
		text: 'View Report',
		icon: '../images/custom_value.png',		
		renderTo: 'view-by-period',
		handler: function() {
			var periods = Ext.getCmp('selected-periods-p').getValue();
			var id = dataSetComboP.getValue();
			
			if(periods==''){			
				showWarningMessage( i18n_period_is_null );
			}else if(id==''){
				showWarningMessage( i18n_dataset_is_null );
			}else{			
				var columns = periods.split(',');	
				
				viewCompleteReportWindow( dataSetComboP.getEl().getValue(), id, columns, 'viewCompleteReportByPeriod', true, 'period');	
			
			}
		}
	});
	
	var periodTypesComboD = new Ext.form.ComboBox({
		store: periodTypesStoreD,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types-d',
		listeners: {
				'select': {
					fn: function() {	
						periodComboD.reset();
						periodsStoreD.baseParams = { name: periodTypesComboD.getValue() };
						periodsStoreD.reload();	
						
						dataSetStoreD.baseParams = { name: periodTypesComboD.getValue() };
						dataSetStoreD.reload();	
					},
					scope: this
				}
		}
	});
	
	var periodComboD = new Ext.form.ComboBox({
		store: periodsStoreD,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		resizable: true,
		listWidth:400,		
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_period,
		selectOnFocus:true,
		applyTo: 'periods-d'		
	});
	
	var dataSetsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default		
		renderTo: 'dataset-d',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-datasets-d',
            name: 'selected-datasets-d',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: dataSetStoreD,
                displayField: 'name',
                valueField: 'id'				
            },{
                width: 250,
                height: 300,
                store: new Ext.data.SimpleStore({fields: ['id', 'name'],data: []}) , 
				displayField: 'name',
                valueField: 'id'	
            }]
        }]
	});
	
	var viewByDataSet = new Ext.Button({
		text: 'View Report',
		icon: '../images/custom_value.png',
		renderTo: 'view-by-dataset',
		handler: function() {
			var datasets = Ext.getCmp('selected-datasets-d').getValue();
			var id = periodComboD.getValue();
			
			if(id==''){
				showWarningMessage( i18n_period_is_null  );
			}else if(datasets==''){
				showWarningMessage( i18n_dataset_is_null  );
			}else{			
				var columns = datasets.split(',');				
				
				viewCompleteReportWindow( periodComboD.getEl().getValue(), id, columns, 'viewCompleteReportByDataSet', true, 'dataset' );
			
			}
		}
	});

});

function viewCompleteReportWindow( title, id, columns, action, hasBookmark, viewBy )
{
	MASK.show();
	
	Ext.Ajax.request({
		url: path + action + type,
		method: 'POST',
		params: {format:'text/html', id: id, columns: columns},
		success: function( html ) {
			viewWindow( html.responseText, title , id, columns, viewBy, hasBookmark ,false, true, 800, 600 );	
			MASK.hide();	
		}
	});
}



function viewWindow( html, title, id, columns, viewBy, hasBookmark, maximized, showHeader, width, height )
{
	var random = (Math.floor(Math.random() *  11) + 1) * 1000;	
	
	if( hasBookmark ){
	
	bookmark = new Ext.FormPanel({
            title: 'Bookmark',
			id: 'bookmark-' + random,
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
						fieldLabel: 'Description',												
					},{
						xtype: 'textarea',
						id: 'bookmark-description-' + random,						
						hideLabel:true,
						allowBlank: false,
						width:190	
					}
			],
			buttons: [{
				text: 'Bookmark',
				icon: '../images/favorite_star.gif',
				iconAlign: 'left',
				handler: function() {					
					description = Ext.getCmp('bookmark-description-' + random).getValue();
				
					contain = '{"id":' + id ;					
					contain += ', "title":"' + title + '"';
					contain += ', "view":"' + viewBy + '"',
					contain += ', "columns":[';					
					for( var i=0; i<columns.length; i++ ){
						contain += '{"column":' + columns[i] + '}';
						if(i<columns.length-1) contain += ', ';
					}
					contain += ']}';
					
					saveBookmarkCompleted( description, contain, '' );
				}
			}]
        });
	}	
	
	panel = new Ext.Panel(
	{
		id: 'panel-' + random,
		title: title,
		html: html,
		region: 'center',
		margins:'3 0 3 3',
        cmargins:'3 3 3 3',
		autoScroll: true,
		header:showHeader,
		buttons : [{
			text: 'Print',
			icon: '../images/printer.png',
			iconAlign: 'left',
			handler: function(){
				jQuery('#panel-' + random).jqprint();
			}
		}]
	});
	
	var window = new Ext.Window(
	{		
		id:'window-' + random,
		title:'<span>Data set completed report</span>',
		defaults:{bodyStyle:'padding:2px; border:0px'},	
		collapsible: true,
		maximizable: true,
		maximized: maximized,		
		plain:true,
		buttonAlign:'right',				
		layout: 'border'	
		
	});
	if(width != undefined )	window.width = width;
	if(height != undefined ) window.height = height;
	
	window.add(panel);
	
	if( hasBookmark ){
		window.add(bookmark);
	}
	
	window.show();
	
		
}

function viewDataSetReport( dataSetId, periodId, organisationUnitId, dataSetName )
{
	MASK.show();
	
	Ext.Ajax.request({
		url: path + 'viewCustomDataSetReport' + type,
		method: 'POST',
		params: {format:'text/html', dataSetId: dataSetId, periodId: periodId, organisationUnitId: organisationUnitId},
		success: function( html ) {
			
			viewWindow( html.responseText, dataSetName, '', '', '' , false, true, false );	
			MASK.hide();	
		}
	});
}

function saveBookmarkCompleted( description, contain, extraContain )
{
	Ext.Ajax.request({
		url: path + 'saveBookmark' + type,
		method: 'POST',
		params: { format:'json', description: description, contain: contain, extraContain: extraContain },
		success: function( json ) {
			showWarningMessage( i18n_bookmark_success );
			getALLBookmarkCompletedReport();
		}
	});
}

function getALLBookmarkCompletedReport()
{
	
	Ext.Ajax.request({
		url: path + 'getAllBookmark' + type,
		method: 'POST',
		params: { format:'json' },
		success: function( json ) {
			var bookmarks =  Ext.util.JSON.decode(json.responseText).bookmarks;
			
			var html = '<table class="listTable" width="100%">';
			html += '<thead>';
			html += '<tr>';
			html += '<th>Description</th>';
			html += '<th class="{sorter: false}"></th>'
			html += '</tr>';
			html += '</thead>';
			
			html += '<tbody id="list">';
			var r = true;
			for (var i = 0; i < bookmarks.length; i++) 
			{
				if( r ){
					html += '<tr class="listRow">';
					r = false;
				}else{
					html += '<tr class="listAlternateRow">';
					r=true;
				}
				html += '<td>' + bookmarks[i].description + '</td>';
				html += '<td width="50px">';
				html += '<a href="javascript:viewCompletedReport(' + bookmarks[i].id + ');">';
				html += '<img src="../images/checked.png"/>';
				html += '</a>';
				html += '<a href="javascript:deleteBookmark(' + bookmarks[i].id + ');">';
				html += '<img src="../images/delete.png"/>';
				html += '</a>';
				html += '</td>';
				html += '</tr>';
			}			
			html += '</tbody>';
			html += '</table>';
			
			jQuery('#bookmark-tab').html( html );
		}
	});
}

function viewCompletedReport( id )
{
	Ext.Ajax.request({
		url: path + 'getBookmark' + type,
		method: 'POST',
		params: { format:'json', id: id },
		success: function( json ) {
		
			var contain = Ext.util.JSON.decode(json.responseText).bookmark.contain;
			
			var columns = new Array();
			for( var i=0; i<contain.columns.length; i++ ){
				columns[i] = contain.columns[i].column;
			}			
			
			if( contain.view == 'dataset' ){
				viewCompleteReportWindow( contain.title, contain.id, columns, 'viewCompleteReportByDataSet', false );
			}else{
				viewCompleteReportWindow( contain.title, contain.id, columns, 'viewCompleteReportByPeriod', false );
			}
			
		}
	});
}

function deleteBookmark( id )
{
	if(window.confirm(' Do you want delete ?')){
		Ext.Ajax.request({
			url: path + 'deleteBookmark' + type,
			method: 'POST',
			params: {format:'json', id: id},
			success: function( json ) {
				getALLBookmarkCompletedReport();
			}
		});
	}
}


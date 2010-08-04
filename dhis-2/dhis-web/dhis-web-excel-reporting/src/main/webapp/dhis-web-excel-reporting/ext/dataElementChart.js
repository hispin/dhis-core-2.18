
Ext.onReady(function(){
	Ext.QuickTips.init();
		
	var periodsCombo = new Ext.form.ComboBox({
		store: periodsStore,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_period,
		selectOnFocus:true,
		applyTo: 'periods'		
	});
	
	var periodTypesCombo = new Ext.form.ComboBox({
		store: periodTypesStore,
		autoWidth :true,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types',
		listeners: {
				'select': {
					fn: function() {						
						periodsCombo.reset();
						periodsStore.baseParams = { name: periodTypesCombo.getValue() };
						periodsStore.reload();							
					},
					scope: this
				}
		}
	});
	
	
   
	var dataElementGroupCombo = new Ext.form.ComboBox({
		store: dataElementGroupsStore,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_dataelement_groups,
		selectOnFocus:true,
		applyTo: 'data-element-groups',
		listeners: {
				'select': {
					fn: function() {						
						Ext.getCmp('selected-dataelement').reset();
						dataElementsStore.baseParams = { id: dataElementGroupCombo.getValue() };
						dataElementsStore.reload();							
					},
					scope: this
				}
		}
	});	
	
	
	var dataElementsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default
		layout: 'column',
		renderTo: 'data-elements',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-dataelement',
            name: 'selected',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: dataElementsStore,
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
	
// ==================================================================================
//	AXIS X  WITH  INDICATORS
// ==================================================================================

	var indicatorGroupCombo = new Ext.form.ComboBox({
		store: indicatorGroupsStore,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_indicator_groups,
		selectOnFocus:true,
		applyTo: 'indicator-groups',
		listeners: {
				'select': {
					fn: function() {						
						Ext.getCmp('selected-indicators').reset();
						indicatorsStore.baseParams = { id: indicatorGroupCombo.getValue() };
						indicatorsStore.reload();							
					},
					scope: this
				}
		}
	});
	
	var indicatorsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default
		layout: 'column',
		renderTo: 'indicators',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-indicators',
            name: 'selected-indicators',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: indicatorsStore,
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
	
	var dataElementChartB = new Ext.Button({
		text: 'View Chart',
		renderTo: 'view-dataelement-chart',
		handler: function() {			
			
			var period = periodsCombo.getValue();

			if(SELECTED_ORGID==''){
				alert("Please select organisation unit !");
			}else if(period==''){
				alert("Please select period !");
			}else{	
			
				if($("#axis-x-de").css("display")=="block"){
					xaxis = Ext.getCmp('selected-dataelement').getValue();					
					url = path + 'generateDataElementChart' + type;					
					xTitle =  "[" + i18n_dataelements + ":" + $("#data-elements-pc").val();						
					xTitle += "-" + i18n_organisation + ":" + ORGANISATION_UNIT + "]";
				}else{
					xaxis = Ext.getCmp('selected-indicators').getValue();
					url = path + 'generateIndicatorChart' + type;		
					xTitle =  "[" + i18n_indicators + ":" + $("#indicators-pc").val();
					xTitle += "-" + i18n_organisation + ":" + ORGANISATION_UNIT + "]";
				}
				
				
				if(xaxis==''){
					if($("#axis-x-de").css("display")=="block"){	
						alert("Please select data element !");	
					}else{
						alert("Please select indicator !");	
					}					
				}else{
					
					xaxis = xaxis.split(',');	
				
					var chartType = columnChartRadio.getGroupValue();

					var store = new Ext.data.JsonStore({
						url: url,
						baseParams: { format: 'json', xaxis: xaxis, yaxis: period},	
						root: 'data',		
						fields: ['name', 'value', 'total'],				
						autoLoad: true	
					});

					if( chartType == FULL_CHART_TYPE ){
						viewFullChart( xTitle, "Full Style Chart", PARENT, ORGANISATION_UNIT, store );
					}else if( chartType == PIE_CHART_TYPE ){
						viewPieChart( xTitle, "Pie Chart", store	);
					}else if( chartType == LINE_CHART_TYPE ){
						viewLineChart( xTitle , "Line Chart", store	);
					}else{
						viewColumnChart( xTitle , "Column Chart", store	);
					}
				}
			
			}
		}
	});

$("#axis-x-in").css("display","none");
	
});



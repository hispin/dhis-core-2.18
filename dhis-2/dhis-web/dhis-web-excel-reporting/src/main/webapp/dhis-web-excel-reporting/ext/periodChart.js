Ext.onReady(function(){
	Ext.QuickTips.init();
	
	var periodTypesComboPc = new Ext.form.ComboBox({
		store: periodTypesStorePc,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types-pc',
		listeners: {
				'select': {
					fn: function() {						
						Ext.getCmp('selected-periods').reset();
						periodsStorePc.baseParams = { name: periodTypesComboPc.getValue() };
						periodsStorePc.reload();							
					},
					scope: this
				}
		}
	});
	
	var periodsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default		
		renderTo: 'periods-pc',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-periods',
            name: 'selected-periods',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: periodsStorePc,
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
	
	var dataElementComboPc = new Ext.form.ComboBox({
		store: dataElementsStorePc,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_dataelements,
		selectOnFocus:true,
		applyTo: 'data-elements-pc'		
	});
	
	var dataElementGroupComboPc = new Ext.form.ComboBox({
		store: dataElementGroupsStorePc,
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
		applyTo: 'data-element-groups-pc',
		listeners: {
				'select': {
					fn: function() {						
						dataElementComboPc.reset();
						dataElementsStorePc.baseParams = { id: dataElementGroupComboPc.getValue() };	
						dataElementsStorePc.reload();	
					},
					scope: this
				}
		}
	});	
	
	var indicatorsComboPc = new Ext.form.ComboBox({
		store: indicatorsStorePc,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_indicators,
		selectOnFocus:true,
		applyTo: 'indicators-pc'		
	});
	
	
	var indicatorGroupComboPc = new Ext.form.ComboBox({
		store: indicatorGroupsStorePc,
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		autoWidth :true,
		listWidth:400,
		width:400,
		resizable: true,
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_indicator_groups,
		selectOnFocus:true,
		applyTo: 'indicator-groups-pc',
		listeners: {
				'select': {
					fn: function() {						
						indicatorsComboPc.reset();
						indicatorsStorePc.baseParams = { id: indicatorGroupComboPc.getValue() };		
						indicatorsStorePc.reload();	
					},
					scope: this
				}
		}
	});
	
	
	var periodChartB = new Ext.Button({
		text: 'View Chart',
		renderTo: 'view-period-chart',
		handler: function() {							
			
			var periods = Ext.getCmp('selected-periods').getValue();			
			
			
			if(SELECTED_ORGID==''){
				showWarningMessage("Please select organisation unit !");
			}else if(periods==''){
				showWarningMessage("Please select period !");
			}else{	
			
				var xaxis = periods.split(',');	
				
				if($("#axis-x-de-pc").css("display")=="block"){				
					yaxis = dataElementComboPc.getValue();
					url = path + 'generatePeriodsDEChart' + type;	
					xTitle =  "[" + i18n_dataelements + ":" + $("#data-elements-pc").val();						
					xTitle += "-" + i18n_organisation + ":" + ORGANISATION_UNIT + "]";
				}else{
					yaxis = indicatorsComboPc.getValue();
					url = path + 'generatePeriodsInChart' + type;	
					xTitle =  "[" + i18n_indicators + ":" + $("#indicators-pc").val();
					xTitle += "-" + i18n_organisation + ":" + ORGANISATION_UNIT + "]";
				}

				if(yaxis==''){
					if($("#axis-x-de-pc").css("display")=="block"){	
						showWarningMessage("Please select data element !");	
					}else{
						showWarningMessage("Please select indicator !");	
					}					
				}else{	
				
					var chartType = columnChartRadio.getGroupValue();	
					
					var store = new Ext.data.JsonStore({
						url: url,
						baseParams: { format: 'json', xaxis: xaxis, yaxis: yaxis},	
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
	

	
});	
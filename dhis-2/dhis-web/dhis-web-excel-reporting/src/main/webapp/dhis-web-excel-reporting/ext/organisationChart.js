Ext.onReady(function(){
	Ext.QuickTips.init();
	
	
	var periodTypesComboOc = new Ext.form.ComboBox({
		store: periodTypesStoreOc,
		autoWidth :true,		
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'remote',
		triggerAction: 'all',
		emptyText: i18n_period_type,
		selectOnFocus:true,
		applyTo: 'period-types-oc',
		listeners: {
				'select': {
					fn: function() {						
						Ext.getCmp('selected-periods-oc').reset();
						periodsStoreOc.baseParams = { name: periodTypesComboOc.getValue() };
						periodsStoreOc.reload();							
					},
					scope: this
				}
		}
	});
	
	var periodsComboOc = new Ext.form.ComboBox({
		store: periodsStoreOc,
		id: 'selected-periods-oc',
		typeAhead: true,
		displayField: 'name',
		valueField: 'id',
		mode: 'local',
		triggerAction: 'all',
		emptyText: i18n_period,
		selectOnFocus:true,
		applyTo: 'periods-oc'		
	});	
	
	var dataElementComboOc = new Ext.form.ComboBox({
		store: dataElementsStoreOc,
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
		applyTo: 'data-elements-oc'		
	});
	
	var dataElementGroupComboOc = new Ext.form.ComboBox({
		store: dataElementGroupsStoreOc,
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
		applyTo: 'data-element-groups-oc',
		listeners: {
				'select': {
					fn: function() {						
						dataElementComboOc.reset();
						dataElementsStoreOc.baseParams = { id: dataElementGroupComboOc.getValue() };		
						dataElementsStoreOc.reload();					
					},
					scope: this
				}
		}
	});	
	
	var indicatorsComboOc = new Ext.form.ComboBox({
		store: indicatorsStoreOc,
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
		applyTo: 'indicators-oc'		
	});
	
	
	var indicatorGroupComboOc = new Ext.form.ComboBox({
		store: indicatorGroupsStoreOc,
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
		applyTo: 'indicator-groups-oc',
		listeners: {
				'select': {
					fn: function() {						
						indicatorsComboOc.reset();
						indicatorsStoreOc.baseParams = { id: indicatorGroupComboOc.getValue() };	
						indicatorsStoreOc.reload();	
					},
					scope: this
				}
		}
	});
	
	
	var organisationUnitsContainer = new Ext.Container({
		autoEl: 'div',  // This is the default		
		renderTo: 'organisation-units-oc',	
		items:[{
            xtype: 'itemselector',
			id: 'selected-organsation-units',
            name: 'selected-organsation-units',
            fieldLabel: 'ItemSelector',
	        imagePath: 'ext/ux/images/',
            multiselects: [{
                width: 250,
                height: 300,
                store: organisationUnitStoreOc,
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
	
	var organisationUnitB = new Ext.Button({
		text: 'View Chart',
		renderTo: 'view-organisation-chart',
		handler: function() {							
			
			var period = Ext.getCmp('selected-periods-oc').getValue();	
			var organisationUnits = Ext.getCmp('selected-organsation-units').getValue();		

			var xaxis = organisationUnits.split(',');	

			if( organisationUnits == '' ){
				alert("Please select organisation unit !");
			}else {
			
				if($("#axis-x-de-oc").css("display")=="block"){				
					yaxis = dataElementComboOc.getValue();				
					url = path + 'generateOrganisationDEChart' + type;	
					xTitle =  "[" + i18n_dataelements + ":" + $("#data-elements-oc").val();
					xTitle += "-";					
					xTitle += i18n_period + ":" + $("#periods-oc").val() + "]";
					
				}else{
					yaxis = indicatorsComboOc.getValue();				
					url = path + 'generateOrganisationInChart' + type;	
					xTitle =  "[" + i18n_indicators + ":" + $("#indicators-oc").val();
					xTitle += "-";					
					xTitle += i18n_period + ":" + $("#periods-oc").val() + "]";
						
				}			
			
				
				if(yaxis==''){
					if($("#axis-x-de-pc").css("display")=="block"){	
						alert("Please select data element !");	
					}else{
						alert("Please select indicator !");	
					}					
				}else if( period=='' ){	
					alert( "Please select period !");
				}else{
				
					var chartType = columnChartRadio.getGroupValue();			
					
						
						
					var store = new Ext.data.JsonStore({
						url: url,
						baseParams: { format: 'json', xaxis: xaxis, yaxis: yaxis, periodId: period },	
						root: 'data',		
						fields: ['name', 'value'],				
						autoLoad: true	
					});

					
					if( chartType == PIE_CHART_TYPE ){
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
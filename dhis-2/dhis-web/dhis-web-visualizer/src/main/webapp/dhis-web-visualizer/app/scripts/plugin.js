DV.plugin = {};
DV.plugin.init = {};

Ext.onReady(function() {
	Ext.Ajax.method = 'GET';

	document.body.oncontextmenu = function() {
		return false;
	};
	
	DV.plugin.getChart = function(config) {
		var validateConfig,
			afterRender,
			createViewport,
			dv,
			initialize;
			
		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Invalid chart configuration');
				return;
			}
			
			if (!Ext.isString(config.el)) {
				console.log('No element provided');
				return;
			}
			
			if (!Ext.isString(config.uid)) {
				console.log('No chart uid provided');
				return;
			}
			
			return true;
		};
			
		afterRender = function() {};
			
		createViewport = function() {
			var el = Ext.get(dv.el),
				setFavorite,
				centerRegion,
				width,
				height;
				
			width = el.getWidth() - parseInt(el.getStyle('border-left-width')) - parseInt(el.getStyle('border-right-width'));
			height = el.getHeight() - parseInt(el.getStyle('border-top-width')) - parseInt(el.getStyle('border-bottom-width'));				
				
			setFavorite = function(layout) {
				dv.util.chart.createChart(layout, dv);
			};
			
			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				bodyStyle: 'border: 0 none',
				width: config.width || width,
				height: config.height || height,
				layout: 'fit',
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});
			
			return {
				setFavorite: setFavorite,
				centerRegion: centerRegion
			};
		};
		
		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}
			
			dv = DV.core.getInstance({
				baseUrl: config.url,
				el: config.el
			});
			
			dv.viewport = createViewport();

			Ext.data.JsonP.request({
				url: dv.baseUrl + '/dhis-web-visualizer/initialize.action',
				success: function(r) {
					dv.init = r;
					
					dv.util.chart.loadChart(config.uid, true);	
				}
			});
		}();
	};
});

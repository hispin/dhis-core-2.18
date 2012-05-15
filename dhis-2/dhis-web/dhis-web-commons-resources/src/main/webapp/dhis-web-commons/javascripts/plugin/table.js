DHIS.table = {};

DHIS.table.finals = {
	dataGet: 'api/reportTables/data.jsonp?minimal=true',
	data: 'data',
	periods: 'periods',
	orgunits: 'orgunits',
	crosstab: 'crosstab',            
	orgUnitIsParent: 'orgUnitIsParent',
	defaultConf: {
		indicators: [],
		dataelements: [],
		datasets: [],
		periods: ['last12Months'],
		orgunits: [],
		crosstab: ['data'],
		orgUnitIsParent: false,
		useExtGrid: false,
		el: '',
		url: ''
	}
};

DHIS.table.utils = {
    appendUrlIfTrue: function(url, param, expression) {
    	if (expression && expression == true) {
    		url = Ext.String.urlAppend(url, param + '=true');
    	}
    	return url;            	
    },
    getDataUrl: function(conf) {
		var url = conf.url + DHIS.table.finals.dataGet;
		
		Ext.Array.each(conf.indicators, function(item) {
			url = Ext.String.urlAppend(url, 'in=' + item);
		});
		Ext.Array.each(conf.dataelements, function(item) {
			url = Ext.String.urlAppend(url, 'de=' + item);
		});
		Ext.Array.each(conf.datasets, function(item) {
			url = Ext.String.urlAppend(url, 'ds=' + item);
		});
		Ext.Array.each(conf.periods, function(item) {
			url = Ext.String.urlAppend(url, item + '=true');
		});
		Ext.Array.each(conf.orgunits, function(item) {
			url = Ext.String.urlAppend(url, 'ou=' + item);
		});
		Ext.Array.each(conf.crosstab, function(item) {
			url = Ext.String.urlAppend(url, 'crosstab=' + item);
		});
		url = DHIS.table.utils.appendUrlIfTrue(url, DHIS.table.finals.orgUnitIsParent, conf.orgUnitIsParent);
		return url;
	}
};

DHIS.table.grid = {
	getHeaderArray: function(data) {
		var headers = [];
		Ext.Array.each(data.headers, function(header, index) {
			headers.push(header.name);
		});
		return headers;
	},
	getColumnArray: function(data) {
		var columns = [];
		Ext.Array.each(data.headers, function(header, index) {
			columns.push({text: header.name, dataIndex: header.name});
		});
		return columns;
	},
	getStore: function(data) {
		var store = Ext.create('Ext.data.ArrayStore', {
        	fields: DHIS.table.grid.getHeaderArray(data),
        	data: data.rows
		});
		return store;
	},
	render: function(conf) {
		Ext.data.JsonP.request({
			url: DHIS.table.utils.getDataUrl(conf),
			disableCaching: false,
			success: function(data) {
				var el = conf.el;
				var grid = Ext.create('Ext.grid.Panel', {
					store: DHIS.table.grid.getStore(data),
					columns: DHIS.table.grid.getColumnArray(data),
					renderTo: el
				});
			}
		});
	}
};

DHIS.table.plain = {
	getMarkup: function(data) {
		var html = '<table><tr>';
		var classMap = []; // Col index -> class markup
		
		Ext.Array.each(data.headers, function(header, index) {
			var clazz = !header.meta ? ' class=\"val\"' : '';	
			classMap[index] = clazz;
			html += '<th' + clazz + '>' + header.name + '<\/th>';	
		});
		
		html += '<\/tr>';
		
		Ext.Array.each(data.rows, function(row) {
			html += '<tr>';
			Ext.Array.each(row, function(field, index) {
				var clazz = classMap[index];				
				html += '<td' + clazz + '>' + field + '<\/td>';
			});
			html += '<\/tr>';
		});
		
		html += '<\/table>';
		return html;
	},	
	render: function(conf) {
		Ext.data.JsonP.request({
			url: DHIS.table.utils.getDataUrl(conf),
			disableCaching: false,
			success: function(data) {
				var html = DHIS.table.plain.getMarkup(data);
				var el = conf.el;
				Ext.get(el).update(html);
			}
		});
	}
};

DHIS.table.impl = {
	render: function(conf) {
		conf = Ext.applyIf(conf, DHIS.table.finals.defaultConf);
		if ( conf.useExtGrid ) {
			DHIS.table.grid.render(conf);
		}
		else {
			DHIS.table.plain.render(conf);
		}
	}
}

DHIS.getTable = DHIS.table.impl.render;

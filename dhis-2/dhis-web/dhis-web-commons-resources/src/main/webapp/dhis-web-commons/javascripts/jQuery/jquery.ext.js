jQuery.extend({
	postJSON: function( url, data, callback ) {
		return jQuery.post(url, data, callback, "json");
	}
});

$.tablesorter.addParser({
	id: "period",
	is: function (s) {
		return /\d{1,2}[\/\-]\d{1,2}[\/\-]\d{2,4}/.test(s);
	}, format: function (s, table, cell) {
		var v = $(cell).attr('value');
		var c = table.config;
		v = v.replace(/\-/g, "/");
		if (c.dateFormat == "us") {
			// reformat the string in ISO format
			v = v.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})/, "$3/$1/$2");
		} else if (c.dateFormat == "uk") {
			// reformat the string in ISO format
			v = v.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{4})/, "$3/$2/$1");
		} else if (c.dateFormat == "dd/mm/yy" || c.dateFormat == "dd-mm-yy") {
			v = v.replace(/(\d{1,2})[\/\-](\d{1,2})[\/\-](\d{2})/, "$1/$2/$3");
		}
		return $.tablesorter.formatFloat(new Date(v).getTime());
	}, type: "numeric"
});
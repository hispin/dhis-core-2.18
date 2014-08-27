{
    parseProperties: function(responseText) {
        var i18n = {}, rows;
        
        if (typeof responseText !== 'string') {
            return i18n;
        }
        
        rows = responseText.split(/\n/);

        for (var i = 0, a; i < rows.length; i++) {
            if (!!(typeof rows[i] === 'string' && rows[i].length && rows[i].indexOf('=') !== -1)) {
                a = rows[i].split('=');
                i18n[a[0].trim()] = eval('"' + a[1].trim().replace(/"/g, '\'') + '"');
            }
        }

        return i18n;
    }
}

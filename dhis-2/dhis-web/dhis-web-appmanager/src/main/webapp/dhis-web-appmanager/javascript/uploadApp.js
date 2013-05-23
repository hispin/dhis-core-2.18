function autoUpload() {
    document.getElementById('uploadPackageForm').addEventListener('change', function(e) {
        var fd = new FormData(document.getElementById('uploadPackageForm'));
        var xhr = new XMLHttpRequest();
        xhr.addEventListener('progress', function(e) {
            var done = e.position || e.loaded, total = e.totalSize || e.total;
            jQuery("#progressbar").progressbar({value: (Math.floor(done / total * 1000) / 10)});
        }, false);
        if (xhr.upload) {
            xhr.upload.onprogress = function(e) {
                var done = e.position || e.loaded, total = e.totalSize || e.total;
                jQuery("#progressbar").progressbar({value: (Math.floor(done / total * 1000) / 10)});
                console.log('xhr.upload progress: ' + done + ' / ' + total + ' = ' + (Math.floor(done / total * 1000) / 10) + '%');
            };
        }
        xhr.onreadystatechange = function(e) {
            if (4 == this.readyState) {
                console.log(['xhr upload complete', e]);
                console.log(jQuery(".ui-progressbar-value"));
                jQuery(".ui-progressbar-value").html('<div style="text-align:center">Upload complete</div>');
                jQuery("#uploadPackageForm")[0].reset();
            }
        };
        xhr.open('post', 'addApp.action', true);
        xhr.send(fd);
    }, false);
}
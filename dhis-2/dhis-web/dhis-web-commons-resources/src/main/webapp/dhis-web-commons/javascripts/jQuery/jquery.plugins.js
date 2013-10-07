
// internal 'plugins' that touches jquery core

;(function( $, window, document, undefined ) {

    // iterate over and trim every item of an array
    $.trimArray = function( arr ) {
        if( !$.isArray(arr) ) {
            throw new Error('requires an array as argument')
        }

        for( var i = 0, len = arr.length; i < len; i++ ) {
            arr[i] = $.trim(arr[i]);
        }

        return arr;
    }

})(jQuery, window, document);

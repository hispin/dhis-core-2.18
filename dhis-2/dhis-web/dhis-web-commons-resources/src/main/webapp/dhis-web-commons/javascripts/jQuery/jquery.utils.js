jQuery.extend( {
	
	postJSON: function( url, data, success ) {
		$.ajax( { url:url, data:data, success:success, type:'post', dataType:'json', contentType:'application/x-www-form-urlencoded;charset=utf-8' } );
	},

	postUTF8: function( url, data, success ) {
		$.ajax( { url:url, data:data, success:success, type:'post', contentType:'application/x-www-form-urlencoded;charset=utf-8' } );
	},
	
	loadNoCache: function( elementId, url, data ) {
		$.ajax( { url:url, data:data, type:'get', dataType:'html', success:function( data ) {
			$( '#' + elementId ).html( data );
		} } );
	}
} );
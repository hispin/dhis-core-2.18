
function showUserInfo( id )
{
	$( "#userInfo" ).load( "../dhis-web-commons-ajax-html/getUser.action?id=" + id );
	$( "#userInfo" ).dialog( {
	        modal : true,
	        width : 350,
	        height : 350,
	        title : "User"
	    } );
}
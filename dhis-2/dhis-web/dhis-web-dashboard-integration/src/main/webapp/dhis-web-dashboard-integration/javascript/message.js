
var selectedOrganisationUnits = [];

function selectOrganisationUnit__( units )
{
	selectedOrganisationUnits = units;
}

function removeMessage( id )
{
    removeItem( id, "", i18n_confirm_delete_message, "removeMessage.action" );
}

function read( id )
{
    window.location.href = "readMessage.action?id=" + id;
}

function validateMessage()
{
	var subject = $( "#subject" ).val();
	var text = $( "#text" ).val();
	
	if ( subject == null || subject.trim() == '' )
	{
		setHeaderMessage( i18n_enter_subject );
		return false;
	}
	
	if ( text == null || text.trim() == '' )
	{
		setHeaderMessage( i18n_enter_text );
		return false;
	}
	
	return true;
}

function showSenderInfo( id )
{
	$.getJSON( "../dhis-web-commons-ajax-json/getUser.action", { id:id }, function( json ) {
		$( "#senderName" ).html( json.user.firstName + " " + json.user.surname );
		$( "#senderEmail" ).html( json.user.email );
		$( "#senderUsername" ).html( json.user.username );
		$( "#senderPhoneNumber" ).html( json.user.phoneNumber );
		$( "#senderOrganisationUnits" ).html( json.user.organisationUnits );
		
		$( "#senderInfo" ).dialog( {
	        modal : true,
	        width : 300,
	        height : 250,
	        title : "Sender"
	    } );
	} );
}

function sendReply()
{
	var id = $( "#conversationId" ).val();
	var text = $( "#text" ).val();
	
	if ( text == null || text.trim() == '' )
	{
		setHeaderMessage( i18n_enter_text );
		return false;
	}
	
	$.postUTF8( "sendReply.action", { id:id, text:text }, function() {
		window.location.href = "readMessage.action?id=" + id;
	} );
}

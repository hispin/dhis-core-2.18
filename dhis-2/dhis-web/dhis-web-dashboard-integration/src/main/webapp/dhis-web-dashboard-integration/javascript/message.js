
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
	var subject = $( '#subject' ).val();
	var text = $( '#text' ).val();
	
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

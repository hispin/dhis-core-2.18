function removeMessage( id )
{
    removeItem( id, "", i18n_confirm_delete_message, "removeMessage.action" );
}

function read( id )
{
    window.location.href = "readMessage.action?id=" + id;
}

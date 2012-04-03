var isChecked = false;
var isOrgunitSelected = false;

function selectedOrganisationUnitSendSMS( unitIds )
{
	isOrgunitSelected = (unitIds && unitIds.length > 0);
}

function toggleSMSGUI( checked )
{
	if ( checked ) {
		selectionTree.clearSelectedOrganisationUnits();
		selectionTree.buildSelectionTree();
	
		hideById( 'phoneType' );
		showById( 'orgunitType' );
	} else {
		showById( 'phoneType' );
		hideById( 'orgunitType' );
	}
	
	isChecked = checked;
}

function sendSMSMessage( _form )
{
	var params = "";

	if ( !isChecked )
	{
		var list = getFieldValue( "recipient" );

		if ( list == '' )
		{
			showErrorMessage( i18n_no_recipient );
			return;
		}
		
		list = list.split( ";" );

		for ( var i in list )
		{
			if ( list[i] && list[i] != '' )
			{
				params += "recipients=" + list[i] + "&";
			}
		}

		params = "?" + params.substring( 0, params.length - 1 );
	}
	else
	{
		if ( !isOrgunitSelected )
		{
			showErrorMessage( i18n_please_select_orgunit );
			return;
		}
	}

	jQuery.postUTF8( _form.action + params,
	{
		gatewayId: getFieldValue( 'gatewayId' ),
		smsMessage: getFieldValue( 'smsMessage' )
	}, function ( json )
	{
		if ( json.response == "success" ) {
			showSuccessMessage( json.message );
		}
		else {
			showErrorMessage( json.message, 7000 );
		}
	} );
}

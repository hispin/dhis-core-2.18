function dobTypeOnChange(){

	var type = byId('dobType').value;
	
	if(type == 'V' || type == 'D'){
		showById('birthdaySpan');
		hideById('ageSpan');
	}else if(type == 'A'){
		hideById('birthdaySpan');
		showById('ageSpan');
	}else {
		hideById('birthdaySpan');
		hideById('ageSpan');
	}
}

// ----------------------------------------------------------------------------
// Search patients by name
// ----------------------------------------------------------------------------

var prename = "";
function startSearch( e )
{	
	var fullName = getFieldValue('fullName');
	
	if ( prename == fullName){
		return;
	}
	prename = fullName;
	
	if ( fullName.length < 3){
		$("#similarPatients").hide();
		return;
	}
	
	byId('searchIcon').style.display = 'block';
	$.post("getPatientsByName.action",
		{
			fullName: fullName
		},
		function (html)
		{
			jQuery("#similarPatients").show();
			var patientCount = $('<div/>').html(html).find('#matchCount');
			jQuery('#patientCount').html( patientCount );
			jQuery('#searchResults').html( html );
			byId('searchIcon').style.display = 'none';
		},'html');
}

// ----------------------------------------------------------------------------
// Show patients
// ----------------------------------------------------------------------------

function showSearchPatients()
{
	tb_show( i18n_child_representative, "#TB_inline?height=350&width=580&inlineId=searchResults",null);	
}
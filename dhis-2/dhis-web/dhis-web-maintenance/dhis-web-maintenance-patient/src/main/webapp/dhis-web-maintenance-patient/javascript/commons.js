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

function startSearch( )
{	
	var fullName = getFieldValue('fullName').replace(/^\s+|\s+$/g,"");
	if( fullName.length > 0) 
	{
		tb_show( i18n_search_result,"getPatientsByName.action?fullName=" + fullName + "&TB_iframe=true&height=400&width=500",null );
	}
	else
	{
		alert( i18n_no_patients_found );
	}
}

// ----------------------------------------------------------------------------
// Show patients
// ----------------------------------------------------------------------------

function showSearchPatients()
{
	tb_show( i18n_child_representative, "#TB_inline?height=350&width=580&inlineId=searchResults",null);	
}

function isDeathOnChange()
{
	var isDeath = byId('isDead').checked;
	if(isDeath)
	{
		showById('deathDateTR');
	}
	else
	{
		hideById('deathDateTR');
	}
}

//------------------------------------------------------------------------------
// Filter data-element
//------------------------------------------------------------------------------

function filterDE( event, value, fieldName )
{
	var field = byId(fieldName);
	for ( var index = 0; index < field.options.length; index++ )
    {
		var option = field.options[index];
		
		if(value.length == 0 )
		{
			option.style.display = "block";
		}
		else
		{
			if (option.text.toLowerCase().indexOf( value.toLowerCase() ) != -1 )
			{
				option.style.display = "block";
			}
			else
			{
				option.style.display = "none";
			}
		}
    }	    
}

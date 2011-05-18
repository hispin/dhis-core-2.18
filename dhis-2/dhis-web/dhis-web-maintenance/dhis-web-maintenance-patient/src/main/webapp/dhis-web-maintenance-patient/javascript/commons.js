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

function getPatientsByName( )
{	
	var fullName = getFieldValue('fullName').replace(/^\s+|\s+$/g,"");
	if( fullName.length > 0) 
	{
		contentDiv = 'searchPatientsByNameDiv';
		$('#searchPatientsByNameDiv' ).load("getPatientsByName.action",
			{
				fullName: fullName
			}).dialog({
				title: i18n_search_result,
				maximize: true, 
				closable: true,
				modal:true,
				overlay:{ background:'#000000', opacity: 0.8},
				width: 800,
				height: 400
		});
	}
	else
	{
		alert( i18n_no_patients_found );
	}
}

// ----------------------------------------------------------------------------
// Show patients
// ----------------------------------------------------------------------------

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

// ----------------------------------------------------------------
// Get Params form Div
// ----------------------------------------------------------------

function getParamsForDiv( patientDiv)
{
	var params = '';
	jQuery("#" + patientDiv + " :input").each(function()
		{
			if( $(this).attr('type') != 'button' )
			{
				var elementId = $(this).attr('id');
				
				if( jQuery(this).context.multiple )
				{
					for ( var i in jQuery(this).val() )
					{
						if( jQuery(this).val()[i] != null )
						{
							params += elementId + "="+ htmlEncode(jQuery(this).val()[i]) + "&";
						}
					}
				}else
				{
					if( jQuery(this).val() != null )
					{
						params += elementId + "="+ htmlEncode(jQuery(this).val()) + "&";
					}
				}
			}
		});
	
	return params;
}


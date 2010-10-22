// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientAttributeDetails( patientAttributeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patientAttribute' );
    request.setCallbackSuccess( patientAttributeReceived );
    request.send( 'getPatientAttribute.action?id=' + patientAttributeId );
}

function patientAttributeReceived( patientAttributeElement )
{
	setInnerHTML( 'idField', getElementValue( patientAttributeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( patientAttributeElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( patientAttributeElement, 'description' ) );
    
    var valueTypeMap = { 'NUMBER':i18n_number, 'BOOL':i18n_yes_no, 'TEXT':i18n_text, 'DATE':i18n_date, 'COMBO':i18n_combo };
    var valueType = getElementValue( patientAttributeElement, 'valueType' );    
    
    setInnerHTML( 'valueTypeField', valueTypeMap[valueType] );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttribute( patientAttributeId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientAttributeCompleted );
        window.location.href = 'removePatientAttribute.action?id=' + patientAttributeId;
    }
}

function removePatientAttributeCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'patientAttribute.action';
    }
    else if ( type = 'error' )
    {
        setInnerHTML( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add Patient Attribute
// -----------------------------------------------------------------------------

function validateAddPatientAttribute()
{
	
	var url = 'validatePatientAttribute.action?' +
			'nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addPatientAttributeForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_atttibute_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update Patient Attribute
// -----------------------------------------------------------------------------

function validateUpdatePatientAttribute()
{
	
    var url = 'validatePatientAttribute.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updatePatientAttributeForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_program_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

ATTRIBUTE_OPTION = 
{
	selectValueType : 	function (this_)
	{
		if ( jQuery(this_).val() == "COMBO" )
		{
			jQuery("#attributeComboRow").show();
			if( jQuery("#attrOptionContainer").find("input").length ==0 ) 
			{
				ATTRIBUTE_OPTION.addOption();
				ATTRIBUTE_OPTION.addOption();
			}
		}else {
			jQuery("#attributeComboRow").hide();
		}
	},
	checkOnSubmit : function ()
	{
		if( jQuery("#valueType").val() != "COMBO" ) 
		{
			jQuery("#attrOptionContainer").children().remove();
			return true;
		}else {
			$("input","#attrOptionContainer").each(function(){ 
				if( !jQuery(this).val() )
					jQuery(this).remove();
			});
			if( $("input","#attrOptionContainer").length < 2)
			{
				alert(i118_at_least_2_option);
				return false;
			}else return true;
		}
	},
	addOption : function ()
	{
		jQuery("#attrOptionContainer").append(ATTRIBUTE_OPTION.createInput());
	},
	remove : function (this_, optionId)
	{
		
		if( jQuery(this_).siblings("input").attr("name") != "attrOptions")
		{
			jQuery.get("removePatientAttributeOption.action?id="+optionId,function(data){
				var type  = jQuery(data).find("message").attr("type");
				alert(type);
				if( type == "success")
				{
					alert("success");
					jQuery(this_).parent().parent().remove();
					alert(jQuery(data).text());
				}else 
				{
					alert(jQuery(data).text());
				}
			});
		}else
		{
			jQuery(this_).parent().parent().remove();
		}
	},
	removeInAddForm : function(this_)
	{
		jQuery(this_).parent().parent().remove();
	},
	createInput : function ()
	{
		return "<tr><td><input type='text' name='attrOptions' /><a href='#' style='text-decoration: none; margin-left:0.5em;' title='"+i18n_remove_option+"'  onClick='ATTRIBUTE_OPTION.remove(this,null)'>[ - ]</a></td></tr>";
	}
}
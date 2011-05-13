
// -----------------------------------------------------------------------------
// Add Relationship Patient
// -----------------------------------------------------------------------------

function showAddRelationshipPatient( patientId )
{
	hideById( 'selectDiv' );
	hideById( 'searchPatientDiv' );
	hideById( 'listPatientDiv' );
	hideById( 'listRelationshipDiv' );
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipPatient.action',
		{
			id:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			jQuery('#loaderDiv').hide();
		});
}

function validateAddRelationshipPatient()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addRelationshipPatientCompleted ); 
	request.sendAsPost( getParamsForDiv('addRelationshipDiv') );	
    request.send( "validateAddRelationshipPatient.action" );        

    return false;
}

function addRelationshipPatientCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	removeDisabledIdentifier();
    	addRelationshipPatient();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        setHeaderMessage( message );
    }
    else if( type == 'duplicate' )
    {
    	if( !checkedDuplicate )
		{
    		showListPatientDuplicate(messageElement, true);
		}
    }
}

function addRelationshipPatient()
{
	jQuery('#loaderDiv').show();
	$.ajax({
      type: "POST",
      url: 'addRelationshipPatient.action',
      data: getParamsForDiv('addRelationshipDiv'),
      success: function( json ) {
		setMessage( i18n_save_success );
		jQuery('#loaderDiv').hide();
      }
     });
    return false;
}

//remove value of all the disabled identifier fields
//an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
//we don't save inherited identifiers. Only save the representative id.
function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

//get and build a param String of all the identifierType id and its value
//excluding inherited identifiers
function getIdParams()
{
	var params = "";
	jQuery("input.idfield").each(function(){
		if( jQuery(this).val() && !jQuery(this).is(":disabled") )
			params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
	});
	return params;
}


/**
 * Show list patient duplicate  by jQuery thickbox plugin
 * @param rootElement : root element of the response xml
 * @param validate  :  is TRUE if this method is called from validation method  
 */
function showListPatientDuplicate(rootElement, validate)
{
	var message = rootElement.firstChild.nodeValue;
	var patients = rootElement.getElementsByTagName('patient');
	var sPatient = "";
	if( patients && patients.length > 0 )
	{
		for( var i = 0; i < patients.length ; i++ )
		{
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td><strong>"+i18n_patient_system_id+"</strong></td><td>"+ getElementValue( patients[i], 'systemIdentifier' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_fullName+"</strong></td><td>"+ getElementValue( patients[i], 'fullName' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_gender+"</strong></td><td>"+ getElementValue(  patients[i], 'gender' )+"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_date_of_birth+"</strong></td><td>"+getElementValue(  patients[i], 'dateOfBirth' ) +"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_age+"</strong></td><td>"+ getElementValue(  patients[i], 'age' ) +"</td></tr>" ;
			sPatient += "<tr><td><strong>"+i18n_patient_blood_group+"</strong></td><td>"+ getElementValue(  patients[i], 'bloodGroup' ) +"</td></tr>";
        	var identifiers =  patients[i].getElementsByTagName('identifier');
        	if( identifiers && identifiers.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2'><strong>"+i18n_patient_identifiers+"</strong></td></tr>"
        		for( var j = 0; j < identifiers.length ; j++ )
        		{
        			sPatient +="<tr class='identifierRow'>"
        				+"<td><strong>"+getElementValue( identifiers[j], 'name' )+"</strong></td>"
        				+"<td>"+getElementValue( identifiers[j], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	var attributes =  patients[i].getElementsByTagName('attribute');
        	if( attributes && attributes.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2'><strong>"+i18n_patient_attributes+"</strong></td></tr>"
        		for( var k = 0; k < attributes.length ; k++ )
        		{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td><strong>"+getElementValue( attributes[k], 'name' )+"</strong></td>"
        				+"<td>"+getElementValue( attributes[k], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+getElementValue(  patients[i], 'id' )+"' value='"+i18n_edit_this_patient+"' onclick='edit(this)'/></td></tr>";
        	sPatient += "</table>";
		}
		jQuery("#thickboxContainer","#hiddenModalContent").html("").append(sPatient);
		if( !validate ) jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();});
		else jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();window.parent.checkedDuplicate = true; window.parent.validatePatient();});
		tb_show( message, "#TB_inline?height=500&width=500&inlineId=hiddenModalContent", null);
	}
}

function validatePatient()
{
	if( jQuery("#id").val() )
		validateUpdatePatient();
	else validateAddPatient();
}

function toggleUnderage( this_ )
{
	
	if( !jQuery(this_).is(":checked"))
	{
		jQuery("input.idfield").each(function(){
			var data = jQuery(this).metadata({type:"attr",name:"data"});
			if( data.related )
			{
				jQuery(this).val("");
				jQuery(this).attr({"disabled":""});
			}
		});
		jQuery(".attrField").each(function(){
			var data = jQuery(this).metadata({type:"attr",name:"data"});
			if( data.inheritable )
			{
				jQuery(this).val("");
//				jQuery(this).attr({"disabled":""});
			}
		});
	}else {
		jQuery("input.idfield").each(function(){
			var data = jQuery(this).metadata({type:"attr",name:"data"});
			if( data.related )
			{
				jQuery(this).val(data.id);
				jQuery(this).attr({"disabled":"disabled"});
			}
		});
		jQuery(".attrField").each(function(){
			var data = jQuery(this).metadata({type:"attr",name:"data"});
			if( data.inheritable )
			{
				jQuery(this).val(data.value);
//				jQuery(this).attr({"disabled":"disabled"});
			}
		});
	}
}
function ageOnchange()
{
	jQuery("#birthDate").val("").removeClass("error").rules("remove","required");
	jQuery("#age").rules("add",{required:true});

}

function bdOnchange()
{
	jQuery("#age").rules("remove","required");
	jQuery("#age").val("")
	jQuery("#birthDate").rules("add",{required:true});
}




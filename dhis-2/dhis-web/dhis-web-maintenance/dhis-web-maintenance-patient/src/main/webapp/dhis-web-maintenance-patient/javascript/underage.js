//-----------------------------------------------------------------------------
//Add Patient
//-----------------------------------------------------------------------------

function validateAddRepresentative()
{	
	$.post("validatePatient.action?" + getIdParams(),
		{
			fullName: getFieldValue( 'fullName' ),
			gender: getFieldValue( 'gender' ) ,
			birthDate: getFieldValue( 'birthDate' ), 	        
			age: getFieldValue( 'age' ) ,
			dobType: getFieldValue( 'dobType' ) ,
			ageType: getFieldValue( 'ageType' )
		},
		function (data)
		{
			addValidationRepresentativeCompleted(data);
		},'xml');
		
}

function addValidationRepresentativeCompleted( messageElement )
{
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	var message = messageElement.firstChild.nodeValue;
	
	 if ( type == 'success' )
	 {
		 // Use jQuery Ajax submit 
		 jQuery.ajax({
			   type: "POST"
			   ,url: "addRepresentative.action"
			   ,data: jQuery("#addRepresentativeForm").serialize()
			   ,dataType : "xml"
			   ,success: function(xml){ 
			 		autoChoosePerson( xml );
				}
			   ,error: function()
			   {
					alert(i18n_error_connect_to_server);
			   }
			 });
		 
	 }
	 else if ( type == 'error' )
	 {
	     window.alert( i18n_adding_patient_failed + ':' + '\n' + message );
	 }
	 else if ( type == 'input' )
	 {
	     document.getElementById( 'message' ).innerHTML = message;
	     document.getElementById( 'message' ).style.display = 'block';
	 }
	 else if( type == 'duplicate' )
	 {
		 jQuery( '#message' ).css({"display":"block"}).html(message);
		 jQuery("#formContainer").hide();
		 showPersons("listPersonsDuplicate", messageElement);
	 }
}

//get and build a param String of all the identifierType id and its value
//excluding  identifiers which related is False
function getIdParams()
{
	var params = "";
	jQuery("input.idfield").each(
			function()
			{
				if( jQuery(this).val() && !jQuery(this).is(":disabled") )
					params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
			}
	);
	return params;
}

function searchPerson()
{
	jQuery.ajax({
		   type: "POST"
		   ,url: "searchPerson.action"
		   ,data: jQuery("#searchForm").serialize()
		   ,dataType : "xml"
		   ,success: function(xmlObject){
				showPersons( "searchForm div[id=listPersons]", xmlObject );
			}
		   ,error: function(request,status,errorThrown)
		   {
				alert(i18n_error_connect_to_server);
		   }
		 });
}

function showPersons( divContainer, xmlElement )
{
	var container = jQuery("#"+divContainer);
	container.html("");
	var patients = xmlElement.getElementsByTagName('patient');
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
        		for( var j = 0; j < identifiers.length ; j++ )
        		{
        			sPatient +="<tr class='identifierRow"+getElementValue( patients[i], 'id' )+"' id='iden"+getElementValue( identifiers[j], 'id' )+"'>"
        				+"<td><strong>"+getElementValue( identifiers[j], 'name' )+"</strong></td>"
        				+"<td class='value'>"+getElementValue( identifiers[j], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	var attributes =  patients[i].getElementsByTagName('attribute');
        	if( attributes && attributes.length > 0 )
        	{
        		for( var k = 0; k < attributes.length ; k++ )
        		{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td><strong>"+getElementValue( attributes[k], 'name' )+"</strong></td>"
        				+"<td>"+getElementValue( attributes[k], 'value' )+"</td>	"	
        				+"</tr>";
        		}
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+getElementValue(  patients[i], 'id' )+"' value='"+i18n_choose_this_person+"' onclick='choosePerson(this)'/></td></tr>";
        	sPatient += "</table>";
		}
		container.append(sPatient);
	}else
	{
		var message = "<p>"+i18n_no_result+"</p>";
		container.html(message);
	}
}

// Will be call after save new person successfully
function autoChoosePerson(xmlElement)
{
	jQuery("#tab-2").html("<center><strong>"+i18n_add_person_successfully+"</strong></center>");
	var root = jQuery(xmlElement);
	window.parent.jQuery("#representativeId").val( root.find("id").text() );
	window.parent.jQuery("#relationshipTypeId").val( root.find("relationshipTypeId").text() );
	root.find("identifier").each(
			function(){
				var inputField = window.parent.jQuery("#iden" + jQuery(this).find("identifierTypeId").text());
				inputField.val( jQuery(this).find("identifierText").text() );
				inputField.attr({"disabled":"disabled"});
			}
	);
	// close thickbox popup
	setTimeout(function(){window.parent.tb_remove();},3000);
	
}

// Set Representative information to parent page.
function choosePerson(this_)
{
	var relationshipTypeId = jQuery("#relationshipTypeId").val();
	if( isBlank( relationshipTypeId ))
	{
		alert(i18n_please_select_relationshipType);
		return;
	}
	var id = jQuery(this_).attr("id");
	window.parent.jQuery("#representativeId").val(id);
	window.parent.jQuery("#relationshipTypeId").val(relationshipTypeId);
	jQuery(".identifierRow"+id).each(function(){
		var inputField = window.parent.jQuery("#"+jQuery(this).attr("id"));
		if( inputField.metadata({type:"attr",name:"data"}).related  )
		{
			// only inherit identifierType which related is true
			inputField.val(jQuery(this).find("td.value").text());
			inputField.attr({"disabled":"disabled"});
		}
	});
	
	// close thickbox popup
	window.parent.tb_remove();
}

function toggleSearchType(this_)
{
	var type = jQuery(this_).val();
	if( "identifier" == type )
	{
		jQuery("#rowIdentifier").show().find("#identifierTypeId").addClass('required:true');
		jQuery("#rowAttribute").hide().find("#attributeId").removeClass("required");
		jQuery("#searchValue").val("");
	}
	else if( "attribute" == type )
	{
		jQuery("#rowIdentifier").hide().find("#identifierTypeId").removeClass("required");
		jQuery("#rowAttribute").show().find("#attributeId").addClass("required:true");
		jQuery("#searchValue").val("");
	}
	else if( "name" == type || "" == type )
	{
		jQuery("#rowIdentifier").hide().find("#identifierTypeId").removeClass("required");
		jQuery("#rowAttribute").hide().find("#attributeId").removeClass("required");
		jQuery("#searchValue").val("");
	}
}

function ageOnchange()
{
	jQuery("#birthDate").val("").removeClass("required");
	jQuery("#age").addClass('required:true');

}

function bdOnchange()
{
	jQuery("#age").val("").removeClass("required");
	jQuery("#birthDate").addClass('required:true');
}
function isBlank(text)
{
	return !text ||  /^\s*$/.test(text);
}


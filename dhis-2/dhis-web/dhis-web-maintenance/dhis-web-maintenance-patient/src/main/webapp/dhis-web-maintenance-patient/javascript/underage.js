jQuery(document).ready(function(){
	jQuery("#tabs").tabs();
	jQuery("#addPersonForm").validate({
		 meta:"validate"
		,errorElement:"td"
		,submitHandler: function(form)
						{
							validateAddPatient();
						}
	});
	jQuery.validator.loadLocaled( jQuery("#curLocaleCode").val() );
	jQuery("#birthDate").rules("add",{required:true,dateISO:true,datelessthanequaltoday:true});
});

function searchPerson()
{
	var identifierTypeId = jQuery("#identifierTypeId").val();
	var attributeId = jQuery("#attributeId").val();
	var searchValue = jQuery("#searchValue").val();
	var searchType = jQuery("#searchType").val();
	var data = "value="+searchValue;
	if( searchType == "identifier" )
	{
		data += "&identifierTypeId="+identifierTypeId;
	}else if(  searchType == "attribute" )
	{
		data += "&attributeId="+attributeId;
	}
	
	jQuery.ajax({
		   type: "POST"
		   ,url: "searchPerson.action"
		   ,data: data
		   ,dataType : "xml"
		   ,success: function(xml){
				showPersons(xml);
		   }
		 });
}

function showPersons( xmlElement )
{
	jQuery("#listPatients").html("");
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
		jQuery("#listPatients").append(sPatient);
	}
}

function choosePerson(this_)
{
	var id = jQuery(this_).attr("id");
	window.parent.document.getElementById("representativeId").value = id;
	jQuery(".identifierRow"+id).each(function(){
		var identifierId = jQuery(this).attr("id");
		var inputField = window.parent.document.getElementById(identifierId);
		inputField.value= jQuery(this).find("td.value").text();
		inputField.disabled = true;
	});
	window.parent.tb_remove();
}

function toggleSearchType(this_)
{
	var type = jQuery(this_).val();
	if( "identifier" == type )
	{
		jQuery("#rowIdentifier").show();
		jQuery("#rowAttribute").hide();
		jQuery("#searchValue").val("");
	}
	else if( "attribute" == type )
	{
		jQuery("#rowIdentifier").hide();
		jQuery("#rowAttribute").show();
		jQuery("#searchValue").val("");
	}
	else if( "name" == type || "" == type )
	{
		jQuery("#rowIdentifier").hide();
		jQuery("#rowAttribute").hide();
		jQuery("#searchValue").val("");
	}
}

function ageOnchange()
{
	jQuery("#birthDate").rules("remove","required");
	jQuery("#birthDate").val("");
	jQuery("#birthDate").removeClass("error");
	jQuery("#age").rules("add",{required:true});

}

function bdOnchange()
{
	jQuery("#age").rules("remove","required");
	jQuery("#age").val("")
	alert(jQuery("#birthDate").val());
	jQuery("#birthDate").rules("add",{required:true});
}

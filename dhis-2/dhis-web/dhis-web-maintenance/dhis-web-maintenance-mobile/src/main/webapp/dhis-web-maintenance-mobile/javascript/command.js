currentType = '';
function changeParserType( value )
{
	hideAll();
    if ( value == 'KEY_VALUE_PARSER' || value == 'J2ME_PARSER') {
        showById( "dataSetParser" );
		enable("selectedDataSetID");
    } else if ( value == 'ALERT_PARSER' || value == 'UNREGISTERED_PARSER' ) {
    	showById( "alertParser" );
		enable("userGroupID");
    } else if (value == 'ANONYMOUS_PROGRAM_PARSER') {
    	showById( "anonymousProgramParser" );
		enable("programId");
    } else if (value == 'TRACKED_ENTITY_REGISTRATION_PARSER') {
    	showById( "registrationParser" );
		enable("selectedProgramId");
    }
	currentType = value;
}

function hideAll() 
{
	hideById( "dataSetParser" ); 
	disable( "selectedDataSetID" ); 

	hideById( "alertParser" );
	disable( "userGroupID" );

	hideById( "anonymousProgramParser" );
	disable( "programId" );

	hideById( "registrationParser" );
	disable( "selectedProgramId" );
}

function generateSpecialCharactersForm()
{
	var rowId = jQuery('.trSpecialCharacter').length + 1;

	var contend = '<tr id="trSpecialCharacter' + rowId + '" name="trSpecialCharacter' + rowId + '" class="trSpecialCharacter">'
				+	'<td><input id="name' + rowId + '" name="name' + rowId + '" type="text" class="name {validate:{required:true}}" onblur="checkDuplicatedSpeCharName(this.value,' + rowId + ')"  placeholder="' + i18_special_characters + '" )/></td>'
				+	'<td><input id="value' + rowId + '" name="value' + rowId + '" type="text" class="value {validate:{required:true}}" onblur="checkDuplicatedSpeCharValue(this.value, ' + rowId + ')" placeholder="' + i18_value + '"/>'
				+   	'<input type="button" value="remove" onclick="removeSpecialCharactersForm(' + rowId + ')"/></td>'
				+ '</tr>';
	jQuery('#specialCharacters').append( contend );

}

function removeSpecialCharactersForm( rowId )
{
	jQuery("[name=trSpecialCharacter" + rowId + "]").remove();
}

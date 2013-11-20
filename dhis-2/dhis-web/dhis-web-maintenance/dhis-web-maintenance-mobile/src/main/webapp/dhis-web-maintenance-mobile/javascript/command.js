currentType = '';
function changeParserType( value )
{
	hideAll();
    if ( value == 'KEY_VALUE_PARSER' || value == 'J2ME_PARSER') {
        showById( "dataSetParser" );
    } else if ( value == 'ALERT_PARSER' || value == 'UNREGISTERED_PARSER' ) {
    	showById( "alertParser" );
    }
	currentType = value;
}

function hideAll() 
{
	 hideById( "dataSetParser" ); 
	 hideById( "alertParser" );
}

function generateSpecialCharactersForm()
{
	var rowId = jQuery('.trSpecialCharacter').length + 1;

	var contend = '<tr id="trSpecialCharacter'+rowId+'" name="trSpecialCharacter'+rowId+'" class="trSpecialCharacter">'
				+	'<td><input id="name'+rowId+'" name="name'+rowId+'" type="text"/></td>'
				+	'<td><input id="value'+rowId+'" name="value'+rowId+'" type="text"/>'
				+   	'<input type="button" value="remove" onclick="removeSpecialCharactersForm('+rowId+')"/></td>'
				+ '</tr>';
	jQuery('#specialCharacters').append( contend );

}

function removeSpecialCharactersForm( rowId )
{
	jQuery("[name=trSpecialCharacter" + rowId + "]").remove();
}

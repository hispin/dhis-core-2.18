// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------

function searchUserName(){
	
	var params = 'key=' + getFieldValue('key');
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( searchUserNameReceived );
	request.sendAsPost(params);
    request.send( 'searchUser.action' );
}

function searchUserNameReceived(xmlObject){

	 var type = xmlObject.getAttribute( 'type' );
	 if (  type != null && type == 'input' ){
		window.location.href = 'alluser.action';
		return;
	 }
	
	currentUserName = getElementValue( xmlObject, 'currentUserName' );
	// get user list
	 var users = xmlObject.getElementsByTagName( "user" );
	// get tbody to add dataelements
	var myTable = byId( 'userList');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	// delete row into tbody
	for(var k = tBody.rows.length; k >= 0;k--)
	{
		myTable.deleteRow(k - 1);
	}
		
	// add header for table
	var newTR = document.createElement('tr');
	// add 1st column username
	var newTD1 = document.createElement('th');
		newTD1.innerHTML = i18n_username;
	// add 2nd column name
	var newTD2 = document.createElement('th');
		newTD2.innerHTML = i18n_name;
	// add 3rd column operators
	var newTD3 = document.createElement('th');
		newTD3.innerHTML = i18n_operations;
		newTD3.setAttribute('colspan', 3);
		newTD3.setAttribute('class', '{sorter: false}');
	
	newTR.appendChild ( newTD1 );
	newTR.appendChild ( newTD2 );
	newTR.appendChild ( newTD3 );

	tBody.appendChild(newTR);	
	
	for ( var i = 0 ; i < users.length ; i++ )
	{

		// get dataelement
		var user = users.item(i);
		var id = user.getElementsByTagName("id")[0].firstChild.nodeValue;
		var username = user.getElementsByTagName("username")[0].firstChild.nodeValue;
		var name = user.getElementsByTagName("surname")[0].firstChild.nodeValue + ", " 
				 + user.getElementsByTagName("firstName")[0].firstChild.nodeValue;
		
		// add new row
		var newTR = document.createElement('tr');
			newTR.setAttribute('id', "tr" + id + "");
		if( i%2 == 0){
			newTR.setAttribute( "class", "listRow" ); 
		}else{
			newTR.setAttribute( "class", "listAlternateRow" ); 
		}
		// add new column - username column
		var newTD1 = document.createElement('td');
			newTD1.innerHTML = username;
		// add new column - name column
		var newTD2 = document.createElement('td');
			newTD2.innerHTML = name;
		// insert column into row
		newTR.appendChild ( newTD1 );
		newTR.appendChild ( newTD2 );
		// add new column
		newTR = addOperatorColumns( newTR, id, username );
		
		tBody.appendChild(newTR);
	}
}

function addOperatorColumns(rowObject, id, username) {
	// add new column - edit button
	var newTD1 = document.createElement('td');
	newTD1.innerHTML = '<a href="showUpdateUserForm.action?id='+ id
				+ '" title=' + i18n_edit + '>'
				+ '<img src=../images/edit.png alt=' + i18n_edit + '></a>';
	rowObject.appendChild(newTD1);
	
	// add new column - remove button
	var newTD2 = document.createElement('td');
	if(currentUserName != username){
		newTD2.innerHTML = "<a href=\"javascript:removeUser("
			+ id + ",'" + username + "' )\" title="
			+ i18n_remove + '>' + '<img src="../images/delete.png" alt="'
			+ i18n_remove + '"></a>';
	}
	rowObject.appendChild(newTD2);
	
	// add new column - show details button
	var newTD3 = document.createElement('td');
	newTD3.innerHTML = '<a href="javascript:showUserDetails( ' + id + ' )" '
			+ 'title="' + i18n_show_details + '">'
			+ '<img src="../images/information.png" alt="' + i18n_show_details + '"></a>';
	rowObject.appendChild(newTD3);

	return rowObject;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUserDetails( userId )
{
    var request = new Request();
    request.setResponseTypeXML( 'user' );
    request.setCallbackSuccess( userReceived );
    request.send( 'getUser.action?id=' + userId );
}

function userReceived( userElement )
{
    setInnerHTML( 'usernameField', getElementValue( userElement, 'username' ) );
    setInnerHTML( 'surnameField', getElementValue( userElement, 'surname' ) );
    setInnerHTML( 'firstNameField', getElementValue( userElement, 'firstName' ) );

    var email = getElementValue( userElement, 'email' );
    setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );

	var numberOrgunit = getElementValue( userElement, 'numberOrgunit' );
	setInnerHTML( 'numberOrgunitField', numberOrgunit ? numberOrgunit : '[' + i18n_none + ']' );
	
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
	removeItem( userId, username, i18n_confirm_delete, 'removeUser.action' );
}
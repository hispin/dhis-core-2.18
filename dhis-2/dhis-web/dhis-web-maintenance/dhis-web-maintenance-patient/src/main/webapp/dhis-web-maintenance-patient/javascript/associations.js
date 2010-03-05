
var numberOfSelects = 0;

function selectAllAtLevel()
{
	setMessage( i18n_loading );
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectLevel.action?level=' + getListValue( 'levelList' ) );
}

function unselectAllAtLevel()
{
	setMessage( i18n_loading );
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectLevel.action?level=' + getListValue( 'levelList' ) );
}

function selectGroup()
{
	setMessage( i18n_loading );
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'selectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function unselectGroup()
{
	setMessage( i18n_loading );
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectOrganisationUnitGroup.action?organisationUnitGroupId=' + getListValue( 'groupList' ) );
}

function unselectAll()
{
	setMessage( i18n_loading );
	var request = new Request();
    request.setCallbackSuccess( selectReceived );
    request.send( 'unselectAll.action' );
}

function selectReceived()
{
    selectionTree.buildSelectionTree();
    hideMessage();
}

function treeClicked()
{
    numberOfSelects++;
    
    setMessage( i18n_loading );
    
    document.getElementById( "submitButton" ).disabled = true;
}

function selectCompleted( selectedUnits )
{
    numberOfSelects--;
    
    if ( numberOfSelects <= 0 )
    {
        hideMessage();
        
        document.getElementById( "submitButton" ).disabled = false;
    }
}

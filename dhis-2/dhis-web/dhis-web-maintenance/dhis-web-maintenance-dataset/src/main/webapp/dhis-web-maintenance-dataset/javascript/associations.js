
var numberOfSelects = 0;

function selectAllAtLevel( dataSetId )
{
	var level = getListValue( 'levelList' );
    var groupId = getListValue( 'groupList' );
    
    window.location.href = 'selectLevel.action?level=' + level + '&organisationUnitGroupId=' + groupId + '&dataSetId=' + dataSetId;
}

function unselectAllAtLevel( dataSetId )
{
	var level = getListValue( 'levelList' );
    var groupId = getListValue( 'groupList' );
    
    window.location.href = 'unselectLevel.action?level=' + level + '&organisationUnitGroupId=' + groupId + '&dataSetId=' + dataSetId;
}

function selectGroup( dataSetId )
{
    var level = getListValue( 'levelList' );
    var groupId = getListValue( 'groupList' );
    
    window.location.href = 'selectOrganisationUnitGroup.action?level=' + level + '&organisationUnitGroupId=' + groupId + '&dataSetId=' + dataSetId;
}

function unselectGroup( dataSetId )
{
    var level = getListValue( 'levelList' );
    var groupId = getListValue( 'groupList' );
    
    window.location.href = 'unselectOrganisationUnitGroup.action?level=' + level + '&organisationUnitGroupId=' + groupId + '&dataSetId=' + dataSetId;
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

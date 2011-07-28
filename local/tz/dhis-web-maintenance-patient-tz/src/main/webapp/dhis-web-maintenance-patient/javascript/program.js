// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramDetails( programId )
{
	jQuery.postJSON( "getProgram.action", {
		id:programId
	}, function(json){
		setInnerHTML( 'idField', json.id );
		setInnerHTML( 'nameField', json.name );	
		setInnerHTML( 'descriptionField', json.description );    
		setInnerHTML( 'programStageCountField',  json.programStageCount );
		setInnerHTML( 'maxDaysFromStartField',  json.maxDay );
   
		showDetails();
	});   
}

// -----------------------------------------------------------------------------
// Remove Program
// -----------------------------------------------------------------------------

function removeProgram( programId, name )
{
	removeItem( programId, name, i18n_confirm_delete, 'removeProgram.action' );
}

// -----------------------------------------------------------------------------
// Create validation for dataelements into program-stages
// -----------------------------------------------------------------------------

var dataelementId;
function getDataElementsFromStage(stageId, dataelementId){

	this.dataelementId = dataelementId;
	clearListById(dataelementId);
	
	if(stageId != ''){
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( getDataElementsFromStageReceived)
		request.send( "getProgramStage.action?id=" + stageId );
	}	
}

function getDataElementsFromStageReceived(xmlObject){
	
	var dataelementField = byId(dataelementId);
	clearListById(dataelementId);
	
	xmlObject = xmlObject.getElementsByTagName('dataElements')[0];
	var dataelementList = xmlObject.getElementsByTagName( 'dataElement' );
  
	for ( var i = 0; i < dataelementList.length; i++ )
    {
        var id = dataelementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = dataelementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        dataelementField.add(option, null);       	
    }
}


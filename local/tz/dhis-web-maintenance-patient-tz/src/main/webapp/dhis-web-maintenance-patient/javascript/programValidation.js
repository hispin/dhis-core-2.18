
jQuery(document).ready(	function(){
	validation( 'programValidationForm', function( form ){			
		form.submit();
	});
});

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramValidationDetails ( programValidationId )
{
    $.ajax({
		url: 'getProgramValidation.action?validationId=' + programValidationId,
		cache: false,
		dataType: "xml",
		success: programValdiationReceived
	});
}

function programValdiationReceived( programValidationElement )
{
	setInnerHTML( 'idField', $( programValidationElement).find('id').text() );
	setInnerHTML( 'descriptionField', $( programValidationElement).find('description' ).text() );	
    setInnerHTML( 'leftSideField', $( programValidationElement).find('leftSide').text() );
	setInnerHTML( 'rightSideField', $( programValidationElement).find('rightSide').text() );
	setInnerHTML( 'programField', $( programValidationElement).find('program').text() );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove ProgramValidation
// -----------------------------------------------------------------------------

function removeProgramValidation( programValidationId, name )
{
	removeItem( programValidationId, name, i18n_confirm_delete, 'removeProgramValidation.action' );	
}

//-----------------------------------------------------------------
// Insert items data-element
//-----------------------------------------------------------------

function insertDataElement( element, target, decriptionDiv )
{
	var value = " " + element.options[element.selectedIndex].value + " ";
	
	insertTextCommon( target, value );
	
	getValidationDescription( decriptionDiv, target );
}

function insertOperator( decriptionDiv, target, value )
{
	insertTextCommon( target, ' ' + value + ' ' );
	
	getValidationDescription( decriptionDiv, target );
}


function getValidationDescription( decriptionDiv, sideDiv )
{
	$.post("getCaseAggregationDescription.action",
		{
			condition: getFieldValue( sideDiv )
		},
		function (data)
		{
			setInnerHTML( decriptionDiv, data );
		},'html');
}

function clearValidation( target, decriptionDiv )
{
	setFieldValue( target,'' );
	setInnerHTML( decriptionDiv, '' );
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into left-side
//------------------------------------------------------------------------------

function getLeftPrgramStageDataElements()
{
	clearListById( 'leftSideDE' );
	
	var programStage = document.getElementById( 'leftStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	if( psId == '') return;
  
	$.ajax({
		url: 'getPSDataElements.action?psId=' + psId,
		dataType: "xml",
		success: getLeftPrgramStageDataElementsCompleted
	});  
}

function getLeftPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = byId( 'leftSideDE' );
	var programstageDEList = $(dataelementElement).find( 'dataelement' );
 
	$( programstageDEList ).each( function( i, item )
        {
            var id = $( item ).find("id").text();
			var name = $( item ).find("name").text();
			var type = $( item ).find("type").text(); 

			var option = document.createElement("option");
			option.value = id;
			option.text = name;
			option.title = name;
			jQuery(option).attr({data:"{type:'"+type+"'}"});
			programstageDE.add(option, null);  			
        } );
}

//------------------------------------------------------------------------------
// Get DataElements of Program-Stage into right-side
//------------------------------------------------------------------------------

function getRightPrgramStageDataElements()
{
	clearListById( 'rightSideDE' );
  	
	var programStage = document.getElementById( 'rightStage' );
	var psId = programStage.options[ programStage.selectedIndex ].value;
	if( psId == '') return;
  
	$.ajax({
		url: 'getPSDataElements.action?psId=' + psId,
		dataType: "xml",
		success: getRightPrgramStageDataElementsCompleted
	});  
}

function getRightPrgramStageDataElementsCompleted( dataelementElement )
{
	var programstageDE = document.getElementById( 'rightSideDE' );
	var programstageDEList = $(dataelementElement).find( 'dataelement' );
 
	$( programstageDEList ).each( function( i, item )
    {
		var id = $( item ).find("id").text();
		var name = $( item ).find("name").text();
		var type = $( item ).find("type").text(); 
		
		var option = document.createElement("option");
		option.value = id;
		option.text = name;
		option.title = name;
		jQuery(option).attr({data:"{type:'"+type+"'}"});
		programstageDE.add(option, null); 
	});
}
